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

    @Test
    public void test_params_6() {
        CompiledMessage message = CompiledMessage.of("Woah: {some,trash#player.healthBar((,20, ,|)|def}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("Woah: "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.healthBar", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals("(", sub.getParams().strAt(0));
        assertEquals(20, sub.getParams().intAt(1));
        assertEquals(" ", sub.getParams().strAt(2));
        assertEquals("|", sub.getParams().strAt(3));
        assertArrayEquals(new String[]{"(", "20", " ", "|"}, sub.getParams().strArr());
    }

    @Test
    public void test_params_7() {
        CompiledMessage message = CompiledMessage.of("Woah: {some,trash#player.healthBar((,20, ,|, ))|def}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("Woah: "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.healthBar", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals("(", sub.getParams().strAt(0));
        assertEquals(20, sub.getParams().intAt(1));
        assertEquals(" ", sub.getParams().strAt(2));
        assertEquals("|", sub.getParams().strAt(3));
        assertEquals(" )", sub.getParams().strAt(4));
        assertArrayEquals(new String[]{"(", "20", " ", "|", " )"}, sub.getParams().strArr());
    }

    @Test
    public void test_params_8() {
        CompiledMessage message = CompiledMessage.of("Woah: {some,trash#player.healthBar((,20,,|,)\\, )|def}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("Woah: "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.healthBar", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals("(", sub.getParams().strAt(0));
        assertEquals(20, sub.getParams().intAt(1));
        assertEquals("", sub.getParams().strAt(2));
        assertEquals("|", sub.getParams().strAt(3));
        assertEquals("), ", sub.getParams().strAt(4));
        assertArrayEquals(new String[]{"(", "20", "", "|", "), "}, sub.getParams().strArr());
    }

    @Test
    public void test_params_9() {
        CompiledMessage message = CompiledMessage.of("Hi: {player.kill()}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("Hi: "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.kill", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertNotNull(sub.getParams());

        assertEquals("", sub.getParams().strAt(0));
        assertArrayEquals(new String[]{""}, sub.getParams().strArr());
    }

    @Test
    public void test_params_10() {
        CompiledMessage message = CompiledMessage.of("?? {player.papi(okapibridge_player.name)} ???");

        assertEquals(3, message.getParts().size());
        assertEquals(MessageStatic.of("?? "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.papi", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertArrayEquals(new String[]{"okapibridge_player.name"}, sub.getParams().strArr());

        assertEquals(MessageStatic.of(" ???"), message.getParts().get(2));
    }

    @Test
    public void test_params_11() {
        CompiledMessage message = CompiledMessage.of("?? {player.papi(okapibridge_player.papi(okapibridge_player.name))} ???");

        assertEquals(3, message.getParts().size());
        assertEquals(MessageStatic.of("?? "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("player.papi", field.getLastSubPath());
        assertNotNull(field.getSub());

        MessageField sub = field.getSub();
        assertArrayEquals(new String[]{"okapibridge_player.papi(okapibridge_player.name)"}, sub.getParams().strArr());

        assertEquals(MessageStatic.of(" ???"), message.getParts().get(2));
    }

    @Test
    public void test_params_12() {
        CompiledMessage message = CompiledMessage.of("yikes  {a.b.c(1,2).f.g(33(),.4,.()5)}");

        assertEquals(2, message.getParts().size());
        assertEquals(MessageStatic.of("yikes  "), message.getParts().get(0));

        MessageField field = (MessageField) message.getParts().get(1);
        assertEquals("a.b.c.f.g", field.getLastSubPath());

        assertNotNull(field.getSub());
        assertNotNull(field.getSub().getSub());
        assertArrayEquals(new String[]{"1", "2"}, field.getSub().getSub().getParams().strArr());

        assertNotNull(field.getSub().getSub().getSub());
        assertNotNull(field.getSub().getSub().getSub().getSub());
        assertArrayEquals(new String[]{"33()", ".4", ".()5"}, field.getSub().getSub().getSub().getSub().getParams().strArr());
    }
}
