package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.schema.Registry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

public class InventoryPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Inventory.class)
            .add("name", Inventory::getName)
            .add("size", Inventory::getSize)
            .add("title", Inventory::getTitle)
            .add("type", Inventory::getType)
            .self(Inventory::getName);

        r.type(InventoryView.class)
            .add("bottomInventory", InventoryView::getBottomInventory)
            .add("cursor", InventoryView::getCursor)
            .add("player", InventoryView::getPlayer)
            .add("title", InventoryView::getTitle)
            .add("topInventory", InventoryView::getTopInventory)
            .add("type", InventoryView::getType);

        r.type(PlayerInventory.class)
            .add("boots", PlayerInventory::getBoots)
            .add("chestplate", PlayerInventory::getChestplate)
            .add("heldItemSlot", PlayerInventory::getHeldItemSlot)
            .add("helmet", PlayerInventory::getHelmet)
            .add("holder", PlayerInventory::getHolder)
            .add("itemInHand", PlayerInventory::getItemInHand)
            .add("leggings", PlayerInventory::getLeggings);
    }
}
