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
            String metaElement = fieldElements[0];
            String fieldName = fieldElements[1];
            String defaultValue = fieldElements[2];

            MessageField messageField = MessageField.of(locale, fieldName);
            messageField.setDefaultValue(defaultValue);
            messageField.setMetadataRaw(metaElement);
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

    private static String[] parseFieldToArray(@NonNull String raw) {

        String[] arr = new String[3];

        int commentIndex = raw.indexOf("#");
        if (commentIndex != -1) {
            arr[0] = raw.substring(0, commentIndex);
            raw = raw.substring(commentIndex + 1);
        }

        int fallbackIndex = raw.lastIndexOf("|");
        int argumentsEndIndex = raw.lastIndexOf(")");
        if ((fallbackIndex != -1) && (fallbackIndex > argumentsEndIndex)) {
            arr[2] = raw.substring(fallbackIndex + 1);
            raw = raw.substring(0, fallbackIndex);
        }

        arr[1] = raw;
        return arr;
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
            if (params != null && params.getParsedParams() != null) {
                for (ParsedArg arg : params.getParsedParams()) {
                    // Only unquoted args (FIELD_REF_OR_LITERAL) could be field references
                    if (arg.mayBeFieldRef()) {
                        String value = arg.getValue();
                        if (value != null && !value.isEmpty()) {
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
