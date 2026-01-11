package eu.okaeri.placeholderstest.reflect.argument;

import eu.okaeri.placeholders.reflect.argument.ArgumentParser;
import eu.okaeri.placeholders.reflect.argument.ArgumentType;
import eu.okaeri.placeholders.reflect.argument.ParsedArgument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ArgumentParser")
class ArgumentParserTest {

    @Nested
    @DisplayName("parse()")
    class Parse {

        @Test
        @DisplayName("parses string literal")
        void parsesStringLiteral() {
            ParsedArgument arg = ArgumentParser.parse("'hello'");
            assertThat(arg.getType()).isEqualTo(ArgumentType.STRING_LITERAL);
            assertThat(arg.getValue()).isEqualTo("hello");
            assertThat(arg.getJavaType()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("parses char literal")
        void parsesCharLiteral() {
            ParsedArgument arg = ArgumentParser.parse("c'x'");
            assertThat(arg.getType()).isEqualTo(ArgumentType.CHAR_LITERAL);
            assertThat(arg.getValue()).isEqualTo('x');
            assertThat(arg.getJavaType()).isEqualTo(Character.class);
        }

        @Test
        @DisplayName("parses integer")
        void parsesInteger() {
            ParsedArgument arg = ArgumentParser.parse("123");
            assertThat(arg.getType()).isEqualTo(ArgumentType.INTEGER);
            assertThat(arg.getValue()).isEqualTo(123);
            assertThat(arg.getJavaType()).isEqualTo(Integer.class);
        }

        @Test
        @DisplayName("parses negative integer")
        void parsesNegativeInteger() {
            ParsedArgument arg = ArgumentParser.parse("-456");
            assertThat(arg.getType()).isEqualTo(ArgumentType.INTEGER);
            assertThat(arg.getValue()).isEqualTo(-456);
        }

        @Test
        @DisplayName("parses long")
        void parsesLong() {
            ParsedArgument arg = ArgumentParser.parse("123L");
            assertThat(arg.getType()).isEqualTo(ArgumentType.LONG);
            assertThat(arg.getValue()).isEqualTo(123L);
            assertThat(arg.getJavaType()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("parses double")
        void parsesDouble() {
            ParsedArgument arg = ArgumentParser.parse("12.34");
            assertThat(arg.getType()).isEqualTo(ArgumentType.DOUBLE);
            assertThat(arg.getValue()).isEqualTo(12.34);
            assertThat(arg.getJavaType()).isEqualTo(Double.class);
        }

        @Test
        @DisplayName("parses float")
        void parsesFloat() {
            ParsedArgument arg = ArgumentParser.parse("12.34f");
            assertThat(arg.getType()).isEqualTo(ArgumentType.FLOAT);
            assertThat(arg.getValue()).isEqualTo(12.34f);
            assertThat(arg.getJavaType()).isEqualTo(Float.class);
        }

        @Test
        @DisplayName("parses boolean true")
        void parsesBooleanTrue() {
            ParsedArgument arg = ArgumentParser.parse("true");
            assertThat(arg.getType()).isEqualTo(ArgumentType.BOOLEAN);
            assertThat(arg.getValue()).isEqualTo(true);
            assertThat(arg.getJavaType()).isEqualTo(Boolean.class);
        }

        @Test
        @DisplayName("parses boolean false")
        void parsesBooleanFalse() {
            ParsedArgument arg = ArgumentParser.parse("false");
            assertThat(arg.getType()).isEqualTo(ArgumentType.BOOLEAN);
            assertThat(arg.getValue()).isEqualTo(false);
        }

        @Test
        @DisplayName("parses byte")
        void parsesByte() {
            ParsedArgument arg = ArgumentParser.parse("42b");
            assertThat(arg.getType()).isEqualTo(ArgumentType.BYTE);
            assertThat(arg.getValue()).isEqualTo((byte) 42);
            assertThat(arg.getJavaType()).isEqualTo(Byte.class);
        }

        @Test
        @DisplayName("parses short")
        void parsesShort() {
            ParsedArgument arg = ArgumentParser.parse("1000s");
            assertThat(arg.getType()).isEqualTo(ArgumentType.SHORT);
            assertThat(arg.getValue()).isEqualTo((short) 1000);
            assertThat(arg.getJavaType()).isEqualTo(Short.class);
        }

        @Test
        @DisplayName("parses context reference")
        void parsesContextRef() {
            ParsedArgument arg = ArgumentParser.parse("field.path");
            assertThat(arg.getType()).isEqualTo(ArgumentType.CONTEXT_REF);
            assertThat(arg.getValue()).isEqualTo("field.path");
        }

        @Test
        @DisplayName("parses unknown as string")
        void parsesUnknownAsString() {
            ParsedArgument arg = ArgumentParser.parse("someIdentifier");
            assertThat(arg.getType()).isEqualTo(ArgumentType.UNKNOWN);
            assertThat(arg.getValue()).isEqualTo("someIdentifier");
            assertThat(arg.getJavaType()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("preserves raw value")
        void preservesRawValue() {
            ParsedArgument arg = ArgumentParser.parse("'hello'");
            assertThat(arg.getRawValue()).isEqualTo("'hello'");

            arg = ArgumentParser.parse("123");
            assertThat(arg.getRawValue()).isEqualTo("123");
        }
    }

    @Nested
    @DisplayName("parseAll()")
    class ParseAll {

        @Test
        @DisplayName("parses multiple arguments")
        void parsesMultipleArguments() {
            String[] raws = {"'hello'", "123", "true"};
            ParsedArgument[] args = ArgumentParser.parseAll(raws);

            assertThat(args).hasSize(3);
            assertThat(args[0].getType()).isEqualTo(ArgumentType.STRING_LITERAL);
            assertThat(args[1].getType()).isEqualTo(ArgumentType.INTEGER);
            assertThat(args[2].getType()).isEqualTo(ArgumentType.BOOLEAN);
        }

        @Test
        @DisplayName("handles empty array")
        void handlesEmptyArray() {
            ParsedArgument[] args = ArgumentParser.parseAll(new String[0]);
            assertThat(args).isEmpty();
        }
    }

    @Nested
    @DisplayName("extractTypes()")
    class ExtractTypes {

        @Test
        @DisplayName("extracts Java types from parsed arguments")
        void extractsJavaTypes() {
            ParsedArgument[] args = ArgumentParser.parseAll(new String[]{"'hello'", "123", "12.34"});
            Class<?>[] types = ArgumentParser.extractTypes(args);

            assertThat(types).containsExactly(String.class, Integer.class, Double.class);
        }

        @Test
        @DisplayName("handles empty array")
        void handlesEmptyArray() {
            Class<?>[] types = ArgumentParser.extractTypes(new ParsedArgument[0]);
            assertThat(types).isEmpty();
        }
    }

    @Nested
    @DisplayName("extractValues()")
    class ExtractValues {

        @Test
        @DisplayName("extracts values from parsed arguments")
        void extractsValues() {
            ParsedArgument[] args = ArgumentParser.parseAll(new String[]{"'hello'", "123", "true"});
            Object[] values = ArgumentParser.extractValues(args);

            assertThat(values).containsExactly("hello", 123, true);
        }

        @Test
        @DisplayName("handles empty array")
        void handlesEmptyArray() {
            Object[] values = ArgumentParser.extractValues(new ParsedArgument[0]);
            assertThat(values).isEmpty();
        }
    }

    @Nested
    @DisplayName("parseAndResolve() without context")
    class ParseAndResolveWithoutContext {

        @Test
        @DisplayName("parses literals without context")
        void parsesLiteralsWithoutContext() {
            String[] raws = {"'hello'", "123"};
            ParsedArgument[] args = ArgumentParser.parseAndResolve(raws, null);

            assertThat(args).hasSize(2);
            assertThat(args[0].getValue()).isEqualTo("hello");
            assertThat(args[1].getValue()).isEqualTo(123);
        }

        @Test
        @DisplayName("context refs return raw string without context")
        void contextRefsReturnRawWithoutContext() {
            String[] raws = {"field.path"};
            ParsedArgument[] args = ArgumentParser.parseAndResolve(raws, null);

            assertThat(args[0].getValue()).isEqualTo("field.path");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("handles empty string literal")
        void handlesEmptyStringLiteral() {
            ParsedArgument arg = ArgumentParser.parse("''");
            assertThat(arg.getType()).isEqualTo(ArgumentType.STRING_LITERAL);
            assertThat(arg.getValue()).isEqualTo("");
        }

        @Test
        @DisplayName("handles string with special characters")
        void handlesStringWithSpecialChars() {
            ParsedArgument arg = ArgumentParser.parse("'hello world!'");
            assertThat(arg.getValue()).isEqualTo("hello world!");
        }

        @Test
        @DisplayName("handles zero values")
        void handlesZeroValues() {
            assertThat(ArgumentParser.parse("0").getValue()).isEqualTo(0);
            assertThat(ArgumentParser.parse("0L").getValue()).isEqualTo(0L);
            assertThat(ArgumentParser.parse("0.0").getValue()).isEqualTo(0.0);
            assertThat(ArgumentParser.parse("0.0f").getValue()).isEqualTo(0.0f);
        }

        @Test
        @DisplayName("handles max/min numeric values")
        void handlesMaxMinValues() {
            // Test large long value
            ParsedArgument arg = ArgumentParser.parse("9999999999L");
            assertThat(arg.getValue()).isEqualTo(9999999999L);
        }
    }
}
