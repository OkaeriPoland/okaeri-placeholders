package eu.okaeri.placeholders;

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

    public static PlaceholderContext create() {
        return new PlaceholderContext();
    }

    public PlaceholderContext with(String field, String value) {
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
                throw new IllegalArgumentException("missing placeholder " + name + " for message " + state);
            }

            String render = placeholder.render();
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
