package eu.okaeri.placeholders.bungee;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.registry.Registry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public final class BungeePlaceholders implements PlaceholderPack {

    /**
     * Create a Placeholders instance with defaults and Bungee packs.
     */
    public static Placeholders create() {
        return Placeholders.create().with(new BungeePlaceholders());
    }

    /**
     * Create an empty Placeholders instance with only Bungee packs.
     */
    public static Placeholders empty() {
        return Placeholders.empty().with(new BungeePlaceholders());
    }

    @Override
    public void register(Registry r) {
        r.type(ChatColor.class)
            .self(ChatColor::toString);

        r.type(CommandSender.class)
            .add("name", CommandSender::getName)
            .self(CommandSender::getName);

        r.type(ProxyServer.class)
            .add("name", ProxyServer::getName)
            .add("onlineCount", ProxyServer::getOnlineCount)
            .add("version", ProxyServer::getVersion)
            .self(ProxyServer::getName);

        r.type(ProxiedPlayer.class)
            .add("displayName", ProxiedPlayer::getDisplayName)
            .add("ping", ProxiedPlayer::getPing)
            .add("server", ProxiedPlayer::getServer)
            .add("uniqueId", ProxiedPlayer::getUniqueId)
            .add("locale", ProxiedPlayer::getLocale)
            .add("viewDistance", ProxiedPlayer::getViewDistance)
            .add("chatMode", ProxiedPlayer::getChatMode);

        r.type(Server.class)
            .add("name", s -> s.getInfo().getName())
            .add("motd", s -> s.getInfo().getMotd())
            .add("address", s -> s.getInfo().getSocketAddress())
            .add("permission", s -> s.getInfo().getPermission())
            .add("playersCount", s -> s.getInfo().getPlayers().size())
            .self(s -> s.getInfo().getName());
    }
}
