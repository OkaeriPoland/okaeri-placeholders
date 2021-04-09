package eu.okaeri.placeholdersbukkittest;

import eu.okaeri.placeholders.bukkit.BukkitPlaceholders;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPlaceholderUtils {

    @Test
    public void test_healthbar_10_of_20() {
        String healthBar = BukkitPlaceholders.renderHealthBarWith(10, 20, "❤", "c", "7");
        assertEquals("§c❤❤❤❤❤❤❤❤❤❤§7❤❤❤❤❤❤❤❤❤❤", healthBar);
        System.out.println(healthBar);
    }

    @Test
    public void test_healthbar_15_of_20() {
        String healthBar = BukkitPlaceholders.renderHealthBarWith(15, 20, "❤", "c", "7");
        assertEquals("§c❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤§7❤❤❤❤❤", healthBar);
        System.out.println(healthBar);
    }

    @Test
    public void test_healthbar_0_of_20() {
        String healthBar = BukkitPlaceholders.renderHealthBarWith(0, 20, "❤", "c", "7");
        assertEquals("§7❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤", healthBar);
        System.out.println(healthBar);
    }

    @Test
    public void test_healthbar_20_of_20() {
        String healthBar = BukkitPlaceholders.renderHealthBarWith(20, 20, "❤", "c", "7");
        assertEquals("§c❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤❤", healthBar);
        System.out.println(healthBar);
    }
}
