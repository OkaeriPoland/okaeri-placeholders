package eu.okaeri.placeholders.resolver;

import eu.okaeri.placeholders.ast.FieldParams;
import eu.okaeri.placeholders.context.PlaceholderContext;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves a placeholder expression to a value for a specific type.
 * <p>
 * Implementations are registered via {@link eu.okaeri.placeholders.Placeholders#type(Class)}
 * or {@link eu.okaeri.placeholders.PlaceholderPack} and handle method calls on objects:
 * <pre>{@code
 * // Register a resolver for String.upper()
 * placeholders.type(String.class)
 *     .add("upper", (str, params, ctx) -> str.toUpperCase());
 *
 * // Template: {name.upper()}
 * }</pre>
 *
 * @param <T> The type this resolver handles
 * @see eu.okaeri.placeholders.Placeholders#type(Class)
 * @see eu.okaeri.placeholders.registry.TypeMethods
 */
public interface PlaceholderResolver<T> {

    /**
     * Resolves a placeholder method call to its value.
     *
     * @param from   The target object (receiver of the method call)
     * @param params Parameters providing method name, arguments, and locale
     * @param context The placeholder context (may be null)
     * @return The resolved value, or null if the value is null/missing
     */
    Object resolve(T from, @NonNull FieldParams params, @Nullable PlaceholderContext context);
}
