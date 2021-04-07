package eu.okaeri.placeholderstest.schema;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.Test;

public class TestSchema {

    @Test
    public void test_read_schema_1() {

        Item item = new Item();
        item.setAmount(123);
        item.setType("Kamien");
        Meta meta = new Meta();
        meta.setName("Czerwony kamień");
        meta.setLore("Bardzo fajny kamien. Polecam.");
        item.setMeta(meta);

        String test = PlaceholderContext.create()
                .with("item", item)
                .apply(CompiledMessage.of("Look at my {item.type} x {item.amount}! I named it '{item.meta.name}' with '{item.meta.lore}' as description!"));

        System.out.println(test);
    }
}
