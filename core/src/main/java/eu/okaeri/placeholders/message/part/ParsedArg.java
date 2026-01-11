package eu.okaeri.placeholders.message.part;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a parsed argument from a placeholder method call.
 * <p>
 * Arguments can be either:
 * <ul>
 *   <li>LITERAL - explicit string literal wrapped in quotes: "value" or 'value'</li>
 *   <li>FIELD_REF_OR_LITERAL - unquoted value, try as field reference first, fallback to literal</li>
 * </ul>
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ParsedArg {

    /**
     * The type of this argument.
     */
    public enum Type {
        /**
         * Explicit string literal (was wrapped in quotes).
         * Always treated as a plain string value.
         */
        LITERAL,
        /**
         * Unquoted value - try to resolve as field reference first,
         * fall back to treating as literal string if field not found.
         * This provides backward compatibility with existing usage.
         */
        FIELD_REF_OR_LITERAL
    }

    @NonNull private final Type type;
    @NonNull private final String value;
    @NonNull private final String rawValue;

    /**
     * Creates a literal argument (from quoted string).
     *
     * @param value    The unquoted value
     * @param rawValue The original value with quotes preserved
     */
    public static ParsedArg literal(@NonNull String value, @NonNull String rawValue) {
        return new ParsedArg(Type.LITERAL, value, rawValue);
    }

    /**
     * Creates a literal argument (from quoted string), reconstructing raw value.
     *
     * @param value     The unquoted value
     * @param quoteChar The quote character used ('"' or '\'')
     */
    public static ParsedArg literal(@NonNull String value, char quoteChar) {
        return new ParsedArg(Type.LITERAL, value, quoteChar + value + quoteChar);
    }

    /**
     * Creates an argument that may be a field reference or literal.
     * Resolution happens at evaluation time.
     */
    public static ParsedArg fieldRefOrLiteral(@NonNull String value) {
        return new ParsedArg(Type.FIELD_REF_OR_LITERAL, value, value);
    }

    /**
     * Returns true if this is an explicit literal (was quoted).
     */
    public boolean isLiteral() {
        return this.type == Type.LITERAL;
    }

    /**
     * Returns true if this might be a field reference.
     */
    public boolean mayBeFieldRef() {
        return this.type == Type.FIELD_REF_OR_LITERAL;
    }
}
