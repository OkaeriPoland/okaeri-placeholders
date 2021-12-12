package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageStatic implements MessageElement {

    private final String value;

    public static MessageStatic of(@NonNull String value) {
        return new MessageStatic(value);
    }
}
