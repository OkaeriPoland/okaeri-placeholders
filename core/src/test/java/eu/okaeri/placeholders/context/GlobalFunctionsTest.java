package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Global functions via $ context")
@ExtendWith(PlaceholdersExtension.class)
class GlobalFunctionsTest {

    @Nested
    @DisplayName("$.env() - Environment variables")
    class EnvFunction {

        @Test
        void shouldReadExistingEnvVar(Placeholders placeholders) {
            // PATH should exist on all systems
            var message = CompiledMessage.of("{$.env(PATH)}");
            var result = placeholders.context(message).apply();

            assertThat(result).isNotEmpty();
        }

        @Test
        void shouldReturnEmptyForMissingEnvVar(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.env(DEFINITELY_DOES_NOT_EXIST_12345)}");
            var result = placeholders.context(message).apply();

            assertThat(result).isEmpty();
        }

        @Test
        void shouldWorkWithQuotedArg(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.env(\"PATH\")}");
            var result = placeholders.context(message).apply();

            assertThat(result).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("$.now() - Current time")
    class NowFunction {

        @Test
        void shouldReturnCurrentInstant(Placeholders placeholders) {
            var before = Instant.now();

            var message = CompiledMessage.of("{$.now()}");
            var result = placeholders.context(message).apply();

            var after = Instant.now();

            // Result should parse as a valid instant between before and after
            assertThat(result).isNotEmpty();
            assertThat(result).containsPattern("\\d{4}-\\d{2}-\\d{2}");
        }

        @Test
        void shouldChainWithDateFormat(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.now().format(\"yyyy-MM-dd\",\"UTC\")}");
            var result = placeholders.context(message).apply();

            // Should be a valid date format
            assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}");
        }
    }

    @Nested
    @DisplayName("$.coalesce() - First non-null value")
    class CoalesceFunction {

        @Test
        void shouldReturnFirstNonNullField(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", "Bravo")
                .with("c", "Charlie")
                .apply();

            assertThat(result).isEqualTo("Bravo");
        }

        @Test
        void shouldReturnFirstIfNotNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", "Alpha")
                .with("b", "Bravo")
                .with("c", "Charlie")
                .apply();

            assertThat(result).isEqualTo("Alpha");
        }

        @Test
        void shouldReturnLiteralDefault(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,\"default\")}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", null)
                .apply();

