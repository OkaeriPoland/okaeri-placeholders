package eu.okaeri.placeholders.registry;

/**
 * Access to method arguments with fluent API.
 * <p>
 * Usage:
 * <pre>
 * // Get argument at index 0, convert to int
 * int value = params.arg(0).asInt();
 *
 * // Get argument with default value
 * String name = params.arg(0).orElse("default");
 *
 * // Get number of arguments
 * int count = params.length();
 * </pre>
 */
public interface Params {

    /**
     * Returns an accessor for the argument at the given index.
     * If index is out of bounds, returns an accessor that will return null/defaults.
     */
    Arg arg(int index);

    /**
     * Returns the number of arguments.
     */
    int length();

    /**
     * Returns all arguments as a String array.
     */
    String[] toStringArray();
}
