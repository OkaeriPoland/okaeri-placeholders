package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;

import java.util.Locale;

/**
 * Placeholder methods for String values.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code toLowerCase} / {@code toUpperCase} - case conversion</li>
 *   <li>{@code capitalize} / {@code capitalizeFully} - capitalization</li>
 *   <li>{@code trim} - remove leading/trailing whitespace</li>
 *   <li>{@code length} / {@code size} - string length</li>
 *   <li>{@code isEmpty} / {@code isBlank} - emptiness checks</li>
 *   <li>{@code replace(search, replacement)} - replace occurrences</li>
 *   <li>{@code prepend(prefix)} / {@code append(suffix)} - add prefix/suffix</li>
 *   <li>{@code substring(start, end)} - extract part of string</li>
 *   <li>{@code contains(search)} - check if contains substring</li>
 *   <li>{@code startsWith(prefix)} / {@code endsWith(suffix)} - prefix/suffix check</li>
 *   <li>{@code repeat(count)} - repeat string N times</li>
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
            .add("append", (str, p) -> str + p.arg(0).orElse(""))

            // Emptiness checks
            .add("isEmpty", String::isEmpty)
            .add("isBlank", str -> str.trim().isEmpty())

            // Substring extraction
            .add("substring", (str, p) -> {
                int start = p.arg(0).asInt(0);
                int end = p.arg(1).asInt(str.length());
                start = Math.max(0, Math.min(start, str.length()));
                end = Math.max(start, Math.min(end, str.length()));
                return str.substring(start, end);
            })

            // Content checks
            .add("contains", (str, p) -> str.contains(p.arg(0).orElse("")))
            .add("startsWith", (str, p) -> str.startsWith(p.arg(0).orElse("")))
            .add("endsWith", (str, p) -> str.endsWith(p.arg(0).orElse("")))

            // Repeat
            .add("repeat", (str, p) -> {
                int count = p.arg(0).asInt(1);
                if (count <= 0) return "";
                StringBuilder sb = new StringBuilder(str.length() * count);
                for (int i = 0; i < count; i++) sb.append(str);
                return sb.toString();
            });
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
