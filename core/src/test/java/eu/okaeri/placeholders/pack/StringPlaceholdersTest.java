package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("String placeholders")
@ExtendWith(PlaceholdersExtension.class)
class StringPlaceholdersTest {

    @Nested
    @DisplayName("Case conversion")
    class CaseConversion {

        @Test
        void shouldConvertToLowerCase(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.toLowerCase}"))
                .with("s", "HELLO WORLD")
                .apply();

            assertThat(result).isEqualTo("hello world");
        }

        @Test
        void shouldConvertToUpperCase(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.toUpperCase}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("HELLO WORLD");
        }

        @Test
        void shouldHandleMixedCase(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.toLowerCase}"))
                .with("s", "HeLLo WoRLD")
                .apply();

            assertThat(result).isEqualTo("hello world");
        }

        @Test
        void shouldHandleEmptyString(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.toUpperCase}"))
                .with("s", "")
                .apply();

            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("eu.okaeri.placeholders.fixture.TestData#unicodeStrings")
        void shouldHandleUnicodeInCaseConversion(String input, String description, Placeholders placeholders) {
            var message = CompiledMessage.of("{s.toLowerCase}");

            assertThatCode(() -> placeholders.contextOf(message)
                .with("s", input)
                .apply())
                .describedAs(description)
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Capitalization")
    class Capitalization {

        @Test
        void shouldCapitalizeFirstLetter(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.capitalize}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("Hello world");
        }

        @Test
        void shouldCapitalizeAlreadyCapitalized(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.capitalize}"))
                .with("s", "Hello World")
                .apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldCapitalizeFully(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.capitalizeFully}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldCapitalizeFullyWithMixedCase(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.capitalizeFully}"))
                .with("s", "hELLO wORLD")
                .apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldHandleEmptyStringCapitalize(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.capitalize}"))
                .with("s", "")
                .apply();

            assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleSingleCharCapitalize(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.capitalize}"))
                .with("s", "h")
                .apply();

            assertThat(result).isEqualTo("H");
        }
    }

    @Nested
    @DisplayName("String replacement")
    class StringReplacement {

        @Test
        void shouldReplaceCharacter(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.replace(_,-)}"))
                .with("s", "hello_world")
                .apply();

            assertThat(result).isEqualTo("hello-world");
        }

        @Test
        void shouldReplaceMultipleOccurrences(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.replace(_,-)}"))
                .with("s", "a_b_c_d")
                .apply();

            assertThat(result).isEqualTo("a-b-c-d");
        }

        @Test
        void shouldHandleNoMatch(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.replace(x,y)}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("hello world");
        }

        @Test
        void shouldReplaceWithEmpty(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.replace(_,)}"))
                .with("s", "hello_world")
                .apply();

            assertThat(result).isEqualTo("helloworld");
        }

        @ParameterizedTest
        @CsvSource({
            "hello_world, _, -, hello-world",
            "a.b.c, ., /, a/b/c"
        })
        void shouldReplaceVariousPatterns(String input, String from, String to, String expected, Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.replace(" + from + "," + to + ")}"))
                .with("s", input)
                .apply();

            assertThat(result).isEqualTo(expected);
        }

        @Test
        void shouldReplaceWithSpace(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.replace(_, )}"))
                .with("s", "snake_case_name")
                .apply();

            assertThat(result).isEqualTo("snake case name");
        }
    }

    @Nested
    @DisplayName("Prepend and append")
    class PrependAppend {

        @Test
        void shouldPrepend(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.prepend(prefix_)}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("prefix_value");
        }

        @Test
        void shouldAppend(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.append(_suffix)}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("value_suffix");
        }

        @Test
        void shouldChainPrependAndAppend(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.prepend([).append(])}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("[value]");
        }

        @Test
        void shouldHandleEmptyPrepend(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.prepend()}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("value");
        }
    }

    @Nested
    @DisplayName("Chained operations")
    class ChainedOperations {

        @Test
        void shouldChainLowercaseAndCapitalize(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.toLowerCase.capitalize}"))
                .with("s", "HELLO WORLD")
                .apply();

            assertThat(result).isEqualTo("Hello world");
        }

        @Test
        void shouldChainReplaceAndUppercase(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.replace(_,-).toUpperCase}"))
                .with("s", "hello_world")
                .apply();

            assertThat(result).isEqualTo("HELLO-WORLD");
        }

        @Test
        void shouldChainMultipleOperations(Placeholders placeholders) {
            // Chain: lowercase -> replace underscore with dash -> uppercase
            // Note: replace with space is not supported - library doesn't strip quotes from params
            var result = placeholders.contextOf(CompiledMessage.of("{s.toLowerCase.replace(_,-).toUpperCase}"))
                .with("s", "HELLO_WORLD")
                .apply();

            assertThat(result).isEqualTo("HELLO-WORLD");
        }
    }

    @Nested
    @DisplayName("Unicode support")
    class UnicodeSupport {

        @Test
        void shouldHandlePolishCharacters(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.toUpperCase}"))
                .with("s", "cześć")
                .apply();

            assertThat(result).isEqualTo("CZEŚĆ");
        }

        @Test
        void shouldHandleCyrillicCharacters(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.toLowerCase}"))
                .with("s", "ПРИВЕТ")
                .apply();

            assertThat(result).isEqualTo("привет");
        }

        @Test
        void shouldCapitalizeUnicode(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{s.capitalize}"))
                .with("s", "cześć świecie")
                .apply();

            assertThat(result).isEqualTo("Cześć świecie");
        }
    }
}
