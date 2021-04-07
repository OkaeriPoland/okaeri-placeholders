//package eu.okaeri.placeholders.benchmark;
//
//import eu.okaeri.placeholders.context.PlaceholderContext;
//import eu.okaeri.placeholders.message.CompiledMessage;
//import org.apache.commons.lang3.StringUtils;
//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.infra.Blackhole;
//
//@Fork(value = 1, warmups = 2)
//@Warmup(iterations = 5, time = 1)
//@Measurement(iterations = 10, time = 1)
//@BenchmarkMode(Mode.Throughput)
//public class PlaceholdersBenchmark {
//
//    @State(Scope.Benchmark)
//    public static class Data {
//
//        public String field1 = "World";
//        public String field2 = "today";
//        public String field3 = "ok";
//
//        // normal messages
//        public CompiledMessage emptyMessage = CompiledMessage.of("");
//        public CompiledMessage staticMessage = CompiledMessage.of("Hello World!");
//        public CompiledMessage staticRealMessage = CompiledMessage.of(StringUtils.repeat("Hello World! ", 10));
//        public CompiledMessage staticLongMessage = CompiledMessage.of(StringUtils.repeat("Hello World! ", 100));
//        public CompiledMessage simpleMessage = CompiledMessage.of("Hello {who}! How are you {when}? I'm {how}.");
//        public CompiledMessage simpleLongerMessage = CompiledMessage.of("Hello {who}, it's nice to see you! How are you doing {when}? I'm {how}, but still recovering after writing that benchmark.");
//
//        // example long message with just few placeholders
//        public CompiledMessage longMessage;
//        {
//            String gibberish = StringUtils.repeat("abcd", 1000);
//            this.longMessage = CompiledMessage.of("Hello " + gibberish + " {who}! How " + gibberish + " are you {when}? I'm " + gibberish + " {how}." + gibberish);
//        }
//
//        // example long message with just few placeholders
//        public CompiledMessage longTrickyMessage;
//        {
//            String gibberish = StringUtils.repeat("abcd", 1000);
//            this.longTrickyMessage = CompiledMessage.of("Hello " + gibberish + " world! How " + gibberish + " are you today? I'm " + gibberish + " {how}." + gibberish);
//        }
//
//        // example messages with repeating fields in varying amount
//        public CompiledMessage repeatedMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 20));
//        public CompiledMessage repeatedLargeMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 100));
//        public CompiledMessage repeatedGiantMessage = CompiledMessage.of(StringUtils.repeat("Hello {who}! How are you {when}? I'm {how}.\n", 1000));
//    }
//
//    public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);
//    }
//
//    // block empty
//    @Benchmark
//    public void empty_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.emptyMessage));
//    }
//
//    @Benchmark
//    public void empty_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.emptyMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void empty_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.emptyMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block static
//    @Benchmark
//    public void static_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.staticMessage));
//    }
//
//    @Benchmark
//    public void static_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.staticMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void static_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.staticMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block static_real
//    @Benchmark
//    public void static_real_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.staticRealMessage));
//    }
//
//    @Benchmark
//    public void static_real_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.staticRealMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void static_real_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.staticRealMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block static_long
//    @Benchmark
//    public void static_long_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.staticLongMessage));
//    }
//
//    @Benchmark
//    public void static_long_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.staticLongMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void static_long_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.staticLongMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block simple
//    @Benchmark
//    public void simple_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.simpleMessage));
//    }
//
//    @Benchmark
//    public void simple_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.simpleMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void simple_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.simpleMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block simple_longer
//    @Benchmark
//    public void simple_longer_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.simpleLongerMessage));
//    }
//
//    @Benchmark
//    public void simple_longer_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.simpleLongerMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void simple_longer_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.simpleLongerMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block long
//    @Benchmark
//    public void long_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.longMessage));
//    }
//
//    @Benchmark
//    public void long_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.longMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void long_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.longMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block long_tricky
//    @Benchmark
//    public void long_tricky_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.longTrickyMessage));
//    }
//
//    @Benchmark
//    public void long_tricky_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.longTrickyMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void long_tricky_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.longTrickyMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block repeated
//    @Benchmark
//    public void repeated_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.repeatedMessage));
//    }
//
//    @Benchmark
//    public void repeated_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.repeatedMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void repeated_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.repeatedMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block repeated_large
//    @Benchmark
//    public void repeated_large_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.repeatedLargeMessage));
//    }
//
//    @Benchmark
//    public void repeated_large_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.repeatedLargeMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void repeated_large_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.repeatedLargeMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//
//    // block repeated_giant
//    @Benchmark
//    public void repeated_giant_placeholders_contextwith(Blackhole blackhole, Data data) {
//        blackhole.consume(PlaceholderContext.create()
//                .with("who", data.field1)
//                .with("when", data.field2)
//                .with("how", data.field3)
//                .apply(data.repeatedGiantMessage));
//    }
//
//    @Benchmark
//    public void repeated_giant_java_replacechained(Blackhole blackhole, Data data) {
//        blackhole.consume(data.repeatedGiantMessage.getRaw()
//                .replace("{who}", data.field1)
//                .replace("{when}", data.field2)
//                .replace("{how}", data.field3));
//    }
//
//    @Benchmark
//    public void repeated_giant_commonslang3_replaceeach(Blackhole blackhole, Data data) {
//        blackhole.consume(StringUtils.replaceEach(data.repeatedGiantMessage.getRaw(),
//                new String[]{"{who}", "{when}", "{how}"},
//                new String[]{data.field1, data.field2, data.field3}
//        ));
//    }
//    // endblock
//}
