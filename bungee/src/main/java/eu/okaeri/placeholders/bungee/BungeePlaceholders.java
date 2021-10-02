package eu.okaeri.placeholders.bungee;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public final class BungeePlaceholders implements PlaceholderPack {

    public static Placeholders create() {
        return create(false);
    }

    public static Placeholders create(boolean registerDefaults) {
        return Placeholders.create(registerDefaults)
                .registerPlaceholders(new BungeePlaceholders());
    }

    @Override
    public void register(Placeholders placeholders) {

        // ChatColor
        placeholders.registerPlaceholder(ChatColor.class, (e, p) -> e.toString());

        // CommandSender
        placeholders.registerPlaceholder(CommandSender.class, "name", (e, p) -> e.getName());
        placeholders.registerPlaceholder(CommandSender.class, (e, p) -> e.getName());

        // ProxyServer
        placeholders.registerPlaceholder(ProxyServer.class, "name", (e, p) -> e.getName());
        placeholders.registerPlaceholder(ProxyServer.class, "onlineCount", (e, p) -> e.getOnlineCount());
        placeholders.registerPlaceholder(ProxyServer.class, "version", (e, p) -> e.getVersion());
        placeholders.registerPlaceholder(ProxyServer.class, (e, p) -> e.getName());

        // ProxiedPlayer
        placeholders.registerPlaceholder(ProxiedPlayer.class, "displayName", (e, p) -> e.getDisplayName());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "ping", (e, p) -> e.getPing());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "server", (e, p) -> e.getServer());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "uniqueId", (e, p) -> e.getUniqueId());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "locale", (e, p) -> e.getLocale());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "viewDistance", (e, p) -> e.getViewDistance());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "chatMode", (e, p) -> e.getChatMode());

        // Server
        placeholders.registerPlaceholder(Server.class, "name", (e, p) -> e.getInfo().getName());
        placeholders.registerPlaceholder(Server.class, "motd", (e, p) -> e.getInfo().getMotd());
        placeholders.registerPlaceholder(Server.class, "address", (e, p) -> e.getInfo().getSocketAddress());
        placeholders.registerPlaceholder(Server.class, "permission", (e, p) -> e.getInfo().getPermission());
        placeholders.registerPlaceholder(Server.class, "playersCount", (e, p) -> e.getInfo().getPlayers().size());
        placeholders.registerPlaceholder(Server.class, (e, p) -> e.getInfo().getName());
    }
}
