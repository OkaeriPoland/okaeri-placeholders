package eu.okaeri.placeholders.benchmark;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@Fork(value = 1, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.Throughput)
public class PlaceholdersBenchmark {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    // block empty
    @Benchmark
    public void empty_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.emptyMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
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

    // block static
    @Benchmark
    public void static_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.staticMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

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

    // block static_real
    @Benchmark
    public void static_real_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.staticRealMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

    @Benchmark
    public void static_real_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.staticRealMessage.getRaw()
            .replace("{who}", data.field1)
            .replace("{when}", data.field2)
            .replace("{how}", data.field3));
    }

    @Benchmark
    public void static_real_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.staticRealMessage.getRaw(),
            new String[]{"{who}", "{when}", "{how}"},
            new String[]{data.field1, data.field2, data.field3}
        ));
    }

    // block static_long
    @Benchmark
    public void static_long_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.staticLongMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

    @Benchmark
    public void static_long_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.staticLongMessage.getRaw()
            .replace("{who}", data.field1)
            .replace("{when}", data.field2)
            .replace("{how}", data.field3));
    }

    @Benchmark
    public void static_long_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.staticLongMessage.getRaw(),
            new String[]{"{who}", "{when}", "{how}"},
            new String[]{data.field1, data.field2, data.field3}
        ));
    }

    // block simple
    @Benchmark
    public void simple_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.simpleMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

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

    // block simple_longer
    @Benchmark
    public void simple_longer_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.simpleLongerMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

    @Benchmark
    public void simple_longer_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.simpleLongerMessage.getRaw()
            .replace("{who}", data.field1)
            .replace("{when}", data.field2)
            .replace("{how}", data.field3));
    }

    @Benchmark
    public void simple_longer_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.simpleLongerMessage.getRaw(),
            new String[]{"{who}", "{when}", "{how}"},
            new String[]{data.field1, data.field2, data.field3}
        ));
    }

    // block simple_essay
    @Benchmark
    public void simple_essay_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.simpleEssayMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

    @Benchmark
    public void simple_essay_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.simpleEssayMessage.getRaw()
            .replace("{who}", data.field1)
            .replace("{when}", data.field2)
            .replace("{how}", data.field3));
    }

    @Benchmark
    public void simple_essay_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.simpleEssayMessage.getRaw(),
            new String[]{"{who}", "{when}", "{how}"},
            new String[]{data.field1, data.field2, data.field3}
        ));
    }

    // block long
    @Benchmark
    public void long_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.longMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

    @Benchmark
    public void long_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.longMessage.getRaw()
            .replace("{who}", data.field1)
            .replace("{when}", data.field2)
            .replace("{how}", data.field3));
    }

    @Benchmark
    public void long_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.longMessage.getRaw(),
            new String[]{"{who}", "{when}", "{how}"},
            new String[]{data.field1, data.field2, data.field3}
        ));
    }

    // block long_tricky
    @Benchmark
    public void long_tricky_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.longTrickyMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

    @Benchmark
    public void long_tricky_java_replacechained(Blackhole blackhole, Data data) {
        blackhole.consume(data.longTrickyMessage.getRaw()
            .replace("{who}", data.field1)
            .replace("{when}", data.field2)
            .replace("{how}", data.field3));
    }

    @Benchmark
    public void long_tricky_commonslang3_replaceeach(Blackhole blackhole, Data data) {
        blackhole.consume(StringUtils.replaceEach(data.longTrickyMessage.getRaw(),
            new String[]{"{who}", "{when}", "{how}"},
            new String[]{data.field1, data.field2, data.field3}
        ));
    }

    // block repeated
    @Benchmark
    public void repeated_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.repeatedMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

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

    // block repeated_large
    @Benchmark
    public void repeated_large_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.repeatedLargeMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

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

    // block repeated_giant
    @Benchmark
    public void repeated_giant_placeholders_contextwith(Blackhole blackhole, Data data) {
        blackhole.consume(PlaceholderContext.of(data.repeatedGiantMessage)
            .with("who", data.field1)
            .with("when", data.field2)
            .with("how", data.field3)
            .apply());
    }
    // endblock

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

    @State(Scope.Benchmark)
    public static class Data {

        public String field1 = "World";
        public String field2 = "today";
        public String field3 = "ok";

        // normal messages
        public CompiledMessage emptyMessage = CompiledMessage.of("");
        public CompiledMessage staticMessage = CompiledMessage.of("Hello World!");
        public CompiledMessage staticRealMessage = CompiledMessage.of(StringUtils.repeat("Hello World! ", 10));
        public CompiledMessage staticLongMessage = CompiledMessage.of(StringUtils.repeat("Hello World! ", 100));
        public CompiledMessage simpleMessage = CompiledMessage.of("Hello {who}! How are you {when}? I'm {how}.");
        public CompiledMessage simpleLongerMessage = CompiledMessage.of("Hello {who}, it's nice to see you! How are you doing {when}? I'm {how}, but still recovering after writing that benchmark.");
        public CompiledMessage simpleEssayMessage = CompiledMessage.of("Hello {who}, it's nice to see you! Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
            " How are you doing {when}? Praesent vitae mauris ligula. Nam dignissim neque quis velit ultrices," +
            " non ullamcorper orci hendrerit. I'm {how}, but still recovering after writing that benchmark.");

        // example long message with just few placeholders
        public CompiledMessage longMessage;
        // example long message with just few placeholders
        public CompiledMessage longTrickyMessage;
        // example messages with repeating fields in varying amount
        public CompiledMessage repeatedMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 20));
        public CompiledMessage repeatedLargeMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 100));
        public CompiledMessage repeatedGiantMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 1000));

        {
            String gibberish = StringUtils.repeat("abcd", 1000);
            this.longMessage = CompiledMessage.of("Hello " + gibberish + " {who}! How " + gibberish + " are you {when}? I'm " + gibberish + " {how}." + gibberish);
        }

        {
            String gibberish = StringUtils.repeat("abcd", 1000);
            this.longTrickyMessage = CompiledMessage.of("Hello " + gibberish + " world! How " + gibberish + " are you today? I'm " + gibberish + " {how}." + gibberish);
        }
    }
    // endblock
}
