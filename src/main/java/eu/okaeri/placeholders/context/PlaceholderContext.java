package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class PlaceholderContext {

    private final Map<String, Placeholder> placeholders = new LinkedHashMap<>();
    private final FailMode failMode;

    public static PlaceholderContext create() {
        return create(FailMode.FAIL_SAFE);
    }

    public static PlaceholderContext create(FailMode failMode) {
        return new PlaceholderContext(failMode);
    }

    public PlaceholderContext with(String field, Object value) {
        this.placeholders.put(field, Placeholder.of(value));
        return this;
    }

    public String apply(CompiledMessage message) {

        // no fields, no need for processing
        if (!message.isWithFields()) {
            return message.getRaw();
        }

        // prepare for fields
        String state = message.getRaw();
        List<MessageElement> parts = message.getParts();
        Map<MessageField, String> rendered = new LinkedHashMap<>();
        int totalRenderLength = 0;

        // render field parts
        for (MessageElement part : parts) {

            if (!(part instanceof MessageField)) {
                continue;
            }

            MessageField field = (MessageField) part;
            String name = field.getName();
            String alreadyRendered = rendered.get(field);

            if (alreadyRendered != null) {
                totalRenderLength += alreadyRendered.length();
                continue;
            }

            Placeholder placeholder = this.placeholders.get(name);
            if (placeholder == null) {
                if (this.failMode == FailMode.FAIL_FAST) {
                    throw new IllegalArgumentException("missing placeholder '" + name + "' for message '" + state + "'");
                } else if (this.failMode == FailMode.FAIL_SAFE) {
                    placeholder = Placeholder.of("<missing:" + name + ">");
                } else {
                    throw new RuntimeException("unknown fail mode: " + this.failMode);
                }
            }

            String render = placeholder.render(field);
            rendered.put(field, render);
            totalRenderLength += render.length();
        }

        // build message
        StringBuilder builder = new StringBuilder(message.getStaticLength() + totalRenderLength);
        for (MessageElement part : parts) {

            if (part instanceof MessageStatic) {
                builder.append(((MessageStatic) part).getValue());
                continue;
            }

            if (part instanceof MessageField) {
                MessageField field = (MessageField) part;
                String render = rendered.get(field);
                builder.append(render);
                continue;
            }

            throw new IllegalArgumentException("unknown message part: " + part);
        }

        return builder.toString();
    }
}
