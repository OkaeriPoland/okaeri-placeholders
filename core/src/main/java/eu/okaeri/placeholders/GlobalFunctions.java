package eu.okaeri.placeholders;

/**
 * Singleton marker class for global placeholder functions.
 * <p>
 * This class serves as the context value for the special "$" placeholder,
 * enabling global function calls like {$.env(HOME)}, {$.now()}, etc.
 * <p>
 * Functions are registered via {@link Placeholders#global} methods
 * which internally register resolvers on this class.
 * <p>
 * Example usage:
 * <pre>
 * {$.env(HOME)}           // environment variable
 * {$.now()}               // current timestamp
 * {$.coalesce(a, b, c)}   // first non-null value
 * {$.if(cond, yes, no)}   // conditional
 * </pre>
 */
public final class GlobalFunctions {

    /**
     * Singleton instance used as the "$" context value.
     */
    public static final GlobalFunctions INSTANCE = new GlobalFunctions();

    private GlobalFunctions() {
        // Singleton - use INSTANCE
    }
}
