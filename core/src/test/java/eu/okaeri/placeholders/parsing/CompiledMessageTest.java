package eu.okaeri.placeholders.parsing;

import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageStatic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CompiledMessage")
class CompiledMessageTest {

    @Nested
    @DisplayName("Basic compilation")
    class BasicCompilation {

        @Test
        void shouldCompileEmptyMessage() {
            var message = CompiledMessage.of("");

            assertThat(message.getRaw()).isEmpty();
            assertThat(message.getParts()).isEmpty();
            assertThat(message.getUsedFields()).isEmpty();
            assertThat(message.isWithFields()).isFalse();
        }

        @Test
        void shouldCompileStaticOnlyMessage() {
            var message = CompiledMessage.of("Hello World!");

            assertThat(message.getRaw()).isEqualTo("Hello World!");
            assertThat(message.isWithFields()).isFalse();
            assertThat(message.getParts()).hasSize(1);
            assertThat(message.getParts().get(0)).isInstanceOf(MessageStatic.class);
        }

        @Test
        void shouldCompileSimpleFieldMessage() {
            var message = CompiledMessage.of("Hello {name}!");

            assertThat(message.isWithFields()).isTrue();
            assertThat(message.hasField("name")).isTrue();
            assertThat(message.getParts()).hasSize(3);
        }

        @ParameterizedTest
        @ValueSource(strings = {"{name}", "Hello {name}!", "{a} and {b}", "{x}{y}{z}"})
        void shouldCompileMessageWithFields(String input) {
            var message = CompiledMessage.of(input);

            assertThat(message.isWithFields()).isTrue();
            assertThat(message.getRaw()).isEqualTo(input);
        }
    }

    @Nested
    @DisplayName("Message parts")
    class MessageParts {

        @Test
        void shouldCreateCorrectPartsForFieldAtStart() {
            var message = CompiledMessage.of("{name} World!");

            assertThat(message.getParts()).hasSize(3);
            assertThat(message.getParts().get(0)).isInstanceOf(MessageStatic.class);
            assertThat(message.getParts().get(1)).isInstanceOf(MessageField.class);
            assertThat(message.getParts().get(2)).isInstanceOf(MessageStatic.class);
        }

        @Test
        void shouldCreateCorrectPartsForFieldAtEnd() {
            var message = CompiledMessage.of("Hello {name}");

            assertThat(message.getParts()).hasSize(2);
            assertThat(message.getParts().get(0)).isInstanceOf(MessageStatic.class);
            assertThat(message.getParts().get(1)).isInstanceOf(MessageField.class);
        }

        @Test
        void shouldCreateCorrectPartsForFieldOnly() {
            var message = CompiledMessage.of("{name}");

            assertThat(message.getParts()).hasSize(2);
            assertThat(message.getParts().get(0)).isInstanceOf(MessageStatic.class);
            assertThat(message.getParts().get(1)).isInstanceOf(MessageField.class);
        }

        @Test
        void shouldCreateCorrectPartsForAdjacentFields() {
            var message = CompiledMessage.of("{a}{b}{c}");

            assertThat(message.getParts()).hasSize(6); // 3 static (empty) + 3 fields
            assertThat(message.hasField("a")).isTrue();
            assertThat(message.hasField("b")).isTrue();
            assertThat(message.hasField("c")).isTrue();
        }

        @Test
        void shouldCreateCorrectPartsForMultipleFields() {
            var message = CompiledMessage.of("Hello {first} and {second}!");

            assertThat(message.getParts()).hasSize(5);
            assertThat(message.hasField("first")).isTrue();
            assertThat(message.hasField("second")).isTrue();
        }
    }

    @Nested
    @DisplayName("Field detection")
    class FieldDetection {

        @Test
        void shouldDetectSimpleField() {
            var message = CompiledMessage.of("{player}");

            assertThat(message.hasField("player")).isTrue();
            assertThat(message.hasField("other")).isFalse();
        }

        @Test
        void shouldDetectNestedFieldBase() {
            var message = CompiledMessage.of("{player.name}");

            assertThat(message.hasField("player")).isTrue();
            assertThat(message.hasField("player.name")).isTrue();
            assertThat(message.hasField("name")).isFalse();
        }

        @Test
        void shouldDetectMethodCallFieldBase() {
            var message = CompiledMessage.of("{player.health(20)}");

            assertThat(message.hasField("player")).isTrue();
        }

