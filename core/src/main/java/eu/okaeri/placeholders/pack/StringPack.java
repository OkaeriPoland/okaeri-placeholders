package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;

import java.util.Locale;

/**
 * Placeholder methods for String values.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code toLowerCase} - convert to lowercase</li>
 *   <li>{@code toUpperCase} - convert to uppercase</li>
 *   <li>{@code capitalize} - capitalize first letter</li>
 *   <li>{@code capitalizeFully} - capitalize each word</li>
 *   <li>{@code trim} - remove leading/trailing whitespace</li>
 *   <li>{@code length} / {@code size} - string length</li>
 *   <li>{@code replace(search, replacement)} - replace occurrences</li>
 *   <li>{@code prepend(prefix)} - add prefix</li>
 *   <li>{@code append(suffix)} - add suffix</li>
 * </ul>
 */
public class StringPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(String.class)
            // Case conversion
            .add("toLowerCase", str -> str.toLowerCase(Locale.ROOT))
            .add("toUpperCase", str -> str.toUpperCase(Locale.ROOT))

            // Capitalization
            .add("capitalize", StringPack::capitalize)
            .add("capitalizeFully", StringPack::capitalizeFully)

            // Trimming and length
            .add("trim", String::trim)
            .add("length", String::length)
            .alias("length", "size")

            // Modification
            .add("replace", (str, p) -> str.replace(
                p.arg(0).asString(),
                p.arg(1).orElse("")))
            .add("prepend", (str, p) -> p.arg(0).orElse("") + str)
            .add("append", (str, p) -> str + p.arg(0).orElse(""));
    }

    private static String capitalize(String text) {
        if ((text == null) || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }

    private static String capitalizeFully(String text) {
        if ((text == null) || text.isEmpty()) {
            return text;
        }
        String[] words = text.split(" ");
        StringBuilder buf = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            buf.append(Character.toUpperCase(word.charAt(0)));
            buf.append(word.substring(1).toLowerCase(Locale.ROOT)).append(" ");
        }
        return buf.toString().trim();
    }
}
