package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageField implements MessageElement {

    public static MessageField of(String name) {

        String[] parts = name.split("\\.");
        MessageField field = null;

        for (int i = parts.length - 1; i >= 0; i--) {
          field = new MessageField(parts[i], field);
        }

        return field;
    }

    private final String name;
    private final MessageField sub;

    public boolean hasSub() {
        return this.sub != null;
    }

    public MessageField lastSub() {
        MessageField last = this.sub;
        while (last.hasSub()) last = last.getSub();
        return last;
    }
}
