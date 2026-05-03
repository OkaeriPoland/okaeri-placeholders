package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.registry.Params;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.ChatColor;
import org.bukkit.permissions.ServerOperator;

/**
 * Miscellaneous Bukkit-specific placeholders and functions.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code color(...args)} - concat args and translate &amp; color codes</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * {color("&amp;a", health, "% HP")}
 * {color("&amp;", cond(health.gt(66),"a","e","c"), health, "% HP")}
 * </pre>
 */
public class MiscPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(ChatColor.class)
            .self(c -> c.toString());

        r.type(ServerOperator.class)
            .add("op", o -> o.isOp());

        r.globals()
            .add("color", (p, ctx) -> evalColor(p, ctx));
    }

    private static String evalColor(Params p, PlaceholderContext ctx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.length(); i++) {
            Object val = p.arg(i).resolve(ctx);
            if (val != null) {
                sb.append(val);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }
}
