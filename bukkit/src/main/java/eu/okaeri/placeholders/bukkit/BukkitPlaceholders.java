package eu.okaeri.placeholders.bukkit;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.bukkit.pack.*;
import eu.okaeri.placeholders.registry.Registry;

public final class BukkitPlaceholders implements PlaceholderPack {

    /**
     * Create a Placeholders instance with defaults and Bukkit packs.
     */
    public static Placeholders create() {
        return Placeholders.create().with(new BukkitPlaceholders());
    }

    /**
     * Create an empty Placeholders instance with only Bukkit packs.
     */
    public static Placeholders empty() {
        return Placeholders.empty().with(new BukkitPlaceholders());
    }

    @Override
    public void register(Registry r) {
        r.packs(
            new MiscPack(),
            new InventoryPack(),
            new EntityPack(),
            new LocationPack(),
            new WorldPack(),
            new ItemPack(),
            new DamageablePack()
        );
    }
}
