package eu.okaeri.placeholders.parsing;

import eu.okaeri.placeholders.message.part.MessageField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MessageField")
class MessageFieldTest {

    @Nested
    @DisplayName("Simple field parsing")
    class SimpleFieldParsing {

        @Test
        void shouldParseSimpleFieldName() {
            var field = MessageField.of("name");

            assertThat(field.getName()).isEqualTo("name");
            assertThat(field.hasSub()).isFalse();
            assertThat(field.getSub()).isNull();
        }

        @Test
        void shouldUseEnglishLocaleByDefault() {
            var field = MessageField.of("name");

            assertThat(field.getLocale()).isEqualTo(Locale.ENGLISH);
        }

        @Test
        void shouldUseSpecifiedLocale() {
            var field = MessageField.of(Locale.GERMAN, "name");

            assertThat(field.getLocale()).isEqualTo(Locale.GERMAN);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "name", "longFieldName", "snake_case", "camelCase"})
        void shouldParseVariousFieldNames(String name) {
            var field = MessageField.of(name);

            assertThat(field.getName()).isEqualTo(name);
        }
    }

    @Nested
    @DisplayName("Nested field parsing (dot notation)")
    class NestedFieldParsing {

        @Test
        void shouldParseTwoLevelNesting() {
            var field = MessageField.of("player.name");

            assertThat(field.getName()).isEqualTo("player");
            assertThat(field.hasSub()).isTrue();
            assertThat(field.getSub().getName()).isEqualTo("name");
            assertThat(field.getSub().hasSub()).isFalse();
        }

        @Test
        void shouldParseThreeLevelNesting() {
            var field = MessageField.of("player.inventory.size");

            assertThat(field.getName()).isEqualTo("player");
            assertThat(field.getSub().getName()).isEqualTo("inventory");
            assertThat(field.getSub().getSub().getName()).isEqualTo("size");
        }

        @Test
        void shouldParseDeeplyNestedField() {
            var field = MessageField.of("a.b.c.d.e.f.g");

            var current = field;
            for (var expected : new String[]{"a", "b", "c", "d", "e", "f", "g"}) {
                assertThat(current.getName()).isEqualTo(expected);
                current = current.getSub();
            }
            assertThat(current).isNull();
        }

        @Test
        void shouldTrackLastSubPath() {
            var field = MessageField.of("player.inventory.item.name");

            assertThat(field.getLastSubPath()).isEqualTo("player.inventory.item.name");
        }

        @Test
        void shouldReturnLastSubField() {
            var field = MessageField.of("player.inventory.size");

            assertThat(field.getLastSub()).isNotNull();
            assertThat(field.getLastSub().getName()).isEqualTo("size");
        }

        @Test
        void shouldReturnNullLastSubForSimpleField() {
            var field = MessageField.of("name");

            assertThat(field.getLastSub()).isNull();
        }

        @ParameterizedTest
        @CsvSource({
            "a.b, 2",
            "a.b.c, 3",
            "a.b.c.d.e, 5"
        })
        void shouldParseCorrectNestingDepth(String path, int expectedDepth) {
            var field = MessageField.of(path);

            int depth = 1;
            var current = field;
            while (current.hasSub()) {
                depth++;
                current = current.getSub();
            }

            assertThat(depth).isEqualTo(expectedDepth);
        }
    }

    @Nested
    @DisplayName("Method call parsing")
    class MethodCallParsing {

        @Test
        void shouldParseMethodWithNoArgs() {
            var field = MessageField.of("value.toUpperCase()");

            assertThat(field.getName()).isEqualTo("value");
            assertThat(field.getSub().getName()).isEqualTo("toUpperCase");
            assertThat(field.getSub().getParams()).isNotNull();
        }

        @Test
        void shouldParseMethodWithSingleArg() {
            var field = MessageField.of("player.healthBar(20)");

            assertThat(field.getName()).isEqualTo("player");
            assertThat(field.getSub().getName()).isEqualTo("healthBar");
            assertThat(field.getSub().getParams().strAt(0)).isEqualTo("20");
        }

        @Test
        void shouldParseMethodWithMultipleArgs() {
            var field = MessageField.of("player.healthBar(20,X)");

            assertThat(field.getName()).isEqualTo("player");
            assertThat(field.getSub().getName()).isEqualTo("healthBar");
            assertThat(field.getSub().getParams().strAt(0)).isEqualTo("20");
            assertThat(field.getSub().getParams().strAt(1)).isEqualTo("X");
        }

        @Test
        void shouldParseChainedMethodCalls() {
            var field = MessageField.of("value.replace(a,b).toUpperCase()");

            assertThat(field.getName()).isEqualTo("value");
            assertThat(field.getSub().getName()).isEqualTo("replace");
            assertThat(field.getSub().getSub().getName()).isEqualTo("toUpperCase");
        }

        @Test
        void shouldParseMixedFieldsAndMethods() {
            var field = MessageField.of("player.inventory.first.type.toLowerCase()");

            assertThat(field.getName()).isEqualTo("player");
            assertThat(field.getLastSub().getName()).isEqualTo("toLowerCase");
        }
    }

    @Nested
    @DisplayName("Default value propagation")
    class DefaultValuePropagation {

        @Test
        void shouldSetDefaultValueOnSimpleField() {
            var field = MessageField.of("name");
            field.setDefaultValue("Unknown");

            assertThat(field.getDefaultValue()).isEqualTo("Unknown");
        }

        @Test
        void shouldPropagateDefaultValueToSubFields() {
            var field = MessageField.of("player.inventory.name");
            field.setDefaultValue("N/A");

            assertThat(field.getDefaultValue()).isEqualTo("N/A");
            assertThat(field.getSub().getDefaultValue()).isEqualTo("N/A");
            assertThat(field.getSub().getSub().getDefaultValue()).isEqualTo("N/A");
        }

        @Test
        void shouldAllowNullDefaultValue() {
            var field = MessageField.of("name");
            field.setDefaultValue("value");
            field.setDefaultValue(null);

            assertThat(field.getDefaultValue()).isNull();
        }
    }

    @Nested
    @DisplayName("Unicode support")
    class UnicodeSupport {

        @Test
        void shouldParsePolishFieldName() {
            var field = MessageField.of("cześć");

            assertThat(field.getName()).isEqualTo("cześć");
        }

        @Test
        void shouldParseCyrillicFieldName() {
            var field = MessageField.of("привет");

            assertThat(field.getName()).isEqualTo("привет");
        }

        @Test
        void shouldParseJapaneseFieldName() {
            var field = MessageField.of("日本語");

            assertThat(field.getName()).isEqualTo("日本語");
        }

        @Test
        void shouldParseNestedUnicodeFields() {
            var field = MessageField.of("игрок.имя");

            assertThat(field.getName()).isEqualTo("игрок");
            assertThat(field.getSub().getName()).isEqualTo("имя");
        }

        @Test
        void shouldHandleMixedAsciiAndUnicode() {
            var field = MessageField.of("player.ім'я");

            assertThat(field.getName()).isEqualTo("player");
            assertThat(field.getSub().getName()).isEqualTo("ім'я");
        }
    }

    @Nested
    @DisplayName("Source tracking")
    class SourceTracking {

        @Test
        void shouldTrackOriginalSource() {
            var source = "player.inventory.size";
            var field = MessageField.of(source);

            assertThat(field.getSource()).isEqualTo(source);
        }

        @Test
        void shouldTrackSourceForAllSubFields() {
            var source = "player.inventory.size";
            var field = MessageField.of(source);

            assertThat(field.getSource()).isEqualTo(source);
            assertThat(field.getSub().getSource()).isEqualTo(source);
            assertThat(field.getSub().getSub().getSource()).isEqualTo(source);
        }
    }

    @Nested
    @DisplayName("Locale propagation")
    class LocalePropagation {

        @Test
        void shouldPropagateLocaleToSubFields() {
            var field = MessageField.of(Locale.GERMAN, "player.name");

            assertThat(field.getLocale()).isEqualTo(Locale.GERMAN);
            assertThat(field.getSub().getLocale()).isEqualTo(Locale.GERMAN);
        }
    }

    @Nested
    @DisplayName("FieldParams access")
    class FieldParamsAccess {

        @Test
        void shouldProvideParamsViaAccessor() {
            var field = MessageField.of("method(arg)");

            assertThat(field.params()).isNotNull();
            assertThat(field.params().strAt(0)).isEqualTo("arg");
        }

        @Test
        void shouldProvideLocaleViaAccessor() {
            var field = MessageField.of(Locale.FRENCH, "name");

            assertThat(field.locale()).isEqualTo(Locale.FRENCH);
        }
    }
}
