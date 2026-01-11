package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.registry.Registry;

/**
 * Placeholder methods for all Object types (base class methods).
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code or(fallback)} - return fallback if null</li>
 *   <li>{@code equals(other)} / {@code eq(other)} - equality check</li>
 *   <li>{@code notEquals(other)} / {@code ne(other)} - inequality check</li>
 *   <li>{@code _meta(...)} - legacy metadata resolver for bool/plural syntax</li>
 * </ul>
 */
public class ObjectPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Object.class)
            // Fallback/default value
            .add("or", (obj, p, ctx) -> (obj != null) ? obj : p.arg(0).resolve(ctx))

            // Equality comparisons (return Boolean for use with $.if)
            .add("equals", (obj, p, ctx) -> {
                Object other = p.arg(0).resolve(ctx);
                return isEqual(obj, other);
            })
            .alias("equals", "eq")

            .add("notEquals", (obj, p, ctx) -> {
                Object other = p.arg(0).resolve(ctx);
                return !isEqual(obj, other);
            })
            .alias("notEquals", "ne")

            // Legacy metadata resolver for {yes,no#active} syntax
            .add("_meta", (obj, p, ctx) -> {
                String[] options = p.toStringArray();

                // Boolean: 2 options + Boolean type -> bool format
                if ((obj instanceof Boolean) && (options.length == 2)) {
                    return (Boolean) obj ? options[0] : options[1];
                }

                // Number: pluralization
                if (obj instanceof Number) {
                    int value = ((Number) obj).intValue();
                    return Placeholders.pluralize(ctx.getLocale(), value, options);
                }

                // Fallback: return first option or stringified value
                return (options.length > 0) ? options[0] : String.valueOf(obj);
            });
    }

    private static boolean isEqual(Object a, Object b) {
        if (a == null) return b == null;
        if (a.equals(b)) return true;
        // String comparison fallback
        return String.valueOf(a).equals(String.valueOf(b));
    }
}
