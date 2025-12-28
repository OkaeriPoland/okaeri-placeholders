package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class PlaceholderContext {

    private final Map<String, Placeholder> fields = new LinkedHashMap<>();
    private final CompiledMessage message;
    private final FailMode failMode;
    private Placeholders placeholders;

    public static PlaceholderContext create() {
        return create(FailMode.FAIL_SAFE);
    }

    public static PlaceholderContext create(@NonNull FailMode failMode) {
        return new PlaceholderContext(null, failMode);
    }

    public static PlaceholderContext of(@NonNull CompiledMessage message) {
        return of(null, message);
    }

    public static PlaceholderContext of(@Nullable Placeholders placeholders, @NonNull CompiledMessage message) {
        return of(placeholders, message, FailMode.FAIL_SAFE);
    }

    public static PlaceholderContext of(@Nullable Placeholders placeholders, @NonNull CompiledMessage message, @NonNull FailMode failMode) {
        PlaceholderContext context = new PlaceholderContext(message, failMode);
        context.setPlaceholders(placeholders);
        return context;
    }

    public PlaceholderContext with(@NonNull String field, @Nullable Object value) {

        // only in fast mode
        if ((this.placeholders != null) && this.placeholders.isFastMode()) {
            // when non-shared context (message assigned) with no fields or field with such name not present
            if ((this.message != null) && (!this.message.isWithFields() || !this.message.hasField(field))) {
                // skip adding placeholder to the context
                return this;
            }
        }

        this.fields.put(field, Placeholder.of(this.placeholders, value, this));
        return this;
    }

    public PlaceholderContext with(@NonNull Map<String, Object> fields) {
        fields.forEach(this::with);
        return this;
    }

    public Map<MessageField, String> renderFields() {
        return this.renderFields(this.message);
    }

    public Map<MessageField, String> renderFields(@NonNull CompiledMessage message) {

        // someone is trying to apply message on the specific non-shareable context
        if ((message != this.message) && (this.message != null)) {
            throw new IllegalArgumentException("cannot apply another message for context created with prepacked message: " +
                "if you intended to use this context as shared please use empty context from #create(), " +
                "if you're just trying to send a message use of(message)");
        }

        // no fields, no need for processing
        if (!message.isWithFields()) {
            return Collections.emptyMap();
        }

        // prepare for fields
        String state = message.getRaw();
        List<MessageElement> parts = message.getParts();
        Map<MessageField, String> rendered = new LinkedHashMap<>();

        // render field parts
        for (MessageElement part : parts) {

            if (!(part instanceof MessageField)) {
                continue;
            }

            MessageField field = (MessageField) part;
            String name = field.getName();
            String alreadyRendered = rendered.get(field);

            if (alreadyRendered != null) {
                continue;
            }

            Placeholder placeholder = this.fields.get(name);
            if ((placeholder == null) || (placeholder.getValue() == null)) {
                if (field.getDefaultValue() != null) {
                    placeholder = Placeholder.of(null, field.getDefaultValue(), this);
                } else if (this.failMode == FailMode.FAIL_FAST) {
                    throw new IllegalArgumentException("missing placeholder '" + name + "' for message '" + state + "'");
                } else if (this.failMode == FailMode.FAIL_SAFE) {
                    placeholder = Placeholder.of(null, "<missing:" + field.getLastSubPath() + ">", this);
                } else {
                    throw new RuntimeException("unknown fail mode: " + this.failMode);
                }
            }

            String render = placeholder.render(field);
            if (render == null) {
                if (field.getDefaultValue() != null) {
                    render = field.getDefaultValue();
                } else if (this.failMode == FailMode.FAIL_FAST) {
                    throw new IllegalArgumentException("rendered null for placeholder '" + name + "' for message '" + state + "'");
                } else if (this.failMode == FailMode.FAIL_SAFE) {
                    render = "<null:" + field.getLastSubPath() + ">";
                } else {
                    throw new RuntimeException("unknown fail mode: " + this.failMode);
                }
            }

            rendered.put(field, render);
        }

        return rendered;
    }

    public String apply() {
        return this.apply(this.message);
    }

    public String apply(@NonNull CompiledMessage message) {

        List<MessageElement> parts = message.getParts();
        Map<MessageField, String> rendered = this.renderFields(message);

        // build message
        StringBuilder builder = new StringBuilder();
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

    /**
     * Extracts and returns the value of a placeholder without string conversion.
     * This method allows you to get the actual typed value of a placeholder by navigating through
     * the chain of fields (e.g., "user.rank.points").
     *
     * @param key The placeholder key, which can contain nested fields separated by dots (e.g., "user.rank.points")
     * @param outputValueType The expected type of the returned value
     * @param <T> The type parameter
     * @return Optional containing the value of the placeholder cast to type T, or empty if the value is null or type doesn't match
     * @throws IllegalArgumentException if the placeholder is not found in context
     */
    public <T> Optional<T> getPlaceholderValue(@NonNull String key, @NonNull Class<T> outputValueType) {
        Object value = this.resolvePlaceholderValue(key);
        
        if (value == null) {
            return Optional.empty();
        }
        
        if (!outputValueType.isInstance(value)) {
            return Optional.empty();
        }
        
        return Optional.of(outputValueType.cast(value));
    }

    /**
     * Extracts and returns the value of a placeholder with custom type conversion.
     * This method allows you to get the value of a placeholder and convert it to the desired type
     * using a custom mapper function.
     *
     * @param key The placeholder key, which can contain nested fields separated by dots (e.g., "user.rank.points")
     * @param valueMapper A function that converts the resolved Object to the desired type
     * @param outputValueType The expected type of the returned value (used for type safety)
     * @param <T> The type parameter
     * @return Optional containing the value of the placeholder converted to type T using the mapper, or empty if the value is null
     * @throws IllegalArgumentException if the placeholder is not found in context
     */
    public <T> Optional<T> getPlaceholderValue(
        @NonNull String key, 
        @NonNull java.util.function.Function<Object, ? extends T> valueMapper,
        @NonNull Class<T> outputValueType
    ) {
        Object value = this.resolvePlaceholderValue(key);
        
        if (value == null) {
            return Optional.empty();
        }
        
        return Optional.of(valueMapper.apply(value));
    }

    /**
     * Internal helper method to resolve a placeholder value.
     * 
     * @param key The placeholder key
     * @return The resolved value or null if not found
     */
    @Nullable
    private Object resolvePlaceholderValue(@NonNull String key) {
        MessageField field = MessageField.of(key);
        String rootName = field.getName();
        
        Placeholder placeholder = this.fields.get(rootName);
        if (placeholder == null) {
            throw new IllegalArgumentException("placeholder '" + rootName + "' not found in context");
        }
        
        return placeholder.resolveValue(field);
    }
}
