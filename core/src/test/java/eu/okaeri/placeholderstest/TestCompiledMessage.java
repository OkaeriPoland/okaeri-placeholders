package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class TestCompiledMessage {

    @Test
    public void test_empty_message() {
        CompiledMessage message = CompiledMessage.of("");
        assertEquals("", message.getRaw());
        assertIterableEquals(message.getParts(), Collections.emptyList());
        System.out.println(message);
    }

    @Test
    public void test_no_fields_message() {
        CompiledMessage message = CompiledMessage.of("Hello World!");
        assertEquals("Hello World!", message.getRaw());
        assertIterableEquals(message.getParts(), Collections.singletonList(MessageStatic.of("Hello World!")));
        System.out.println(message);
    }

    @Test
    public void test_simple_message() {
        CompiledMessage message = CompiledMessage.of("Hello {who}! How are you {when}? I'm {how}.");
        assertEquals("Hello {who}! How are you {when}? I'm {how}.", message.getRaw());
        assertIterableEquals(message.getParts(), Arrays.asList(
                MessageStatic.of("Hello "),
                MessageField.of("who"),
                MessageStatic.of("! How are you "),
                MessageField.of("when"),
                MessageStatic.of("? I'm "),
                MessageField.of("how"),
                MessageStatic.of(".")
        ));
        System.out.println(message);
    }
}
