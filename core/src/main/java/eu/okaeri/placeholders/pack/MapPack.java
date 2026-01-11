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
 *   <li>{@code localized} - get value for current locale from a Map&lt;Locale, ?&gt;</li>
 * </ul>
 */
public class MapPack implements PlaceholderPack {

    @Override
    @SuppressWarnings("unchecked")
    public void register(Registry r) {
        r.type(Map.class)
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
