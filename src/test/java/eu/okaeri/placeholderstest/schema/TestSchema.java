package eu.okaeri.placeholderstest.schema;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSchema {

    @Test
    public void test_read_schema_1() {

        Item item = new Item();
        item.setAmount(123);
        item.setType("Stone");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        Meta meta = new Meta();
        meta.setName("Red stone");
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of("Look at my {item.type} x {item.amount}! I named it '{item.meta.name}' with '{item.meta.lore}' as description!");
        String test = PlaceholderContext.create().with("item", item).apply(message);
        assertEquals("Look at my Stone x 123! I named it 'Red stone' with 'Really nice stone. I like it.' as description!", test);

        System.out.println(test);
    }
}
