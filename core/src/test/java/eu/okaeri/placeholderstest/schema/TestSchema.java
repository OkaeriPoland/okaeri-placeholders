package eu.okaeri.placeholderstest.schema;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholderstest.schema.external.ExternalItem;
import eu.okaeri.placeholderstest.schema.external.ExternalMeta;
import eu.okaeri.placeholderstest.schema.own.Item;
import eu.okaeri.placeholderstest.schema.own.Meta;
import org.junit.jupiter.api.Test;

import java.util.Locale;

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

        CompiledMessage message = CompiledMessage.of("Look at my {item.type} x {item.amount}! I named it '{item.meta.name}' with '{item.meta.lore}' as description! {Ston.,Stonks.#item.amount}");
        String test = PlaceholderContext.of(message).with("item", item).apply();
        assertEquals("Look at my Stone x 123! I named it 'Red stone' with 'Really nice stone. I like it.' as description! Stonks.", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_1() {

        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore());

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
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore());

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
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore());

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

    @Test
    public void test_read_schema_external_4() {

        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore());

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("Stone");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        ExternalMeta meta = new ExternalMeta();
        meta.setName(null); // explicit null
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of(Locale.ENGLISH, "Look at my {item,items#item.amount}: {item.type} x {item.amount}! I named it '{item.meta.name|i didn't}' with '{item.meta.lore|no}' as description!");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("Look at my items: Stone x 123! I named it 'i didn't' with 'Really nice stone. I like it.' as description!", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_5() {

        Placeholders placeholders = Placeholders.create()
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore())
            .registerPlaceholder(ExternalItem.class, "typeEnum", (e, a, o) -> e.getTypeEnum());

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("Stone");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        item.setTypeEnum(ExternalItem.Type.DIAMOND_PICKAXE);
        ExternalMeta meta = new ExternalMeta();
        meta.setName(null); // explicit null
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of(Locale.ENGLISH, "abc {item.typeEnum.lower} aaa");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("abc <noresolver:typeEnum@lower> aaa", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_7() {

        Placeholders placeholders = Placeholders.create(true)
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore())
            .registerPlaceholder(ExternalItem.class, "typeEnum", (e, a, o) -> e.getTypeEnum());

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("DIAMOND_PICKAXE");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        item.setTypeEnum(ExternalItem.Type.DIAMOND_PICKAXE);
        ExternalMeta meta = new ExternalMeta();
        meta.setName(null); // explicit null
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of(Locale.ENGLISH, "abc {item.type.replace(_, ).toLowerCase().capitalize()} aaa");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("abc Diamond pickaxe aaa", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_8() {

        Placeholders placeholders = Placeholders.create(true)
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore())
            .registerPlaceholder(ExternalItem.class, "typeEnum", (e, a, o) -> e.getTypeEnum());

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("DIAMOND_PICKAXE");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        item.setTypeEnum(ExternalItem.Type.DIAMOND_PICKAXE);
        ExternalMeta meta = new ExternalMeta();
        meta.setName(null); // explicit null
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of(Locale.ENGLISH, "abc {item.typeEnum.pretty} aaa");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("abc Diamond Pickaxe aaa", test);

        System.out.println(test);
    }

    @Test
    public void test_read_schema_external_9() {

        Placeholders placeholders = Placeholders.create(true)
            .registerPlaceholder(ExternalItem.class, "type", (e, a, o) -> e.getType())
            .registerPlaceholder(ExternalItem.class, "amount", (e, a, o) -> e.getAmount())
            .registerPlaceholder(ExternalItem.class, "damage", (e, a, o) -> e.getDamage())
            .registerPlaceholder(ExternalItem.class, "data", (e, a, o) -> e.getData())
            .registerPlaceholder(ExternalItem.class, "meta", (e, a, o) -> e.getMeta())
            .registerPlaceholder(ExternalMeta.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(ExternalMeta.class, "lore", (e, a, o) -> e.getLore())
            .registerPlaceholder(ExternalItem.class, "typeEnum", (e, a, o) -> e.getTypeEnum());

        ExternalItem item = new ExternalItem();
        item.setAmount(123);
        item.setType("DIAMOND_PICKAXE");
        item.setDamage((short) 10);
        item.setData((byte) 1);
        item.setTypeEnum(ExternalItem.Type.DIAMOND_PICKAXE);
        ExternalMeta meta = new ExternalMeta();
        meta.setName(null); // explicit null
        meta.setLore("Really nice stone. I like it.");
        item.setMeta(meta);

        CompiledMessage message = CompiledMessage.of(Locale.ENGLISH, "abc {item.amount.multiply(0).add(1)} aaa");
        String test = placeholders.contextOf(message).with("item", item).apply();
        assertEquals("abc 1 aaa", test);

        System.out.println(test);
    }
}
