package eu.okaeri.placeholders.schema;

import eu.okaeri.placeholders.PlaceholderPack;

/**
 * Registry for placeholder methods and global functions.
 * Entry point for the fluent registration API.
 * <p>
 * Usage:
 * <pre>
 * public void register(Registry r) {
 *     r.type(String.class)
 *         .add("trim", String::trim)
 *         .add("length", String::length)
 *         .add("replace", (str, p) -> str.replace(p.arg(0).asString(), p.arg(1).orElse("")))
 *         .alias("size", "length");
 *
 *     r.globals()
 *         .add("now", Instant::now)
 *         .add("min", (p, ctx) -> findMin(p, ctx));
 * }
 * </pre>
 */
public interface Registry {

    /**
     * Start registering methods for a specific type.
     */
    <T> TypeMethods<T> type(Class<T> type);

    /**
     * Start registering global functions.
     */
    GlobalMethods globals();

    /**
     * Register multiple placeholder packs.
     */
    default void packs(PlaceholderPack... packs) {
        for (PlaceholderPack pack : packs) {
            pack.register(this);
        }
    }
}
