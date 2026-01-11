package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.GlobalFunctions;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.ast.EvaluationResult;
import eu.okaeri.placeholders.ast.ExpressionEvaluator;
import eu.okaeri.placeholders.exception.MissingFieldException;
import eu.okaeri.placeholders.exception.NullValueException;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.MessageRenderer;
import eu.okaeri.placeholders.message.StringMessageRenderer;
import eu.okaeri.placeholders.message.ExpressionPart;
import eu.okaeri.placeholders.message.MessageElement;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Holds field values and renders placeholders in compiled messages.
 * <p>
 * A context can be created in two modes:
 * <ul>
 *   <li><b>Shared context</b> via {@link #create()} - reusable across multiple messages</li>
 *   <li><b>Message-bound context</b> via {@link #of(CompiledMessage)} - tied to one message</li>
 * </ul>
 *
 * <h2>Basic Usage</h2>
 * <pre>{@code
 * // Simple rendering
 * String result = PlaceholderContext.of(CompiledMessage.of("Hello {name}!"))
 *     .with("name", "World")
 *     .apply();
 * // → "Hello World!"
 *
 * // With Placeholders instance for resolvers
 * String result = placeholders.contextOf(CompiledMessage.of("{player.health}"))
 *     .with("player", playerObject)
 *     .apply();
 * }</pre>
 *
 * <h2>Fail Modes</h2>
 * <ul>
 *   <li>{@link FailMode#FAIL_SAFE} (default) - missing fields render as {@code <missing:expr>}</li>
 *   <li>{@link FailMode#FAIL_FAST} - missing fields throw {@link MissingFieldException}</li>
 * </ul>
 *
 * <h2>Rendering Methods</h2>
 * <ul>
 *   <li>{@link #apply()} - renders to final string</li>
 *   <li>{@link #renderFields()} - returns map of expression → formatted string</li>
 *   <li>{@link #renderFieldResults()} - returns map of expression → typed result</li>
 * </ul>
 *
 * @see Placeholders#context(CompiledMessage)
 * @see FailMode
 */
@Data
public class PlaceholderContext {

    private final Map<String, FieldValue> fields = new LinkedHashMap<>();
    private final CompiledMessage message;
    private final FailMode failMode;
    private Placeholders placeholders;

    /**
     * Creates a shared context with {@link FailMode#FAIL_SAFE}.
     * <p>
     * Shared contexts have no bound message and can be reused with multiple messages
     * via {@link #apply(CompiledMessage)}.
     *
     * @return A new shared context
     */
    public static PlaceholderContext create() {
        return create(FailMode.FAIL_SAFE);
    }

    /**
     * Creates a shared context with the specified fail mode.
     * <p>
     * Shared contexts have no bound message and can be reused with multiple messages
     * via {@link #apply(CompiledMessage)}.
     *
     * @param failMode How to handle missing/null placeholders
     * @return A new shared context
     */
    public static PlaceholderContext create(@NonNull FailMode failMode) {
        PlaceholderContext context = new PlaceholderContext(null, failMode);
        context.fields.put(Placeholders.GLOBAL_FUNCTIONS_KEY, FieldValue.of(null, GlobalFunctions.INSTANCE, context));
        return context;
    }

    /**
     * Creates a message-bound context with {@link FailMode#FAIL_SAFE}.
     * <p>
     * Message-bound contexts can only render their bound message via {@link #apply()}.
     * Attempting to use {@link #apply(CompiledMessage)} with a different message will throw.
     *
     * @param message The message this context is bound to
     * @return A new message-bound context
     */
    public static PlaceholderContext of(@NonNull CompiledMessage message) {
        return of(null, message);
    }

    /**
     * Creates a message-bound context with a Placeholders instance.
     * <p>
     * The Placeholders instance provides resolvers for method calls like {@code {player.health}}.
     *
     * @param placeholders Resolver registry (may be null for simple field substitution)
     * @param message      The message this context is bound to
     * @return A new message-bound context
     */
    public static PlaceholderContext of(@Nullable Placeholders placeholders, @NonNull CompiledMessage message) {
        return of(placeholders, message, FailMode.FAIL_SAFE);
    }

    /**
     * Creates a message-bound context with full configuration.
     *
     * @param placeholders Resolver registry (may be null for simple field substitution)
     * @param message      The message this context is bound to
     * @param failMode     How to handle missing/null placeholders
     * @return A new message-bound context
     */
    public static PlaceholderContext of(@Nullable Placeholders placeholders, @NonNull CompiledMessage message, @NonNull FailMode failMode) {
        PlaceholderContext context = new PlaceholderContext(message, failMode);
        context.setPlaceholders(placeholders);
        context.fields.put(Placeholders.GLOBAL_FUNCTIONS_KEY, FieldValue.of(placeholders, GlobalFunctions.INSTANCE, context));
        return context;
    }

    /**
     * Adds a field value to this context.
     * <p>
     * The field name corresponds to the root identifier in placeholder expressions.
     * For {@code {player.name}}, the field name is {@code "player"}.
     *
     * @param field The field name
     * @param value The value (may be null)
     * @return This context for chaining
     */
    public PlaceholderContext with(@NonNull String field, @Nullable Object value) {
        this.fields.put(field, FieldValue.of(this.placeholders, value, this));
        return this;
    }

    /**
     * Adds multiple field values to this context.
     *
     * @param fields Map of field names to values
     * @return This context for chaining
     */
    public PlaceholderContext with(@NonNull Map<String, Object> fields) {
        fields.forEach(this::with);
        return this;
    }

    /**
     * Renders all placeholders to strings.
     * <p>
     * <b>Map keys:</b> The expression exactly as written in the template (including default if present).
     * This allows easy search-replace in the original template string.
     * <p>
     * <b>Map values:</b> The formatted string result.
     * <p>
     * Examples:
     * <ul>
     *   <li>{@code {name}}         → key: {@code "name"}</li>
     *   <li>{@code {player.health}} → key: {@code "player.health"}</li>
     *   <li>{@code {name|Anonymous}} → key: {@code "name|Anonymous"}</li>
     * </ul>
     *
     * @return Map of expressions to their rendered string values
     * @see #renderFieldResults() for typed results with null/missing distinction
     */
    public Map<String, String> renderFields() {
        return this.renderFields(this.message);
    }

    /**
     * Renders all placeholders to strings.
     *
     * @param message The message to render fields for
     * @return Map of expressions to their rendered string values
     * @see #renderFields() for key format documentation
     */
    public Map<String, String> renderFields(@NonNull CompiledMessage message) {
        this.validateMessage(message);

        // no fields, no need for processing
        if (!message.isWithFields()) {
            return Collections.emptyMap();
        }

        Map<String, EvaluationResult> results = this.renderFieldResults(message);
        Map<String, String> strings = new LinkedHashMap<>();

        for (Map.Entry<String, EvaluationResult> entry : results.entrySet()) {
            EvaluationResult result = entry.getValue();
            this.checkFailFast(result, message.getRaw());
            strings.put(entry.getKey(), result.format());
        }

        return strings;
    }

    /**
     * Renders all placeholders to typed {@link EvaluationResult} objects.
     * <p>
     * <b>Map keys:</b> The expression exactly as written in the template (including default if present).
     * This allows easy search-replace in the original template string.
     * <p>
     * <b>Map values:</b> One of:
     * <ul>
     *   <li>{@link EvaluationResult.Value} - successfully resolved value</li>
     *   <li>{@link EvaluationResult.NullValue} - field exists but resolved to null</li>
     *   <li>{@link EvaluationResult.MissingValue} - root field not in context</li>
     * </ul>
     * <p>
     * Use {@link EvaluationResult#getValueOrNull()} for the raw value,
     * or {@link EvaluationResult#format()} for string output.
     *
     * @return Map of expressions to their evaluation results
     * @see #renderFields() for pre-formatted string results
     */
    public Map<String, EvaluationResult> renderFieldResults() {
        return this.renderFieldResults(this.message);
    }

    /**
     * Renders all placeholders to typed {@link EvaluationResult} objects.
     *
     * @param message The message to render fields for
     * @return Map of expressions to their evaluation results
     * @see #renderFieldResults() for key format and value type documentation
     */
    public Map<String, EvaluationResult> renderFieldResults(@NonNull CompiledMessage message) {
        this.validateMessage(message);

        // no fields, no need for processing
        if (!message.isWithFields()) {
            return Collections.emptyMap();
        }

        Map<String, EvaluationResult> results = new LinkedHashMap<>();
        ExpressionEvaluator evaluator = this.createEvaluator(message);

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
     * Renders the bound message to a string.
     * <p>
     * This is the most common way to use a context:
     * <pre>{@code
     * String result = PlaceholderContext.of(message)
     *     .with("name", "World")
     *     .apply();
     * }</pre>
     *
     * @return The rendered string
     * @throws IllegalStateException if this is a shared context with no bound message
     * @throws MissingFieldException if fail mode is FAIL_FAST and a field is missing
     * @throws NullValueException    if fail mode is FAIL_FAST and a field resolves to null
     */
    public String apply() {
        return this.apply(this.message);
    }

    /**
     * Renders the bound message using a custom renderer.
     * <p>
     * Example with Adventure components:
     * <pre>{@code
     * Component result = context.apply(adventureRenderer);
     * }</pre>
     *
     * @param renderer The renderer to use
     * @param <T>      The output type
     * @return The rendered result
     */
    public <T> T apply(@NonNull MessageRenderer<T> renderer) {
        return renderer.render(this.message, this);
    }

    /**
     * Renders the given message to a string.
     * <p>
     * For shared contexts created via {@link #create()}, this allows rendering
     * different messages with the same field values:
     * <pre>{@code
     * var ctx = PlaceholderContext.create()
     *     .with("name", "World");
     * ctx.apply(message1);  // reuse context
     * ctx.apply(message2);
     * }</pre>
     *
     * @param message The message to render
     * @return The rendered string
     * @throws IllegalArgumentException if this context is bound to a different message
     */
    public String apply(@NonNull CompiledMessage message) {
        return StringMessageRenderer.INSTANCE.render(message, this);
    }

    /**
     * Builds a values map from the context fields for AST evaluation.
     */
    private Map<String, Object> buildValuesMap() {
        Map<String, Object> values = new HashMap<>();
        for (Map.Entry<String, FieldValue> entry : this.fields.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getValue());
        }
        return values;
    }

    /**
     * Creates an evaluator for the given message.
     * <p>
     * This is primarily for custom {@link MessageRenderer} implementations
     * that need to evaluate expressions manually.
     *
     * @param message The compiled message (used for locale)
     * @return A configured evaluator ready to evaluate AST nodes
     */
    public ExpressionEvaluator createEvaluator(@NonNull CompiledMessage message) {
        Map<String, Object> values = this.buildValuesMap();
        Locale locale = (message.getLocale() != null) ? message.getLocale() : Locale.ENGLISH;
        return ExpressionEvaluator.of(values, this.placeholders, locale, this);
    }

    /**
     * Returns a snapshot of field names to their values.
     * <p>
     * Useful for debugging or checking which fields are set.
     * The returned map is unmodifiable.
     *
     * @return Unmodifiable map of field names to values
     */
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(this.buildValuesMap());
    }

    /**
     * Returns the effective locale for this context.
     * <p>
     * Uses the bound message's locale if available, otherwise {@link Locale#ENGLISH}.
     *
     * @return The locale used for formatting
     */
    public Locale getLocale() {
        if ((this.message != null) && (this.message.getLocale() != null)) {
            return this.message.getLocale();
        }
        return Locale.ENGLISH;
    }

    /**
     * Validates that the message can be used with this context.
     *
     * @param message The message to validate
     * @throws IllegalArgumentException if this context was created with a different message
     */
    private void validateMessage(CompiledMessage message) {
        if ((message != this.message) && (this.message != null)) {
            throw new IllegalArgumentException("cannot apply another message for context created with prepacked message: " +
                "if you intended to use this context as shared please use empty context from #create(), " +
                "if you're just trying to send a message use of(message)");
        }
    }

    /**
     * Throws appropriate exception if fail mode is FAIL_FAST and result is not a value.
     *
     * @param result The evaluation result to check
     * @param messageRaw The raw message template (for error context)
     * @throws NullValueException if result is NullValue and fail mode is FAIL_FAST
     * @throws MissingFieldException if result is MissingValue and fail mode is FAIL_FAST
     */
    private void checkFailFast(EvaluationResult result, String messageRaw) {
        if (this.failMode != FailMode.FAIL_FAST) {
            return;
        }

        if (result instanceof EvaluationResult.NullValue) {
            throw new NullValueException(result.getExpression(), messageRaw);
        }
        if (result instanceof EvaluationResult.MissingValue) {
            EvaluationResult.MissingValue mv = (EvaluationResult.MissingValue) result;
            throw new MissingFieldException(mv.getFieldName(), mv.getExpression(), messageRaw);
        }
    }
}
