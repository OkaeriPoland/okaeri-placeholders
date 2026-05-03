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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Number placeholders")
@ExtendWith(PlaceholdersExtension.class)
class NumberPlaceholdersTest {

    @Nested
    @DisplayName("Integer operations")
    class IntegerOperations {

        @Test
        void shouldAddIntegers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.add(5)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("15");
        }

        @Test
        void shouldUsePlusAlias(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.plus(5)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("15");
        }

        @Test
        void shouldSubtractIntegers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.minus(3)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("7");
        }

        @Test
        void shouldUseSubtractAlias(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.subtract(3)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("7");
        }

        @Test
        void shouldMultiplyIntegers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.multiply(3)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("30");
        }

        @Test
        void shouldDivideIntegers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.divide(2)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("5");
        }

        @Test
        void shouldHandleIntegerDivision(Placeholders placeholders) {
            // Integer division truncates
            var result = placeholders.context(CompiledMessage.of("{n.divide(3)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void shouldHandleNegativeResult(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.minus(20)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("-10");
        }

        @Test
        void shouldMultiplyByZero(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.multiply(0)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldChainOperations(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.multiply(2).add(5)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("25");
        }

        @ParameterizedTest
        @CsvSource({
            "10, add(5), 15",
            "10, plus(5), 15",
            "10, minus(3), 7",
            "10, subtract(3), 7",
            "10, multiply(3), 30",
            "10, divide(2), 5",
            "0, add(10), 10",
            "-5, add(10), 5"
        })
        void shouldPerformIntegerArithmetic(int input, String operation, int expected, Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n." + operation + "}"))
                .with("n", input)
                .apply();

            assertThat(result).isEqualTo(String.valueOf(expected));
        }
    }

    @Nested
    @DisplayName("Double operations")
    class DoubleOperations {

        @Test
        void shouldAddDoubles(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.add(0.5)}"))
                .with("n", 10.0)
                .apply();

            assertThat(result).isEqualTo("10.50");
        }

        @Test
        void shouldSubtractDoubles(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.minus(0.25)}"))
                .with("n", 10.0)
                .apply();

            assertThat(result).isEqualTo("9.75");
        }

        @Test
        void shouldMultiplyDoublesWithWholeResult(Placeholders placeholders) {
            // 10.0 * 2.5 = 25.0 -> returns integer since result is whole
            var result = placeholders.context(CompiledMessage.of("{n.multiply(2.5)}"))
                .with("n", 10.0)
                .apply();

            assertThat(result).isEqualTo("25");
        }

        @Test
        void shouldDivideDoubles(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.divide(4.0)}"))
                .with("n", 10.0)
                .apply();

            assertThat(result).isEqualTo("2.50");
        }

        @Test
        void shouldDivideDoublesWithWholeResult(Placeholders placeholders) {
            // 10.0 / 2.0 = 5.0 -> returns integer since result is whole
            var result = placeholders.context(CompiledMessage.of("{n.divide(2.0)}"))
                .with("n", 10.0)
                .apply();

            assertThat(result).isEqualTo("5");
        }

        @ParameterizedTest
        @CsvSource({
            "10.0, add(0.5), 10.50",
            "10.0, plus(0.5), 10.50",
            "10.0, minus(0.25), 9.75",
            "10.0, multiply(2.5), 25",
            "10.0, divide(4.0), 2.50",
            "10.0, divide(2.0), 5",
            "10.0, add(5.0), 15"
        })
        void shouldPerformDoubleArithmetic(double input, String operation, String expected, Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n." + operation + "}"))
                .with("n", input)
                .apply();

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Long values")
    class LongValues {

        @Test
        void shouldRenderLongAsString(Placeholders placeholders) {
            // Note: Long doesn't have arithmetic operations registered in DefaultPlaceholderPack
            var result = placeholders.context(CompiledMessage.of("{n}"))
                .with("n", 1000000000L)
                .apply();

            assertThat(result).isEqualTo("1000000000");
        }

        @Test
        void shouldHandleLargeNumbers(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n}"))
                .with("n", Long.MAX_VALUE)
                .apply();

            assertThat(result).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Float operations")
    class FloatOperations {

        @Test
        void shouldAddFloats(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.add(0.5)}"))
                .with("n", 10.0f)
                .apply();

            assertThat(result).isEqualTo("10.50");
        }

        @Test
        void shouldMultiplyFloatsWithWholeResult(Placeholders placeholders) {
            // 5.0 * 2.0 = 10.0 -> returns integer since result is whole
            var result = placeholders.context(CompiledMessage.of("{n.multiply(2.0)}"))
                .with("n", 5.0f)
                .apply();

            assertThat(result).isEqualTo("10");
        }
    }

    @Nested
    @DisplayName("Short operations")
    class ShortOperations {

        @Test
        void shouldAddShorts(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.add(5)}"))
                .with("n", (short) 10)
                .apply();

            assertThat(result).isEqualTo("15");
        }

        @Test
        void shouldMultiplyShorts(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.multiply(3)}"))
                .with("n", (short) 10)
                .apply();

            assertThat(result).isEqualTo("30");
        }

        @Test
        void shouldReturnDecimalWhenNotWhole(Placeholders placeholders) {
            // 10 / 4 = 2.5 -> not whole, returns decimal
            var result = placeholders.context(CompiledMessage.of("{n.divide(4)}"))
                .with("n", (short) 10)
                .apply();

            assertThat(result).isEqualTo("2.50");
        }
    }

    @Nested
    @DisplayName("Byte operations")
    class ByteOperations {

        @Test
        void shouldAddBytes(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.add(5)}"))
                .with("n", (byte) 10)
                .apply();

            assertThat(result).isEqualTo("15");
        }

        @Test
        void shouldMultiplyBytes(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.multiply(3)}"))
                .with("n", (byte) 10)
                .apply();

            assertThat(result).isEqualTo("30");
        }

        @Test
        void shouldReturnDecimalWhenNotWhole(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.divide(4)}"))
                .with("n", (byte) 10)
                .apply();

            assertThat(result).isEqualTo("2.50");
        }
    }

    @Nested
    @DisplayName("Math functions (abs, round, floor, ceil)")
    class MathFunctions {

        @Test
        void shouldReturnAbsoluteValueOfNegative(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.abs}"))
                .with("n", -42)
                .apply();

            assertThat(result).isEqualTo("42");
        }

        @Test
        void shouldReturnPositiveAsIs(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.abs}"))
                .with("n", 42)
                .apply();

            assertThat(result).isEqualTo("42");
        }

        @Test
        void shouldAbsWorkWithDouble(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.abs}"))
                .with("n", -3.14)
                .apply();

            assertThat(result).isEqualTo("3.14");
        }

        @Test
        void shouldRoundToInteger(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.round}"))
                .with("n", 3.7)
                .apply();

            assertThat(result).isEqualTo("4");
        }

        @Test
        void shouldRoundDown(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.round}"))
                .with("n", 3.2)
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void shouldRoundToSpecifiedPrecision(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.round(2)}"))
                .with("n", 3.14159)
                .apply();

            assertThat(result).isEqualTo("3.14");
        }

        @Test
        void shouldFloor(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.floor}"))
                .with("n", 3.9)
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void shouldCeil(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.ceil}"))
                .with("n", 3.1)
                .apply();

            assertThat(result).isEqualTo("4");
        }

        @Test
        void shouldFloorNegative(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.floor}"))
                .with("n", -3.1)
                .apply();

            assertThat(result).isEqualTo("-4");
        }

        @Test
        void shouldCeilNegative(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.ceil}"))
                .with("n", -3.9)
                .apply();

            assertThat(result).isEqualTo("-3");
        }

        @Test
        void shouldCombineAbsWithComparison(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(price.abs.gt(100),\"expensive\",\"affordable\")}"))
                .with("price", -150)
                .apply();

            assertThat(result).isEqualTo("expensive");
        }
    }

    @Nested
    @DisplayName("Comparison methods (gt, gte, lt, lte)")
    class ComparisonMethods {

        @Test
        void shouldTestGreaterThan(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(n.gt(5),\"big\",\"small\")}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("big");
        }

        @Test
        void shouldTestLessThan(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(n.lt(5),\"small\",\"big\")}"))
                .with("n", 3)
                .apply();

            assertThat(result).isEqualTo("small");
        }

        @Test
        void shouldTestGreaterOrEqual(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(n.gte(5),\"yes\",\"no\")}"))
                .with("n", 5)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldTestLessOrEqual(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(n.lte(5),\"yes\",\"no\")}"))
                .with("n", 5)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldCombineComparisonWithFormatting(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(score.gte(50),\"Pass\",\"Fail\")}: {score}%"))
                .with("score", 75)
                .apply();

            assertThat(result).isEqualTo("Pass: 75%");
        }
    }

    @Nested
    @DisplayName("Range methods (between, clamp, mod)")
    class RangeMethods {

        @Test
        void shouldReturnTrueWhenBetween(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.between(10,20)}"))
                .with("n", 15)
                .apply();

            assertThat(result).isEqualTo("true");
        }

        @Test
        void shouldReturnFalseWhenNotBetween(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.between(10,20)}"))
                .with("n", 25)
                .apply();

            assertThat(result).isEqualTo("false");
        }

        @Test
        void shouldReturnTrueAtLowerBound(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.between(10,20)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("true");
        }

        @Test
        void shouldReturnTrueAtUpperBound(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.between(10,20)}"))
                .with("n", 20)
                .apply();

            assertThat(result).isEqualTo("true");
        }

        @Test
        void shouldClampToMin(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.clamp(0,100)}"))
                .with("n", -50)
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldClampToMax(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.clamp(0,100)}"))
                .with("n", 150)
                .apply();

            assertThat(result).isEqualTo("100");
        }

        @Test
        void shouldNotClampWhenInRange(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.clamp(0,100)}"))
                .with("n", 50)
                .apply();

            assertThat(result).isEqualTo("50");
        }

        @Test
        void shouldCalculateMod(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.mod(3)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("1");
        }

        @Test
        void shouldReturnZeroWhenDivisible(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.mod(5)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldUseBetweenInCondition(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{$.if(n.between(34,66),\"medium\",\"other\")}"))
                .with("n", 50)
                .apply();

            assertThat(result).isEqualTo("medium");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        void shouldHandleZero(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.add(10)}"))
                .with("n", 0)
                .apply();

            assertThat(result).isEqualTo("10");
        }

        @Test
        void shouldHandleNegativeInput(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.multiply(2)}"))
                .with("n", -5)
                .apply();

            assertThat(result).isEqualTo("-10");
        }

        @Test
        void shouldHandleNegativeOperand(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.add(-5)}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("5");
        }
    }

    @Nested
    @DisplayName("signed - explicit-sign formatting")
    class Signed {

        @Test
        void shouldPrefixPositiveIntWithPlus(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.signed}"))
                .with("n", 5)
                .apply();

            assertThat(result).isEqualTo("+5");
        }

        @Test
        void shouldPreserveNegativeIntSign(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.signed}"))
                .with("n", -3)
                .apply();

            assertThat(result).isEqualTo("-3");
        }

        @Test
        void shouldRenderZeroWithoutSign(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.signed}"))
                .with("n", 0)
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldHandlePositiveDouble(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.signed}"))
                .with("n", 3.14)
                .apply();

            assertThat(result).isEqualTo("+3.14");
        }

        @Test
        void shouldHandleNegativeDouble(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.signed}"))
                .with("n", -2.5)
                .apply();

            assertThat(result).isEqualTo("-2.5");
        }

        @Test
        void shouldNormalizeWholeDoubleToInt(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.signed}"))
                .with("n", 5.0)
                .apply();

            assertThat(result).isEqualTo("+5");
        }

        @Test
        void shouldRenderNegativeZeroAsZero(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.signed}"))
                .with("n", -0.0)
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldComposeWithArithmetic(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.minus(3).signed}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("+7");
        }

        @Test
        void shouldComposeWithNegativeArithmeticResult(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{n.minus(15).signed}"))
                .with("n", 10)
                .apply();

            assertThat(result).isEqualTo("-5");
        }
    }
}
