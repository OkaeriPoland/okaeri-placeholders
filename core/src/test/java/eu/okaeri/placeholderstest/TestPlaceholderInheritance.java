package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPlaceholderInheritance {

    protected static class X {
        public String getName() {
            return "John";
        }
    }

    protected static class Y extends X {
        public String getSurname() {
            return "Paul";
        }
    }

    protected static class Z extends Y {
    }

    Placeholders placeholders;

    @BeforeEach
    public void setup() {
        this.placeholders = Placeholders.create()
            .registerPlaceholder(X.class, "name", (e, a, o) -> e.getName())
            .registerPlaceholder(Y.class, "surname", (e, a, o) -> e.getSurname());
    }

    @Test
    public void test_x_name_works_from_x() {
        CompiledMessage message = CompiledMessage.of("hix {var.name}");
        String textY = this.placeholders.contextOf(message).with("var", new X()).apply();
        assertEquals("hix John", textY);
    }

    @Test
    public void test_x_name_works_from_y_which_has_own_placeholders() {
        CompiledMessage message = CompiledMessage.of("hiy {var.name} {var.surname}");
        String textY = this.placeholders.contextOf(message).with("var", new Y()).apply();
        assertEquals("hiy John Paul", textY);
    }

    @Test
    public void test_x_name_works_from_z() {
        CompiledMessage message = CompiledMessage.of("hiz {var.name}");
        String textY = this.placeholders.contextOf(message).with("var", new Z()).apply();
        assertEquals("hiz John", textY);
    }
}
