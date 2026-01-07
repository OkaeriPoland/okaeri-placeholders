package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Default renderer that produces String output.
 * <p>
 * This is the standard renderer used by {@link PlaceholderContext#apply()}.
 */
public class StringMessageRenderer implements MessageRenderer<String> {

    public static final StringMessageRenderer INSTANCE = new StringMessageRenderer();

    @Override
    public String render(@NonNull CompiledMessage message, @NonNull PlaceholderContext context) {
        List<MessageElement> parts = message.getParts();
        Map<MessageField, String> rendered = context.renderFields(message);

        StringBuilder builder = new StringBuilder();
        for (MessageElement part : parts) {
            if (part instanceof MessageStatic) {
                builder.append(((MessageStatic) part).getValue());
            } else if (part instanceof MessageField) {
                builder.append(rendered.get(part));
            } else {
                throw new IllegalArgumentException("unknown message part: " + part);
            }
        }

        return builder.toString();
    }
}
