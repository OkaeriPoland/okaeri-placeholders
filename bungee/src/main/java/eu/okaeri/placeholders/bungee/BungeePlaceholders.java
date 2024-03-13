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
        placeholders.registerPlaceholder(ChatColor.class, (e, a, o) -> e.toString());

        // CommandSender
        placeholders.registerPlaceholder(CommandSender.class, "name", (e, a, o) -> e.getName());
        placeholders.registerPlaceholder(CommandSender.class, (e, a, o) -> e.getName());

        // ProxyServer
        placeholders.registerPlaceholder(ProxyServer.class, "name", (e, a, o) -> e.getName());
        placeholders.registerPlaceholder(ProxyServer.class, "onlineCount", (e, a, o) -> e.getOnlineCount());
        placeholders.registerPlaceholder(ProxyServer.class, "version", (e, a, o) -> e.getVersion());
        placeholders.registerPlaceholder(ProxyServer.class, (e, a, o) -> e.getName());

        // ProxiedPlayer
        placeholders.registerPlaceholder(ProxiedPlayer.class, "displayName", (e, a, o) -> e.getDisplayName());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "ping", (e, a, o) -> e.getPing());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "server", (e, a, o) -> e.getServer());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "uniqueId", (e, a, o) -> e.getUniqueId());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "locale", (e, a, o) -> e.getLocale());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "viewDistance", (e, a, o) -> e.getViewDistance());
        placeholders.registerPlaceholder(ProxiedPlayer.class, "chatMode", (e, a, o) -> e.getChatMode());

        // Server
        placeholders.registerPlaceholder(Server.class, "name", (e, a, o) -> e.getInfo().getName());
        placeholders.registerPlaceholder(Server.class, "motd", (e, a, o) -> e.getInfo().getMotd());
        placeholders.registerPlaceholder(Server.class, "address", (e, a, o) -> e.getInfo().getSocketAddress());
        placeholders.registerPlaceholder(Server.class, "permission", (e, a, o) -> e.getInfo().getPermission());
        placeholders.registerPlaceholder(Server.class, "playersCount", (e, a, o) -> e.getInfo().getPlayers().size());
        placeholders.registerPlaceholder(Server.class, (e, a, o) -> e.getInfo().getName());
    }
}
