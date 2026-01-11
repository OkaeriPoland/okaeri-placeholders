package eu.okaeri.placeholders;

import eu.okaeri.placeholders.schema.DefaultRegistry;
import eu.okaeri.placeholders.schema.Registry;

/**
 * A pack of placeholder methods and global functions to register.
 * <p>
 * Packs can implement either {@link #register(Placeholders)} for legacy style
 * or {@link #register(Registry)} for the new fluent API.
 * <p>
 * Example using new fluent API:
 * <pre>
 * public class MyPack implements PlaceholderPack {
 *     {@literal @}Override
 *     public void register(Registry r) {
 *         r.type(String.class)
 *             .add("trim", String::trim)
 *             .add("length", String::length);
 *     }
 * }
 * </pre>
 */
public interface PlaceholderPack {

    /**
     * Register placeholders using direct Placeholders access (legacy style).
     * <p>
     * Default implementation delegates to {@link #register(Registry)}.
     */
    default void register(Placeholders placeholders) {
        this.register(DefaultRegistry.of(placeholders));
    }

    /**
     * Register placeholders using the fluent Registry API (preferred).
     * <p>
     * Default implementation does nothing - override this for new-style packs.
     */
    default void register(Registry registry) {
        // Default: no-op for legacy packs that override register(Placeholders)
    }
}
