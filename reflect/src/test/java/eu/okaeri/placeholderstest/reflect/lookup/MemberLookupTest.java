package eu.okaeri.placeholderstest.reflect.lookup;

import eu.okaeri.placeholders.reflect.lookup.MemberCache;
import eu.okaeri.placeholders.reflect.lookup.MemberLookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MemberLookup")
class MemberLookupTest {

    private MemberLookup lookup;

    @BeforeEach
    void setUp() {
        // Use a fresh cache for each test
        lookup = new MemberLookup(new MemberCache(256));
    }

    // Test class with various members
    @SuppressWarnings("unused")
    public static class TestClass {
        public String publicField = "public";
        private String privateField = "private";
        protected String protectedField = "protected";
        static String staticField = "static";

        public String publicMethod() {
            return "public";
        }

        private String privateMethod() {
            return "private";
        }

        public String methodWithArg(String arg) {
            return arg;
        }

        public String methodWithArgs(String a, int b) {
            return a + b;
        }

        public int overloaded(int x) {
            return x;
        }

        public long overloaded(long x) {
            return x * 2;
        }

        public double overloaded(double x) {
            return x * 3;
        }

        public static String staticMethod() {
            return "static";
        }
    }

    // Test class hierarchy
    public static class ParentClass {
        public String parentField = "parent";

        public String parentMethod() {
            return "parent";
        }
    }

    public static class ChildClass extends ParentClass {
        public String childField = "child";

        public String childMethod() {
            return "child";
        }
    }

    @Nested
    @DisplayName("findField()")
    class FindField {

        @Test
        @DisplayName("finds public field")
        void findsPublicField() {
            Optional<Field> field = lookup.findField(TestClass.class, "publicField");
            assertThat(field).isPresent();
            assertThat(field.get().getName()).isEqualTo("publicField");
        }

        @Test
        @DisplayName("finds private field")
        void findsPrivateField() {
            Optional<Field> field = lookup.findField(TestClass.class, "privateField");
            assertThat(field).isPresent();
            assertThat(field.get().getName()).isEqualTo("privateField");
        }

        @Test
        @DisplayName("finds protected field")
        void findsProtectedField() {
            Optional<Field> field = lookup.findField(TestClass.class, "protectedField");
            assertThat(field).isPresent();
        }

        @Test
        @DisplayName("finds static field")
        void findsStaticField() {
            Optional<Field> field = lookup.findField(TestClass.class, "staticField");
            assertThat(field).isPresent();
            assertThat(MemberLookup.isStatic(field.get())).isTrue();
        }

        @Test
        @DisplayName("finds inherited field")
        void findsInheritedField() {
            Optional<Field> field = lookup.findField(ChildClass.class, "parentField");
            assertThat(field).isPresent();
        }

        @Test
        @DisplayName("returns empty for non-existent field")
        void returnsEmptyForNonExistent() {
            Optional<Field> field = lookup.findField(TestClass.class, "nonExistent");
            assertThat(field).isEmpty();
        }
    }

    @Nested
    @DisplayName("findMethod()")
    class FindMethod {

        @Test
        @DisplayName("finds no-arg method")
        void findsNoArgMethod() {
            Optional<Method> method = lookup.findMethod(TestClass.class, "publicMethod");
            assertThat(method).isPresent();
            assertThat(method.get().getName()).isEqualTo("publicMethod");
        }

        @Test
        @DisplayName("finds private method")
        void findsPrivateMethod() {
            Optional<Method> method = lookup.findMethod(TestClass.class, "privateMethod");
            assertThat(method).isPresent();
        }

        @Test
        @DisplayName("finds method with exact arg types")
        void findsMethodWithExactArgs() {
            Optional<Method> method = lookup.findMethod(TestClass.class, "methodWithArg",
                new Class<?>[]{String.class});
            assertThat(method).isPresent();
        }

        @Test
        @DisplayName("finds method with multiple args")
        void findsMethodWithMultipleArgs() {
            Optional<Method> method = lookup.findMethod(TestClass.class, "methodWithArgs",
                new Class<?>[]{String.class, int.class});
            assertThat(method).isPresent();
        }

