package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageStatic implements MessageElement {

    public static MessageStatic of(String value) {
        return new MessageStatic(value);
    }

    private final String value;
}