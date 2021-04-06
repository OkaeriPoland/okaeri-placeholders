package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CompiledMessage {

    private static final Pattern FIELD_PATTERN = Pattern.compile("\\{([^\\s]+)\\}");

    public static CompiledMessage of(String source) {

        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }

        if (source.isEmpty()) {
            return new CompiledMessage(source, 0, 0, 0, false, Collections.emptyList());
        }

        Matcher matcher = FIELD_PATTERN.matcher(source);
        List<MessageElement> parts = new ArrayList<>();
        int lastIndex = 0;
        int rawLength = source.length();
        int fieldsLength = 0;

        while (matcher.find()) {
            parts.add(MessageStatic.of(source.substring(lastIndex, matcher.start())));
            parts.add(MessageField.of(matcher.group(1)));
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

        return new CompiledMessage(source, rawLength, fieldsLength, (rawLength - fieldsLength), withFields, Collections.unmodifiableList(parts));
    }

    private final String raw;
    private final int rawLength;
    private final int fieldsLength;
    private final int staticLength;
    private final boolean withFields;
    private final List<MessageElement> parts;
}
