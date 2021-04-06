package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.message.part.MessageField;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestPlaceholderField {

    @Test
    public void test_simple_fields() {
        assertEquals("name", MessageField.of("name").getName());
        assertEquals("player_name", MessageField.of("player_name").getName());
        assertEquals("AttaCker", MessageField.of("AttaCker").getName());
        assertEquals("ĆŻĘŚĆ", MessageField.of("ĆŻĘŚĆ").getName());
    }

    @Test
    public void test_sub_single_1() {
        MessageField test = MessageField.of("player.name");
        assertEquals("player", test.getName());
        assertNotNull(test.getSub());
        assertEquals("name", test.getSub().getName());
        System.out.println(test);
    }

    @Test
    public void test_sub_single_2() {
        MessageField test = MessageField.of("sęnder.xxxxxx");
        assertEquals("sęnder", test.getName());
        assertNotNull(test.getSub());
        assertEquals("xxxxxx", test.getSub().getName());
        System.out.println(test);
    }

    @Test
    public void test_sub_double_1() {
        MessageField test = MessageField.of("player.inventory.name");
        assertEquals("player", test.getName());
        assertNotNull(test.getSub());
        assertEquals("inventory", test.getSub().getName());
        assertNotNull(test.getSub().getSub());
        assertEquals("name", test.getSub().getSub().getName());
        System.out.println(test);
    }

    @Test
    public void test_sub_triple_1() {
        MessageField test = MessageField.of("player.itemInHand.itemMeta.displayName");
        assertEquals("player", test.getName());
        assertNotNull(test.getSub());
        assertEquals("itemInHand", test.getSub().getName());
        assertNotNull(test.getSub().getSub());
        assertEquals("itemMeta", test.getSub().getSub().getName());
        assertNotNull(test.getSub().getSub().getSub());
        assertEquals("displayName", test.getSub().getSub().getSub().getName());
        System.out.println(test);
    }
}
