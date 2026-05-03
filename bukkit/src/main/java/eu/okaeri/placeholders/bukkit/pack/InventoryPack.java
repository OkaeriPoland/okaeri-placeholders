package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

public class InventoryPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        // wrapped in explicit lambdas so removed methods (e.g. Inventory.getName/getTitle)
        // don't trigger eager LambdaMetafactory resolution at registration time
        r.type(Inventory.class)
            .add("name", (inv, p, c) -> inv.getName())
            .add("size", (inv, p, c) -> inv.getSize())
            .add("title", (inv, p, c) -> inv.getTitle())
            .add("type", (inv, p, c) -> inv.getType())
            .self((inv, p, c) -> inv.getName());

        r.type(InventoryView.class)
            .add("bottomInventory", (v, p, c) -> v.getBottomInventory())
            .add("cursor", (v, p, c) -> v.getCursor())
            .add("player", (v, p, c) -> v.getPlayer())
            .add("title", (v, p, c) -> v.getTitle())
            .add("topInventory", (v, p, c) -> v.getTopInventory())
            .add("type", (v, p, c) -> v.getType());

        r.type(PlayerInventory.class)
            .add("boots", (i, p, c) -> i.getBoots())
            .add("chestplate", (i, p, c) -> i.getChestplate())
            .add("heldItemSlot", (i, p, c) -> i.getHeldItemSlot())
            .add("helmet", (i, p, c) -> i.getHelmet())
            .add("holder", (i, p, c) -> i.getHolder())
            .add("itemInHand", (i, p, c) -> i.getItemInHand())
            .add("leggings", (i, p, c) -> i.getLeggings());
    }
}
