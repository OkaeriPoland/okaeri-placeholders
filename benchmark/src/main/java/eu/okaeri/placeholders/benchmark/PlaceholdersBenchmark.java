package eu.okaeri.placeholders.benchmark;

import eu.okaeri.placeholders.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@Fork(value = 1, warmups = 2)
@BenchmarkMode(Mode.Throughput)
public class PlaceholdersBenchmark {

    @State(Scope.Benchmark)
    public static class Data {

        public String field1 = "World";
        public String field2 = "today";
        public String field3 = "ok";

        // normal messages
        public CompiledMessage emptyMessage = CompiledMessage.of("");
        public CompiledMessage staticMessage = CompiledMessage.of("Hello World!");
        public CompiledMessage simpleMessage = CompiledMessage.of("Hello {who}! How are you {when}? I'm {how}.");

        // example long message with just few placeholders
        public CompiledMessage longMessage;
        {
            String gibberrish = StringUtils.repeat("abcd", 1000);
            this.longMessage = CompiledMessage.of("Hello " + gibberrish + " {who}! How "  + gibberrish + " are you {when}? I'm " + gibberrish + " {how}." + gibberrish);
        }

        // example messages with repeating fields in varying amount
        public CompiledMessage repeatedMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 20));
        public CompiledMessage repeatedLargeMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 100));
        public CompiledMessage repeatedGiantMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 1000));
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    // block empty
    @Benchmark
    public void empty_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("who", data.field1)
                .with("when", data.field2)
                .with("how", data.field3)
                .apply(data.emptyMessage));
    }

    @Benchmark
    public void empty_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.emptyMessage.getRaw()
                .replace("{who}", data.field1)
                .replace("{when}", data.field2)
                .replace("{how}", data.field3));
    }

    @Benchmark
    public void empty_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.emptyMessage.getRaw(),
                new String[]{"{who}", "{when}", "{how}"},
                new String[]{data.field1, data.field2, data.field3}
        ));
    }
    // endblock

    // block static
    @Benchmark
    public void static_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("who", data.field1)
                .with("when", data.field2)
                .with("how", data.field3)
                .apply(data.staticMessage));
    }

    @Benchmark
    public void static_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.staticMessage.getRaw()
                .replace("{who}", data.field1)
                .replace("{when}", data.field2)
                .replace("{how}", data.field3));
    }

    @Benchmark
    public void static_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.staticMessage.getRaw(),
                new String[]{"{who}", "{when}", "{how}"},
                new String[]{data.field1, data.field2, data.field3}
        ));
    }
    // endblock

    // block simple
    @Benchmark
    public void simple_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("who", data.field1)
                .with("when", data.field2)
                .with("how", data.field3)
                .apply(data.simpleMessage));
    }

    @Benchmark
    public void simple_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.simpleMessage.getRaw()
                .replace("{who}", data.field1)
                .replace("{when}", data.field2)
                .replace("{how}", data.field3));
    }

    @Benchmark
    public void simple_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.simpleMessage.getRaw(),
                new String[]{"{who}", "{when}", "{how}"},
                new String[]{data.field1, data.field2, data.field3}
        ));
    }
    // endblock

    // block repeated
    @Benchmark
    public void repeated_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("who", data.field1)
                .with("when", data.field2)
                .with("how", data.field3)
                .apply(data.repeatedMessage));
    }

    @Benchmark
    public void repeated_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.repeatedMessage.getRaw()
                .replace("{who}", data.field1)
                .replace("{when}", data.field2)
                .replace("{how}", data.field3));
    }

    @Benchmark
    public void repeated_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.repeatedMessage.getRaw(),
                new String[]{"{who}", "{when}", "{how}"},
                new String[]{data.field1, data.field2, data.field3}
        ));
    }
    // endblock

    // block repeated_large
    @Benchmark
    public void repeated_large_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("who", data.field1)
                .with("when", data.field2)
                .with("how", data.field3)
                .apply(data.repeatedLargeMessage));
    }

    @Benchmark
    public void repeated_large_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.repeatedLargeMessage.getRaw()
                .replace("{who}", data.field1)
                .replace("{when}", data.field2)
                .replace("{how}", data.field3));
    }

    @Benchmark
    public void repeated_large_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.repeatedLargeMessage.getRaw(),
                new String[]{"{who}", "{when}", "{how}"},
                new String[]{data.field1, data.field2, data.field3}
        ));
    }
    // endblock

    // block repeated_giant
    @Benchmark
    public void repeated_giant_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.create()
                .with("who", data.field1)
                .with("when", data.field2)
                .with("how", data.field3)
                .apply(data.repeatedGiantMessage));
    }

    @Benchmark
    public void repeated_giant_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.repeatedGiantMessage.getRaw()
                .replace("{who}", data.field1)
                .replace("{when}", data.field2)
                .replace("{how}", data.field3));
    }

    @Benchmark
    public void repeated_giant_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.repeatedGiantMessage.getRaw(),
                new String[]{"{who}", "{when}", "{how}"},
                new String[]{data.field1, data.field2, data.field3}
        ));
    }
    // endblock
}
