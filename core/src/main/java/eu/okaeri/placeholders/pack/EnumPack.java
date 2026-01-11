package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.schema.Registry;

import java.util.Locale;

/**
 * Placeholder methods for Enum values.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code name} - enum constant name</li>
 *   <li>{@code ordinal} - enum ordinal position</li>
 *   <li>{@code pretty} - human-readable format (MY_CONSTANT -> My Constant)</li>
 * </ul>
 */
public class EnumPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Enum.class)
            .add("name", Enum::name)
            .add("ordinal", Enum::ordinal)
            .add("pretty", EnumPack::pretty);
    }

    private static String pretty(Enum<?> e) {
        String name = e.name().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder buf = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            buf.append(Character.toUpperCase(word.charAt(0)));
            buf.append(word.substring(1).toLowerCase(Locale.ROOT)).append(" ");
        }
        return buf.toString().trim();
    }
}
