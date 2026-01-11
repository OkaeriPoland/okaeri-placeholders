package eu.okaeri.placeholderstest.reflect.argument;

import eu.okaeri.placeholders.reflect.argument.ArgumentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ArgumentType")
class ArgumentTypeTest {

    @Nested
    @DisplayName("detect()")
    class Detect {

        @Test
        @DisplayName("detects char literal c'x'")
        void detectsCharLiteral() {
            assertThat(ArgumentType.detect("c'x'")).isEqualTo(ArgumentType.CHAR_LITERAL);
            assertThat(ArgumentType.detect("c'A'")).isEqualTo(ArgumentType.CHAR_LITERAL);
            assertThat(ArgumentType.detect("c' '")).isEqualTo(ArgumentType.CHAR_LITERAL);
        }

        @Test
        @DisplayName("detects string literal 'hello'")
        void detectsStringLiteral() {
            assertThat(ArgumentType.detect("'hello'")).isEqualTo(ArgumentType.STRING_LITERAL);
            assertThat(ArgumentType.detect("''")).isEqualTo(ArgumentType.STRING_LITERAL);
            assertThat(ArgumentType.detect("'with spaces'")).isEqualTo(ArgumentType.STRING_LITERAL);
        }

        @ParameterizedTest
        @ValueSource(strings = {"true", "false"})
        @DisplayName("detects boolean values")
        void detectsBoolean(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.BOOLEAN);
        }

