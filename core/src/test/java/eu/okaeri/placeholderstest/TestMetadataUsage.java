package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMetadataUsage {

    @Test
    public void test_plurals_message_1() {
        CompiledMessage message = CompiledMessage.of(Locale.ENGLISH, "I would like {amount} {apple,apples#amount}.");
        assertEquals("I would like -1 apples.", PlaceholderContext.of(message).with("amount", -1).apply());
        assertEquals("I would like 0 apples.", PlaceholderContext.of(message).with("amount", 0).apply());
        assertEquals("I would like 1 apple.", PlaceholderContext.of(message).with("amount", 1).apply());
        assertEquals("I would like 2 apples.", PlaceholderContext.of(message).with("amount", 2).apply());
    }

    @Test
    public void test_plurals_message_2() {
        CompiledMessage message = CompiledMessage.of(Locale.forLanguageTag("pl"), "Mam w domu {dogs} {psa,psy,psów#dogs}.");
        assertEquals("Mam w domu -1 psów.", PlaceholderContext.of(message).with("dogs", -1).apply());
        assertEquals("Mam w domu 0 psów.", PlaceholderContext.of(message).with("dogs", 0).apply());
        assertEquals("Mam w domu 1 psa.", PlaceholderContext.of(message).with("dogs", 1).apply());
        assertEquals("Mam w domu 2 psy.", PlaceholderContext.of(message).with("dogs", 2).apply());
        assertEquals("Mam w domu 5 psów.", PlaceholderContext.of(message).with("dogs", 5).apply());
        assertEquals("Mam w domu 16 psów.", PlaceholderContext.of(message).with("dogs", 16).apply());
        assertEquals("Mam w domu 22 psy.", PlaceholderContext.of(message).with("dogs", 22).apply());
        assertEquals("Mam w domu 25 psów.", PlaceholderContext.of(message).with("dogs", 25).apply());
    }

    @Test
    public void test_boolean_message_1() {
        CompiledMessage message = CompiledMessage.of("Active: {yes,no#status}");
        assertEquals("Active: yes", PlaceholderContext.of(message).with("status", true).apply());
        assertEquals("Active: no", PlaceholderContext.of(message).with("status", false).apply());
    }

    @Test
    public void test_fp_message_1() {
        CompiledMessage message = CompiledMessage.of("Value: {%.2f#value}");
        assertEquals("Value: 0.20", PlaceholderContext.of(message).with("value", 0.2).apply());
        assertEquals("Value: 1.00", PlaceholderContext.of(message).with("value", 1).apply());
    }

    @Test
    public void test_fp_message_0() {
        CompiledMessage message = CompiledMessage.of("Value: {%.0f#value}");
        assertEquals("Value: 0", PlaceholderContext.of(message).with("value", 0.2).apply());
        assertEquals("Value: 1", PlaceholderContext.of(message).with("value", 0.6).apply());
        assertEquals("Value: 1", PlaceholderContext.of(message).with("value", 1).apply());
    }
}
