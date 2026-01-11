package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;

public class DamageablePack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Damageable.class)
            .add("health", Damageable::getHealth)
            .add("healthHearts", d -> (int) (d.getHealth() / 2))
            .add("healthHeartsWithMax", d -> {
                int current = (int) (d.getHealth() / 2);
                int max = (int) (d.getMaxHealth() / 2);
                return current + "/" + max;
            })
            .add("maxHealth", Damageable::getMaxHealth)
            .add("maxHealthHearts", d -> (int) (d.getMaxHealth() / 2))
            .add("healthBarHearts", (d, p) -> {
                int maxHearts = (int) (d.getMaxHealth() / 2);
                String okColor = p.arg(0).orElse("c");
                String emptyColor = p.arg(1).orElse("7");
                String symbol = p.arg(2).orElse("❤");
                return renderHealthBar(d, maxHearts, symbol, okColor, emptyColor);
            })
            .add("healthBar", (d, p) -> {
                int barLength = p.arg(0).asInt(40);
                String okColor = p.arg(1).orElse("c");
                String emptyColor = p.arg(2).orElse("7");
                String symbol = p.arg(3).orElse("|");
                return renderHealthBar(d, barLength, symbol, okColor, emptyColor);
            });
    }

    public static String renderHealthBar(Damageable damageable, int limit, String symbol, String okColor, String emptyColor) {
        double result = (damageable.getHealth() / damageable.getMaxHealth()) * limit;
        if ((result < 1) && (result > 0)) result = 1;
        return renderHealthBarWith((int) result, limit, symbol, okColor, emptyColor);
    }

    public static String renderHealthBarWith(int value, int max, String symbol, String okColor, String emptyColor) {
        StringBuilder buf = new StringBuilder();

        // empty
        if (value == 0) {
            buf.append(ChatColor.COLOR_CHAR).append(emptyColor);
            for (int i = 0; i < max; i++) buf.append(symbol);
            return buf.toString();
        }

        // full
        if (value == max) {
            buf.append(ChatColor.COLOR_CHAR).append(okColor);
            for (int i = 0; i < max; i++) buf.append(symbol);
            return buf.toString();
        }

        // partial
        buf.append(ChatColor.COLOR_CHAR).append(okColor);
        for (int i = 0; i < max; i++) {
            if (i == value) {
                buf.append(ChatColor.COLOR_CHAR).append(emptyColor);
            }
            buf.append(symbol);
        }

        return buf.toString();
    }
}
