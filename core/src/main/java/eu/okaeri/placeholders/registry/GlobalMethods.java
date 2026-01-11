package eu.okaeri.placeholders.registry;

import eu.okaeri.placeholders.context.PlaceholderContext;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fluent builder for registering global functions.
 * <p>
 * Global functions are accessed via {@code {$.funcName()}} or {@code {funcName()}} syntax.
 * <p>
 * Supports multiple signatures:
 * <ul>
 *   <li>{@code Supplier<Object>} - No params (e.g., now())</li>
 *   <li>{@code Function<Params, Object>} - With params only</li>
 *   <li>{@code BiFunction<Params, PlaceholderContext, Object>} - With params and context</li>
 * </ul>
 */
public interface GlobalMethods {

    /**
     * Register a global function with no parameters.
     */
    GlobalMethods add(String name, Supplier<Object> resolver);

    /**
     * Register a global function that uses parameters.
     */
    GlobalMethods add(String name, Function<Params, Object> resolver);

    /**
     * Register a global function that uses parameters and needs context for field resolution.
     */
    GlobalMethods add(String name, BiFunction<Params, PlaceholderContext, Object> resolver);

    /**
     * Create aliases for an existing global function.
     *
     * @param target  The existing function name
     * @param aliases One or more alias names
     */
    GlobalMethods alias(String target, String... aliases);

    /**
     * Switch to registering methods for a type.
     */
    <T> TypeMethods<T> type(Class<T> type);
}
