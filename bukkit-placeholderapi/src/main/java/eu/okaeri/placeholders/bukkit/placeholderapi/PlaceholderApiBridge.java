package eu.okaeri.placeholders.bukkit.placeholderapi;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@RequiredArgsConstructor
public class PlaceholderApiBridge extends PlaceholderExpansion {

    protected final Plugin plugin;
    protected final Placeholders placeholders;
    protected final @Getter String author;
    protected final @Getter String name;
    protected final @Getter String identifier;
    protected final @Getter String version;
    protected final @Getter @Accessors(fluent = true) boolean persist = true;

    public PlaceholderApiBridge(@NonNull Plugin plugin, @NonNull Placeholders placeholders) {
        this.plugin = plugin;
        this.placeholders = placeholders;
        this.author = plugin.getDescription().getAuthors().isEmpty() ? "Unknown" : plugin.getDescription().getAuthors().get(0);
        this.name = plugin.getName() + " (Okaeri Placeholders Bridge)";
        this.identifier = plugin.getName().toLowerCase(Locale.ROOT).replaceAll("[^a-zA-Z0-9]", "") + "bridge";
        this.version = plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player target, @NonNull String identifier) {

        if (target == null) {
            return null;
        }

        CompiledMessage message = CompiledMessage.of("{" + identifier + "}");
        return PlaceholderContext.of(this.placeholders, message).with("player", target).apply();
    }
}
