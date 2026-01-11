package eu.okaeri.placeholders.metadata;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Boolean translation metadata")
@ExtendWith(PlaceholdersExtension.class)
class BooleanTranslationTest {

    @Nested
    @DisplayName("Basic translation")
    class BasicTranslation {

        @Test
        void shouldTranslateTrueToFirstOption(Placeholders placeholders) {
            var message = CompiledMessage.of("{yes,no#status}");
            var result = placeholders.context(message)
                .with("status", true)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldTranslateFalseToSecondOption(Placeholders placeholders) {
            var message = CompiledMessage.of("{yes,no#status}");
            var result = placeholders.context(message)
                .with("status", false)
                .apply();

            assertThat(result).isEqualTo("no");
        }
    }

    @Nested
    @DisplayName("Common translations")
    class CommonTranslations {

        @ParameterizedTest
        @CsvSource({
            "true, yes",
            "false, no"
        })
        void shouldTranslateYesNo(boolean value, String expected, Placeholders placeholders) {
            var message = CompiledMessage.of("{yes,no#value}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "true, enabled",
            "false, disabled"
        })
        void shouldTranslateEnabledDisabled(boolean value, String expected, Placeholders placeholders) {
            var message = CompiledMessage.of("{enabled,disabled#value}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "true, on",
            "false, off"
        })
        void shouldTranslateOnOff(boolean value, String expected, Placeholders placeholders) {
            var message = CompiledMessage.of("{on,off#value}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "true, active",
            "false, inactive"
        })
        void shouldTranslateActiveInactive(boolean value, String expected, Placeholders placeholders) {
            var message = CompiledMessage.of("{active,inactive#value}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Symbols and special characters")
    class SymbolsAndSpecial {

        @Test
        void shouldTranslateToCheckmark(Placeholders placeholders) {
            var message = CompiledMessage.of("{check}");
            var result = placeholders.context(message)
                .with("check", true)
                .apply();

            assertThat(result).isNotEmpty();
        }

        @ParameterizedTest
        @CsvSource({
            "true, TRUE",
            "false, FALSE"
        })
        void shouldTranslateToUppercase(boolean value, String expected, Placeholders placeholders) {
            var message = CompiledMessage.of("{TRUE,FALSE#value}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("In context")
    class InContext {

        @Test
        void shouldWorkWithSurroundingText(Placeholders placeholders) {
            var message = CompiledMessage.of("Status: {yes,no#active}");
            var result = placeholders.context(message)
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("Status: yes");
        }

        @Test
        void shouldHandleMultipleBooleans(Placeholders placeholders) {
            var message = CompiledMessage.of("A: {yes,no#a}, B: {yes,no#b}");
            var result = placeholders.context(message)
                .with("a", true)
                .with("b", false)
                .apply();

            assertThat(result).isEqualTo("A: yes, B: no");
        }
    }

    @Nested
    @DisplayName("Boolean wrapper and primitive")
    class BooleanTypes {

        @Test
        void shouldHandlePrimitiveBoolean(Placeholders placeholders) {
            boolean value = true;
            var message = CompiledMessage.of("{yes,no#value}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldHandleBooleanWrapper(Placeholders placeholders) {
            Boolean value = Boolean.FALSE;
            var message = CompiledMessage.of("{yes,no#value}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo("no");
        }
    }

    @Nested
    @DisplayName("Localized translations")
    class LocalizedTranslations {

        @Test
        void shouldTranslatePolishYesNo(Placeholders placeholders) {
            var message = CompiledMessage.of("{tak,nie#value}");

            assertThat(placeholders.context(message).with("value", true).apply()).isEqualTo("tak");
            assertThat(placeholders.context(message).with("value", false).apply()).isEqualTo("nie");
        }

        @Test
        void shouldTranslateGermanJaNein(Placeholders placeholders) {
            var message = CompiledMessage.of("{ja,nein#value}");

            assertThat(placeholders.context(message).with("value", true).apply()).isEqualTo("ja");
            assertThat(placeholders.context(message).with("value", false).apply()).isEqualTo("nein");
        }
    }

    @Nested
    @DisplayName("Method-style .bool() syntax")
    class MethodStyleBool {

        @Test
        void shouldTranslateTrueToFirstOption(Placeholders placeholders) {
            var message = CompiledMessage.of("{status.bool(\"yes\",\"no\")}");
            var result = placeholders.context(message)
                .with("status", true)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldTranslateFalseToSecondOption(Placeholders placeholders) {
            var message = CompiledMessage.of("{status.bool(\"yes\",\"no\")}");
            var result = placeholders.context(message)
                .with("status", false)
                .apply();

            assertThat(result).isEqualTo("no");
        }

        @ParameterizedTest
        @CsvSource({
            "true, enabled",
            "false, disabled"
        })
        void shouldTranslateEnabledDisabled(boolean value, String expected, Placeholders placeholders) {
            var message = CompiledMessage.of("{value.bool(\"enabled\",\"disabled\")}");
            var result = placeholders.context(message)
                .with("value", value)
                .apply();

            assertThat(result).isEqualTo(expected);
        }

        @Test
        void shouldWorkWithSingleQuotes(Placeholders placeholders) {
            var message = CompiledMessage.of("{active.bool('on','off')}");
            var result = placeholders.context(message)
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("on");
        }

        @Test
        void shouldWorkWithFormatAlias(Placeholders placeholders) {
            var message = CompiledMessage.of("{active.format(\"on\",\"off\")}");
            var result = placeholders.context(message)
                .with("active", false)
                .apply();

            assertThat(result).isEqualTo("off");
        }

        @Test
        void shouldWorkWithSurroundingText(Placeholders placeholders) {
            var message = CompiledMessage.of("Status: {active.bool(\"online\",\"offline\")}");
            var result = placeholders.context(message)
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("Status: online");
        }

        @Test
        void shouldHandleMultipleBooleans(Placeholders placeholders) {
            var message = CompiledMessage.of("A: {a.bool(\"yes\",\"no\")}, B: {b.bool(\"yes\",\"no\")}");
            var result = placeholders.context(message)
                .with("a", true)
                .with("b", false)
                .apply();

            assertThat(result).isEqualTo("A: yes, B: no");
        }
    }
}
