package eu.okaeri.placeholders.adventure;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.registry.Params;
import eu.okaeri.placeholders.registry.Registry;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Adventure-specific functions that return Components.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code color(...args)} - concat args and parse through MiniMessage</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * {color("&lt;gradient:green:dark_green&gt;", health, "% HP&lt;/gradient&gt;")}
 * {color("&lt;gradient:", cond(health.gt(66),"green:dark_green","yellow:gold","red:dark_red"), "&gt;HP&lt;/gradient&gt;")}
 * {color("&lt;red&gt;Error: ", message, "&lt;/red&gt;")}
 * </pre>
 */
public class AdventurePack implements PlaceholderPack {

    private final MiniMessage miniMessage;

    /**
     * Creates pack with default MiniMessage instance.
     */
    public AdventurePack() {
        this(MiniMessage.miniMessage());
    }

    /**
     * Creates pack with custom MiniMessage instance.
     *
     * @param miniMessage The MiniMessage instance to use for parsing
     */
    public AdventurePack(@NonNull MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public void register(Registry r) {
        r.globals()
            .add("color", (p, ctx) -> evalColor(p, ctx, this.miniMessage));
    }

    private static Component evalColor(Params p, PlaceholderContext ctx, MiniMessage mm) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.length(); i++) {
            Object val = p.arg(i).resolve(ctx);
            if (val != null) {
                sb.append(val);
            }
        }
        return mm.deserialize(sb.toString());
    }
}
