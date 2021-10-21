package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
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

    public static CompiledMessage of(@NonNull String source) {
        return of(Locale.ENGLISH, source);
    }

    public static CompiledMessage of(@NonNull Locale locale, @NonNull String source) {

        if (source.isEmpty()) {
            return new CompiledMessage(source, 0, 0, 0, false, Collections.emptyList(), Collections.emptySet());
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

            parts.add(messageField);
            usedFields.add(fieldName);
            usedFields.add(fieldName.split("\\.", 2)[0]);

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

        return new CompiledMessage(source, rawLength, fieldsLength, (rawLength - fieldsLength), withFields, Collections.unmodifiableList(parts), Collections.unmodifiableSet(usedFields));
    }

    private final String raw;
    private final int rawLength;
    private final int fieldsLength;
    private final int staticLength;
    private final boolean withFields;
    private final List<MessageElement> parts;
    private final Set<String> usedFields;

    public boolean hasField(@Nullable String name) {
        return this.usedFields.contains(name);
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
}
