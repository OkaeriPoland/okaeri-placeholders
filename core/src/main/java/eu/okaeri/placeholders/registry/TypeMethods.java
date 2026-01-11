package eu.okaeri.placeholders.registry;

import eu.okaeri.placeholders.context.PlaceholderContext;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Fluent builder for registering methods on a specific type.
 * <p>
 * Supports three resolver signatures:
 * <ul>
 *   <li>{@code Function<T, Object>} - Simple, no params (method reference friendly)</li>
 *   <li>{@code BiFunction<T, Params, Object>} - With params</li>
 *   <li>{@code TriFunction<T, Params, PlaceholderContext, Object>} - With params and context</li>
 * </ul>
 */
public interface TypeMethods<T> {

    /**
     * Register a simple method that takes no parameters.
     * Ideal for method references like {@code String::trim}.
     */
    TypeMethods<T> add(String name, Function<T, Object> resolver);

    /**
     * Register a method that uses parameters.
     */
    TypeMethods<T> add(String name, BiFunction<T, Params, Object> resolver);

    /**
     * Register a method that uses parameters and needs context for field resolution.
     */
    TypeMethods<T> add(String name, TriFunction<T, Params, PlaceholderContext, Object> resolver);

    /**
     * Register the self resolver - what happens when the value is used directly
     * without calling a method (e.g., {@code {duration}} instead of {@code {duration.hours}}).
     */
    TypeMethods<T> self(Function<T, Object> resolver);

    /**
     * Register the self resolver with parameter access.
     */
    TypeMethods<T> self(BiFunction<T, Params, Object> resolver);

    /**
     * Register the self resolver with parameter and context access.
     */
    TypeMethods<T> self(TriFunction<T, Params, PlaceholderContext, Object> resolver);

    /**
     * Create aliases for an existing method.
     *
     * @param target  The existing method name
     * @param aliases One or more alias names
     */
    TypeMethods<T> alias(String target, String... aliases);

    /**
     * Switch to registering methods for a different type.
     */
    <U> TypeMethods<U> type(Class<U> type);

    /**
     * Switch to registering global functions.
     */
    GlobalMethods globals();

    /**
     * Functional interface for methods that need value, params, and context.
     */
    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
}