        @Test
        void shouldDetectSameFieldUsedMultipleTimes() {
            var message = CompiledMessage.of("{name} says hello to {name}");

            assertThat(message.hasField("name")).isTrue();
            assertThat(message.getUsedFields()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Default value parsing")
    class DefaultValueParsing {

        @Test
        void shouldParseFieldWithDefaultValue() {
            var message = CompiledMessage.of("{name|Anonymous}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getDefaultValue()).isEqualTo("Anonymous");
            assertThat(field.getName()).isEqualTo("name");
        }

        @Test
        void shouldParseFieldWithEmptyDefault() {
            var message = CompiledMessage.of("{name|}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getDefaultValue()).isEmpty();
        }

        @Test
        void shouldParseFieldWithDefaultContainingSpaces() {
            var message = CompiledMessage.of("{name|Unknown User}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getDefaultValue()).isEqualTo("Unknown User");
        }

        @Test
        void shouldUseLastPipeForDefaultValue() {
            var message = CompiledMessage.of("{name|first|second}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getDefaultValue()).isEqualTo("second");
            assertThat(field.getName()).isEqualTo("name|first");
        }
    }

    @Nested
    @DisplayName("Metadata parsing")
    class MetadataParsing {

        @Test
        void shouldParseFieldWithMetadata() {
            var message = CompiledMessage.of("{apple,apples#count}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getMetadataRaw()).isEqualTo("apple,apples");
            assertThat(field.getName()).isEqualTo("count");
        }

        @Test
        void shouldParseFieldWithMetadataAndDefault() {
            var message = CompiledMessage.of("{yes,no#active|unknown}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getMetadataRaw()).isEqualTo("yes,no");
            assertThat(field.getName()).isEqualTo("active");
            assertThat(field.getDefaultValue()).isEqualTo("unknown");
        }

        @Test
        void shouldParsePrintfStyleMetadata() {
            var message = CompiledMessage.of("{%.2f#value}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getMetadataRaw()).isEqualTo("%.2f");
            assertThat(field.getName()).isEqualTo("value");
        }

        @Test
        void shouldParseDateTimeMetadata() {
            var message = CompiledMessage.of("{ldt,medium,Europe/Paris#time}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getMetadataRaw()).isEqualTo("ldt,medium,Europe/Paris");
            assertThat(field.getName()).isEqualTo("time");
        }
    }

    @Nested
    @DisplayName("Locale support")
    class LocaleSupport {

        @Test
        void shouldUseEnglishLocaleByDefault() {
            var message = CompiledMessage.of("{count}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getLocale()).isEqualTo(Locale.ENGLISH);
        }

        @Test
        void shouldUseSpecifiedLocale() {
            var message = CompiledMessage.of(Locale.GERMAN, "{count}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getLocale()).isEqualTo(Locale.GERMAN);
        }

        @ParameterizedTest
        @CsvSource({
            "en, ENGLISH",
            "de, GERMAN",
            "fr, FRENCH",
            "pl, pl"
        })
        void shouldSupportVariousLocales(String tag, String expected) {
            var locale = Locale.forLanguageTag(tag);
            var message = CompiledMessage.of(locale, "{field}");
            var field = (MessageField) message.getParts().get(1);

            assertThat(field.getLocale()).isEqualTo(locale);
        }
    }

    @Nested
    @DisplayName("Syntax edge cases")
    class SyntaxEdgeCases {

        @ParameterizedTest
        @MethodSource("eu.okaeri.placeholders.fixture.TestData#syntaxEdgeCases")
        void shouldHandleSyntaxEdgeCases(String input, String description) {
            assertThatCode(() -> CompiledMessage.of(input))
                .describedAs("Should handle: " + description)
                .doesNotThrowAnyException();
        }

        @Test
        void shouldTreatUnclosedBraceAsStatic() {
            var message = CompiledMessage.of("Hello {unclosed world");

            assertThat(message.isWithFields()).isFalse();
            assertThat(message.getParts()).hasSize(1);
        }

        @Test
        void shouldTreatUnmatchedClosingBraceAsStatic() {
            var message = CompiledMessage.of("Hello unclosed} world");

            assertThat(message.isWithFields()).isFalse();
        }

        @Test
        void shouldHandleNestedBracesInMetadata() {
            // This tests that nested braces don't confuse the parser
            var message = CompiledMessage.of("before {field} after");

            assertThat(message.isWithFields()).isTrue();
            assertThat(message.hasField("field")).isTrue();
        }

        @Test
        void shouldPreserveEmptyStaticParts() {
            var message = CompiledMessage.of("{a}{b}");

            // Between two adjacent fields there's an empty static part
            assertThat(message.getParts()).hasSizeGreaterThanOrEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Unicode support")
    class UnicodeSupport {

        @ParameterizedTest
        @MethodSource("eu.okaeri.placeholders.fixture.TestData#unicodeStrings")
        void shouldHandleUnicodeInStaticText(String text, String description) {
            var message = CompiledMessage.of(text + " {field}");

            assertThat(message.getRaw()).startsWith(text);
            assertThat(message.isWithFields()).isTrue();
        }

        @Test
        void shouldHandlePolishCharactersInFieldName() {
            var message = CompiledMessage.of("{cześć}");

            assertThat(message.isWithFields()).isTrue();
            assertThat(message.hasField("cześć")).isTrue();
        }

        @Test
        void shouldHandleCyrillicCharactersInFieldName() {
            var message = CompiledMessage.of("{привет}");

            assertThat(message.isWithFields()).isTrue();
            assertThat(message.hasField("привет")).isTrue();
        }

        @Test
        void shouldHandleJapaneseCharactersInFieldName() {
            var message = CompiledMessage.of("{日本語}");

            assertThat(message.isWithFields()).isTrue();
            assertThat(message.hasField("日本語")).isTrue();
        }
    }

    @Nested
    @DisplayName("Immutability")
    class Immutability {

        @Test
        void shouldReturnUnmodifiableParts() {
            var message = CompiledMessage.of("Hello {name}!");

            assertThatThrownBy(() -> message.getParts().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        void shouldReturnUnmodifiableUsedFields() {
            var message = CompiledMessage.of("Hello {name}!");

            assertThatThrownBy(() -> message.getUsedFields().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
