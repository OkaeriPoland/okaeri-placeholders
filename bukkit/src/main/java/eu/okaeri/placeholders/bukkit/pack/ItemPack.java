package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(ItemStack.class)
            .add("amount", i -> i.getAmount())
            .add("durability", i -> i.getDurability())
            .add("itemMeta", i -> i.getItemMeta())
            .add("maxStackSize", i -> i.getMaxStackSize())
            .add("type", i -> i.getType())
            .add("hasItemMeta", i -> i.hasItemMeta())
            .self(i -> (i.getAmount() == 1)
                ? i.getType().name()
                : (i.getType().name() + " x " + i.getAmount()));

        r.type(ItemMeta.class)
            .add("displayName", m -> m.getDisplayName())
            .add("itemFlags", m -> enumList(m.getItemFlags()))
            .add("lore", m -> String.join("\n", m.getLore()))
            .add("hasDisplayName", m -> m.hasDisplayName())
            .add("hasEnchants", m -> m.hasEnchants())
            .add("hasLore", m -> m.hasLore())
            .self(m -> "(name=" + m.getDisplayName() + ", lore=" + String.join(", ", m.getLore()) + ")");
    }

    private static String enumList(Collection<? extends Enum<?>> enums) {
        return enums.stream()
            .map(Enum::name)
            .collect(Collectors.joining(", "));
    }
}
