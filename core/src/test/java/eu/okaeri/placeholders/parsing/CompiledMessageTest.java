package eu.okaeri.placeholders.parsing;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.exception.ParseException;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.ExpressionPart;
import eu.okaeri.placeholders.message.StaticPart;
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
            assertThat(message.getParts().get(0)).isInstanceOf(StaticPart.class);
        }

        @Test
        void shouldCompileSimpleFieldMessage() {
            var message = CompiledMessage.of("Hello {name}!");

            assertThat(message.isWithFields()).isTrue();
            assertThat(message.hasField("name")).isTrue();
            // static("Hello ") + expression(name) + static("!")
            assertThat(message.getParts()).hasSize(3);
            assertThat(message.getParts().get(0)).isInstanceOf(StaticPart.class);
            assertThat(message.getParts().get(1)).isInstanceOf(ExpressionPart.class);
            assertThat(message.getParts().get(2)).isInstanceOf(StaticPart.class);
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

            // expression(name) + static(" World!")
            assertThat(message.getParts()).hasSize(2);
            assertThat(message.getParts().get(0)).isInstanceOf(ExpressionPart.class);
            assertThat(message.getParts().get(1)).isInstanceOf(StaticPart.class);
        }

        @Test
        void shouldCreateCorrectPartsForFieldAtEnd() {
            var message = CompiledMessage.of("Hello {name}");

            // static("Hello ") + expression(name)
            assertThat(message.getParts()).hasSize(2);
            assertThat(message.getParts().get(0)).isInstanceOf(StaticPart.class);
            assertThat(message.getParts().get(1)).isInstanceOf(ExpressionPart.class);
        }

        @Test
        void shouldCreateCorrectPartsForFieldOnly() {
            var message = CompiledMessage.of("{name}");

            // Just the expression, no empty statics
            assertThat(message.getParts()).hasSize(1);
            assertThat(message.getParts().get(0)).isInstanceOf(ExpressionPart.class);
        }

        @Test
        void shouldCreateCorrectPartsForAdjacentFields() {
            var message = CompiledMessage.of("{a}{b}{c}");

            // 3 expressions, no empty statics
            assertThat(message.getParts()).hasSize(3);
            assertThat(message.getParts().get(0)).isInstanceOf(ExpressionPart.class);
            assertThat(message.getParts().get(1)).isInstanceOf(ExpressionPart.class);
            assertThat(message.getParts().get(2)).isInstanceOf(ExpressionPart.class);
            assertThat(message.hasField("a")).isTrue();
            assertThat(message.hasField("b")).isTrue();
            assertThat(message.hasField("c")).isTrue();
        }

        @Test
        void shouldCreateCorrectPartsForMultipleFields() {
            var message = CompiledMessage.of("Hello {first} and {second}!");

            // static + expression + static(" and ") + expression + static("!")
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
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("Anonymous");
        }

        @Test
        void shouldParseFieldWithEmptyDefault() {
            var message = CompiledMessage.of("{name|}");
            var expr = (ExpressionPart) message.getParts().get(0);

            // Empty string after | is parsed as empty identifier, which becomes the default
            assertThat(expr.getDefaultValue()).isEmpty();
        }

        @Test
        void shouldParseFieldWithDefaultContainingSpaces() {
            var message = CompiledMessage.of("{name|Unknown User}");
            var expr = (ExpressionPart) message.getParts().get(0);

            // Everything after | is literal text, including spaces
            assertThat(expr.getDefaultValue()).isEqualTo("Unknown User");
        }

        @Test
        void shouldUseLiteralDefaultWithMultiplePipes() {
            var message = CompiledMessage.of("{name|first|second}");
            var expr = (ExpressionPart) message.getParts().get(0);

            // First | marks start of default, everything after is literal text
            assertThat(expr.getDefaultValue()).isEqualTo("first|second");
        }

        @Test
        void shouldFindPipeAfterBareApostropheInArgs() {
            // bare `'` in `'s)` must not open a quote scan that swallows the `|`
            var message = CompiledMessage.of("{x.append('s)|fallback}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("fallback");
        }

        @Test
        void shouldKeepPipeInsidePairedQuotesOutOfDefault() {
            // a `|` inside a properly paired string is part of the expression, not the default
            var message = CompiledMessage.of("{x.append(\"a|b\")|fallback}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("fallback");
        }

        @Test
        void shouldFindPipeAfterMultiWordLiteralArg() {
            // `(hello world)` is a bare multi-word literal arg
            var message = CompiledMessage.of("{x.append(hello world)|fallback}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("fallback");
        }

        @Test
        void shouldFindPipeAfterTrailingDotArg() {
            // `(Wł.)` is a literal-with-trailing-dot, can't be a method chain
            var message = CompiledMessage.of("{x.append(Wł.)|fallback}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("fallback");
        }

        @Test
        void shouldFindPipeAfterArgWithTrailingWhitespace() {
            // user's case: `(/cub extend <days> )` — trailing space preserved as literal
            var message = CompiledMessage.of("{cuboid.prepend(/cub extend <days> )|fallback}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("fallback");
        }

        @Test
        void shouldFindPipeAfterPaddedSingleIdentArg() {
            // single-ident with edge whitespace — `getLiteral()` carries the padded source
            var message = CompiledMessage.of("{s.prepend( wrap )|fallback}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("fallback");
        }

        @Test
        void shouldFindPipeAfterDotSeparatedTrailingDotArg() {
            // `(Wł./Wył.)` — multi-section literal arg with trailing dots
            var message = CompiledMessage.of("{p.localize(Wł./Wył.)|fallback}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getDefaultValue()).isEqualTo("fallback");
        }
    }

    @Nested
    @DisplayName("Legacy metadata transformation")
    class LegacyMetadataTransformation {

        @Test
        void shouldTransformPluralMetadataToMethodCall() {
            // {apple,apples#count} → {count._meta("apple","apples")}
            var message = CompiledMessage.of("{apple,apples#count}");
            var expr = (ExpressionPart) message.getParts().get(0);

            // Should parse successfully
            assertThat(expr.getAst()).isNotNull();
            assertThat(message.hasField("count")).isTrue();
        }

        @Test
        void shouldTransformMetadataWithDefault() {
            // {yes,no#active|unknown} → {active._meta("yes","no")|unknown}
            var message = CompiledMessage.of("{yes,no#active|unknown}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getAst()).isNotNull();
            assertThat(expr.getDefaultValue()).isEqualTo("unknown");
            assertThat(message.hasField("active")).isTrue();
        }

        @Test
        void shouldTransformPrintfMetadataToFormatCall() {
            // {%.2f#value} → {value.format("%.2f")}
            var message = CompiledMessage.of("{%.2f#value}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getAst()).isNotNull();
            assertThat(message.hasField("value")).isTrue();
        }

        @Test
        void shouldTransformDateTimeMetadataToMethodCall() {
            // {ldt,medium,Europe/Paris#time} → {time.ldt("medium","Europe/Paris")}
            var message = CompiledMessage.of("{ldt,medium,Europe/Paris#time}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getAst()).isNotNull();
            assertThat(message.hasField("time")).isTrue();
        }

        @Test
        void shouldTransformLocalizedTimeToMethodCall() {
            // {lt,short#time} → {time.lt("short")}
            var message = CompiledMessage.of("{lt,short#time}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getAst()).isNotNull();
            assertThat(message.hasField("time")).isTrue();
        }

        @Test
        void shouldTransformPatternDateTimeToMethodCall() {
            // {p,yyyy-MM-dd#date} → {date.format("yyyy-MM-dd")}
            var message = CompiledMessage.of("{p,yyyy-MM-dd#date}");
            var expr = (ExpressionPart) message.getParts().get(0);

            assertThat(expr.getAst()).isNotNull();
            assertThat(message.hasField("date")).isTrue();
        }

        @Test
        void shouldSplitOptionsContainingApostrophes() {
            // `don't,won't` previously collapsed because `'` paired across the comma; the closer-before-comma
            // bound now keeps them as two distinct options
            var placeholders = Placeholders.create();
            var trueResult = placeholders.context(CompiledMessage.of("{don't,won't#active}")).with("active", true).apply();
            var falseResult = placeholders.context(CompiledMessage.of("{don't,won't#active}")).with("active", false).apply();

            assertThat(trueResult).isEqualTo("don't");
            assertThat(falseResult).isEqualTo("won't");
        }

        @Test
        void shouldKeepLeadingApostropheInOptions() {
            var placeholders = Placeholders.create();
            var trueResult = placeholders.context(CompiledMessage.of("{a's,b's#active}")).with("active", true).apply();
            var falseResult = placeholders.context(CompiledMessage.of("{a's,b's#active}")).with("active", false).apply();

            assertThat(trueResult).isEqualTo("a's");
            assertThat(falseResult).isEqualTo("b's");
        }

        @Test
        void shouldStillStripPairedQuotesInOptions() {
            // regression guard: paired `'on'`/`'off'` style still strips the quote chars
            var placeholders = Placeholders.create();
            var trueResult = placeholders.context(CompiledMessage.of("{'on','off'#active}")).with("active", true).apply();
            var falseResult = placeholders.context(CompiledMessage.of("{'on','off'#active}")).with("active", false).apply();

            assertThat(trueResult).isEqualTo("on");
            assertThat(falseResult).isEqualTo("off");
        }

        @Test
        void shouldStillEscapeCommaInOptions() {
            // regression guard: `\,` keeps the literal comma inside an option
            var placeholders = Placeholders.create();
            var trueResult = placeholders.context(CompiledMessage.of("{a\\,b,c#active}")).with("active", true).apply();
            var falseResult = placeholders.context(CompiledMessage.of("{a\\,b,c#active}")).with("active", false).apply();

            assertThat(trueResult).isEqualTo("a,b");
            assertThat(falseResult).isEqualTo("c");
        }
    }

    @Nested
    @DisplayName("Locale support")
    class LocaleSupport {

        @Test
        void shouldAcceptLocaleInFactory() {
            // Locale is now used during evaluation, not stored on parts
            // Just verify the factory methods work
            assertThatCode(() -> CompiledMessage.of("{count}")).doesNotThrowAnyException();
            assertThatCode(() -> CompiledMessage.of(Locale.GERMAN, "{count}")).doesNotThrowAnyException();
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
            // Just verify parsing works with various locales
            assertThatCode(() -> CompiledMessage.of(locale, "{field}")).doesNotThrowAnyException();
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
        void shouldNotCreateEmptyStaticParts() {
            var message = CompiledMessage.of("{a}{b}");

            // Adjacent fields don't need empty static parts between them
            assertThat(message.getParts()).hasSize(2);
            assertThat(message.getParts().get(0)).isInstanceOf(ExpressionPart.class);
            assertThat(message.getParts().get(1)).isInstanceOf(ExpressionPart.class);
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
    @DisplayName("Quote and brace handling")
    class QuoteAndBraceHandling {

        @Test
        void shouldHandleTwoAdjacentBareApostrophePlaceholders() {
            var message = CompiledMessage.of("{a.append('s)} and {b.append('s)}");

            assertThat(message.getParts()).hasSize(3);
            assertThat(message.getParts().get(0)).isInstanceOf(ExpressionPart.class);
            assertThat(((StaticPart) message.getParts().get(1)).getValue()).isEqualTo(" and ");
            assertThat(message.getParts().get(2)).isInstanceOf(ExpressionPart.class);
        }

        @Test
        void shouldPreserveApostrophesInStaticText() {
            var message = CompiledMessage.of("It's {player}'s turn");

            assertThat(message.getParts()).hasSize(3);
            assertThat(((StaticPart) message.getParts().get(0)).getValue()).isEqualTo("It's ");
            assertThat(message.getParts().get(1)).isInstanceOf(ExpressionPart.class);
            assertThat(((StaticPart) message.getParts().get(2)).getValue()).isEqualTo("'s turn");
        }

        @Test
        void shouldNotPairBareApostropheWithLaterStaticQuotes() {
            // unbounded quote-skip would pair `'s)} 'other` and eat the `}` between them
            var message = CompiledMessage.of("{x.append('s)} 'other text'");

            assertThat(message.getParts()).hasSize(2);
            assertThat(((ExpressionPart) message.getParts().get(0)).getOriginalRaw())
                .isEqualTo("x.append('s)");
            assertThat(((StaticPart) message.getParts().get(1)).getValue()).isEqualTo(" 'other text'");
        }

        @Test
        void shouldKeepCommaInsideQuotedArg() {
            var message = CompiledMessage.of("{x.append(\"a,b\")}");

            assertThat(message.getParts()).hasSize(1);
            assertThat(message.getParts().get(0)).isInstanceOf(ExpressionPart.class);
        }

        @Test
        void shouldAllowLeftBraceInsideQuotedArg() {
            var message = CompiledMessage.of("{x.append(\"a{b\")}");

            assertThat(message.getParts()).hasSize(1);
            assertThat(((ExpressionPart) message.getParts().get(0)).getOriginalRaw())
                .isEqualTo("x.append(\"a{b\")");
        }

        @Test
        void shouldAllowBothBracesInsideDoubleQuotedArg() {
            // greedy `"` scan must jump over both `{` and `}` so depth stays balanced
            var message = CompiledMessage.of("{x.append(\"a{b}c\")}");

            assertThat(message.getParts()).hasSize(1);
            assertThat(((ExpressionPart) message.getParts().get(0)).getOriginalRaw())
                .isEqualTo("x.append(\"a{b}c\")");
        }

        @Test
        void shouldAllowRightBraceInsideDoubleQuotedArg() {
            // `"` is scanned greedily — embedded `}` is consumed as string content
            var message = CompiledMessage.of("{x.append(\"a}b\")}");

            assertThat(message.getParts()).hasSize(1);
            assertThat(((ExpressionPart) message.getParts().get(0)).getOriginalRaw())
                .isEqualTo("x.append(\"a}b\")");
        }

        @Test
        void shouldFailOnRightBraceInsideSingleQuotedArg() {
            // `'` is bounded by `}` (so bare apostrophes like `'s` work) — closing brace
            // inside `'...'` therefore short-circuits. workaround: use `"a}b"` instead
            assertThatThrownBy(() -> CompiledMessage.of("{x.append('a}b')}"))
                .isInstanceOf(ParseException.class);
        }

        @Test
        void shouldErrorLoudlyOnAdjacentBareApostropheArgs() {
            // `'a,'` pairs into STRING("a,"), then `b` is unexpected — loud error beats silent corruption
            assertThatThrownBy(() -> CompiledMessage.of("{x.f('a,'b,c)}"))
                .isInstanceOf(ParseException.class);
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
