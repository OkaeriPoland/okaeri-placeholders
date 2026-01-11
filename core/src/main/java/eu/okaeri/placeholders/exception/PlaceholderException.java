package eu.okaeri.placeholders.exception;

import lombok.Getter;

/**
 * Base exception for all placeholder-related errors.
 * <p>
 * Provides structured context for debugging:
 * <ul>
 *   <li>{@link #expression} - The placeholder expression being processed</li>
 *   <li>{@link #messageTemplate} - The full message template (optional)</li>
 * </ul>
 */
@Getter
public abstract class PlaceholderException extends RuntimeException {

    protected final String expression;
    protected final String messageTemplate;

    protected PlaceholderException(String message, String expression, String messageTemplate) {
        super(message);
        this.expression = expression;
        this.messageTemplate = messageTemplate;
    }

    protected PlaceholderException(String message, String expression, String messageTemplate, Throwable cause) {
        super(message, cause);
        this.expression = expression;
        this.messageTemplate = messageTemplate;
    }

    /**
     * Formats a detailed error message including all available context.
     */
    public String formatMessage() {
        StringBuilder sb = new StringBuilder(this.getMessage());
        if (this.expression != null) {
            sb.append(" [expression='").append(this.expression).append("']");
        }
        if (this.messageTemplate != null) {
            sb.append(" [template='").append(truncate(this.messageTemplate, 50)).append("']");
        }
        return sb.toString();
    }

    protected static String truncate(String s, int maxLen) {
        if ((s == null) || (s.length() <= maxLen)) return s;
        return s.substring(0, maxLen - 3) + "...";
    }
}
