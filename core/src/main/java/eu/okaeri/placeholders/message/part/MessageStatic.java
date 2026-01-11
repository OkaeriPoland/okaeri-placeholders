package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageStatic implements MessageElement {

    private String value;

    public static MessageStatic of(@NonNull String value) {
        return new MessageStatic(value);
    }
}
