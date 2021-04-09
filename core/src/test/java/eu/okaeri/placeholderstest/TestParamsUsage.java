package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.MessageElement;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestParamsUsage {

    @Test
    public void test_params_1() {
        CompiledMessage message = CompiledMessage.of("Woah: {player.healthBar(20)}");
        List<MessageElement> compare = Arrays.asList(
                MessageStatic.of("Woah: "),
                MessageField.of("player.healthBar(20)")
        );
        assertIterableEquals(compare, message.getParts());

        MessageField field = (MessageField) message.getParts().get(1);
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertEquals(20, sub.getParams().intAt(0));
        assertArrayEquals(new String[]{"20"}, sub.getParams().strArr());
    }

    @Test
    public void test_params_2() {
        CompiledMessage message = CompiledMessage.of("Woah: {player.healthBar(20,X)}");
        List<MessageElement> compare = Arrays.asList(
                MessageStatic.of("Woah: "),
                MessageField.of("player.healthBar(20,X)")
        );
        assertIterableEquals(compare, message.getParts());

        MessageField field = (MessageField) message.getParts().get(1);
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals(20, sub.getParams().intAt(0));
        assertEquals("X", sub.getParams().strAt(1));
        assertArrayEquals(new String[]{"20", "X"}, sub.getParams().strArr());
    }

    @Test
    public void test_params_3() {
        CompiledMessage message = CompiledMessage.of("Woah: {some,trash#player.healthBar(20,X)|def}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("Woah: "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.healthBar", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals(20, sub.getParams().intAt(0));
        assertEquals("X", sub.getParams().strAt(1));
        assertArrayEquals(new String[]{"20", "X"}, sub.getParams().strArr());
    }

    @Test
    public void test_params_4() {
        CompiledMessage message = CompiledMessage.of("Woah: {some,trash#player.healthBar(20,X,|)|def}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("Woah: "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.healthBar", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals(20, sub.getParams().intAt(0));
        assertEquals("X", sub.getParams().strAt(1));
        assertEquals("|", sub.getParams().strAt(2));
        assertArrayEquals(new String[]{"20", "X", "|"}, sub.getParams().strArr());
    }

    @Test
    public void test_params_5() {
        CompiledMessage message = CompiledMessage.of("Woah: {some,trash#player.healthBar((,20,X,|)|def}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("Woah: "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.healthBar", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals("(", sub.getParams().strAt(0));
        assertEquals(20, sub.getParams().intAt(1));
        assertEquals("X", sub.getParams().strAt(2));
        assertEquals("|", sub.getParams().strAt(3));
        assertArrayEquals(new String[]{"(", "20", "X", "|"}, sub.getParams().strArr());
    }
}
