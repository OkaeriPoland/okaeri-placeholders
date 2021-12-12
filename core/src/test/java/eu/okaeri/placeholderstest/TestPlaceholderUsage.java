package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPlaceholderUsage {

    @Test
    public void test_simple_message_1() {

        PlaceholderContext context = PlaceholderContext.of(CompiledMessage.of("Hello {who}! How are you {when}? I'm {how}."))
            .with("who", "World")
            .with("when", "today")
            .with("how", "ok");
        String test = context.apply();
        assertEquals("Hello World! How are you today? I'm ok.", test);

        System.out.println(context);
        System.out.println(test);
    }

    @Test
    public void test_simple_message_2() {

        PlaceholderContext context = PlaceholderContext.of(CompiledMessage.of("Hola {who}! ¿Cómo estás {when}? Estoy {how}."))
            .with("who", "Mundo")
            .with("when", "hoy")
            .with("how", "bien");
        String test = context.apply();
        assertEquals("Hola Mundo! ¿Cómo estás hoy? Estoy bien.", test);

        System.out.println(context);
        System.out.println(test);
    }
}
