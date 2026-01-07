package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.GlobalFunctions;
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
        PlaceholderContext context = new PlaceholderContext(null, failMode);
        context.fields.put(Placeholders.GLOBAL_FUNCTIONS_KEY, Placeholder.of(null, GlobalFunctions.INSTANCE, context));
        return context;
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
        context.fields.put(Placeholders.GLOBAL_FUNCTIONS_KEY, Placeholder.of(placeholders, GlobalFunctions.INSTANCE, context));
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
            MessageField originalField = field;  // Keep reference to original for map key
            String name = field.getName();
            String alreadyRendered = rendered.get(field);

            if (alreadyRendered != null) {
                continue;
            }

            Placeholder placeholder = this.fields.get(name);

            // Fallback: if field not found, try as a global function via $ context
            // This allows {now()} or {now} instead of {$.now()}
            if (placeholder == null && this.placeholders != null) {
                Placeholder globalFunctions = this.fields.get(Placeholders.GLOBAL_FUNCTIONS_KEY);
                if (globalFunctions != null) {
                    // Check if this function exists on GlobalFunctions
                    if (this.placeholders.getResolver(GlobalFunctions.class, name) != null) {
                        // Transform field to be a sub-field of $ so rendering works correctly
                        // e.g., {now()} becomes equivalent to {$.now()}
                        String transformedSource = Placeholders.GLOBAL_FUNCTIONS_KEY + "." + field.getSource();
                        MessageField transformedField = MessageField.of(field.getLocale(), transformedSource);
                        transformedField.setDefaultValue(field.getDefaultValue());
                        field = transformedField;
                        placeholder = globalFunctions;
                    }
                }
            }

            // If field is missing but has a method call (sub with params like .or()), try with null value
            // This allows methods like .or() to provide fallbacks for missing/null fields
            // Only apply to method calls (with params), not plain field access like player.inventory.name
            if (placeholder == null && field.hasSub() && field.getSub().getParams() != null && field.getSub().getParams().length() > 0) {
                placeholder = Placeholder.of(this.placeholders, null, this);
            }

            if (placeholder == null || (placeholder.getValue() == null && !field.hasSub())) {
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

            rendered.put(originalField, render);  // Use original field as key for lookup in apply()
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
}
