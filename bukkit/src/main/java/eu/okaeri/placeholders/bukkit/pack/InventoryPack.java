package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

public class InventoryPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Inventory.class)
            .add("name", inv -> inv.getName())
            .add("size", inv -> inv.getSize())
            .add("title", inv -> inv.getTitle())
            .add("type", inv -> inv.getType())
            .self(inv -> inv.getName());

        r.type(InventoryView.class)
            .add("bottomInventory", v -> v.getBottomInventory())
            .add("cursor", v -> v.getCursor())
            .add("player", v -> v.getPlayer())
            .add("title", v -> v.getTitle())
            .add("topInventory", v -> v.getTopInventory())
            .add("type", v -> v.getType());

        r.type(PlayerInventory.class)
            .add("boots", i -> i.getBoots())
            .add("chestplate", i -> i.getChestplate())
            .add("heldItemSlot", i -> i.getHeldItemSlot())
            .add("helmet", i -> i.getHelmet())
            .add("holder", i -> i.getHolder())
            .add("itemInHand", i -> i.getItemInHand())
            .add("leggings", i -> i.getLeggings());
    }
}
