package eu.okaeri.placeholders.ast;

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
     * Successfully resolved value.
     */
    class Value implements EvaluationResult {
        private final Object value;
        private final String expression;

        public Value(Object value, String expression) {
            this.value = value;
            this.expression = expression;
        }

        public Object getValue() {
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
