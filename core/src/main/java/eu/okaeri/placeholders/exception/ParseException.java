package eu.okaeri.placeholders.exception;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Exception thrown when parsing a placeholder expression fails.
 * <p>
 * Provides detailed context about what was expected, what was found,
 * and where in the source the error occurred.
 */
@Getter
public class ParseException extends PlaceholderException {

    private final String source;
    private final int position;
    private final String found;
    private final String expected;

    public ParseException(String message, String source, int position) {
        this(message, source, position, null, null);
    }

    public ParseException(String message, String source, int position, @Nullable String found, @Nullable String expected) {
        super(message, source, null);
        this.source = source;
        this.position = position;
        this.found = found;
        this.expected = expected;
    }

    public ParseException(String message, String source, int position, @Nullable String found, @Nullable String expected,
                          Throwable cause) {
        super(message, source, null, cause);
        this.source = source;
        this.position = position;
        this.found = found;
        this.expected = expected;
    }

    @Override
    public String formatMessage() {
        StringBuilder sb = new StringBuilder(this.getMessage());
        sb.append(" at position ").append(this.position);

        if (this.expected != null) {
            sb.append(" [expected=").append(this.expected);
            if (this.found != null) {
                sb.append(", found=").append(this.found);
            }
            sb.append("]");
        } else if (this.found != null) {
            sb.append(" [found=").append(this.found).append("]");
        }

        if (this.source != null) {
            sb.append(" in '").append(truncate(this.source, 50)).append("'");
        }

        return sb.toString();
    }
}
