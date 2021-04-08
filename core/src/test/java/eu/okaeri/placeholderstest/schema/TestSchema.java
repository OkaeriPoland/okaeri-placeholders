package eu.okaeri.placeholderstest.schema;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholderstest.schema.external.ExternalItem;
import eu.okaeri.placeholderstest.schema.external.ExternalMeta;
import eu.okaeri.placeholderstest.schema.own.Item;
import eu.okaeri.placeholderstest.schema.own.Meta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSchema {

    @Test
    public void test_read_schema_own_1() {

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
        String test = PlaceholderContext.of(message).with("item", item).apply();
        assertEquals("Look at my Stone x 123! I named it 'Red stone' with 'Really nice stone. I like it.' as description!", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_1() {

        Placeholders placeholders = Placeholders.create()
                .registerPlaceholder(ExternalItem.class, "type", ExternalItem::getType)
                .registerPlaceholder(ExternalItem.class, "amount", ExternalItem::getAmount)
                .registerPlaceholder(ExternalItem.class, "damage", ExternalItem::getDamage)
                .registerPlaceholder(ExternalItem.class, "data", ExternalItem::getData)
                .registerPlaceholder(ExternalItem.class, "meta", ExternalItem::getMeta)
                .registerPlaceholder(ExternalMeta.class, "name", ExternalMeta::getName)
                .registerPlaceholder(ExternalMeta.class, "lore", ExternalMeta::getLore);

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("Stone");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        ExternalMeta meta = new ExternalMeta();
        meta.setName("Red stone");
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of("Look at my {item.type} x {item.amount}! I named it '{item.meta.name}' with '{item.meta.lore}' as description!");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("Look at my Stone x 123! I named it 'Red stone' with 'Really nice stone. I like it.' as description!", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_2() {

        Placeholders placeholders = Placeholders.create()
                .registerPlaceholder(ExternalItem.class, "type", ExternalItem::getType)
                .registerPlaceholder(ExternalItem.class, "amount", ExternalItem::getAmount)
                .registerPlaceholder(ExternalItem.class, "damage", ExternalItem::getDamage)
                .registerPlaceholder(ExternalItem.class, "data", ExternalItem::getData)
                .registerPlaceholder(ExternalItem.class, "meta", ExternalItem::getMeta)
                .registerPlaceholder(ExternalMeta.class, "name", ExternalMeta::getName)
                .registerPlaceholder(ExternalMeta.class, "lore", ExternalMeta::getLore);

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("Stone");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        item.setMeta(null); // explicit null

        CompiledMessage message = CompiledMessage.of("Look at my {item.type} x {item.amount}! I named it '{item.meta.name|i didn't}' with '{item.meta.lore|no}' as description!");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("Look at my Stone x 123! I named it 'i didn't' with 'no' as description!", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_3() {

        Placeholders placeholders = Placeholders.create()
                .registerPlaceholder(ExternalItem.class, "type", ExternalItem::getType)
                .registerPlaceholder(ExternalItem.class, "amount", ExternalItem::getAmount)
                .registerPlaceholder(ExternalItem.class, "damage", ExternalItem::getDamage)
                .registerPlaceholder(ExternalItem.class, "data", ExternalItem::getData)
                .registerPlaceholder(ExternalItem.class, "meta", ExternalItem::getMeta)
                .registerPlaceholder(ExternalMeta.class, "name", ExternalMeta::getName)
                .registerPlaceholder(ExternalMeta.class, "lore", ExternalMeta::getLore);

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("Stone");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        ExternalMeta meta = new ExternalMeta();
        meta.setName(null); // explicit null
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of("Look at my {item.type} x {item.amount}! I named it '{item.meta.name|i didn't}' with '{item.meta.lore|no}' as description!");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("Look at my Stone x 123! I named it 'i didn't' with 'Really nice stone. I like it.' as description!", test);

        System.out.println(test);
    }
}
