package eu.okaeri.placeholders.message.part;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageFieldTokenizer {

    public List<FieldParams> tokenize(@NonNull String field) {

        List<FieldParams> tokens = new ArrayList<>();
        char[] charArray = field.toCharArray();
        StringBuilder buffer = new StringBuilder();
        boolean parsingArgs = false;
        int parenDepth = 0;
        String name = "";

        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];

            if ((c == '(') && !parsingArgs) {
                name = buffer.toString();
                buffer.setLength(0);
                parsingArgs = true;
                parenDepth = 1;
                continue;
            }

            if ((c == '(') && parsingArgs) {
                parenDepth++;
                buffer.append(c);
                continue;
            }

            if ((c == ')') && parsingArgs) {
                parenDepth--;
                if (parenDepth == 0) {
                    // Closing the outermost paren
                    String argText = buffer.toString();
                    List<ParsedArg> parsedArgs = this.tokenizeArgsParsed(argText);
                    tokens.add(FieldParams.ofParsed(name, parsedArgs.toArray(new ParsedArg[0])));
                    buffer.setLength(0);
                    parsingArgs = false;
                    // Skip the dot after ) if present
                    if (((i + 1) < charArrayLength) && (charArray[i + 1] == '.')) {
                        i++;
                    }
                    continue;
                } else {
                    // Nested closing paren
                    buffer.append(c);
                    continue;
                }
            }

            if (parsingArgs) {
                buffer.append(c);
                continue;
            }

            if (((charArrayLength == (i + 1)) || (charArray[i + 1] == '.'))) {
                buffer.append(c);
                tokens.add(FieldParams.of(buffer.toString(), new String[0]));
                buffer.setLength(0);
                i++;
                continue;
            }

            buffer.append(c);
        }

        return tokens;
    }

    /**
     * Tokenizes argument text into raw strings, preserving the original format.
     * This method does NOT strip quotes - it preserves them for backward compatibility.
     * Uses quote-aware and paren-aware splitting (commas inside quotes or nested parens are not treated as separators).
     */
    public List<String> tokenizeArgs(@NonNull String argText) {

        if (argText.isEmpty()) {
            return Collections.singletonList("");
        }

        List<String> args = new ArrayList<>();
        char[] charArray = argText.toCharArray();
        StringBuilder buffer = new StringBuilder();
        char quoteChar = 0; // Track if we're inside quotes (to not split on commas inside quotes)
        int parenDepth = 0; // Track nested parentheses

        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];

            // Track quote state (but still append the quote character)
            if (((c == '"') || (c == '\'')) && ((i == 0) || (charArray[i - 1] != '\\'))) {
                if (quoteChar == 0) {
                    quoteChar = c;
                } else if (quoteChar == c) {
                    quoteChar = 0;
                }
            }

            // Track parentheses depth (only outside quotes)
            if (quoteChar == 0) {
                if (c == '(') {
                    parenDepth++;
                } else if (c == ')') {
                    parenDepth--;
                }
            }

            // Handle comma - split only if not in quotes AND not inside nested parens
            if ((c == ',') && (quoteChar == 0) && (parenDepth == 0)) {
                if ((i > 0) && (charArray[i - 1] == '\\')) {
                    buffer.setCharAt(buffer.length() - 1, c);
                    continue;
                }
                args.add(buffer.toString());
                buffer.setLength(0);
                continue;
            }

            // Handle end of input
            if (charArrayLength == (i + 1)) {
                buffer.append(c);
                args.add(buffer.toString());
                buffer.setLength(0);
                continue;
            }

            buffer.append(c);
        }

        // Handle case where buffer has content but wasn't added
        if (buffer.length() > 0) {
            args.add(buffer.toString());
        }

        return args;
    }

    /**
     * Tokenizes argument text into ParsedArg objects, tracking whether each argument
     * was quoted (explicit literal) or unquoted (field reference or literal).
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code "hello"} → ParsedArg.literal("hello")</li>
     *   <li>{@code 'world'} → ParsedArg.literal("world")</li>
     *   <li>{@code player.name} → ParsedArg.fieldRefOrLiteral("player.name")</li>
     *   <li>{@code value} → ParsedArg.fieldRefOrLiteral("value")</li>
     * </ul>
     *
     * @param argText The argument text to tokenize
     * @return List of parsed arguments with type information
     */
    public List<ParsedArg> tokenizeArgsParsed(@NonNull String argText) {

        if (argText.isEmpty()) {
            return Collections.singletonList(ParsedArg.fieldRefOrLiteral(""));
        }

        List<ParsedArg> args = new ArrayList<>();
        char[] charArray = argText.toCharArray();
        StringBuilder buffer = new StringBuilder();
        char quoteChar = 0; // 0 = not in quotes, '"' or '\'' = in that quote type
        int parenDepth = 0; // Track nested parentheses
        boolean wasQuoted = false; // Track if current arg started with a quote
        char usedQuoteChar = 0; // Remember which quote char was used for rawValue reconstruction

        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];

            // Handle quote start/end
            if (((c == '"') || (c == '\'')) && ((i == 0) || (charArray[i - 1] != '\\'))) {
                if (quoteChar == 0) {
                    // Start of quoted section
                    quoteChar = c;
                    usedQuoteChar = c;
                    wasQuoted = true;
                    continue;
                } else if (quoteChar == c) {
                    // End of quoted section (matching quote)
                    quoteChar = 0;
                    continue;
                }
                // Different quote type inside - treat as literal
            }

            // Track parentheses depth (only outside quotes)
            if (quoteChar == 0) {
                if (c == '(') {
                    parenDepth++;
                } else if (c == ')') {
                    parenDepth--;
                }
            }

            // Handle escaped characters inside quotes
            if ((c == '\\') && (quoteChar != 0) && ((i + 1) < charArrayLength)) {
                char next = charArray[i + 1];
                if ((next == quoteChar) || (next == '\\')) {
                    buffer.append(next);
                    i++;
                    continue;
                }
            }

            // Handle comma - split only if not in quotes AND not inside nested parens
            if ((c == ',') && (quoteChar == 0) && (parenDepth == 0)) {
                if ((i > 0) && (charArray[i - 1] == '\\')) {
                    buffer.setCharAt(buffer.length() - 1, c);
                    continue;
                }
                // Add current argument
                String argValue = buffer.toString();
                args.add(wasQuoted
                    ? ParsedArg.literal(argValue, usedQuoteChar)
                    : ParsedArg.fieldRefOrLiteral(argValue));
                buffer.setLength(0);
                wasQuoted = false;
                usedQuoteChar = 0;
                continue;
            }

            // Handle end of input
            if (charArrayLength == (i + 1)) {
                buffer.append(c);
                String argValue = buffer.toString();
                args.add(wasQuoted
                    ? ParsedArg.literal(argValue, usedQuoteChar)
                    : ParsedArg.fieldRefOrLiteral(argValue));
                buffer.setLength(0);
                wasQuoted = false;
                usedQuoteChar = 0;
                continue;
            }

            buffer.append(c);
        }

        // Handle case where buffer has content but wasn't added (unclosed quote at end)
        // Also handle empty quoted strings like "" where wasQuoted is true but buffer is empty
        if ((buffer.length() > 0) || wasQuoted) {
            String argValue = buffer.toString();
            args.add(wasQuoted
                ? ParsedArg.literal(argValue, usedQuoteChar)
                : ParsedArg.fieldRefOrLiteral(argValue));
        }

        return args;
    }
}
