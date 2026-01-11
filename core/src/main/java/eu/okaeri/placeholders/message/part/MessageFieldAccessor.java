package eu.okaeri.placeholders.message.part;

import java.util.Locale;

/**
 * Provides access to method/field call information for resolvers.
 * <p>
 * Implementations provide the locale and parameters for the current call.
 *
 * @see eu.okaeri.placeholders.ast.bridge.AstMessageFieldAccessor
 */
public interface MessageFieldAccessor {

    /**
     * The locale for this evaluation context.
     */
    Locale locale();

    /**
     * The parameters passed to this method call.
     */
    FieldParams params();
}
