package eu.okaeri.placeholders.ast.parser;

import lombok.Getter;

/**
 * Exception thrown when parsing fails.
 */
@Getter
public class ParseException extends RuntimeException {

    private final int position;

    public ParseException(String message, int position) {
        super(message + " at position " + position);
        this.position = position;
    }

    public ParseException(String message, int position, Throwable cause) {
        super(message + " at position " + position, cause);
        this.position = position;
    }
}
