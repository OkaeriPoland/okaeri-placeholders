package eu.okaeri.placeholders.parsing;

import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.message.part.MessageFieldTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MessageFieldTokenizer")
class MessageFieldTokenizerTest {

    private MessageFieldTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new MessageFieldTokenizer();
    }

    @Nested
    @DisplayName("Field path tokenization")
    class FieldPathTokenization {

        @Test
        void shouldTokenizeSimpleField() {
            var result = tokenizer.tokenize("name");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("name");
            assertThat(result.get(0).getParams()).isEmpty();
        }

        @Test
        void shouldTokenizeTwoPartPath() {
            var result = tokenizer.tokenize("player.name");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("player");
            assertThat(result.get(1).getField()).isEqualTo("name");
        }

        @Test
        void shouldTokenizeThreePartPath() {
            var result = tokenizer.tokenize("player.inventory.size");

            assertThat(result).hasSize(3)
                .extracting(FieldParams::getField)
                .containsExactly("player", "inventory", "size");
        }

        @Test
        void shouldTokenizeDeeplyNestedPath() {
            var result = tokenizer.tokenize("a.b.c.d.e.f.g");

            assertThat(result).hasSize(7)
                .extracting(FieldParams::getField)
                .containsExactly("a", "b", "c", "d", "e", "f", "g");
        }

        @ParameterizedTest
        @ValueSource(strings = {"single", "two.parts", "three.part.path"})
        void shouldTokenizeVariousPaths(String path) {
            var result = tokenizer.tokenize(path);
            var expectedCount = path.split("\\.").length;

            assertThat(result).hasSize(expectedCount);
        }
    }

    @Nested
    @DisplayName("Method call tokenization")
    class MethodCallTokenization {

        @Test
        void shouldTokenizeMethodWithNoArgs() {
            var result = tokenizer.tokenize("method()");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("method");
            assertThat(result.get(0).getParams()).containsExactly("");
        }

        @Test
        void shouldTokenizeMethodWithSingleArg() {
            var result = tokenizer.tokenize("healthBar(20)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("healthBar");
            assertThat(result.get(0).getParams()).containsExactly("20");
        }

        @Test
        void shouldTokenizeMethodWithMultipleArgs() {
            var result = tokenizer.tokenize("healthBar(20,X)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("healthBar");
            assertThat(result.get(0).getParams()).containsExactly("20", "X");
        }

        @Test
        void shouldTokenizeMethodWithManyArgs() {
            var result = tokenizer.tokenize("method(a,b,c,d,e)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getParams()).containsExactly("a", "b", "c", "d", "e");
        }

        @Test
        void shouldTokenizeMethodAfterField() {
            var result = tokenizer.tokenize("player.healthBar(20)");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("player");
            assertThat(result.get(0).getParams()).isEmpty();
            assertThat(result.get(1).getField()).isEqualTo("healthBar");
            assertThat(result.get(1).getParams()).containsExactly("20");
        }

        @Test
        void shouldTokenizeChainedMethodCalls() {
            var result = tokenizer.tokenize("a.b(1,2).c.d(33)");

            assertThat(result).hasSize(4);
            assertThat(result.get(0).getField()).isEqualTo("a");
            assertThat(result.get(1).getField()).isEqualTo("b");
            assertThat(result.get(1).getParams()).containsExactly("1", "2");
            assertThat(result.get(2).getField()).isEqualTo("c");
            assertThat(result.get(3).getField()).isEqualTo("d");
            assertThat(result.get(3).getParams()).containsExactly("33");
        }

        @Test
        void shouldTokenizeMethodFollowedByField() {
            var result = tokenizer.tokenize("method(arg).field");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("method");
            assertThat(result.get(0).getParams()).containsExactly("arg");
            assertThat(result.get(1).getField()).isEqualTo("field");
        }
    }

    @Nested
    @DisplayName("Argument tokenization")
    class ArgumentTokenization {

        @Test
        void shouldTokenizeEmptyArgs() {
            var result = tokenizer.tokenizeArgs("");

            assertThat(result).containsExactly("");
        }

        @Test
        void shouldTokenizeSingleArg() {
            var result = tokenizer.tokenizeArgs("value");

            assertThat(result).containsExactly("value");
        }

        @Test
        void shouldTokenizeMultipleArgs() {
            var result = tokenizer.tokenizeArgs("a,b,c");

            assertThat(result).containsExactly("a", "b", "c");
        }

        @Test
        void shouldHandleEscapedComma() {
            var result = tokenizer.tokenizeArgs("a\\,b");

            assertThat(result).containsExactly("a,b");
        }

        @Test
        void shouldHandleEscapedCommaInMiddle() {
            var result = tokenizer.tokenizeArgs("first,a\\,b,last");

            assertThat(result).containsExactly("first", "a,b", "last");
        }

        @Test
        void shouldHandleMultipleEscapedCommas() {
            var result = tokenizer.tokenizeArgs("a\\,b\\,c");

            assertThat(result).containsExactly("a,b,c");
        }

        @Test
        void shouldPreserveSpacesInArgs() {
            var result = tokenizer.tokenizeArgs("hello world,goodbye world");

            assertThat(result).containsExactly("hello world", "goodbye world");
        }

        @Test
        void shouldHandleEmptyArgsInList() {
            var result = tokenizer.tokenizeArgs(",second");

            assertThat(result).containsExactly("", "second");
        }

        @Test
        void shouldHandleTrailingComma() {
            // Note: Trailing comma does NOT create an empty element (implementation behavior)
            var result = tokenizer.tokenizeArgs("first,");

            assertThat(result).containsExactly("first");
        }

        @ParameterizedTest
        @CsvSource({
            "'single', 1",
            "'a,b', 2",
            "'a,b,c', 3",
            "'a,b,c,d,e', 5"
        })
        void shouldCountArgsCorrectly(String input, int expectedCount) {
            var result = tokenizer.tokenizeArgs(input);

            assertThat(result).hasSize(expectedCount);
        }
    }

    @Nested
    @DisplayName("Special characters in arguments")
    class SpecialCharactersInArguments {

        @Test
        void shouldHandleParenthesesInArgs() {
            var result = tokenizer.tokenize("papi(some.nested.call)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("papi");
            assertThat(result.get(0).getParams()).containsExactly("some.nested.call");
        }

        @Test
        void shouldHandleNestedParenthesesInArgs() {
            // or(secondary.replace(_,-)) should keep nested parens intact
            var result = tokenizer.tokenize("primary.or(secondary.replace(_,-))");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("primary");
            assertThat(result.get(1).getField()).isEqualTo("or");
            assertThat(result.get(1).getParams()).containsExactly("secondary.replace(_,-)");
        }

        @Test
        void shouldHandleDeepNestedParenthesesInArgs() {
            // 3 levels: $.if(cond, a.or(b.replace(x,y)), fallback)
            // $ and if are separate tokens ($ is the global functions container)
            // Tokenizer preserves spaces - trimming happens at field resolution time
            var result = tokenizer.tokenize("$.if(active, primary.or(secondary.replace(_,-)), fallback)");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("$");
            assertThat(result.get(1).getField()).isEqualTo("if");
            assertThat(result.get(1).getParams()).containsExactly("active", " primary.or(secondary.replace(_,-))", " fallback");
        }

        @Test
        void shouldHandleDeepNestedParenthesesInArgsWithoutSpaces() {
            // Same as above but without spaces after commas
            var result = tokenizer.tokenize("$.if(active,primary.or(secondary.replace(_,-)),fallback)");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("$");
            assertThat(result.get(1).getField()).isEqualTo("if");
            assertThat(result.get(1).getParams()).containsExactly("active", "primary.or(secondary.replace(_,-))", "fallback");
        }

        @Test
        void shouldHandlePipeInArgs() {
            var result = tokenizer.tokenize("method(|)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getParams()).containsExactly("|");
        }

        @Test
        void shouldHandleSpaceInArgs() {
            var result = tokenizer.tokenize("method( )");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getParams()).containsExactly(" ");
        }

        @Test
        void shouldHandleHashInArgs() {
            var result = tokenizer.tokenize("method(#tag)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getParams()).containsExactly("#tag");
        }

        @Test
        void shouldHandleComplexArgumentWithCommas() {
            // Note: Unescaped commas in arguments are split as separate args
            var result = tokenizer.tokenize("format([h]< hour, hours> (m)< minute, minutes>)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("format");
            // Commas inside the argument are treated as separators
            assertThat(result.get(0).getParams()).hasSize(3);
        }

        @Test
        void shouldHandleEscapedCommasInComplexArgument() {
            // Use escaped commas to keep the format string as single argument
            var result = tokenizer.tokenize("format([h]< hour\\, hours> (m)< minute\\, minutes>)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("format");
            // Escaped commas are preserved, so single argument
            assertThat(result.get(0).getParams()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Unicode support")
    class UnicodeSupport {

        @Test
        void shouldTokenizePolishFieldName() {
            var result = tokenizer.tokenize("cześć");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("cześć");
        }

        @Test
        void shouldTokenizeCyrillicPath() {
            var result = tokenizer.tokenize("игрок.имя");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("игрок");
            assertThat(result.get(1).getField()).isEqualTo("имя");
        }

        @Test
        void shouldHandleUnicodeInArgs() {
            var result = tokenizer.tokenize("метод(аргумент)");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("метод");
            assertThat(result.get(0).getParams()).containsExactly("аргумент");
        }

        @Test
        void shouldTokenizeJapanesePath() {
            var result = tokenizer.tokenize("プレイヤー.名前");

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getField()).isEqualTo("プレイヤー");
            assertThat(result.get(1).getField()).isEqualTo("名前");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        void shouldHandleSingleCharacterField() {
            var result = tokenizer.tokenize("a");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("a");
        }

        @Test
        void shouldHandleSingleCharacterPath() {
            var result = tokenizer.tokenize("a.b.c");

            assertThat(result).hasSize(3)
                .extracting(FieldParams::getField)
                .containsExactly("a", "b", "c");
        }

        @Test
        void shouldHandleMethodOnSingleChar() {
            var result = tokenizer.tokenize("a.b(x)");

            assertThat(result).hasSize(2);
            assertThat(result.get(1).getField()).isEqualTo("b");
            assertThat(result.get(1).getParams()).containsExactly("x");
        }

        @Test
        void shouldHandleNumericFieldName() {
            var result = tokenizer.tokenize("field123");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("field123");
        }

        @Test
        void shouldHandleFieldStartingWithNumber() {
            var result = tokenizer.tokenize("123field");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("123field");
        }

        @Test
        void shouldHandleUnderscoreInFieldName() {
            var result = tokenizer.tokenize("snake_case_field");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("snake_case_field");
        }

        @Test
        void shouldHandleMixedCaseFieldName() {
            var result = tokenizer.tokenize("camelCaseField");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getField()).isEqualTo("camelCaseField");
        }
    }

    @Nested
    @DisplayName("Metadata argument parsing")
    class MetadataArgumentParsing {

        @Test
        void shouldParsePluralizationOptions() {
            var result = tokenizer.tokenizeArgs("apple,apples");

            assertThat(result).containsExactly("apple", "apples");
        }

        @Test
        void shouldParsePolishPluralizationOptions() {
            var result = tokenizer.tokenizeArgs("pies,psy,psów");

            assertThat(result).containsExactly("pies", "psy", "psów");
        }

        @Test
        void shouldParseDateTimeOptions() {
            var result = tokenizer.tokenizeArgs("ldt,medium,Europe/Paris");

            assertThat(result).containsExactly("ldt", "medium", "Europe/Paris");
        }

        @Test
        void shouldParseBooleanOptions() {
            var result = tokenizer.tokenizeArgs("yes,no");

            assertThat(result).containsExactly("yes", "no");
        }
    }
}
