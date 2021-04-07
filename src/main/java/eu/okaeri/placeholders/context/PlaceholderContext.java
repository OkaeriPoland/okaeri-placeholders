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
    private final CompiledMessage message;
    private final FailMode failMode;

    public static PlaceholderContext create() {
        return create(FailMode.FAIL_SAFE);
    }

    public static PlaceholderContext create(FailMode failMode) {
        if (failMode == null) throw new IllegalArgumentException("failMode cannot be null");
        return new PlaceholderContext(null, failMode);
    }

    public static PlaceholderContext of(CompiledMessage message) {
        return of(message, FailMode.FAIL_SAFE);
    }

    public static PlaceholderContext of(CompiledMessage message, FailMode failMode) {
        if (message == null) throw new IllegalArgumentException("message cannot be null");
        if (failMode == null) throw new IllegalArgumentException("failMode cannot be null");
        return new PlaceholderContext(message, failMode);
    }

    public PlaceholderContext with(String field, Object value) {

        if ((this.message != null) && (!this.message.isWithFields() || !this.message.hasField(field))) {
            return this;
        }

        this.placeholders.put(field, Placeholder.of(value));
        return this;
    }

    public String apply() {
        return this.apply(this.message);
    }

    public String apply(CompiledMessage message) {

        // someone is trying to apply message on the specific non-shareable context
        if ((message != this.message) && (this.message != null)) {
            throw new IllegalArgumentException("cannot apply another message for context created with prepacked message: " +
                    "if you intended to use this context as shared please use empty context from #create(), " +
                    "if you're just trying to send a message use of(message)");
        }

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
