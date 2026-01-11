package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;

/**
 * Placeholder methods for Boolean values.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code bool(trueValue, falseValue)} - format as custom strings</li>
 *   <li>{@code format(trueValue, falseValue)} - alias for bool</li>
 * </ul>
 */
public class BooleanPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Boolean.class)
            .add("bool", (bool, p) ->
                bool ? p.arg(0).orElse("true") : p.arg(1).orElse("false"))
            .alias("bool", "format");
    }
}
