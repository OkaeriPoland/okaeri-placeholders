package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.schema.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(ItemStack.class)
            .add("amount", ItemStack::getAmount)
            .add("durability", ItemStack::getDurability)
            .add("itemMeta", ItemStack::getItemMeta)
            .add("maxStackSize", ItemStack::getMaxStackSize)
            .add("type", ItemStack::getType)
            .add("hasItemMeta", ItemStack::hasItemMeta)
            .self(item -> (item.getAmount() == 1)
                ? item.getType().name()
                : (item.getType().name() + " x " + item.getAmount()));

        r.type(ItemMeta.class)
            .add("displayName", ItemMeta::getDisplayName)
            .add("itemFlags", meta -> enumList(meta.getItemFlags()))
            .add("lore", meta -> String.join("\n", meta.getLore()))
            .add("hasDisplayName", ItemMeta::hasDisplayName)
            .add("hasEnchants", ItemMeta::hasEnchants)
            .add("hasLore", ItemMeta::hasLore)
            .self(meta -> "(name=" + meta.getDisplayName() + ", lore=" + String.join(", ", meta.getLore()) + ")");
    }

    private static String enumList(Collection<? extends Enum<?>> enums) {
        return enums.stream()
            .map(Enum::name)
            .collect(Collectors.joining(", "));
    }
}
