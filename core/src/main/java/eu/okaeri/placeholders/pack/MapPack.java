package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;

import java.util.Locale;
import java.util.Map;

/**
 * Placeholder methods for Map values.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code size} / {@code length}, {@code isEmpty} - basic accessors</li>
 *   <li>{@code keys}, {@code values} - collection views</li>
 *   <li>{@code get(key)} - explicit key lookup</li>
 *   <li>{@code localized} - get value for current locale from a Map&lt;Locale, ?&gt;</li>
 * </ul>
 */
public class MapPack implements PlaceholderPack {

    @Override
    @SuppressWarnings("unchecked")
    public void register(Registry r) {
        r.type(Map.class)
            .add("size", Map::size)
            .alias("size", "length")
            .add("isEmpty", Map::isEmpty)
            .add("keys", Map::keySet)
            .add("values", Map::values)

            .add("get", (map, p, ctx) -> {
                Object key = p.arg(0).resolve(ctx);
                if ((key != null) && map.containsKey(key)) return map.get(key);
                // Literal args resolve as null; fall back to string-key lookup
                String keyStr = p.arg(0).asString();
                if (keyStr.isEmpty()) return null;
                if (map.containsKey(keyStr)) return map.get(keyStr);
                for (Object existing : map.keySet()) {
                    if (keyStr.equals(String.valueOf(existing))) return map.get(existing);
                }
                return null;
            })

            .add("localized", (map, p, ctx) -> {
                if (map.isEmpty()) {
                    return null;
                }

                Locale locale = ctx.getLocale();
                Object result = map.get(locale);
                if (result == null) {
                    result = map.get(Locale.forLanguageTag(locale.getLanguage()));
                }
                if (result == null) {
                    result = map.get(map.keySet().stream().findFirst().orElse(null));
                }

                return result;
            });
    }
}
