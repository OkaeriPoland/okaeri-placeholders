package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.ChatColor;
import org.bukkit.permissions.ServerOperator;

public class MiscPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(ChatColor.class)
            .self(ChatColor::toString);

        r.type(ServerOperator.class)
            .add("op", ServerOperator::isOp);
    }
}
