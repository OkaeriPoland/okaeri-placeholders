package eu.okaeri.placeholderstest.reflect.invoke;

import eu.okaeri.placeholders.reflect.exception.ReflectException;
import eu.okaeri.placeholders.reflect.invoke.FieldInvoker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FieldInvoker")
class FieldInvokerTest {

    @SuppressWarnings("unused")
    public static class TestClass {
        public String publicField = "public";
        private String privateField = "private";
        protected String protectedField = "protected";
        String packageField = "package";

        public static String staticField = "static";
        private static String privateStaticField = "privateStatic";

        public final String finalField = "final";
        public int primitiveField = 42;
        public Integer wrapperField = 100;
        public String nullField = null;
    }

    private final TestClass instance = new TestClass();

    @Nested
    @DisplayName("getValue()")
    class GetValue {

        @Test
        @DisplayName("gets public field value")
        void getsPublicField() throws Exception {
            Field field = TestClass.class.getField("publicField");
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo("public");
        }

        @Test
        @DisplayName("gets private field value when accessible")
        void getsPrivateField() throws Exception {
            Field field = TestClass.class.getDeclaredField("privateField");
            field.setAccessible(true);
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo("private");
        }

        @Test
        @DisplayName("gets protected field value")
        void getsProtectedField() throws Exception {
            Field field = TestClass.class.getDeclaredField("protectedField");
            field.setAccessible(true);
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo("protected");
        }

        @Test
        @DisplayName("gets package-private field value")
        void getsPackageField() throws Exception {
            Field field = TestClass.class.getDeclaredField("packageField");
            field.setAccessible(true);
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo("package");
        }

        @Test
        @DisplayName("gets primitive field value")
        void getsPrimitiveField() throws Exception {
            Field field = TestClass.class.getField("primitiveField");
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo(42);
        }

        @Test
        @DisplayName("gets wrapper field value")
        void getsWrapperField() throws Exception {
            Field field = TestClass.class.getField("wrapperField");
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo(100);
        }

        @Test
        @DisplayName("gets null field value")
        void getsNullField() throws Exception {
            Field field = TestClass.class.getField("nullField");
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("gets final field value")
        void getsFinalField() throws Exception {
            Field field = TestClass.class.getField("finalField");
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo("final");
        }
    }

    @Nested
    @DisplayName("getValue() static fields")
    class StaticFields {

        @Test
        @DisplayName("gets static field with null target")
        void getsStaticFieldNullTarget() throws Exception {
            Field field = TestClass.class.getField("staticField");
            Object result = FieldInvoker.getValue(field, null);
            assertThat(result).isEqualTo("static");
        }

        @Test
        @DisplayName("gets static field with instance target (ignores target)")
        void getsStaticFieldWithTarget() throws Exception {
            Field field = TestClass.class.getField("staticField");
            Object result = FieldInvoker.getValue(field, instance);
            assertThat(result).isEqualTo("static");
        }

        @Test
        @DisplayName("gets private static field when accessible")
        void getsPrivateStaticField() throws Exception {
            Field field = TestClass.class.getDeclaredField("privateStaticField");
            field.setAccessible(true);
            Object result = FieldInvoker.getValue(field, null);
            assertThat(result).isEqualTo("privateStatic");
        }
    }

    @Nested
    @DisplayName("getValue() error handling")
    class GetValueErrors {

        @Test
        @DisplayName("throws ReflectException for null target on instance field")
        void throwsForNullTarget() throws Exception {
            Field field = TestClass.class.getField("publicField");
            assertThatThrownBy(() -> FieldInvoker.getValue(field, null))
                .isInstanceOf(ReflectException.class)
                .hasMessageContaining("publicField")
                .hasCauseInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("setValue()")
    class SetValue {

        @Test
        @DisplayName("sets public field value")
        void setsPublicField() throws Exception {
            TestClass obj = new TestClass();
            Field field = TestClass.class.getField("publicField");
            FieldInvoker.setValue(field, obj, "newValue");
            assertThat(obj.publicField).isEqualTo("newValue");
        }

        @Test
        @DisplayName("sets private field value when accessible")
        void setsPrivateField() throws Exception {
            TestClass obj = new TestClass();
            Field field = TestClass.class.getDeclaredField("privateField");
            field.setAccessible(true);
            FieldInvoker.setValue(field, obj, "newPrivate");
            assertThat(field.get(obj)).isEqualTo("newPrivate");
        }

        @Test
        @DisplayName("sets primitive field value")
        void setsPrimitiveField() throws Exception {
            TestClass obj = new TestClass();
            Field field = TestClass.class.getField("primitiveField");
            FieldInvoker.setValue(field, obj, 999);
            assertThat(obj.primitiveField).isEqualTo(999);
        }

        @Test
        @DisplayName("sets field to null")
        void setsFieldToNull() throws Exception {
            TestClass obj = new TestClass();
            Field field = TestClass.class.getField("publicField");
            FieldInvoker.setValue(field, obj, null);
            assertThat(obj.publicField).isNull();
        }
    }

    @Nested
    @DisplayName("setValue() static fields")
    class SetValueStaticFields {

        @Test
        @DisplayName("sets static field with null target")
        void setsStaticFieldNullTarget() throws Exception {
            String originalValue = TestClass.staticField;
            try {
                Field field = TestClass.class.getField("staticField");
                FieldInvoker.setValue(field, null, "newStatic");
                assertThat(TestClass.staticField).isEqualTo("newStatic");
            } finally {
                TestClass.staticField = originalValue;
            }
        }
    }

    @Nested
    @DisplayName("setValue() error handling")
    class SetValueErrors {

        @Test
        @DisplayName("throws ReflectException for null target on instance field")
        void throwsForNullTarget() throws Exception {
            Field field = TestClass.class.getField("publicField");
            assertThatThrownBy(() -> FieldInvoker.setValue(field, null, "value"))
                .isInstanceOf(ReflectException.class)
                .hasMessageContaining("publicField")
                .hasCauseInstanceOf(NullPointerException.class);
        }
    }
}
