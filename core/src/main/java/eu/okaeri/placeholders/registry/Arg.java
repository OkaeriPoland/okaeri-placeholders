package eu.okaeri.placeholders.registry;

import eu.okaeri.placeholders.context.PlaceholderContext;

/**
 * Fluent accessor for a single argument at a specific index.
 * Provides type conversion and default value support.
 */
public interface Arg {

    /**
     * Returns the raw value without conversion.
     */
    Object raw();

    /**
     * Converts to String.
     */
    String asString();

    /**
     * Converts to int.
     *
     * @throws NumberFormatException if value cannot be parsed
     */
    int asInt();

    /**
     * Converts to int with default if null/missing.
     */
    int asInt(int defaultValue);

    /**
     * Converts to double.
     *
     * @throws NumberFormatException if value cannot be parsed
     */
    double asDouble();

    /**
     * Converts to double with default if null/missing.
     */
    double asDouble(double defaultValue);

    /**
     * Converts to boolean.
     */
    boolean asBool();

    /**
     * Converts to boolean with default if null/missing.
     */
    boolean asBool(boolean defaultValue);

    /**
     * Returns default value if this arg is null/missing.
     */
    <T> T orElse(T defaultValue);

    /**
     * Resolves this argument as a field reference using the given context.
     * Used when the argument might be a variable name rather than a literal.
     */
    Object resolve(PlaceholderContext ctx);
}
