package eu.okaeri.placeholders.fixture;

import java.util.List;

/**
 * Pure JDK 21 records for general placeholder tests.
 * These are simple data holders without @Placeholder annotation.
 */
public final class TestRecords {

    private TestRecords() {
    }

    // Simple item record
    public record Item(String type, int amount, short damage, byte data, Meta meta) {
    }

    // Nested metadata record
    public record Meta(String name, String lore) {
    }

    // Inheritance hierarchy using sealed interfaces
    public sealed interface Entity permits Player, NPC {
        String name();
    }

    public record Player(String name, int health, Inventory inventory) implements Entity {
    }

    public record NPC(String name, String dialogue) implements Entity {
    }

    public record Inventory(List<Item> items) {
        public int size() {
            return this.items.size();
        }

        public Item first() {
            return this.items.isEmpty() ? null : this.items.getFirst();
        }
    }

    // For number operation tests
    public record Numbers(
        int intValue,
        long longValue,
        double doubleValue,
        float floatValue,
        short shortValue,
        byte byteValue
    ) {
    }

    // For enum tests
    public enum ItemType {
        DIAMOND_SWORD,
        STONE_AXE,
        IRON_PICKAXE;

        public String pretty() {
            return this.name().replace("_", " ").toLowerCase();
        }
    }

    public record TypedItem(ItemType type, int amount) {
    }

    // Factory methods for common test data
    public static Meta sampleMeta() {
        return new Meta("Excalibur", "A legendary sword");
    }

    public static Item sampleItem() {
        return new Item("DIAMOND_SWORD", 1, (short) 0, (byte) 0, sampleMeta());
    }

    public static Item itemWithNullMeta() {
        return new Item("STONE", 64, (short) 0, (byte) 0, null);
    }

    public static Inventory sampleInventory() {
        return new Inventory(List.of(
            sampleItem(),
            new Item("GOLDEN_APPLE", 5, (short) 0, (byte) 0, null),
            new Item("IRON_PICKAXE", 1, (short) 50, (byte) 0, new Meta("Miner's Pick", "Durable"))
        ));
    }

    public static Inventory emptyInventory() {
        return new Inventory(List.of());
    }

    public static Player samplePlayer() {
        return new Player("Steve", 20, sampleInventory());
    }

    public static Player playerWithEmptyInventory() {
        return new Player("Alex", 18, emptyInventory());
    }

    public static Player playerWithNullInventory() {
        return new Player("Notch", 20, null);
    }

    public static NPC sampleNPC() {
        return new NPC("Villager", "Hello, traveler!");
    }

    public static Numbers sampleNumbers() {
        return new Numbers(42, 1000L, 3.14159, 2.5f, (short) 100, (byte) 8);
    }

    public static TypedItem sampleTypedItem() {
        return new TypedItem(ItemType.DIAMOND_SWORD, 1);
    }
}
