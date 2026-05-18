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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("String placeholders")
@ExtendWith(PlaceholdersExtension.class)
class StringPlaceholdersTest {

    @Nested
    @DisplayName("Case conversion")
    class CaseConversion {

        @Test
        void shouldConvertToLowerCase(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toLowerCase}"))
                .with("s", "HELLO WORLD")
                .apply();

            assertThat(result).isEqualTo("hello world");
        }

        @Test
        void shouldConvertToUpperCase(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toUpperCase}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("HELLO WORLD");
        }

        @Test
        void shouldHandleMixedCase(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toLowerCase}"))
                .with("s", "HeLLo WoRLD")
                .apply();

            assertThat(result).isEqualTo("hello world");
        }

        @Test
        void shouldHandleEmptyString(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toUpperCase}"))
                .with("s", "")
                .apply();

            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("eu.okaeri.placeholders.fixture.TestData#unicodeStrings")
        void shouldHandleUnicodeInCaseConversion(String input, String description, Placeholders placeholders) {
            var message = CompiledMessage.of("{s.toLowerCase}");

            assertThatCode(() -> placeholders.context(message)
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
            var result = placeholders.context(CompiledMessage.of("{s.capitalize}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("Hello world");
        }

        @Test
        void shouldCapitalizeAlreadyCapitalized(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.capitalize}"))
                .with("s", "Hello World")
                .apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldCapitalizeFully(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.capitalizeFully}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldCapitalizeFullyWithMixedCase(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.capitalizeFully}"))
                .with("s", "hELLO wORLD")
                .apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldHandleEmptyStringCapitalize(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.capitalize}"))
                .with("s", "")
                .apply();

            assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleSingleCharCapitalize(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.capitalize}"))
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
            var result = placeholders.context(CompiledMessage.of("{s.replace(_,-)}"))
                .with("s", "hello_world")
                .apply();

            assertThat(result).isEqualTo("hello-world");
        }

        @Test
        void shouldReplaceMultipleOccurrences(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.replace(_,-)}"))
                .with("s", "a_b_c_d")
                .apply();

            assertThat(result).isEqualTo("a-b-c-d");
        }

        @Test
        void shouldHandleNoMatch(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.replace(x,y)}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("hello world");
        }

        @Test
        void shouldReplaceWithEmpty(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.replace(_,)}"))
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
            var result = placeholders.context(CompiledMessage.of("{s.replace(" + from + "," + to + ")}"))
                .with("s", input)
                .apply();

            assertThat(result).isEqualTo(expected);
        }

        @Test
        void shouldReplaceWithSpace(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.replace(_, )}"))
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
            var result = placeholders.context(CompiledMessage.of("{s.prepend(prefix_)}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("prefix_value");
        }

        @Test
        void shouldAppend(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.append(_suffix)}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("value_suffix");
        }

        @Test
        void shouldChainPrependAndAppend(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.prepend([).append(])}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("[value]");
        }

        @Test
        void shouldHandleEmptyPrepend(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.prepend()}"))
                .with("s", "value")
                .apply();

            assertThat(result).isEqualTo("value");
        }

        @Test
        void shouldAppendBareApostropheS(Placeholders placeholders) {
            // English possessive — `'s)` doesn't open a string scan because there's no closer ahead
            var result = placeholders.context(CompiledMessage.of("{player.append('s)}"))
                .with("player", "Sandra")
                .apply();

            assertThat(result).isEqualTo("Sandra's");
        }

        @Test
        void shouldAppendDoubleQuotedApostropheS(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{player.append(\"'s\")}"))
                .with("player", "Sandra")
                .apply();

            assertThat(result).isEqualTo("Sandra's");
        }

        @Test
        void shouldAppendEscapedApostropheInSingleQuotes(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{player.append('\\'s')}"))
                .with("player", "Sandra")
                .apply();

            assertThat(result).isEqualTo("Sandra's");
        }

        @Test
        void shouldPreserveTrailingSpaceInBareLiteralArg(Placeholders placeholders) {
            // user case: command template needs the trailing space so the cuboid name doesn't fuse
            var result = placeholders.context(CompiledMessage.of("{cuboid.prepend(/cub extend <days> )}"))
                .with("cuboid", "player#1")
                .apply();

            assertThat(result).isEqualTo("/cub extend <days> player#1");
        }

        @Test
        void shouldPreserveEdgeWhitespaceAroundSingleIdentArg(Placeholders placeholders) {
            // single-ident bare args still parse as Refs (so they look up values) but
            // carry the padded source as a literalFallback — when no value is bound to
            // the trimmed name, the padded literal is returned, so `( wrap )` preserves
            // its spaces. `default( name , "Guest" )` still resolves `name` (value lookup
            // wins over fallback) so existing formatted templates are unaffected.
            var result = placeholders.context(CompiledMessage.of("{s.prepend( wrap )}"))
                .with("s", "x")
                .apply();

            assertThat(result).isEqualTo(" wrap x");
        }

        @Test
        void shouldStillResolveValueForPaddedRefWhenBound(Placeholders placeholders) {
            // value lookup wins over the literalFallback — `default( name , "Guest" )`
            // and similar formatted templates keep working with explicit `name` binding
            var result = placeholders.context(CompiledMessage.of("{s.prepend( wrap )}"))
                .with("s", "x")
                .with("wrap", "X")
                .apply();

            assertThat(result).isEqualTo("Xx");
        }

        @Test
        void shouldAppendBareLiteralEndingInDot(Placeholders placeholders) {
            // Polish abbreviation `Wł.` (włączone — "on") trailing a placeholder
            var result = placeholders.context(CompiledMessage.of("{s.append(Wł.)}"))
                .with("s", "status: ")
                .apply();

            assertThat(result).isEqualTo("status: Wł.");
        }

        @Test
        void shouldAppendBareLiteralWithDotSeparatedSections(Placeholders placeholders) {
            // multiple dot-suffixed sections in one arg
            var result = placeholders.context(CompiledMessage.of("{s.append(Wł./Wył.)}"))
                .with("s", "tryb: ")
                .apply();

            assertThat(result).isEqualTo("tryb: Wł./Wył.");
        }

        @Test
        void shouldRenderMessageDefaultWhenTargetMissingForMultiWordLiteralArg(Placeholders placeholders) {
            // message-level pipe `|fallback` kicks in when `s` is missing, even with the
            // unusual arg shape — the bare-arg parser doesn't swallow the pipe
            var result = placeholders.context(CompiledMessage.of("{s.append(hello world)|fallback}")).apply();

            assertThat(result).isEqualTo("fallback");
        }

        @Test
        void shouldRenderMessageDefaultWhenTargetMissingForTrailingWhitespaceArg(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.prepend(/cub extend <day> )|fallback}")).apply();

            assertThat(result).isEqualTo("fallback");
        }

        @Test
        void shouldRenderMessageDefaultWhenTargetMissingForTrailingDotArg(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.append(Wł./Wył.)|fallback}")).apply();

            assertThat(result).isEqualTo("fallback");
        }

        @Test
        void shouldApplyFunctionWhenTargetIsBoundForFormattedArgs(Placeholders placeholders) {
            // pipe fallback NOT used when target is bound — the bare literal arg still
            // applies through the resolver
            var result = placeholders.context(CompiledMessage.of("{s.append(hello world)|fallback}"))
                .with("s", "x ")
                .apply();

            assertThat(result).isEqualTo("x hello world");
        }
    }

    @Nested
    @DisplayName("Chained operations")
    class ChainedOperations {

        @Test
        void shouldChainLowercaseAndCapitalize(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toLowerCase.capitalize}"))
                .with("s", "HELLO WORLD")
                .apply();

            assertThat(result).isEqualTo("Hello world");
        }

        @Test
        void shouldChainReplaceAndUppercase(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.replace(_,-).toUpperCase}"))
                .with("s", "hello_world")
                .apply();

            assertThat(result).isEqualTo("HELLO-WORLD");
        }

        @Test
        void shouldChainMultipleOperations(Placeholders placeholders) {
            // Chain: lowercase -> replace underscore with dash -> uppercase
            // Note: replace with space is not supported - library doesn't strip quotes from params
            var result = placeholders.context(CompiledMessage.of("{s.toLowerCase.replace(_,-).toUpperCase}"))
                .with("s", "HELLO_WORLD")
                .apply();

            assertThat(result).isEqualTo("HELLO-WORLD");
        }
    }

    @Nested
    @DisplayName("String.trim")
    class StringTrim {

        @Test
        void shouldTrimWhitespace(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.trim}"))
                .with("s", "  hello world  ")
                .apply();

            assertThat(result).isEqualTo("hello world");
        }

        @Test
        void shouldTrimTabs(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.trim}"))
                .with("s", "\thello\t")
                .apply();

            assertThat(result).isEqualTo("hello");
        }

        @Test
        void shouldHandleEmptyStringTrim(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.trim}"))
                .with("s", "   ")
                .apply();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("String.length")
    class StringLength {

        @Test
        void shouldReturnLength(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.length}"))
                .with("s", "hello")
                .apply();

            assertThat(result).isEqualTo("5");
        }

        @Test
        void shouldReturnZeroForEmpty(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.length}"))
                .with("s", "")
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldWorkWithSizeAlias(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.size}"))
                .with("s", "test")
                .apply();

            assertThat(result).isEqualTo("4");
        }

        @Test
        void shouldChainTrimWithLength(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.trim.length}"))
                .with("s", "  hi  ")
                .apply();

            assertThat(result).isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("Unicode support")
    class UnicodeSupport {

        @Test
        void shouldHandlePolishCharacters(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toUpperCase}"))
                .with("s", "cześć")
                .apply();

            assertThat(result).isEqualTo("CZEŚĆ");
        }

        @Test
        void shouldHandleCyrillicCharacters(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toLowerCase}"))
                .with("s", "ПРИВЕТ")
                .apply();

            assertThat(result).isEqualTo("привет");
        }

        @Test
        void shouldCapitalizeUnicode(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.capitalize}"))
                .with("s", "cześć świecie")
                .apply();

            assertThat(result).isEqualTo("Cześć świecie");
        }
    }

    @Nested
    @DisplayName("padStart / padEnd")
    class Padding {

        @Test
        void padStartShouldDefaultToSpace(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{s.padStart(8)}]"))
                .with("s", "abc")
                .apply();

            assertThat(result).isEqualTo("[     abc]");
        }

        @Test
        void padEndShouldDefaultToSpace(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{s.padEnd(8)}]"))
                .with("s", "abc")
                .apply();

            assertThat(result).isEqualTo("[abc     ]");
        }

        @Test
        void padStartShouldUseExplicitFillChar(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.padStart(6,\"0\")}"))
                .with("s", "42")
                .apply();

            assertThat(result).isEqualTo("000042");
        }

        @Test
        void padEndShouldUseExplicitFillChar(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.padEnd(6,\".\")}"))
                .with("s", "abc")
                .apply();

            assertThat(result).isEqualTo("abc...");
        }

        @Test
        void padStartShouldRepeatMultiCharFill(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.padStart(10,\"ab\")}"))
                .with("s", "x")
                .apply();

            assertThat(result).isEqualTo("ababababax");
        }

        @Test
        void padEndShouldTruncateMultiCharFillToFit(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.padEnd(7,\"abc\")}"))
                .with("s", "x")
                .apply();

            assertThat(result).isEqualTo("xabcabc");
        }

        @Test
        void padStartShouldReturnAsIsWhenAlreadyLongEnough(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.padStart(3,\"-\")}"))
                .with("s", "longer")
                .apply();

            assertThat(result).isEqualTo("longer");
        }

        @Test
        void padEndShouldReturnAsIsWhenExactLength(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.padEnd(3,\"-\")}"))
                .with("s", "abc")
                .apply();

            assertThat(result).isEqualTo("abc");
        }

        @Test
        void padStartShouldReturnAsIsWhenFillStringEmpty(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.padStart(10,\"\")}"))
                .with("s", "abc")
                .apply();

            assertThat(result).isEqualTo("abc");
        }

        @Test
        void padStartShouldHandleEmptyInput(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{s.padStart(4,\"-\")}]"))
                .with("s", "")
                .apply();

            assertThat(result).isEqualTo("[----]");
        }

        @Test
        void padStartShouldComposeWithOtherStringMethods(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toUpperCase.padStart(6,\"0\")}"))
                .with("s", "ab")
                .apply();

            assertThat(result).isEqualTo("0000AB");
        }
    }

    @Nested
    @DisplayName("truncate")
    class Truncate {

        @Test
        void shouldDefaultToTripleDotEllipsis(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.truncate(10)}"))
                .with("s", "hello world this is long")
                .apply();

            assertThat(result).isEqualTo("hello w...");
        }

        @Test
        void shouldRespectCustomEllipsis(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.truncate(10,\"…\")}"))
                .with("s", "hello world this is long")
                .apply();

            assertThat(result).isEqualTo("hello wor…");
        }

        @Test
        void shouldKeepOutputAtMostMaxLengthIncludingEllipsis(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.truncate(8)}"))
                .with("s", "abcdefghijklmnop")
                .apply();

            assertThat(result).isEqualTo("abcde...");
            assertThat(result.length()).isEqualTo(8);
        }

        @Test
        void shouldReturnAsIsWhenShorterThanMax(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.truncate(20)}"))
                .with("s", "short")
                .apply();

            assertThat(result).isEqualTo("short");
        }

        @Test
        void shouldReturnAsIsWhenExactlyMaxLength(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.truncate(5)}"))
                .with("s", "abcde")
                .apply();

            assertThat(result).isEqualTo("abcde");
        }

        @Test
        void shouldReturnEmptyWhenMaxLengthZero(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{s.truncate(0)}]"))
                .with("s", "anything")
                .apply();

            assertThat(result).isEqualTo("[]");
        }

        @Test
        void shouldReturnEmptyWhenMaxLengthNegative(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{s.truncate(-3)}]"))
                .with("s", "anything")
                .apply();

            assertThat(result).isEqualTo("[]");
        }

        @Test
        void shouldTruncateEllipsisItselfWhenLongerThanMax(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.truncate(2,\"...\")}"))
                .with("s", "anything")
                .apply();

            assertThat(result).isEqualTo("..");
        }

        @Test
        void shouldUseEmptyEllipsisAsHardCut(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.truncate(5,\"\")}"))
                .with("s", "abcdefghij")
                .apply();

            assertThat(result).isEqualTo("abcde");
        }

        @Test
        void shouldHandleEmptyInput(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{s.truncate(10)}]"))
                .with("s", "")
                .apply();

            assertThat(result).isEqualTo("[]");
        }

        @Test
        void shouldComposeWithOtherStringMethods(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.toUpperCase.truncate(8,\"…\")}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("HELLO W…");
        }
    }

    @Nested
    @DisplayName("reverse")
    class Reverse {

        @Test
        void shouldReverseCharacterOrder(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.reverse}"))
                .with("s", "hello")
                .apply();

            assertThat(result).isEqualTo("olleh");
        }

        @Test
        void shouldRoundtripViaDoubleReverse(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.reverse.reverse}"))
                .with("s", "anything goes here 123 ąść")
                .apply();

            assertThat(result).isEqualTo("anything goes here 123 ąść");
        }

        @Test
        void shouldHandleUnicodeWithoutSplittingSurrogatePairs(Placeholders placeholders) {
            // emoji uses a surrogate pair; reverse must keep the pair together
            var result = placeholders.context(CompiledMessage.of("{s.reverse}"))
                .with("s", "ab😀cd")
                .apply();

            assertThat(result).isEqualTo("dc😀ba");
        }
    }

    @Nested
    @DisplayName("removePrefix / removeSuffix")
    class RemoveAffix {

        @Test
        void removePrefixShouldStripWhenPresent(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.removePrefix(\"Mr. \")}"))
                .with("s", "Mr. Smith")
                .apply();

            assertThat(result).isEqualTo("Smith");
        }

        @Test
        void removePrefixShouldPassThroughWhenAbsent(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.removePrefix(\"Mr. \")}"))
                .with("s", "Smith")
                .apply();

            assertThat(result).isEqualTo("Smith");
        }

        @Test
        void removeSuffixShouldStripWhenPresent(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.removeSuffix(\".txt\")}"))
                .with("s", "report.txt")
                .apply();

            assertThat(result).isEqualTo("report");
        }

        @Test
        void removeSuffixShouldPassThroughWhenAbsent(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.removeSuffix(\".txt\")}"))
                .with("s", "report")
                .apply();

            assertThat(result).isEqualTo("report");
        }

        @Test
        void shouldComposeToStripWrapper(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.removePrefix(\"<\").removeSuffix(\">\")}"))
                .with("s", "<value>")
                .apply();

            assertThat(result).isEqualTo("value");
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        void shouldCountMultipleOccurrences(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.count(\"l\")}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void shouldReturnZeroWhenNotPresent(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.count(\"z\")}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldNotCountOverlappingMatches(Placeholders placeholders) {
            // "aaaa" contains "aa" non-overlapping -> 2 matches at positions 0 and 2
            var result = placeholders.context(CompiledMessage.of("{s.count(\"aa\")}"))
                .with("s", "aaaa")
                .apply();

            assertThat(result).isEqualTo("2");
        }

        @Test
        void shouldReturnZeroForEmptySearch(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.count(\"\")}"))
                .with("s", "hello")
                .apply();

            assertThat(result).isEqualTo("0");
        }
    }

    @Nested
    @DisplayName("indexOf / lastIndexOf")
    class IndexOf {

        @Test
        void indexOfShouldReturnFirstPosition(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.indexOf(\"l\")}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("2");
        }

        @Test
        void indexOfShouldReturnMinusOneWhenNotFound(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.indexOf(\"z\")}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("-1");
        }

        @Test
        void lastIndexOfShouldReturnLastPosition(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.lastIndexOf(\"l\")}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("9");
        }

        @Test
        void lastIndexOfShouldReturnMinusOneWhenNotFound(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.lastIndexOf(\"z\")}"))
                .with("s", "hello world")
                .apply();

            assertThat(result).isEqualTo("-1");
        }

        @Test
        void shouldComposeWithSubstringToSplitAtSeparator(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{s.substring(0,s.indexOf(\":\"))}"))
                .with("s", "key:value")
                .apply();

            assertThat(result).isEqualTo("key");
        }
    }
}
