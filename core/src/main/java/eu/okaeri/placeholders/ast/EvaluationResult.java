package eu.okaeri.placeholders.ast;

import eu.okaeri.placeholders.util.FormatUtils;

import java.util.function.Function;

/**
 * Result of evaluating a placeholder expression.
 * <p>
 * Implementations:
 * <ul>
 *   <li>{@link Value} - Successfully resolved to a value</li>
 *   <li>{@link NullValue} - Expression resolved but result was null</li>
 *   <li>{@link MissingValue} - Root field was not provided in context</li>
 * </ul>
 */
public interface EvaluationResult {

    /**
     * The original expression that was evaluated (e.g., "player.name").
     */
    String getExpression();

    /**
     * Returns the value if this is a successful result, null otherwise.
     */
    default Object getValueOrNull() {
        return null;
    }

    /**
     * Formats this result for string output using standard rules.
     * <ul>
     *   <li>Value → formatted via {@link FormatUtils#objectToString(Object)}</li>
     *   <li>NullValue → "null"</li>
     *   <li>MissingValue → "&lt;missing:expression&gt;"</li>
     * </ul>
     */
    default String format() {
        return this.format(FormatUtils::objectToString);
    }

    /**
     * Formats this result for string output with a custom value formatter.
     *
     * @param valueFormatter Function to format successful values
     * @return Formatted string representation
     */
    default String format(Function<Object, String> valueFormatter) {
        if (this instanceof Value) {
            return valueFormatter.apply(((Value) this).getValue());
        } else if (this instanceof NullValue) {
            return "null";
        } else if (this instanceof MissingValue) {
            return "<missing:" + this.getExpression() + ">";
        }
        throw new IllegalStateException("Unknown EvaluationResult type: " + this.getClass());
    }

    /**
     * Successfully resolved value.
     */
    class Value implements EvaluationResult {
        private final Object value;
        private final String expression;
        private final boolean literal;

        public Value(Object value, String expression) {
            this(value, expression, false);
        }

        public Value(Object value, String expression, boolean literal) {
            this.value = value;
            this.expression = expression;
            this.literal = literal;
        }

        public Object getValue() {
            return this.value;
        }

        /**
         * Returns true if this value came directly from a string/number literal
         * in the template expression, rather than from context or computation.
         * <p>
         * This is useful for renderers that want to process literal values differently
         * (e.g., parsing color codes in literals but not in user-provided values).
         */
        public boolean isLiteral() {
            return this.literal;
        }

        @Override
        public Object getValueOrNull() {
            return this.value;
        }

        @Override
        public String getExpression() {
            return this.expression;
        }

        @Override
        public String toString() {
            return String.valueOf(this.value);
        }
    }

    /**
     * Expression resolved but the result was null.
     * This happens when a field exists but its value (or a value in the chain) is null.
     */
    class NullValue implements EvaluationResult {
        private final String expression;

        public NullValue(String expression) {
            this.expression = expression;
        }

        @Override
        public String getExpression() {
            return this.expression;
        }

        @Override
        public String toString() {
            return "null";
        }
    }

    /**
     * The root field was not provided in the context.
     * This happens when .with("fieldName", value) was never called for the root field.
     */
    class MissingValue implements EvaluationResult {
        private final String fieldName;
        private final String expression;

        public MissingValue(String fieldName, String expression) {
            this.fieldName = fieldName;
            this.expression = expression;
        }

        /**
         * The name of the missing root field.
         */
        public String getFieldName() {
            return this.fieldName;
        }

        @Override
        public String getExpression() {
            return this.expression;
        }

        @Override
        public String toString() {
            return "<missing:" + this.expression + ">";
        }
    }
}