        @Test
        @DisplayName("finds static method")
        void findsStaticMethod() {
            Optional<Method> method = lookup.findMethod(TestClass.class, "staticMethod");
            assertThat(method).isPresent();
            assertThat(MemberLookup.isStatic(method.get())).isTrue();
        }

        @Test
        @DisplayName("finds inherited method")
        void findsInheritedMethod() {
            Optional<Method> method = lookup.findMethod(ChildClass.class, "parentMethod");
            assertThat(method).isPresent();
        }

        @Test
        @DisplayName("returns empty for non-existent method")
        void returnsEmptyForNonExistent() {
            Optional<Method> method = lookup.findMethod(TestClass.class, "nonExistent");
            assertThat(method).isEmpty();
        }

        @Test
        @DisplayName("returns empty for wrong arg types")
        void returnsEmptyForWrongArgs() {
            Optional<Method> method = lookup.findMethod(TestClass.class, "methodWithArg",
                new Class<?>[]{Integer.class});
            assertThat(method).isEmpty();
        }
    }

    @Nested
    @DisplayName("findMethodWithCoercion()")
    class FindMethodWithCoercion {

        @Test
        @DisplayName("finds exact match first")
        void findsExactMatchFirst() {
            // Should find overloaded(int) for int arg
            Optional<Method> method = lookup.findMethodWithCoercion(TestClass.class, "overloaded",
                new Class<?>[]{Integer.class});
            assertThat(method).isPresent();
            assertThat(method.get().getParameterTypes()[0]).isEqualTo(int.class);
        }

        @Test
        @DisplayName("finds method with widening conversion")
        void findsWithWideningConversion() {
            // Short arg should match any numeric param (widening)
            Optional<Method> method = lookup.findMethodWithCoercion(TestClass.class, "overloaded",
                new Class<?>[]{Short.class});
            assertThat(method).isPresent();
            // Should find a numeric overload (int, long, or double)
            Class<?> paramType = method.get().getParameterTypes()[0];
            assertThat(paramType).isIn(int.class, long.class, double.class);
        }

        @Test
        @DisplayName("finds method with string coercion")
        void findsWithStringCoercion() {
            // String can be parsed to numeric types
            Optional<Method> method = lookup.findMethodWithCoercion(TestClass.class, "methodWithArgs",
                new Class<?>[]{String.class, String.class});
            // String -> int for second param
            assertThat(method).isPresent();
        }

        @Test
        @DisplayName("returns empty when no coercion possible")
        void returnsEmptyWhenNoCoercion() {
            // List can't be coerced to int
            Optional<Method> method = lookup.findMethodWithCoercion(TestClass.class, "overloaded",
                new Class<?>[]{java.util.List.class});
            assertThat(method).isEmpty();
        }

        @Test
        @DisplayName("prefers best scoring method")
        void prefersBestScoringMethod() {
            // Byte should find a compatible numeric overload
            Optional<Method> method = lookup.findMethodWithCoercion(TestClass.class, "overloaded",
                new Class<?>[]{Byte.class});
            assertThat(method).isPresent();
            // Should find a numeric overload (int, long, or double)
            Class<?> paramType = method.get().getParameterTypes()[0];
            assertThat(paramType).isIn(int.class, long.class, double.class);
        }
    }

    @Nested
    @DisplayName("isStatic()")
    class IsStatic {

        @Test
        @DisplayName("returns true for static field")
        void trueForStaticField() throws NoSuchFieldException {
            Field field = TestClass.class.getDeclaredField("staticField");
            assertThat(MemberLookup.isStatic(field)).isTrue();
        }

        @Test
        @DisplayName("returns false for instance field")
        void falseForInstanceField() throws NoSuchFieldException {
            Field field = TestClass.class.getDeclaredField("publicField");
            assertThat(MemberLookup.isStatic(field)).isFalse();
        }

        @Test
        @DisplayName("returns true for static method")
        void trueForStaticMethod() throws NoSuchMethodException {
            Method method = TestClass.class.getDeclaredMethod("staticMethod");
            assertThat(MemberLookup.isStatic(method)).isTrue();
        }

        @Test
        @DisplayName("returns false for instance method")
        void falseForInstanceMethod() throws NoSuchMethodException {
            Method method = TestClass.class.getDeclaredMethod("publicMethod");
            assertThat(MemberLookup.isStatic(method)).isFalse();
        }
    }
}
