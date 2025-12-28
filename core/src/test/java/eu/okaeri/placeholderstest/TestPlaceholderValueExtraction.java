package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholderstest.schema.external.ExternalItem;
import eu.okaeri.placeholderstest.schema.external.ExternalMeta;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlaceholderValueExtraction {

    @Test
    public void test_extract_simple_value() {
        PlaceholderContext context = PlaceholderContext.create()
            .with("name", "John")
            .with("age", 25);

        Optional<String> name = context.getPlaceholderValue("name", String.class);
        assertTrue(name.isPresent());
        assertEquals("John", name.get());

        Optional<Integer> age = context.getPlaceholderValue("age", Integer.class);
        assertTrue(age.isPresent());
        assertEquals(25, age.get());
    }

    @Test
    public void test_extract_nested_value() {
        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore());

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("Stone");
        ExternalMeta meta = new ExternalMeta();
        meta.setName("Red stone");
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        PlaceholderContext context = PlaceholderContext.create()
            .setPlaceholders(placeholders)
            .with("item", item);

        // Extract nested value
        Optional<String> metaName = context.getPlaceholderValue("item.meta.name", String.class);
        assertTrue(metaName.isPresent());
        assertEquals("Red stone", metaName.get());

        // Extract intermediate object
        Optional<ExternalMeta> extractedMeta = context.getPlaceholderValue("item.meta", ExternalMeta.class);
        assertTrue(extractedMeta.isPresent());
        assertEquals(meta, extractedMeta.get());
        assertEquals("Red stone", extractedMeta.get().getName());

        // Extract primitive type
        Optional<Integer> amount = context.getPlaceholderValue("item.amount", Integer.class);
        assertTrue(amount.isPresent());
        assertEquals(123, amount.get());
    }

    @Test
    public void test_extract_with_type_mismatch() {
        PlaceholderContext context = PlaceholderContext.create()
            .with("age", 25);

        // Try to extract as wrong type - should return empty Optional
        Optional<String> result = context.getPlaceholderValue("age", String.class);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_extract_missing_placeholder() {
        PlaceholderContext context = PlaceholderContext.create()
            .with("name", "John");

        // Missing placeholder still throws exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            context.getPlaceholderValue("missing", String.class);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void test_extract_with_mapper() {
        PlaceholderContext context = PlaceholderContext.create()
            .with("age", 25);

        // Convert Integer to String using mapper
        Optional<String> ageStr = context.getPlaceholderValue("age", obj -> String.valueOf(obj), String.class);
        assertTrue(ageStr.isPresent());
        assertEquals("25", ageStr.get());

        // Convert Integer to Double using mapper
        Optional<Double> ageDouble = context.getPlaceholderValue("age", obj -> ((Integer) obj).doubleValue(), Double.class);
        assertTrue(ageDouble.isPresent());
        assertEquals(25.0, ageDouble.get());
    }

    @Test
    public void test_extract_nested_with_mapper() {
        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount());

        ExternalItem item = new ExternalItem();
        item.setAmount(123);

        PlaceholderContext context = PlaceholderContext.create()
            .setPlaceholders(placeholders)
            .with("item", item);

        // Extract and convert amount to string
        Optional<String> amountStr = context.getPlaceholderValue("item.amount", obj -> "Amount: " + obj, String.class);
        assertTrue(amountStr.isPresent());
        assertEquals("Amount: 123", amountStr.get());
    }

    @Test
    public void test_extract_null_value() {
        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta());

        ExternalItem item = new ExternalItem();
        item.setMeta(null);

        PlaceholderContext context = PlaceholderContext.create()
            .setPlaceholders(placeholders)
            .with("item", item);

        // Extracting null should return empty Optional
        Optional<ExternalMeta> result = context.getPlaceholderValue("item.meta", ExternalMeta.class);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_extract_chained_values() {
        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName());

        ExternalItem item = new ExternalItem();
        item.setAmount(456);
        ExternalMeta meta = new ExternalMeta();
        meta.setName("Special Item");
        item.setMeta(meta);

        PlaceholderContext context = PlaceholderContext.create()
            .setPlaceholders(placeholders)
            .with("item", item);

        // Extract multiple values
        Optional<Integer> amount = context.getPlaceholderValue("item.amount", Integer.class);
        assertTrue(amount.isPresent());
        assertEquals(456, amount.get());

        Optional<String> name = context.getPlaceholderValue("item.meta.name", String.class);
        assertTrue(name.isPresent());
        assertEquals("Special Item", name.get());
    }

    @Test
    public void test_extract_value_with_function_params() {
        // Create a simple class to test function parameters
        class Stats {
            public String getStatValue(String statName) {
                if ("kills".equals(statName)) return "150";
                if ("deaths".equals(statName)) return "42";
                return "unknown";
            }
        }

        class Player {
            private Stats stats = new Stats();
            public Stats getStats() { return stats; }
        }

        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(Player.class, "stats", (player, field, ctx) -> player.getStats())
            .registerPlaceholder(Stats.class, "value", (stats, field, ctx) -> {
                // The field parameter contains the params via field.params()
                String statName = field.params().strAt(0, "unknown");
                return stats.getStatValue(statName);
            });

        Player player = new Player();
        PlaceholderContext context = PlaceholderContext.create()
            .setPlaceholders(placeholders)
            .with("player", player);

        // Extract value with function parameter - player.stats.value(kills)
        Optional<String> kills = context.getPlaceholderValue("player.stats.value(kills)", String.class);
        assertTrue(kills.isPresent());
        assertEquals("150", kills.get());

        // Extract with different parameter
        Optional<String> deaths = context.getPlaceholderValue("player.stats.value(deaths)", String.class);
        assertTrue(deaths.isPresent());
        assertEquals("42", deaths.get());
    }
}
