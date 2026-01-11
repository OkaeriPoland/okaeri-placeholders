package eu.okaeri.placeholderstest.reflect.invoke;

import eu.okaeri.placeholders.reflect.exception.ReflectException;
import eu.okaeri.placeholders.reflect.invoke.MethodInvoker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MethodInvoker")
class MethodInvokerTest {

    @SuppressWarnings("unused")
    public static class TestClass {
        public String noArgMethod() {
            return "noArg";
        }

        public String oneArgMethod(String arg) {
            return "oneArg:" + arg;
        }

        public String twoArgMethod(String a, int b) {
            return a + ":" + b;
        }

        public int numericMethod(int x) {
            return x * 2;
        }

        public long wideningMethod(long x) {
            return x * 3;
        }

        public String overloaded(String s) {
            return "string:" + s;
        }

        public String overloaded(int n) {
            return "int:" + n;
        }

        public static String staticMethod() {
            return "static";
        }

        public static String staticMethodWithArg(String arg) {
            return "static:" + arg;
        }

        public void voidMethod() {
            // does nothing
        }

        public String throwingMethod() {
            throw new IllegalStateException("test exception");
        }

        private String privateMethod() {
            return "private";
        }
    }

    private final TestClass instance = new TestClass();

    @Nested
    @DisplayName("invoke() no-arg methods")
    class NoArgMethods {

        @Test
        @DisplayName("invokes no-arg method")
        void invokesNoArgMethod() throws Exception {
            Method method = TestClass.class.getMethod("noArgMethod");
            Object result = MethodInvoker.invoke(method, instance);
            assertThat(result).isEqualTo("noArg");
        }

        @Test
        @DisplayName("invokes void method returns null")
        void invokesVoidMethod() throws Exception {
            Method method = TestClass.class.getMethod("voidMethod");
            Object result = MethodInvoker.invoke(method, instance);
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("invoke() with arguments")
    class WithArguments {

        @Test
        @DisplayName("invokes method with string argument")
        void invokesWithStringArg() throws Exception {
            Method method = TestClass.class.getMethod("oneArgMethod", String.class);
            Object result = MethodInvoker.invoke(method, instance, new Object[]{"hello"});
            assertThat(result).isEqualTo("oneArg:hello");
        }

        @Test
        @DisplayName("invokes method with multiple arguments")
        void invokesWithMultipleArgs() throws Exception {
            Method method = TestClass.class.getMethod("twoArgMethod", String.class, int.class);
            Object result = MethodInvoker.invoke(method, instance, new Object[]{"test", 42});
            assertThat(result).isEqualTo("test:42");
        }

        @Test
        @DisplayName("invokes method with numeric argument")
        void invokesWithNumericArg() throws Exception {
            Method method = TestClass.class.getMethod("numericMethod", int.class);
            Object result = MethodInvoker.invoke(method, instance, new Object[]{5});
            assertThat(result).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("invoke() with type coercion")
    class WithTypeCoercion {

        @Test
        @DisplayName("coerces Integer to int parameter")
        void coercesIntegerToInt() throws Exception {
            Method method = TestClass.class.getMethod("numericMethod", int.class);
            Object result = MethodInvoker.invoke(method, instance, new Object[]{Integer.valueOf(7)});
            assertThat(result).isEqualTo(14);
        }

        @Test
        @DisplayName("coerces int to long parameter (widening)")
        void coercesIntToLong() throws Exception {
            Method method = TestClass.class.getMethod("wideningMethod", long.class);
            Object result = MethodInvoker.invoke(method, instance, new Object[]{10});
            assertThat(result).isEqualTo(30L);
        }

        @Test
        @DisplayName("coerces String to int when possible")
        void coercesStringToInt() throws Exception {
            Method method = TestClass.class.getMethod("numericMethod", int.class);
            Object result = MethodInvoker.invoke(method, instance, new Object[]{"25"});
            assertThat(result).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("invoke() static methods")
    class StaticMethods {

        @Test
        @DisplayName("invokes static method with null target")
        void invokesStaticWithNullTarget() throws Exception {
            Method method = TestClass.class.getMethod("staticMethod");
            Object result = MethodInvoker.invoke(method, null);
            assertThat(result).isEqualTo("static");
        }

        @Test
        @DisplayName("invokes static method with argument")
        void invokesStaticWithArg() throws Exception {
            Method method = TestClass.class.getMethod("staticMethodWithArg", String.class);
            Object result = MethodInvoker.invoke(method, null, new Object[]{"test"});
            assertThat(result).isEqualTo("static:test");
        }

        @Test
        @DisplayName("invokes static method ignoring instance target")
        void invokesStaticIgnoringTarget() throws Exception {
            Method method = TestClass.class.getMethod("staticMethod");
            // Even with a non-null target, static method should work
            Object result = MethodInvoker.invoke(method, instance);
            assertThat(result).isEqualTo("static");
        }
    }

    @Nested
    @DisplayName("invoke() private methods")
    class PrivateMethods {

        @Test
        @DisplayName("invokes private method when accessible")
        void invokesPrivateMethod() throws Exception {
            Method method = TestClass.class.getDeclaredMethod("privateMethod");
            method.setAccessible(true);
            Object result = MethodInvoker.invoke(method, instance);
            assertThat(result).isEqualTo("private");
        }
    }

    @Nested
    @DisplayName("invoke() error handling")
    class ErrorHandling {

        @Test
        @DisplayName("throws ReflectException for null target on instance method")
        void throwsForNullTarget() throws Exception {
            Method method = TestClass.class.getMethod("noArgMethod");
            assertThatThrownBy(() -> MethodInvoker.invoke(method, null))
                .isInstanceOf(ReflectException.class)
                .hasMessageContaining("noArgMethod")
                .hasCauseInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("wraps exceptions from invoked method")
        void wrapsInvocationExceptions() throws Exception {
            Method method = TestClass.class.getMethod("throwingMethod");
            assertThatThrownBy(() -> MethodInvoker.invoke(method, instance))
                .isInstanceOf(ReflectException.class)
                .hasMessageContaining("throwingMethod")
                .hasCauseInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("throws for argument count mismatch")
        void throwsForArgumentCountMismatch() throws Exception {
            Method method = TestClass.class.getMethod("twoArgMethod", String.class, int.class);
            assertThatThrownBy(() -> MethodInvoker.invoke(method, instance, new Object[]{"only one"}))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("invoke() null arguments")
    class NullArguments {

        @Test
        @DisplayName("handles null argument value")
        void handlesNullArgument() throws Exception {
            Method method = TestClass.class.getMethod("oneArgMethod", String.class);
            Object result = MethodInvoker.invoke(method, instance, new Object[]{null});
            assertThat(result).isEqualTo("oneArg:null");
        }

        @Test
        @DisplayName("handles null args array")
        void handlesNullArgsArray() throws Exception {
            Method method = TestClass.class.getMethod("noArgMethod");
            Object result = MethodInvoker.invoke(method, instance, null);
            assertThat(result).isEqualTo("noArg");
        }

        @Test
        @DisplayName("handles empty args array")
        void handlesEmptyArgsArray() throws Exception {
            Method method = TestClass.class.getMethod("noArgMethod");
            Object result = MethodInvoker.invoke(method, instance, new Object[0]);
            assertThat(result).isEqualTo("noArg");
        }
    }
}
