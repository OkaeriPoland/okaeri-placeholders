package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.GlobalFunctions;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.MessageRenderer;
import eu.okaeri.placeholders.message.StringMessageRenderer;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;
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

        String state = message.getRaw();
        Map<MessageField, ResolvedField> resolved = this.resolveFieldsInternal(message);
        Map<MessageField, String> rendered = new LinkedHashMap<>();

        for (Map.Entry<MessageField, ResolvedField> entry : resolved.entrySet()) {
            MessageField originalField = entry.getKey();
            ResolvedField rf = entry.getValue();

            // Use placeholder.render() for proper formatting (printf, plural, bool, etc.)
            String render = rf.placeholder.render(rf.transformedField);
            if (render == null) {
                if (rf.transformedField.getDefaultValue() != null) {
                    render = rf.transformedField.getDefaultValue();
                } else if (this.failMode == FailMode.FAIL_FAST) {
                    throw new IllegalArgumentException("rendered null for placeholder '" + originalField.getName() + "' for message '" + state + "'");
                } else if (this.failMode == FailMode.FAIL_SAFE) {
                    render = "<null:" + rf.transformedField.getLastSubPath() + ">";
                } else {
                    throw new RuntimeException("unknown fail mode: " + this.failMode);
                }
            }

            rendered.put(originalField, render);
        }

        return rendered;
    }

    /**
     * Renders field values preserving their types (not converting to String).
     * Returns Map keyed by field raw expression for easy lookup.
     * <p>
     * This is useful for typed composition with external systems like MiniMessage
     * that need to handle values differently based on their type (e.g., Component vs String).
     *
     * @return Map of field raw expressions to their resolved typed values
     */
    public Map<String, Object> renderFieldValues() {
        return this.renderFieldValues(this.message);
    }

    /**
     * Renders field values preserving their types (not converting to String).
     * Returns Map keyed by field raw expression for easy lookup.
     *
     * @param message The message to render fields for
     * @return Map of field raw expressions to their resolved typed values
     */
    public Map<String, Object> renderFieldValues(@NonNull CompiledMessage message) {

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

        String state = message.getRaw();
        Map<MessageField, ResolvedField> resolved = this.resolveFieldsInternal(message);
        Map<String, Object> rendered = new LinkedHashMap<>();

        for (Map.Entry<MessageField, ResolvedField> entry : resolved.entrySet()) {
            MessageField originalField = entry.getKey();
            ResolvedField rf = entry.getValue();

            // Resolve typed value (not String)
            Object value = rf.placeholder.resolveValue(rf.transformedField);

            // Handle null result
            if (value == null) {
                if (rf.transformedField.getDefaultValue() != null) {
                    rendered.put(originalField.getRaw(), rf.transformedField.getDefaultValue());
                } else if (this.failMode == FailMode.FAIL_FAST) {
                    throw new IllegalArgumentException("resolved null for placeholder '" + originalField.getName() + "' for message '" + state + "'");
                } else if (this.failMode == FailMode.FAIL_SAFE) {
                    rendered.put(originalField.getRaw(), "<null:" + rf.transformedField.getLastSubPath() + ">");
                } else {
                    throw new RuntimeException("unknown fail mode: " + this.failMode);
                }
                continue;
            }

            rendered.put(originalField.getRaw(), value);
        }

        return rendered;
    }

    /**
     * Helper class holding a resolved placeholder and its potentially transformed field.
     */
    @Value
    private static class ResolvedField {
        Placeholder placeholder;
        MessageField transformedField;
    }

    /**
     * Core field resolution returning placeholder + transformed field pairs.
     * Handles global function fallback, .or() fallback, and missing placeholder cases.
     */
    private Map<MessageField, ResolvedField> resolveFieldsInternal(@NonNull CompiledMessage message) {

        String state = message.getRaw();
        List<MessageElement> parts = message.getParts();
        Map<MessageField, ResolvedField> resolved = new LinkedHashMap<>();

        for (MessageElement part : parts) {

            if (!(part instanceof MessageField)) {
                continue;
            }

            MessageField originalField = (MessageField) part;
            MessageField field = originalField;
            String name = field.getName();

            // Skip if already resolved (duplicate field in message)
            if (resolved.containsKey(originalField)) {
                continue;
            }

            Placeholder placeholder = this.fields.get(name);

            // Fallback: if field not found, try as a global function via $ context
            if ((placeholder == null) && (this.placeholders != null)) {
                Placeholder globalFunctions = this.fields.get(Placeholders.GLOBAL_FUNCTIONS_KEY);
                if (globalFunctions != null) {
                    if (this.placeholders.getResolver(GlobalFunctions.class, name) != null) {
                        String transformedSource = Placeholders.GLOBAL_FUNCTIONS_KEY + "." + field.getSource();
                        MessageField transformedField = MessageField.of(field.getLocale(), transformedSource);
                        transformedField.setDefaultValue(field.getDefaultValue());
                        field = transformedField;
                        placeholder = globalFunctions;
                    }
                }
            }

            // If field is missing but has a method call (like .or()), try with null value
            if ((placeholder == null) && field.hasSub() && (field.getSub().getParams() != null) && (field.getSub().getParams().length() > 0)) {
                placeholder = Placeholder.of(this.placeholders, null, this);
            }

            // Handle missing placeholder
            if ((placeholder == null) || ((placeholder.getValue() == null) && !field.hasSub())) {
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

            resolved.put(originalField, new ResolvedField(placeholder, field));
        }

        return resolved;
    }

    public String apply() {
        return this.apply(this.message);
    }

    public <T> T apply(@NonNull MessageRenderer<T> renderer) {
        return renderer.render(this.message, this);
    }

    public String apply(@NonNull CompiledMessage message) {
        return StringMessageRenderer.INSTANCE.render(message, this);
    }
}