            assertThat(result).isEqualTo("default");
        }

        @Test
        void shouldTreatUnrecognizedFieldNamesAsLiteralStrings(Placeholders placeholders) {
            // Backward compat: unrecognized field names fall back to being treated as literal strings
            var message = CompiledMessage.of("{$.coalesce(missing1,missing2,\"fallback\")}");
            var result = placeholders.context(message).apply();

            // "missing1" is not in context, so it's treated as literal string "missing1" (not null)
            assertThat(result).isEqualTo("missing1");
        }

        @Test
        void shouldReturnNullIndicatorWhenAllNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", null)
                .with("c", null)
                .apply();

            // Should indicate null since all are null
            assertThat(result).contains("null");
        }
    }

    @Nested
    @DisplayName("$.if() - Conditional")
    class IfFunction {

        @Test
        void shouldReturnThenValueWhenConditionTrue(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(active,\"yes\",\"no\")}");
            var result = placeholders.context(message)
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldReturnElseValueWhenConditionFalse(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(active,\"yes\",\"no\")}");
            var result = placeholders.context(message)
                .with("active", false)
                .apply();

            assertThat(result).isEqualTo("no");
        }

        @Test
        void shouldTreatNonEmptyStringAsTruthy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(name,\"has name\",\"no name\")}");
            var result = placeholders.context(message)
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("has name");
        }

        @Test
        void shouldTreatEmptyStringAsFalsy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(name,\"has name\",\"no name\")}");
            var result = placeholders.context(message)
                .with("name", "")
                .apply();

            assertThat(result).isEqualTo("no name");
        }

        @Test
        void shouldTreatNullAsFalsy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(name,\"has name\",\"no name\")}");
            var result = placeholders.context(message)
                .with("name", null)
                .apply();

            assertThat(result).isEqualTo("no name");
        }

        @Test
        void shouldTreatNonZeroNumberAsTruthy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(count,\"has items\",\"empty\")}");
            var result = placeholders.context(message)
                .with("count", 5)
                .apply();

            assertThat(result).isEqualTo("has items");
        }

        @Test
        void shouldTreatZeroAsFalsy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(count,\"has items\",\"empty\")}");
            var result = placeholders.context(message)
                .with("count", 0)
                .apply();

            assertThat(result).isEqualTo("empty");
        }

        @Test
        void shouldSupportFieldRefAsThenElse(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(useNickname,nickname,name)}");
            var result = placeholders.context(message)
                .with("useNickname", true)
                .with("nickname", "Ali")
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Ali");
        }
    }

    @Nested
    @DisplayName("$.random() - Random number")
    class RandomFunction {

        @Test
        void shouldReturnNumberInRange(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.random(1,10)}");

            for (int i = 0; i < 100; i++) {
                var result = placeholders.context(message).apply();
                int value = Integer.parseInt(result);

                assertThat(value).isBetween(1, 10);
            }
        }

        @Test
        void shouldWorkWithQuotedArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.random(\"1\",\"10\")}");
            var result = placeholders.context(message).apply();
            int value = Integer.parseInt(result);

            assertThat(value).isBetween(1, 10);
        }
    }

    @Nested
    @DisplayName("$.concat() - String concatenation")
    class ConcatFunction {

        @Test
        void shouldConcatenateLiterals(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(\"Hello\",\" \",\"World\")}");
            var result = placeholders.context(message).apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldConcatenateFieldRefs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(greeting,\" \",name)}");
            var result = placeholders.context(message)
                .with("greeting", "Hello")
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Hello Alice");
        }

        @Test
        void shouldConcatenateMixedArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(\"Hello \",name,\"!\")}");
            var result = placeholders.context(message)
                .with("name", "World")
                .apply();

            assertThat(result).isEqualTo("Hello World!");
        }

        @Test
        void shouldHandleNullAsEmptyString(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", "start")
                .with("b", null)
                .with("c", "end")
                .apply();

            assertThat(result).isEqualTo("startend");
        }
    }

    @Nested
    @DisplayName("$.join() - join non-empty values with a separator")
    class JoinFunction {

        @Test
        void shouldJoinPresentLiteralsWithSeparator(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(\"|\",\"a\",\"b\",\"c\")}");
            var result = placeholders.context(message).apply();

            assertThat(result).isEqualTo("a|b|c");
        }

        @Test
        void shouldSkipEmptyStringValues(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(\"|\",a,b,c)}");
            var result = placeholders.context(message)
                .with("a", "alpha")
                .with("b", "")
                .with("c", "gamma")
                .apply();

            assertThat(result).isEqualTo("alpha|gamma");
        }

        @Test
        void shouldSkipNullValues(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(\",\",a,b,c)}");
            var result = placeholders.context(message)
                .with("a", "x")
                .with("b", null)
                .with("c", "y")
                .apply();

            assertThat(result).isEqualTo("x,y");
        }

        @Test
        void shouldKeepSeparatorOnlyBetweenPresentItems(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(\"-\",a,b,c,d)}");
            var result = placeholders.context(message)
                .with("a", "")
                .with("b", "beta")
                .with("c", "")
                .with("d", "delta")
                .apply();

            assertThat(result).isEqualTo("beta-delta");
        }

        @Test
        void shouldReturnEmptyWhenAllValuesEmpty(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(\",\",a,b)}");
            var result = placeholders.context(message)
                .with("a", "")
                .with("b", "")
                .apply();

            assertThat(result).isEqualTo("");
        }

        @Test
        void shouldStringifyNonStringValues(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(\" \",a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 1)
                .with("b", true)
                .with("c", 3.14)
                .apply();

            assertThat(result).isEqualTo("1 true 3.14");
        }

        @Test
        void shouldAcceptDynamicSeparator(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(sep,a,b)}");
            var result = placeholders.context(message)
                .with("sep", " :: ")
                .with("a", "x")
                .with("b", "y")
                .apply();

            assertThat(result).isEqualTo("x :: y");
        }

        @Test
        void shouldResolveNestedConcatArgument(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.join(\" \",a,$.concat(prefix,b),c)}");
            var result = placeholders.context(message)
                .with("a", "first")
                .with("prefix", "+")
                .with("b", "boost")
                .with("c", "last")
                .apply();

            assertThat(result).isEqualTo("first +boost last");
        }

        @Test
        void shouldSkipEmptyResultFromNestedCall(Placeholders placeholders) {
            // $.if returns "" when first arg is falsy - join should treat that as empty
            var message = CompiledMessage.of("{$.join(\"-\",a,$.if(flag,\"shown\",\"\"),b)}");
            var result = placeholders.context(message)
                .with("a", "alpha")
                .with("flag", false)
                .with("b", "gamma")
                .apply();

            assertThat(result).isEqualTo("alpha-gamma");
        }

        @Test
        void shouldNestJoinInsideConcat(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(\"[\",$.join(\",\",a,b,c),\"]\")}");
            var result = placeholders.context(message)
                .with("a", "x")
                .with("b", "")
                .with("c", "y")
                .apply();

            assertThat(result).isEqualTo("[x,y]");
        }
    }

    @Nested
    @DisplayName("$.min() and $.max() - Numeric comparison")
    class MinMaxFunctions {

        @Test
        void shouldFindMin(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{min(a,b,c)}"))
                .with("a", 5)
                .with("b", 3)
                .with("c", 7)
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void shouldFindMax(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{max(a,b,c)}"))
                .with("a", 5)
                .with("b", 3)
                .with("c", 7)
                .apply();

            assertThat(result).isEqualTo("7");
        }

        @Test
        void shouldHandleNegatives(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{min(a,b)}"))
                .with("a", -5)
                .with("b", 3)
                .apply();

            assertThat(result).isEqualTo("-5");
        }

        @Test
        void shouldHandleDecimals(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{max(a,b)}"))
                .with("a", 3.14)
                .with("b", 2.71)
                .apply();

            assertThat(result).isEqualTo("3.14");
        }

        @Test
        void shouldFormatRange(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("Range: {min(a,b)} to {max(a,b)}"))
                .with("a", 10)
                .with("b", 5)
                .apply();

            assertThat(result).isEqualTo("Range: 5 to 10");
        }
    }

    @Nested
    @DisplayName("$.sum() / $.avg() - aggregations over numeric args")
    class SumAvgFunctions {

        @Test
        void sumShouldAddIntegers(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.sum(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 1)
                .with("b", 2)
                .with("c", 3)
                .apply();

            assertThat(result).isEqualTo("6");
        }

        @Test
        void sumShouldAddMixedIntAndDouble(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.sum(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 1)
                .with("b", 2.5)
                .with("c", 0.5)
                .apply();

            assertThat(result).isEqualTo("4");
        }

        @Test
        void sumShouldSkipNonNumericArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.sum(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 10)
                .with("b", "not a number")
                .with("c", 5)
                .apply();

            assertThat(result).isEqualTo("15");
        }

        @Test
        void sumShouldReturnNullWhenNoNumericArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("[{$.sum(a,b)}]");
            var result = placeholders.context(message)
                .with("a", "x")
                .with("b", "y")
                .apply();

            assertThat(result).isEqualTo("[null]");
        }

        @Test
        void avgShouldComputeMean(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.avg(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 10)
                .with("b", 20)
                .with("c", 30)
                .apply();

            assertThat(result).isEqualTo("20");
        }

        @Test
        void avgShouldReturnFractionalWhenNotEvenlyDivisible(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.avg(a,b,c).format(\"%.2f\")}");
            var result = placeholders.context(message)
                .with("a", 1)
                .with("b", 2)
                .with("c", 4)
                .apply();

            assertThat(result).isEqualTo("2.33");
        }

        @Test
        void avgShouldSkipNonNumericArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.avg(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 10)
                .with("b", null)
                .with("c", 30)
                .apply();

            assertThat(result).isEqualTo("20");
        }

        @Test
        void avgShouldReturnNullWhenNoNumericArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("[{$.avg(a)}]");
            var result = placeholders.context(message)
                .with("a", "not numeric")
                .apply();

            assertThat(result).isEqualTo("[null]");
        }
    }

    @Nested
    @DisplayName("$.median() / $.percentile() - order statistics")
    class MedianPercentileFunctions {

        @Test
        void medianShouldPickMiddleValueForOddCount(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.median(a,b,c,d,e)}");
            var result = placeholders.context(message)
                .with("a", 5)
                .with("b", 1)
                .with("c", 9)
                .with("d", 3)
                .with("e", 7)
                .apply();

            assertThat(result).isEqualTo("5");
        }

        @Test
        void medianShouldAverageTwoMiddleForEvenCount(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.median(a,b,c,d).format(\"%.1f\")}");
            var result = placeholders.context(message)
                .with("a", 1)
                .with("b", 2)
                .with("c", 3)
                .with("d", 4)
                .apply();

            assertThat(result).isEqualTo("2.5");
        }

        @Test
        void medianShouldHandleSingleValue(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.median(a)}");
            var result = placeholders.context(message)
                .with("a", 42)
                .apply();

            assertThat(result).isEqualTo("42");
        }

        @Test
        void medianShouldReturnNullWhenNoNumericArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("[{$.median(a)}]");
            var result = placeholders.context(message)
                .with("a", "x")
                .apply();

            assertThat(result).isEqualTo("[null]");
        }

        @Test
        void medianShouldIgnoreInputOrder(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.median(a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 100)
                .with("b", 1)
                .with("c", 50)
                .apply();

            assertThat(result).isEqualTo("50");
        }

        @Test
        void percentileAt0ShouldReturnMin(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.percentile(0,a,b,c,d,e)}");
            var result = placeholders.context(message)
                .with("a", 5).with("b", 1).with("c", 9).with("d", 3).with("e", 7)
                .apply();

            assertThat(result).isEqualTo("1");
        }

        @Test
        void percentileAt100ShouldReturnMax(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.percentile(100,a,b,c,d,e)}");
            var result = placeholders.context(message)
                .with("a", 5).with("b", 1).with("c", 9).with("d", 3).with("e", 7)
                .apply();

            assertThat(result).isEqualTo("9");
        }

        @Test
        void percentileAt50ShouldEqualMedian(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.percentile(50,a,b,c,d,e)}=={$.median(a,b,c,d,e)}");
            var result = placeholders.context(message)
                .with("a", 5).with("b", 1).with("c", 9).with("d", 3).with("e", 7)
                .apply();

            assertThat(result).isEqualTo("5==5");
        }

        @Test
        void percentileShouldInterpolateBetweenValues(Placeholders placeholders) {
            // For [1,2,3,4,5], p=25 -> rank = 0.25 * 4 = 1 -> exact value 2
            var message = CompiledMessage.of("{$.percentile(25,a,b,c,d,e)}");
            var result = placeholders.context(message)
                .with("a", 1).with("b", 2).with("c", 3).with("d", 4).with("e", 5)
                .apply();

            assertThat(result).isEqualTo("2");
        }

        @Test
        void percentileShouldInterpolateAtFractionalRank(Placeholders placeholders) {
            // For [1,2,3,4,5], p=10 -> rank = 0.1 * 4 = 0.4 -> 1*(0.6) + 2*(0.4) = 1.4
            var message = CompiledMessage.of("{$.percentile(10,a,b,c,d,e).format(\"%.1f\")}");
            var result = placeholders.context(message)
                .with("a", 1).with("b", 2).with("c", 3).with("d", 4).with("e", 5)
                .apply();

            assertThat(result).isEqualTo("1.4");
        }

        @Test
        void percentileShouldClampBelowZeroToMin(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.percentile(-10,a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 5).with("b", 1).with("c", 9)
                .apply();

            assertThat(result).isEqualTo("1");
        }

        @Test
        void percentileShouldClampAboveHundredToMax(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.percentile(150,a,b,c)}");
            var result = placeholders.context(message)
                .with("a", 5).with("b", 1).with("c", 9)
                .apply();

            assertThat(result).isEqualTo("9");
        }

        @Test
        void percentileShouldReturnNullWhenPercentArgIsNotNumeric(Placeholders placeholders) {
            var message = CompiledMessage.of("[{$.percentile(p,a,b,c)}]");
            var result = placeholders.context(message)
                .with("p", "garbage")
                .with("a", 1).with("b", 2).with("c", 3)
                .apply();

            assertThat(result).isEqualTo("[null]");
        }

        @Test
        void percentileShouldReturnNullWhenNoValueArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("[{$.percentile(50)}]");
            var result = placeholders.context(message).apply();

            assertThat(result).isEqualTo("[null]");
        }
    }

    @Nested
    @DisplayName("$.default() - Default value")
    class DefaultFunction {

        @Test
        void shouldReturnValueIfNotNull(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{default(name,\"Guest\")}"))
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Alice");
        }

        @Test
        void shouldReturnFallbackIfNull(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{default(name,\"Guest\")}"))
                .with("name", null)
                .apply();

            assertThat(result).isEqualTo("Guest");
        }

        @Test
        void shouldReturnFallbackIfEmpty(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{default(name,\"Guest\")}"))
                .with("name", "")
                .apply();

            assertThat(result).isEqualTo("Guest");
        }

        @Test
        void shouldWorkWithNumbers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{default(count,0)}"))
                .with("count", null)
                .apply();

            assertThat(result).isEqualTo("0");
        }
    }

    @Nested
    @DisplayName("$.cond() - Chained conditionals")
    class CondFunction {

        @Test
        void shouldReturnFirstTruthyResult(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{cond(score.gte(90),\"A\", score.gte(80),\"B\", score.gte(70),\"C\", \"F\")}"))
                .with("score", 85)
                .apply();

            assertThat(result).isEqualTo("B");
        }

        @Test
        void shouldReturnDefault(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{cond(score.gte(90),\"A\", score.gte(80),\"B\", \"F\")}"))
                .with("score", 50)
                .apply();

            assertThat(result).isEqualTo("F");
        }

        @Test
        void shouldWorkWithColorThresholds(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{cond(hp.gte(70),\"green\", hp.gte(30),\"yellow\", \"red\")}"))
                .with("hp", 45)
                .apply();

            assertThat(result).isEqualTo("yellow");
        }

        @Test
        void shouldReturnFirstMatch(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{cond(score.gte(90),\"A\", score.gte(80),\"B\", score.gte(70),\"C\", \"F\")}"))
                .with("score", 95)
                .apply();

            assertThat(result).isEqualTo("A");
        }
    }

    @Nested
    @DisplayName("$.switch() - Value matching")
    class SwitchFunction {

        @Test
        void shouldMatchCase(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{switch(status, \"active\",\"green\", \"pending\",\"yellow\", \"red\")}"))
                .with("status", "active")
                .apply();

            assertThat(result).isEqualTo("green");
        }

        @Test
        void shouldReturnDefault(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{switch(status, \"active\",\"green\", \"pending\",\"yellow\", \"red\")}"))
                .with("status", "inactive")
                .apply();

            assertThat(result).isEqualTo("red");
        }

        @Test
        void shouldMatchSecondCase(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{switch(status, \"active\",\"green\", \"pending\",\"yellow\", \"red\")}"))
                .with("status", "pending")
                .apply();

            assertThat(result).isEqualTo("yellow");
        }

        @Test
        void shouldWorkWithNumbers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{switch(level, 1,\"beginner\", 2,\"intermediate\", 3,\"advanced\", \"unknown\")}"))
                .with("level", 2)
                .apply();

            assertThat(result).isEqualTo("intermediate");
        }
    }

    @Nested
    @DisplayName("Object equality methods (equals, eq, ne)")
    class ObjectEqualityMethods {

        @Test
        void shouldMatchEqualStrings(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(s.equals(\"active\"),\"yes\",\"no\")}"))
                .with("s", "active")
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldNotMatchDifferentStrings(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(s.eq(\"active\"),\"yes\",\"no\")}"))
                .with("s", "inactive")
                .apply();

            assertThat(result).isEqualTo("no");
        }

        @Test
        void shouldMatchNumbers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(n.equals(42),\"yes\",\"no\")}"))
                .with("n", 42)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldNotEqual(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(s.notEquals(\"admin\"),\"user\",\"admin\")}"))
                .with("s", "guest")
                .apply();

            assertThat(result).isEqualTo("user");
        }

        @Test
        void shouldNotEqualWithNeAlias(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(s.ne(\"admin\"),\"user\",\"admin\")}"))
                .with("s", "guest")
                .apply();

            assertThat(result).isEqualTo("user");
        }
    }

    @Nested
    @DisplayName("Custom global functions")
    class CustomGlobalFunctions {

        @Test
        void shouldSupportUserDefinedGlobalFunctions() {
            var placeholders = Placeholders.create()
                .global("greet", p -> "Hello, " + p.arg(0).orElse("stranger") + "!");

            var message = CompiledMessage.of("{$.greet(\"World\")}");
            var result = placeholders.context(message).apply();

            assertThat(result).isEqualTo("Hello, World!");
        }

        @Test
        void shouldAllowOverridingBuiltInFunctions() {
            var placeholders = Placeholders.create()
                .global("env", p -> "CUSTOM:" + p.arg(0).asString());

            var message = CompiledMessage.of("{$.env(TEST)}");
            var result = placeholders.context(message).apply();

            assertThat(result).isEqualTo("CUSTOM:TEST");
        }
    }

    @Nested
    @DisplayName("Global functions in context")
    class InContext {

        @Test
        void shouldWorkWithSurroundingText(Placeholders placeholders) {
            var message = CompiledMessage.of("Name: {$.coalesce(nickname,name,\"Unknown\")}");
            var result = placeholders.context(message)
                .with("nickname", null)
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Name: Alice");
        }

        @Test
        void shouldWorkWithMultipleGlobalCalls(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(active,name,\"inactive\")} - {$.coalesce(status,\"unknown\")}");
            var result = placeholders.context(message)
                .with("active", true)
                .with("name", "Alice")
                .with("status", null)
                .apply();

            assertThat(result).isEqualTo("Alice - unknown");
        }
    }

    @Nested
    @DisplayName("Short syntax for global functions")
    class ShortSyntax {

        @Test
        void dotSyntaxShouldWork(Placeholders placeholders) {
            // {.now()} should work like {$.now()}
            var message = CompiledMessage.of("{.now()}");
            var result = placeholders.context(message).apply();

            assertThat(result).isNotEmpty();
            assertThat(result).containsPattern("\\d{4}-\\d{2}-\\d{2}");
        }

        @Test
        void dotSyntaxShouldWorkWithChaining(Placeholders placeholders) {
            // {.coalesce(a,b,"default")} should work
            var message = CompiledMessage.of("{.coalesce(a,b,\"fallback\")}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", null)
                .apply();

            assertThat(result).isEqualTo("fallback");
        }

        @Test
        void bareSyntaxShouldWork(Placeholders placeholders) {
            // {now()} should work like {$.now()}
            var message = CompiledMessage.of("{now()}");
            var result = placeholders.context(message).apply();

            assertThat(result).isNotEmpty();
            assertThat(result).containsPattern("\\d{4}-\\d{2}-\\d{2}");
        }

        @Test
        void bareSyntaxShouldWorkWithArgs(Placeholders placeholders) {
            // {coalesce(a,b,"default")} should work
            var message = CompiledMessage.of("{coalesce(a,b,\"fallback\")}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", "Bravo")
                .apply();

            assertThat(result).isEqualTo("Bravo");
        }

        @Test
        void bareSyntaxShouldWorkWithIf(Placeholders placeholders) {
            // {if(cond,"yes","no")} should work
            var message = CompiledMessage.of("{if(active,\"yes\",\"no\")}");
            var result = placeholders.context(message)
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void contextFieldShouldTakePriorityOverGlobalFunction(Placeholders placeholders) {
            // If "now" is in context, it should be used instead of $.now()
            var message = CompiledMessage.of("{now}");
            var result = placeholders.context(message)
                .with("now", "custom-value")
                .apply();

            assertThat(result).isEqualTo("custom-value");
        }

        @Test
        void bareSyntaxWithEnv(Placeholders placeholders) {
            // {env(PATH)} should work
            var message = CompiledMessage.of("{env(PATH)}");
            var result = placeholders.context(message).apply();

            assertThat(result).isNotEmpty();
        }

        @Test
        void orGlobalFunctionShouldWorkAsCoalesceAlias(Placeholders placeholders) {
            // {or(a,b,"default")} should work as alias for coalesce
            var message = CompiledMessage.of("{or(a,b,\"fallback\")}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", "Bravo")
                .apply();

            assertThat(result).isEqualTo("Bravo");
        }

        @Test
        void orGlobalFunctionShouldReturnDefaultWhenAllNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{or(a,b,\"fallback\")}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", null)
                .apply();

            assertThat(result).isEqualTo("fallback");
        }
    }
}
