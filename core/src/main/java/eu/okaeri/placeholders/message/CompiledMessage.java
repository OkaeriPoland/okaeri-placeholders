package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.node.Call;
import eu.okaeri.placeholders.ast.node.Ref;
import eu.okaeri.placeholders.ast.node.WithDefault;
import eu.okaeri.placeholders.ast.parser.ExpressionParser;
import eu.okaeri.placeholders.ast.parser.Lexer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CompiledMessage {

    private static final Pattern FIELD_PATTERN = Pattern.compile("\\{(?<content>[^}]+)\\}");

    private final String raw;
    private final List<MessageElement> parts;
    private final Set<String> usedFields;
    private final Locale locale;

    public static CompiledMessage of(@NonNull String raw, @NonNull List<MessageElement> parts) {
        return of(Locale.ENGLISH, raw, parts);
    }

    public static CompiledMessage of(@NonNull Locale locale, @NonNull String raw, @NonNull List<MessageElement> parts) {
        Set<String> usedFields = new HashSet<>();
        for (MessageElement part : parts) {
            if (part instanceof ExpressionPart) {
                extractUsedFieldsFromAst(((ExpressionPart) part).getAst(), usedFields);
            }
        }
        return new CompiledMessage(raw, parts, usedFields, locale);
    }

    public static CompiledMessage of(@NonNull String source) {
        return of(Locale.ENGLISH, source);
    }

    public static CompiledMessage of(@NonNull Locale locale, @NonNull String source) {
        // Use AST-based parsing
        return ofAst(locale, source);
    }

    /**
     * Transforms legacy # metadata syntax to equivalent method call syntax.
     * <pre>
     * {%.2f#value}         → {value.format("%.2f")}
     * {lt,medium,UTC#time} → {time.lt("medium","UTC")}
     * {p,yyyy-MM-dd#time}  → {time.format("yyyy-MM-dd")}
     * {apple,apples#count} → {count._meta("apple","apples")}
     * {yes,no#active}      → {active._meta("yes","no")}
     * </pre>
     */
    private static String transformLegacyMetadata(@NonNull String field, @NonNull String metadata) {
        // Printf format: starts with %
        if (metadata.startsWith("%")) {
            return field + ".format(\"" + escapeQuotes(metadata) + "\")";
        }

        // Parse comma-separated options
        String[] options = parseMetadataOptions(metadata);
        if (options.length == 0) {
            return field;
        }

        // Datetime: first option is lt/ld/ldt/p
        String first = options[0].toLowerCase(Locale.ROOT);
        if ("lt".equals(first) || "ld".equals(first) || "ldt".equals(first)) {
            return field + "." + first + "(" + formatMethodArgs(options, 1) + ")";
        }
        // Pattern-based datetime: p → format
        if ("p".equals(first)) {
            return field + ".format(" + formatMethodArgs(options, 1) + ")";
        }

        // Generic: bool/plural decided at runtime (underscore = internal/legacy)
        return field + "._meta(" + formatMethodArgs(options, 0) + ")";
    }

    /**
     * Parses comma-separated metadata options, respecting quotes and backslash escapes.
     * <p>
     * Rules:
     * <ul>
     *   <li>{@code \,} escapes a literal comma; other {@code \X} sequences keep the backslash literal.</li>
     *   <li>A {@code '} or {@code "} only opens a quoted region if a closing quote exists before the
     *       next {@code ,} (delegated to {@link Lexer#findClosingQuote}). Otherwise the quote is a
     *       regular character — so {@code don't,won't} cleanly splits into two options.</li>
     *   <li>When a paired-quote region is consumed, the quote characters themselves are stripped
     *       from the option value.</li>
     * </ul>
     */
    private static String[] parseMetadataOptions(@NonNull String metadata) {
        List<String> options = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;
        int length = metadata.length();

        while (i < length) {
            char c = metadata.charAt(i);

            if ((c == '\\') && ((i + 1) < length)) {
                char next = metadata.charAt(i + 1);
                if (next == ',') {
                    current.append(',');
                    i += 2;
                    continue;
                }
                // other `\X` sequences: keep the backslash literally
            }

            if ((c == '"') || (c == '\'')) {
                int closer = Lexer.findClosingQuote(metadata, i + 1, c, ',');
                if (closer != -1) {
                    current.append(metadata, i + 1, closer);
                    i = closer + 1;
                    continue;
                }
                // unterminated within the option — fall through and treat `c` as literal
            }

            if (c == ',') {
                options.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
            i++;
        }

        if (current.length() > 0) {
            options.add(current.toString().trim());
        }

        return options.toArray(new String[0]);
    }

    /**
     * Formats options as method arguments starting from given index.
     */
    private static String formatMethodArgs(String[] options, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < options.length; i++) {
            if (i > startIndex) {
                sb.append(",");
            }
            sb.append("\"").append(escapeQuotes(options[i])).append("\"");
        }
        return sb.toString();
    }

    /**
     * Escapes double quotes in a string for use in method arguments.
     */
    private static String escapeQuotes(@NonNull String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public boolean hasField(@Nullable String name) {
        return this.usedFields.contains(name);
    }

    public boolean isWithFields() {
        return !this.usedFields.isEmpty();
    }

    // === AST-based parsing ===

    /**
     * Compiles a message using AST-based parsing.
     * <p>
     * This method properly handles nested braces and parses expressions to a full AST.
     * It's the recommended way to compile messages for the new AST-based evaluation.
     *
     * @param source The message source string
     * @return A compiled message with AST-based expression parts
     */
    public static CompiledMessage ofAst(@NonNull String source) {
        return ofAst(Locale.ENGLISH, source);
    }

    /**
     * Compiles a message using AST-based parsing with a specific locale.
     *
     * @param locale The locale for the message
     * @param source The message source string
     * @return A compiled message with AST-based expression parts
     */
    public static CompiledMessage ofAst(@NonNull Locale locale, @NonNull String source) {

        if (source.isEmpty()) {
            return new CompiledMessage(source, Collections.emptyList(), Collections.emptySet(), locale);
        }

        List<MessageElement> parts = new ArrayList<>();
        Set<String> usedFields = new HashSet<>();

        int lastIndex = 0;
        int length = source.length();

        int i = 0;
        while (i < length) {
            char c = source.charAt(i);

            if (c == '{') {
                // Found placeholder start - scan to find matching close
                int start = i;
                int depth = 1;
                i++;

                while ((i < length) && (depth > 0)) {
                    char ch = source.charAt(i);
                    if (ch == '{') {
                        depth++;
                    } else if (ch == '}') {
                        depth--;
                    } else if ((ch == '"') || (ch == '\'')) {
                        // Asymmetric scan: `"` is greedy (scan to EOF) because it almost never
                        // appears bare in real text, so embedded `}` inside `"..."` works fine.
                        // `'` is bounded at the next `}` because it must coexist with English
                        // possessives like `{player.append('s)}` and contractions in static text.
                        char stop = (ch == '\'') ? '}' : '\0';
                        int closer = Lexer.findClosingQuote(source, i + 1, ch, stop);
                        if (closer != -1) {
                            i = closer; // jump straight to the closing quote; outer i++ advances past it
                        }
                    }
                    i++;
                }

                if (depth == 0) {
                    // Found complete placeholder
                    // Add static text before placeholder
                    if (start > lastIndex) {
                        parts.add(StaticPart.of(source.substring(lastIndex, start)));
                    }

                    // Extract and parse placeholder content
                    String content = source.substring(start + 1, i - 1);
                    String originalContent = content; // Store original before transformations

                    // Extract default value BEFORE parsing - everything after last | is literal text
                    // This allows {a|&afeature enabled} to work
                    String defaultValue = null;
                    int pipeIndex = findDefaultPipe(content);
                    if (pipeIndex != -1) {
                        defaultValue = content.substring(pipeIndex + 1);
                        content = content.substring(0, pipeIndex);
                        // Also update originalContent to exclude the default value for key matching
                        originalContent = content;
                    }

                    // Transform legacy # syntax
                    String transformedContent = transformLegacyHashSyntax(content);

                    // Transform shorthand .func() to $.func()
                    if (transformedContent.startsWith(".")) {
                        transformedContent = Placeholders.GLOBAL_FUNCTIONS_KEY + transformedContent;
                    }

                    // Parse to AST
                    AstNode ast = new ExpressionParser(transformedContent).parse();

                    // Extract root field name for usedFields
                    extractUsedFieldsFromAst(ast, usedFields);

                    parts.add(ExpressionPart.of(originalContent, transformedContent, ast, defaultValue));
                    lastIndex = i;
                }
            } else {
                i++;
            }
        }

        // Add remaining static text
        if (lastIndex < length) {
            parts.add(StaticPart.of(source.substring(lastIndex)));
        }

        return new CompiledMessage(
            source,
            Collections.unmodifiableList(parts),
            Collections.unmodifiableSet(usedFields),
            locale
        );
    }

    /**
     * Transforms legacy # metadata syntax at parse time.
     * This is applied before AST parsing for backward compatibility.
     */
    private static String transformLegacyHashSyntax(@NonNull String content) {
        int hashIndex = content.indexOf('#');
        if (hashIndex == -1) {
            return content;
        }

        String metadata = content.substring(0, hashIndex);
        String fieldPart = content.substring(hashIndex + 1);

        // Extract default value from field part BEFORE transforming
        int fallbackInField = fieldPart.lastIndexOf('|');
        int argsEndInField = fieldPart.lastIndexOf(')');
        String defaultSuffix = "";
        if ((fallbackInField != -1) && (fallbackInField > argsEndInField)) {
            defaultSuffix = fieldPart.substring(fallbackInField);
            fieldPart = fieldPart.substring(0, fallbackInField);
        }

        // Transform: active → active._meta("yes","no")
        return transformLegacyMetadata(fieldPart, metadata) + defaultSuffix;
    }

    /**
     * Extracts field names from an AST for usedFields tracking.
     * Adds both root field names and full dotted paths.
     */
    private static void extractUsedFieldsFromAst(AstNode ast, Set<String> usedFields) {
        if (ast instanceof Ref) {
            usedFields.add(((Ref) ast).getName());
        } else if (ast instanceof WithDefault) {
            extractUsedFieldsFromAst(((WithDefault) ast).getExpression(), usedFields);
            extractUsedFieldsFromAst(((WithDefault) ast).getDefaultValue(), usedFields);
        } else if (ast instanceof Call) {
            Call call = (Call) ast;
            // Add full path like "player.name"
            String fullPath = buildFieldPath(call);
            if (fullPath != null) {
                usedFields.add(fullPath);
            }
            // Also recurse to add root ref
            extractUsedFieldsFromAst(call.getTarget(), usedFields);
            for (AstNode arg : call.getArgs()) {
                extractUsedFieldsFromAst(arg, usedFields);
            }
        }
        // Literals don't contribute to usedFields
    }

    /**
     * Builds a dotted path from a Call chain like player.name → "player.name".
     * Returns null if the chain doesn't start with a Ref.
     */
    private static String buildFieldPath(AstNode node) {
        if (node instanceof Ref) {
            return ((Ref) node).getName();
        } else if (node instanceof Call) {
            Call call = (Call) node;
            String targetPath = buildFieldPath(call.getTarget());
            if (targetPath != null) {
                return targetPath + "." + call.getName();
            }
        }
        return null;
    }

    /**
     * Finds the pipe character for default value, respecting parentheses and quotes.
     * Returns -1 if no default pipe found.
     * <p>
     * The default pipe is the FIRST | that's not inside parentheses or quotes.
     * Everything after this pipe is treated as literal text (the default value).
     * This allows: {field|default with | pipes} where the default is "default with | pipes".
     */
    private static int findDefaultPipe(String content) {
        int depth = 0;
        int i = 0;
        int length = content.length();

        while (i < length) {
            char c = content.charAt(i);

            if ((c == '"') || (c == '\'')) {
                // A quote only opens a string region if a closer exists somewhere ahead.
                // Otherwise a bare apostrophe (e.g. `{x.append('s)|fallback}`) would eat the `|`.
                int closer = Lexer.findClosingQuote(content, i + 1, c, '\0');
                if (closer != -1) {
                    i = closer + 1;
                    continue;
                }
                // unterminated quote → fall through, treat as literal char
            }

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if ((c == '|') && (depth == 0)) {
                return i;
            }
            i++;
        }

        return -1;
    }
}
