package eu.okaeri.placeholders.metadata;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for printf-style number formatting metadata.
 * Note: The library converts all numbers to doubles before formatting,
 * so only %f, %e, %g format specifiers work. %d will throw an error.
 */
@DisplayName("Number formatting metadata")
class NumberFormattingTest {

    @Nested
    @DisplayName("Printf-style decimal formatting")
    class DecimalFormatting {

        @Test
        void shouldFormatWithTwoDecimalPlaces() {
            var message = CompiledMessage.of("{%.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 0.2)
                .apply();

            assertThat(result).isEqualTo("0.20");
        }

        @Test
        void shouldFormatWithZeroDecimalPlaces() {
            var message = CompiledMessage.of("{%.0f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 3.7)
                .apply();

            assertThat(result).isEqualTo("4");
        }

        @Test
        void shouldFormatWithFourDecimalPlaces() {
            var message = CompiledMessage.of("{%.4f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 0.1)
                .apply();

            assertThat(result).isEqualTo("0.1000");
        }

        @ParameterizedTest
        @CsvSource({
            "0.2, %.2f, 0.20",
            "1.0, %.2f, 1.00",
            "3.14159, %.2f, 3.14",
            "3.14159, %.4f, 3.1416",
            "0.6, %.0f, 1",
            "0.2, %.0f, 0"
        })
        void shouldFormatDecimals(double input, String format, String expected) {
            var message = CompiledMessage.of("{" + format + "#value}");
            var result = PlaceholderContext.of(message)
                .with("value", input)
                .apply();

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Integer formatting (as float)")
    class IntegerFormatting {

        @Test
        void shouldFormatIntegerAsFloat() {
            // Note: %d doesn't work - library converts all numbers to doubles
            var message = CompiledMessage.of("{%.0f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 42)
                .apply();

            assertThat(result).isEqualTo("42");
        }

        @Test
        void shouldFormatIntegerToFloatWithDecimals() {
            var message = CompiledMessage.of("{%.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 1)
                .apply();

            assertThat(result).isEqualTo("1.00");
        }
    }

    @Nested
    @DisplayName("Padding and alignment (with floats)")
    class PaddingAndAlignment {

        @Test
        void shouldPadWithZeros() {
            var message = CompiledMessage.of("{%08.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 42.0)
                .apply();

            assertThat(result).isEqualTo("00042.00");
        }

        @Test
        void shouldPadWithSpaces() {
            var message = CompiledMessage.of("{%8.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 42.0)
                .apply();

            assertThat(result).isEqualTo("   42.00");
        }

        @Test
        void shouldLeftAlign() {
            var message = CompiledMessage.of("{%-8.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 42.0)
                .apply();

            assertThat(result).isEqualTo("42.00   ");
        }
    }

    @Nested
    @DisplayName("Sign handling")
    class SignHandling {

        @Test
        void shouldShowPlusSign() {
            var message = CompiledMessage.of("{%+.0f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 42)
                .apply();

            assertThat(result).isEqualTo("+42");
        }

        @Test
        void shouldShowMinusForNegative() {
            var message = CompiledMessage.of("{%+.0f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", -42)
                .apply();

            assertThat(result).isEqualTo("-42");
        }

        @Test
        void shouldShowPlusOnFloat() {
            var message = CompiledMessage.of("{%+.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 3.14)
                .apply();

            assertThat(result).isEqualTo("+3.14");
        }
    }

    @Nested
    @DisplayName("Grouping separator")
    class GroupingSeparator {

        @Test
        void shouldGroupThousands() {
            var message = CompiledMessage.of(Locale.US, "{%,.0f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 1234567)
                .apply();

            assertThat(result).isEqualTo("1,234,567");
        }

        @Test
        void shouldGroupThousandsWithDecimals() {
            var message = CompiledMessage.of(Locale.US, "{%,.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 1234567.89)
                .apply();

            assertThat(result).isEqualTo("1,234,567.89");
        }
    }

    @Nested
    @DisplayName("In context")
    class InContext {

        @Test
        void shouldWorkWithSurroundingText() {
            var message = CompiledMessage.of("Value: {%.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 3.14159)
                .apply();

            assertThat(result).isEqualTo("Value: 3.14");
        }

        @Test
        void shouldHandleMultipleFormattedNumbers() {
            var message = CompiledMessage.of("A: {%.1f#a}, B: {%.0f#b}");
            var result = PlaceholderContext.of(message)
                .with("a", 1.5)
                .with("b", 42)
                .apply();

            assertThat(result).isEqualTo("A: 1.5, B: 42");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        void shouldHandleZero() {
            var message = CompiledMessage.of("{%.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 0.0)
                .apply();

            assertThat(result).isEqualTo("0.00");
        }

        @Test
        void shouldHandleNegative() {
            var message = CompiledMessage.of("{%.2f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", -3.14)
                .apply();

            assertThat(result).isEqualTo("-3.14");
        }

        @Test
        void shouldHandleRounding() {
            var message = CompiledMessage.of("{%.1f#value}");
            var result = PlaceholderContext.of(message)
                .with("value", 2.55)
                .apply();

            // Java rounds 2.55 to 2.5 (banker's rounding) or 2.6 depending on representation
            assertThat(result).matches("2\\.[56]");
        }
    }
}
