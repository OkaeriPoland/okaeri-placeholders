package eu.okaeri.placeholders.exception;

import lombok.Getter;

/**
 * Exception thrown when type coercion fails.
 */
@Getter
public class CoercionException extends PlaceholderException {

    private final Object sourceValue;
    private final Class<?> sourceType;
    private final Class<?> targetType;
    private final String reason;

    public CoercionException(Object sourceValue, Class<?> sourceType, Class<?> targetType, String reason) {
        super(buildMessage(sourceType, targetType, reason), null, null);
        this.sourceValue = sourceValue;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.reason = reason;
    }

    public CoercionException(Object sourceValue, Class<?> sourceType, Class<?> targetType, String reason,
                             Throwable cause) {
        super(buildMessage(sourceType, targetType, reason), null, null, cause);
        this.sourceValue = sourceValue;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.reason = reason;
    }

    private static String buildMessage(Class<?> sourceType, Class<?> targetType, String reason) {
        String from = (sourceType != null) ? sourceType.getSimpleName() : "null";
        String to = (targetType != null) ? targetType.getSimpleName() : "null";
        return "Cannot coerce " + from + " to " + to + ": " + reason;
    }

    @Override
    public String formatMessage() {
        StringBuilder sb = new StringBuilder(this.getMessage());
        if (this.sourceValue != null) {
            String valueStr = truncate(String.valueOf(this.sourceValue), 30);
            sb.append(" [value='").append(valueStr).append("']");
        }
        return sb.toString();
    }
}
