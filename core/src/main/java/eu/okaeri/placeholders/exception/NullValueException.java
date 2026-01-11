package eu.okaeri.placeholders.exception;

/**
 * Exception thrown when placeholder evaluation results in null.
 * <p>
 * This happens when a field exists but its value (or a value in the chain) is null.
 */
public class NullValueException extends EvaluationException {

    public NullValueException(String expression, String messageTemplate) {
        super("Placeholder resolved to null", expression, messageTemplate, null, null);
    }

    @Override
    public String formatMessage() {
        StringBuilder sb = new StringBuilder("Placeholder resolved to null");
        if (this.getExpression() != null) {
            sb.append(" [expression='").append(this.getExpression()).append("']");
        }
        if (this.getMessageTemplate() != null) {
            sb.append(" [template='").append(truncate(this.getMessageTemplate(), 50)).append("']");
        }
        return sb.toString();
    }
}
