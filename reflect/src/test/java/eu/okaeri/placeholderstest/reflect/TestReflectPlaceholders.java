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
    public void test_method_noarg_single() {
        assertEquals("JOHN", this.reflect.contextOf(CompiledMessage.of("{name.toUpperCase()}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void test_method_noarg_double() {
        assertEquals("String", this.reflect.contextOf(CompiledMessage.of("{name.getClass().getSimpleName()}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void test_method_noarg_double_builtin() {
        assertEquals("Java.lang.String", this.reflectWithDefaults.contextOf(CompiledMessage.of("{name.getClass().getName().capitalize}"))
            .with("name", "John")
            .apply());
    }

    @Test
    public void test_method_static() {
        assertEquals(TestType.staticMethod(), this.reflect.contextOf(CompiledMessage.of("{test.staticMethod()}"))
            .with("test", TestType.class)
            .apply());
    }

//    @Test
//    public void test_method_2arg() {
//        assertEquals("jeve.leng.String", this.reflect.contextOf(CompiledMessage.of("{name.getClass().getName().replace(a,e)}"))
//            .with("name", "John")
//            .apply());
//    }

    @Test
    public void test_field_static() {
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
