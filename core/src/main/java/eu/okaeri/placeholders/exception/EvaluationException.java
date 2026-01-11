package eu.okaeri.placeholders.exception;

import lombok.Getter;

/**
 * Exception thrown when AST evaluation fails.
 * <p>
 * Captures the target object and its type for debugging.
 */
@Getter
public class EvaluationException extends PlaceholderException {

    protected final Object targetValue;
    protected final Class<?> targetType;

    public EvaluationException(String message, String expression, String messageTemplate,
                               Object targetValue, Class<?> targetType) {
        super(message, expression, messageTemplate);
        this.targetValue = targetValue;
        this.targetType = targetType;
    }

    public EvaluationException(String message, String expression, String messageTemplate,
                               Object targetValue, Class<?> targetType, Throwable cause) {
        super(message, expression, messageTemplate, cause);
        this.targetValue = targetValue;
        this.targetType = targetType;
    }

    @Override
    public String formatMessage() {
        StringBuilder sb = new StringBuilder(super.formatMessage());
        if (this.targetType != null) {
            sb.append(" [targetType=").append(this.targetType.getName()).append("]");
        }
        return sb.toString();
    }
}
