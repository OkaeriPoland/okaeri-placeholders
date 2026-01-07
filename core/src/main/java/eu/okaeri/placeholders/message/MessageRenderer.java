package eu.okaeri.placeholders.message;

import eu.okaeri.placeholders.context.PlaceholderContext;
import lombok.NonNull;

/**
 * Renders a compiled message with a placeholder context to a target type.
 * <p>
 * Implementations can render to any type (String, Component, etc.) and
 * can be used with {@link PlaceholderContext#apply(MessageRenderer)}.
 *
 * @param <T> The output type of the renderer
 */
@FunctionalInterface
public interface MessageRenderer<T> {

    /**
     * Renders the message using the provided context.
     *
     * @param message The compiled message template
     * @param context The placeholder context with field values
     * @return The rendered result
     */
    T render(@NonNull CompiledMessage message, @NonNull PlaceholderContext context);
}
