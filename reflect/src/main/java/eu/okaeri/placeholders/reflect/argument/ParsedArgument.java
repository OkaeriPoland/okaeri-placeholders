package eu.okaeri.placeholders.reflect.argument;

import lombok.NonNull;
import lombok.Value;

/**
 * Immutable holder for a parsed argument with its detected type.
 */
@Value
public class ParsedArgument {

    /**
     * The detected type of this argument.
     */
    @NonNull ArgumentType type;

    /**
     * The parsed value (may be null for context refs that couldn't be resolved).
     */
    Object value;

    /**
     * The Java type of the value (for method matching).
     */
    @NonNull Class<?> javaType;

    /**
     * The original raw string before parsing.
     */
    @NonNull String rawValue;

    /**
     * Creates a ParsedArgument from a detected type and raw value.
     */
    public static ParsedArgument of(ArgumentType type, Object value, String rawValue) {
        Class<?> javaType = (value != null) ? value.getClass() : type.getJavaType();
        return new ParsedArgument(type, value, javaType, rawValue);
    }

    /**
     * Creates a ParsedArgument with an explicitly specified Java type.
     * Useful for context refs where the type is determined at resolution time.
     */
    public static ParsedArgument of(ArgumentType type, Object value, Class<?> javaType, String rawValue) {
        return new ParsedArgument(type, value, javaType, rawValue);
    }

    /**
     * Creates a ParsedArgument for a resolved context reference.
     */
    public static ParsedArgument contextRef(Object resolvedValue, String rawValue) {
        Class<?> javaType = (resolvedValue != null) ? resolvedValue.getClass() : Object.class;
        return new ParsedArgument(ArgumentType.CONTEXT_REF, resolvedValue, javaType, rawValue);
    }

    /**
     * Creates a ParsedArgument for an unresolved reference (treated as literal string).
     */
    public static ParsedArgument literal(String value) {
        return new ParsedArgument(ArgumentType.UNKNOWN, value, String.class, value);
    }

    /**
     * Returns true if this argument is a context reference that needs resolution.
     */
    public boolean isContextRef() {
        return this.type == ArgumentType.CONTEXT_REF;
    }

    /**
     * Returns true if this argument is a literal value (not a reference).
     */
    public boolean isLiteral() {
        return (this.type != ArgumentType.CONTEXT_REF) && (this.type != ArgumentType.UNKNOWN);
    }

    /**
     * Returns true if this is an unknown/unresolved argument.
     */
    public boolean isUnknown() {
        return this.type == ArgumentType.UNKNOWN;
    }
}
