package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.GlobalFunctions;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.ast.EvaluationResult;
import eu.okaeri.placeholders.ast.bridge.PlaceholdersEvaluator;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.MessageRenderer;
import eu.okaeri.placeholders.message.StringMessageRenderer;
import eu.okaeri.placeholders.message.part.ExpressionPart;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
        Map<MessageField, String> rendered = new LinkedHashMap<>();
        PlaceholdersEvaluator evaluator = this.createEvaluator(message);
        Locale locale = (message.getLocale() != null) ? message.getLocale() : Locale.ENGLISH;

        // Handle AST-based ExpressionPart parts
        for (MessageElement part : message.getParts()) {
            if (part instanceof ExpressionPart) {
                ExpressionPart expr = (ExpressionPart) part;

                String result = evaluator.evaluateToString(expr.getAst());

                if (result == null) {
                    if (expr.getDefaultValue() != null) {
                        result = expr.getDefaultValue();
                    } else if (this.failMode == FailMode.FAIL_FAST) {
                        throw new IllegalArgumentException("resolved null for placeholder '{" + expr.getRaw() + "}' in message '" + state + "'");
                    } else if (this.failMode == FailMode.FAIL_SAFE) {
                        result = "<null:" + expr.getRaw() + ">";
                    } else {
                        throw new RuntimeException("unknown fail mode: " + this.failMode);
                    }
                }

                // Create synthetic MessageField for the key to maintain API compatibility
                MessageField syntheticField = MessageField.of(locale, expr.getRaw());
                rendered.put(syntheticField, result);
            }
        }

        // Also handle legacy MessageField parts
        Map<MessageField, ResolvedField> resolved = this.resolveFieldsInternal(message);

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
     * Renders field results as {@link EvaluationResult} objects.
     * Returns Map keyed by field raw expression for easy lookup.
     * <p>
     * This is the primary method for renderers that need to handle
     * missing/null placeholders differently (e.g., showing error styles).
     *
     * @return Map of field raw expressions to their evaluation results
     */
    public Map<String, EvaluationResult> renderFieldResults() {
        return this.renderFieldResults(this.message);
    }

    /**
     * Renders field results as {@link EvaluationResult} objects.
     * Returns Map keyed by field raw expression for easy lookup.
     *
     * @param message The message to render fields for
     * @return Map of field raw expressions to their evaluation results
     */
    public Map<String, EvaluationResult> renderFieldResults(@NonNull CompiledMessage message) {

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

        Map<String, EvaluationResult> results = new LinkedHashMap<>();
        PlaceholdersEvaluator evaluator = this.createEvaluator(message);

        // Handle AST-based ExpressionPart parts
        for (MessageElement part : message.getParts()) {
            if (part instanceof ExpressionPart) {
                ExpressionPart expr = (ExpressionPart) part;

                // Build the key for this expression
                String baseRaw = (expr.getOriginalRaw() != null) ? expr.getOriginalRaw() : expr.getRaw();
                String fullRaw = (expr.getDefaultValue() != null)
                    ? (baseRaw + "|" + expr.getDefaultValue())
                    : baseRaw;

                // Evaluate and get typed result
                EvaluationResult result = evaluator.evaluateToResult(expr.getAst(), expr.getRaw());

                // Apply pipe-syntax default if evaluation failed
                if (!(result instanceof EvaluationResult.Value) && (expr.getDefaultValue() != null)) {
                    result = new EvaluationResult.Value(expr.getDefaultValue(), expr.getRaw());
                }

                results.put(fullRaw, result);
            }
        }

        return results;
    }

    /**
     * Renders field values preserving their types (not converting to String).
     * Returns Map keyed by field raw expression for easy lookup.
     * <p>
     * This is useful for typed composition with external systems like MiniMessage
     * that need to handle values differently based on their type (e.g., Component vs String).
     * <p>
     * Note: For access to missing/null status, use {@link #renderFieldResults()} instead.
     *
     * @return Map of field raw expressions to their resolved typed values
     */
    public Map<String, Object> renderFieldValues() {
        return this.renderFieldValues(this.message);
    }

    /**
     * Renders field values preserving their types (not converting to String).
     * Returns Map keyed by field raw expression for easy lookup.
     * <p>
     * Note: For access to missing/null status, use {@link #renderFieldResults(CompiledMessage)} instead.
     *
     * @param message The message to render fields for
     * @return Map of field raw expressions to their resolved typed values
     */
    public Map<String, Object> renderFieldValues(@NonNull CompiledMessage message) {
        Map<String, EvaluationResult> results = this.renderFieldResults(message);
        Map<String, Object> values = new LinkedHashMap<>();

        for (Map.Entry<String, EvaluationResult> entry : results.entrySet()) {
            EvaluationResult result = entry.getValue();

            if (result instanceof EvaluationResult.Value) {
                values.put(entry.getKey(), ((EvaluationResult.Value) result).getValue());
            } else if (result instanceof EvaluationResult.NullValue) {
                if (this.failMode == FailMode.FAIL_FAST) {
                    throw new IllegalArgumentException("resolved null for placeholder '{" + result.getExpression() + "}' in message '" + message.getRaw() + "'");
                }
                // In FAIL_SAFE mode, put null - let renderer decide how to display
                values.put(entry.getKey(), null);
            } else if (result instanceof EvaluationResult.MissingValue) {
                if (this.failMode == FailMode.FAIL_FAST) {
                    throw new IllegalArgumentException("missing placeholder '" + ((EvaluationResult.MissingValue) result).getFieldName() + "' for message '" + message.getRaw() + "'");
                }
                // In FAIL_SAFE mode, put null - let renderer decide how to display
                values.put(entry.getKey(), null);
            }
        }

        return values;
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

    /**
     * Builds a values map from the context fields for AST evaluation.
     */
    private Map<String, Object> buildValuesMap() {
        Map<String, Object> values = new HashMap<>();
        for (Map.Entry<String, Placeholder> entry : this.fields.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getValue());
        }
        return values;
    }

    /**
     * Creates an evaluator for the given message.
     * <p>
     * This is the shared entry point for AST-based expression evaluation.
     * Used by both internal render methods and external renderers like {@link StringMessageRenderer}.
     *
     * @param message The compiled message (used for locale)
     * @return A configured evaluator ready to evaluate AST nodes
     */
    public PlaceholdersEvaluator createEvaluator(@NonNull CompiledMessage message) {
        Map<String, Object> values = this.buildValuesMap();
        Locale locale = (message.getLocale() != null) ? message.getLocale() : Locale.ENGLISH;
        return PlaceholdersEvaluator.of(values, this.placeholders, locale, this);
    }

    /**
     * Returns a map of field names to their raw values.
     * <p>
     * This is useful for checking if a field exists in the context.
     *
     * @return Unmodifiable map of field names to values
     */
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(this.buildValuesMap());
    }

    /**
     * Returns the effective locale for this context.
     * Falls back to {@link Locale#ENGLISH} if no locale is set.
     */
    public Locale getLocale() {
        if ((this.message != null) && (this.message.getLocale() != null)) {
            return this.message.getLocale();
        }
        return Locale.ENGLISH;
    }
}
