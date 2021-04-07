package eu.okaeri.placeholders.benchmark;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.annotation.Placeholder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.Throughput)
public class FieldsBenchmark {

    @State(Scope.Benchmark)
    public static class Data {

        public Item item;
        {
            this.item = new Item();
            this.item.setAmount(123);
            this.item.setType("Stone");
            this.item.setDamage((short) 10);
            this.item.setData((byte) 1);
            Meta meta = new Meta();
            meta.setName("Red stone");
            meta.setLore("Really nice stone. I like it.");
            this.item.setMeta(meta);
        }

        public CompiledMessage itemSingleMessage = CompiledMessage.of("Look, I have found '{item.meta.name}'! It is beautiful! I really like it :00000000000000000000000000000000000");
        public CompiledMessage itemThreeMessage = CompiledMessage.of("Look, I have found '{item.meta.name}'! It is beautiful! I really like it it being {item.type} x {item.amount}!");
        public CompiledMessage itemMessage = CompiledMessage.of("Look at my {item.type} x {item.amount}! I named it '{item.meta.name}' with '{item.meta.lore}' as description! [{item.damage}:{item.data}]");
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    // block item_single
    @Benchmark
    public void item_single_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("item", data.item)
                .apply(data.itemSingleMessage));
    }

    @Benchmark
    public void item_single_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.itemSingleMessage.getRaw()
                .replace("{item.amount}", String.valueOf(data.item.getAmount()))
                .replace("{item.type}", data.item.getType())
                .replace("{item.damage}", String.valueOf(data.item.getDamage()))
                .replace("{item.data}", String.valueOf(data.item.getData()))
                .replace("{item.meta.name}", data.item.getMeta().getName())
                .replace("{item.meta.lore}", data.item.getMeta().getLore()));
    }

    @Benchmark
    public void item_single_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.itemSingleMessage.getRaw(),
                new String[]{"{item.amount}", "{item.type}", "{item.damage}", "{item.data}", "{item.meta.name}", "{item.meta.lore}"},
                new String[]{String.valueOf(data.item.getAmount()), data.item.getType(), String.valueOf(data.item.getDamage()),
                        String.valueOf(data.item.getData()), data.item.getMeta().getName(), data.item.getMeta().getLore()}
        ));
    }
    // endblock

    // block item_three
    @Benchmark
    public void item_three_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("item", data.item)
                .apply(data.itemThreeMessage));
    }

    @Benchmark
    public void item_three_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.itemThreeMessage.getRaw()
                .replace("{item.amount}", String.valueOf(data.item.getAmount()))
                .replace("{item.type}", data.item.getType())
                .replace("{item.damage}", String.valueOf(data.item.getDamage()))
                .replace("{item.data}", String.valueOf(data.item.getData()))
                .replace("{item.meta.name}", data.item.getMeta().getName())
                .replace("{item.meta.lore}", data.item.getMeta().getLore()));
    }

    @Benchmark
    public void item_three_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.itemThreeMessage.getRaw(),
                new String[]{"{item.amount}", "{item.type}", "{item.damage}", "{item.data}", "{item.meta.name}", "{item.meta.lore}"},
                new String[]{String.valueOf(data.item.getAmount()), data.item.getType(), String.valueOf(data.item.getDamage()),
                        String.valueOf(data.item.getData()), data.item.getMeta().getName(), data.item.getMeta().getLore()}
        ));
    }
    // endblock

    // block item_full
    @Benchmark
    public void item_full_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("item", data.item)
                .apply(data.itemMessage));
    }

    @Benchmark
    public void item_full_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.itemMessage.getRaw()
                .replace("{item.amount}", String.valueOf(data.item.getAmount()))
                .replace("{item.type}", data.item.getType())
                .replace("{item.damage}", String.valueOf(data.item.getDamage()))
                .replace("{item.data}", String.valueOf(data.item.getData()))
                .replace("{item.meta.name}", data.item.getMeta().getName())
                .replace("{item.meta.lore}", data.item.getMeta().getLore()));
    }

    @Benchmark
    public void item_full_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.itemMessage.getRaw(),
                new String[]{"{item.amount}", "{item.type}", "{item.damage}", "{item.data}", "{item.meta.name}", "{item.meta.lore}"},
                new String[]{String.valueOf(data.item.getAmount()), data.item.getType(), String.valueOf(data.item.getDamage()),
                        String.valueOf(data.item.getData()), data.item.getMeta().getName(), data.item.getMeta().getLore()}
        ));
    }
    // endblock
}

@Data
class Item implements PlaceholderSchema {
    @Placeholder private String type;
    @Placeholder private int amount;
    @Placeholder private short damage;
    @Placeholder private byte data;
    @Placeholder private Meta meta;
}

@Data
class Meta implements PlaceholderSchema {
    @Placeholder private String name;
    @Placeholder private String lore;
}