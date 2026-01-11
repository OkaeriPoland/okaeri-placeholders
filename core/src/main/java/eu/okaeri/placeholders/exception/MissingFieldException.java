package eu.okaeri.placeholders.exception;

import lombok.Getter;

/**
 * Exception thrown when a root field is not provided in the context.
 * <p>
 * This happens when {@code .with("fieldName", value)} was never called
 * for the root field referenced in the expression.
 */
@Getter
public class MissingFieldException extends EvaluationException {

    private final String fieldName;

    public MissingFieldException(String fieldName, String expression, String messageTemplate) {
        super("Missing placeholder field '" + fieldName + "'", expression, messageTemplate, null, null);
        this.fieldName = fieldName;
    }

    @Override
    public String formatMessage() {
        StringBuilder sb = new StringBuilder("Missing placeholder field '").append(this.fieldName).append("'");
        if (this.getExpression() != null) {
            sb.append(" [expression='").append(this.getExpression()).append("']");
        }
        if (this.getMessageTemplate() != null) {
            sb.append(" [template='").append(truncate(this.getMessageTemplate(), 50)).append("']");
        }
        return sb.toString();
    }
}
