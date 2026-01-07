package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import eu.okaeri.placeholders.message.part.ParsedArg;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CompiledMessage {

    private static final Pattern FIELD_PATTERN = Pattern.compile("\\{(?<content>[^}]+)\\}");

    private final String raw;
    private final List<MessageElement> parts;
    private final Set<String> usedFields;

    public static CompiledMessage of(@NonNull String raw, @NonNull List<MessageElement> parts) {

        Set<String> usedFields = new HashSet<>();
        for (MessageElement part : parts) {

            if (!(part instanceof MessageField)) {
                continue;
            }

            MessageField field = (MessageField) part;
            String fieldName = field.getName();

            usedFields.add(fieldName);
            usedFields.add(fieldName.split("(\\.|\\()", 2)[0]);
        }

        return new CompiledMessage(raw, parts, usedFields);
    }

    public static CompiledMessage of(@NonNull String source) {
        return of(Locale.ENGLISH, source);
    }

    public static CompiledMessage of(@NonNull Locale locale, @NonNull String source) {

        if (source.isEmpty()) {
            return new CompiledMessage(source, Collections.emptyList(), Collections.emptySet());
        }

        Matcher matcher = FIELD_PATTERN.matcher(source);
        List<MessageElement> parts = new ArrayList<>();
        Set<String> usedFields = new HashSet<>();

        int lastIndex = 0;
        int rawLength = source.length();
        int fieldsLength = 0;

        while (matcher.find()) {

            parts.add(MessageStatic.of(source.substring(lastIndex, matcher.start())));
            String content = matcher.group("content");
            String[] fieldElements = parseFieldToArray(content);
            String fieldName = fieldElements[0];
            String defaultValue = fieldElements[1];

            MessageField messageField = MessageField.of(locale, fieldName);
            messageField.setDefaultValue(defaultValue);
            messageField.setRaw(content);

            parts.add(messageField);
            usedFields.add(fieldName);
            usedFields.add(fieldName.split("(\\.|\\()", 2)[0]);

            // Also extract potential field references from method arguments
            // This allows fast mode to work with field refs in args like {$.coalesce(a,b,c)}
            extractFieldRefsFromArgs(messageField, usedFields);

            lastIndex = matcher.end();
            fieldsLength += matcher.group().length();
        }

        if (lastIndex != source.length()) {
            parts.add(MessageStatic.of(source.substring(lastIndex)));
        }

        boolean withFields = (fieldsLength > 0);
        if (!withFields && (parts.size() > 1)) {
            throw new RuntimeException("noticed message without fields with more than one element: " + parts);
        }

        return new CompiledMessage(
            source,
            Collections.unmodifiableList(parts),
            Collections.unmodifiableSet(usedFields)
        );
    }

    /**
     * Parses field content into [fieldName, defaultValue].
     * Legacy # metadata syntax is transformed to method calls.
     */
    private static String[] parseFieldToArray(@NonNull String raw) {

        String[] arr = new String[2];

        // Transform legacy # metadata to method call syntax
        int commentIndex = raw.indexOf("#");
        if (commentIndex != -1) {
            String metadata = raw.substring(0, commentIndex);
            String fieldPart = raw.substring(commentIndex + 1);

            // Extract default value from field part BEFORE transforming
            // {yes,no#active|unknown} → field="active", default="unknown", metadata="yes,no"
            int fallbackInField = fieldPart.lastIndexOf("|");
            int argsEndInField = fieldPart.lastIndexOf(")");
            if ((fallbackInField != -1) && (fallbackInField > argsEndInField)) {
                arr[1] = fieldPart.substring(fallbackInField + 1);
                fieldPart = fieldPart.substring(0, fallbackInField);
            }

            // Transform: active → active._meta("yes","no")
            raw = transformLegacyMetadata(fieldPart, metadata);
        } else {
            // No metadata - just extract default value
            int fallbackIndex = raw.lastIndexOf("|");
            int argumentsEndIndex = raw.lastIndexOf(")");
            if ((fallbackIndex != -1) && (fallbackIndex > argumentsEndIndex)) {
                arr[1] = raw.substring(fallbackIndex + 1);
                raw = raw.substring(0, fallbackIndex);
            }
        }

        arr[0] = raw;
        return arr;
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
     * Backslash can escape commas: \, becomes a literal comma in the option value.
     */
    private static String[] parseMetadataOptions(@NonNull String metadata) {
        List<String> options = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;
        boolean escaped = false;

        for (int i = 0; i < metadata.length(); i++) {
            char c = metadata.charAt(i);

            if (escaped) {
                // Previous char was backslash - treat this char literally
                current.append(c);
                escaped = false;
            } else if ((c == '\\') && ((i + 1) < metadata.length())) {
                // Backslash - check if escaping comma, otherwise keep it
                char next = metadata.charAt(i + 1);
                if (next == ',') {
                    escaped = true;  // Will append the comma literally
                } else {
                    current.append(c);  // Keep the backslash for other escapes
                }
            } else if (!inQuotes && ((c == '"') || (c == '\''))) {
                inQuotes = true;
                quoteChar = c;
            } else if (inQuotes && (c == quoteChar)) {
                inQuotes = false;
            } else if (!inQuotes && (c == ',')) {
                options.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
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

    /**
     * Extracts potential field references from method arguments in a MessageField chain.
     * For example, in {$.coalesce(a,b,"literal")}, this extracts "a" and "b" as potential fields.
     * Quoted arguments are ignored as they are explicit literals.
     */
    private static void extractFieldRefsFromArgs(MessageField field, Set<String> usedFields) {
        MessageField current = field;
        while (current != null) {
            FieldParams params = current.getParams();
            if ((params != null) && (params.getParsedParams() != null)) {
                for (ParsedArg arg : params.getParsedParams()) {
                    // Only unquoted args (FIELD_REF_OR_LITERAL) could be field references
                    if (arg.mayBeFieldRef()) {
                        String value = arg.getValue();
                        if ((value != null) && !value.isEmpty()) {
                            // Add the root field name (before any dots)
                            String rootField = value.split("\\.", 2)[0];
                            usedFields.add(rootField);
                            usedFields.add(value);
                        }
                    }
                }
            }
            current = current.getSub();
        }
    }
}
