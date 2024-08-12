package eu.okaeri.placeholders.bukkit.placeholderapi;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.bukkit.BukkitPlaceholders;
import eu.okaeri.placeholders.message.part.FieldParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Getter
@Setter
@Accessors(fluent = true)
public final class PlaceholderApiPlaceholders implements PlaceholderPack {

    private String viewerField = "player";

    public static Placeholders create() {
        return create(false);
    }

    public static Placeholders create(boolean registerDefaults) {
        return Placeholders.create(registerDefaults)
            .registerPlaceholders(new BukkitPlaceholders())
            .registerPlaceholders(new PlaceholderApiPlaceholders())
            .fastMode(false);
    }

    public static void registerBridge(@NonNull Plugin plugin, @NonNull Placeholders placeholders) {
        new PlaceholderApiBridge(plugin, placeholders).register();
    }

    @Override
    // target.papi(example_player_nickname)
    // target.papi(viewer,rel_example_player_title)
    public void register(Placeholders placeholders) {
        placeholders.registerPlaceholder(Player.class, "papi", (from, field, context) -> {

            FieldParams params = field.params();
            String viewerField = (params.length() > 1) ? params.strAt(0) : this.viewerField();
            String text = (params.length() > 1) ? params.strAt(1) : params.strAt(0);
            text = PlaceholderAPI.setPlaceholders(from, "%" + text + "%");

            if (context != null) {
                Object viewer = context.getFields().get(viewerField).getValue();
                if (viewer instanceof Player) {
                    text = PlaceholderAPI.setRelationalPlaceholders((Player) viewer, from, text);
                }
            }

            return text;
        });
    }
}
