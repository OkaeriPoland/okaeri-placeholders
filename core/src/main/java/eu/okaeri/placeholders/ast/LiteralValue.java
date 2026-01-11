package eu.okaeri.placeholders.ast;

/**
 * Wrapper that marks a value as coming from a literal in the AST.
 * <p>
 * Used internally during evaluation to track whether a value originated
 * from a string/number literal vs from context lookup or computation.
 * Functions that pass through values (like cond, or) preserve this wrapper.
 */
public final class LiteralValue {
    private final Object value;

    public LiteralValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    /**
     * Unwraps a value if it's a LiteralValue, otherwise returns it as-is.
     */
    public static Object unwrap(Object value) {
        return (value instanceof LiteralValue) ? ((LiteralValue) value).getValue() : value;
    }

    /**
     * Checks if a value is wrapped as a literal.
     */
    public static boolean isLiteral(Object value) {
        return value instanceof LiteralValue;
    }
}