        @ParameterizedTest
        @ValueSource(strings = {"123L", "-456L", "0L", "9999999999L"})
        @DisplayName("detects long values with L suffix")
        void detectsLong(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.LONG);
        }

        @ParameterizedTest
        @ValueSource(strings = {"12.34f", "-56.78f", "0.0f"})
        @DisplayName("detects float values with f suffix")
        void detectsFloat(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.FLOAT);
        }

        @ParameterizedTest
        @ValueSource(strings = {"12.34", "-56.78", "0.0"})
        @DisplayName("detects double values")
        void detectsDouble(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.DOUBLE);
        }

        @ParameterizedTest
        @ValueSource(strings = {"12b", "-5b", "0b", "127b"})
        @DisplayName("detects byte values with b suffix")
        void detectsByte(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.BYTE);
        }

        @ParameterizedTest
        @ValueSource(strings = {"12s", "-5s", "0s", "32767s"})
        @DisplayName("detects short values with s suffix")
        void detectsShort(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.SHORT);
        }

        @ParameterizedTest
        @ValueSource(strings = {"123", "-456", "0", "999999"})
        @DisplayName("detects integer values")
        void detectsInteger(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.INTEGER);
        }

        @ParameterizedTest
        @ValueSource(strings = {"field.path", "obj.method()", "a.b.c", "x()"})
        @DisplayName("detects context references with dots or parens")
        void detectsContextRef(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.CONTEXT_REF);
        }

        @ParameterizedTest
        @ValueSource(strings = {"fieldName", "someVar", "x", "ABC"})
        @DisplayName("detects unknown (simple identifiers)")
        void detectsUnknown(String value) {
            assertThat(ArgumentType.detect(value)).isEqualTo(ArgumentType.UNKNOWN);
        }
    }

    @Nested
    @DisplayName("parse()")
    class Parse {

        @Test
        @DisplayName("parses char literal to Character")
        void parsesCharLiteral() {
            assertThat(ArgumentType.CHAR_LITERAL.parse("c'x'")).isEqualTo('x');
            assertThat(ArgumentType.CHAR_LITERAL.parse("c'A'")).isEqualTo('A');
        }

        @Test
        @DisplayName("parses string literal without quotes")
        void parsesStringLiteral() {
            assertThat(ArgumentType.STRING_LITERAL.parse("'hello'")).isEqualTo("hello");
            assertThat(ArgumentType.STRING_LITERAL.parse("''")).isEqualTo("");
            assertThat(ArgumentType.STRING_LITERAL.parse("'with spaces'")).isEqualTo("with spaces");
        }

        @ParameterizedTest
        @CsvSource({"true,true", "false,false"})
        @DisplayName("parses boolean values")
        void parsesBoolean(String input, boolean expected) {
            assertThat(ArgumentType.BOOLEAN.parse(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("parses long values")
        void parsesLong() {
            assertThat(ArgumentType.LONG.parse("123L")).isEqualTo(123L);
            assertThat(ArgumentType.LONG.parse("-456L")).isEqualTo(-456L);
        }

        @Test
        @DisplayName("parses float values")
        void parsesFloat() {
            assertThat(ArgumentType.FLOAT.parse("12.34f")).isEqualTo(12.34f);
            assertThat(ArgumentType.FLOAT.parse("-56.78f")).isEqualTo(-56.78f);
        }

        @Test
        @DisplayName("parses double values")
        void parsesDouble() {
            assertThat(ArgumentType.DOUBLE.parse("12.34")).isEqualTo(12.34);
            assertThat(ArgumentType.DOUBLE.parse("-56.78")).isEqualTo(-56.78);
        }

        @Test
        @DisplayName("parses byte values")
        void parsesByte() {
            assertThat(ArgumentType.BYTE.parse("12b")).isEqualTo((byte) 12);
            assertThat(ArgumentType.BYTE.parse("-5b")).isEqualTo((byte) -5);
        }

        @Test
        @DisplayName("parses short values")
        void parsesShort() {
            assertThat(ArgumentType.SHORT.parse("12s")).isEqualTo((short) 12);
            assertThat(ArgumentType.SHORT.parse("-5s")).isEqualTo((short) -5);
        }

        @Test
        @DisplayName("parses integer values")
        void parsesInteger() {
            assertThat(ArgumentType.INTEGER.parse("123")).isEqualTo(123);
            assertThat(ArgumentType.INTEGER.parse("-456")).isEqualTo(-456);
        }

        @Test
        @DisplayName("context ref returns raw string")
        void contextRefReturnsRaw() {
            assertThat(ArgumentType.CONTEXT_REF.parse("field.path")).isEqualTo("field.path");
        }

        @Test
        @DisplayName("unknown returns raw string")
        void unknownReturnsRaw() {
            assertThat(ArgumentType.UNKNOWN.parse("someVar")).isEqualTo("someVar");
        }
    }

    @Nested
    @DisplayName("getJavaType()")
    class JavaType {

        @Test
        @DisplayName("returns correct Java types")
        void returnsCorrectTypes() {
            assertThat(ArgumentType.CHAR_LITERAL.getJavaType()).isEqualTo(Character.class);
            assertThat(ArgumentType.STRING_LITERAL.getJavaType()).isEqualTo(String.class);
            assertThat(ArgumentType.BOOLEAN.getJavaType()).isEqualTo(Boolean.class);
            assertThat(ArgumentType.INTEGER.getJavaType()).isEqualTo(Integer.class);
            assertThat(ArgumentType.LONG.getJavaType()).isEqualTo(Long.class);
            assertThat(ArgumentType.DOUBLE.getJavaType()).isEqualTo(Double.class);
            assertThat(ArgumentType.FLOAT.getJavaType()).isEqualTo(Float.class);
            assertThat(ArgumentType.BYTE.getJavaType()).isEqualTo(Byte.class);
            assertThat(ArgumentType.SHORT.getJavaType()).isEqualTo(Short.class);
            assertThat(ArgumentType.CONTEXT_REF.getJavaType()).isEqualTo(Object.class);
            assertThat(ArgumentType.UNKNOWN.getJavaType()).isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("Detection order priority")
    class DetectionOrder {

        @Test
        @DisplayName("c'x' detected as CHAR before STRING")
        void charBeforeString() {
            // c'x' should be CHAR_LITERAL, not STRING_LITERAL
            assertThat(ArgumentType.detect("c'a'")).isEqualTo(ArgumentType.CHAR_LITERAL);
        }

        @Test
        @DisplayName("123L detected as LONG before INTEGER")
        void longBeforeInteger() {
            assertThat(ArgumentType.detect("123L")).isEqualTo(ArgumentType.LONG);
        }

        @Test
        @DisplayName("12.34f detected as FLOAT before DOUBLE")
        void floatBeforeDouble() {
            assertThat(ArgumentType.detect("12.34f")).isEqualTo(ArgumentType.FLOAT);
        }

        @Test
        @DisplayName("123b detected as BYTE before INTEGER")
        void byteBeforeInteger() {
            assertThat(ArgumentType.detect("123b")).isEqualTo(ArgumentType.BYTE);
        }

        @Test
        @DisplayName("123s detected as SHORT before INTEGER")
        void shortBeforeInteger() {
            assertThat(ArgumentType.detect("123s")).isEqualTo(ArgumentType.SHORT);
        }
    }
}
