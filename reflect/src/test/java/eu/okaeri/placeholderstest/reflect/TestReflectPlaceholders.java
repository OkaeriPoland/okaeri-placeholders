package eu.okaeri.placeholderstest.reflect;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.reflect.ReflectPlaceholders;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestReflectPlaceholders {

    private final Placeholders reflect = ReflectPlaceholders.create();
    private final Placeholders reflectWithDefaults = ReflectPlaceholders.create(true);

    @Test
    public void should_reflect_single_noarg_method() {
        assertEquals("JOHN", this.reflect.contextOf(CompiledMessage.of("{name.toUpperCase()}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void should_reflect_two_noarg_methods() {
        assertEquals("String", this.reflect.contextOf(CompiledMessage.of("{name.getClass().getSimpleName()}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void should_reflect_two_noarg_methods_and_builtin() {
        assertEquals("Java.lang.String", this.reflectWithDefaults.contextOf(CompiledMessage.of("{name.getClass().getName().capitalize}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void should_reflect_two_noarg_methods_and_2arg_method() {
        assertEquals("jeve.leng.String", this.reflect.contextOf(CompiledMessage.of("{name.getClass().getName().replace('a','e')}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void should_reflect_two_noarg_methods_and_2arg_method_with_context() {
        assertEquals("jeve.leng.String", this.reflect.contextOf(CompiledMessage.of("{name.getClass().getName().replace(from,to)}"))
            .with("name", "John")
            .with("from", "a")
            .with("to", "e")
            .apply());
    }

    @Test
    public void should_reflect_two_noarg_methods_and_2arg_method_with_ints() {
        assertEquals("lang", this.reflect.contextOf(CompiledMessage.of("{name.getClass().getName().substring(5,9)}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void should_reflect_static_method() {
        assertEquals(TestType.staticMethod(), this.reflect.contextOf(CompiledMessage.of("{test.staticMethod()}"))
            .with("test", TestType.class)
            .apply());
    }

    @Test
    public void should_reflect_static_field() {
        assertEquals(TestType.STATIC_STRING, this.reflect.contextOf(CompiledMessage.of("{test.STATIC_STRING}"))
            .with("test", TestType.class)
            .apply());
    }

    static final class TestType {
        static final String STATIC_STRING = "static string!";
        static String staticMethod() {
            return "static method!";
        }
    }
}
