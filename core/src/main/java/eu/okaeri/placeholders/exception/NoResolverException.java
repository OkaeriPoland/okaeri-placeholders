package eu.okaeri.placeholders.exception;

import lombok.Getter;

/**
 * Exception thrown when no resolver is found for a type and method/field combination.
 */
@Getter
public class NoResolverException extends EvaluationException {

    private final String methodName;

    public NoResolverException(String methodName, String expression, String messageTemplate,
                               Object targetValue, Class<?> targetType) {
        super("No resolver found for method/field '" + methodName + "'", expression, messageTemplate,
            targetValue, targetType);
        this.methodName = methodName;
    }

    @Override
    public String formatMessage() {
        StringBuilder sb = new StringBuilder("No resolver found for '").append(this.methodName).append("'");
        if (this.getTargetType() != null) {
            sb.append(" on type ").append(this.getTargetType().getName());
        }
        if (this.getExpression() != null) {
            sb.append(" [expression='").append(this.getExpression()).append("']");
        }
        return sb.toString();
    }
}
