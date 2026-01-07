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

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Duration placeholders")
@ExtendWith(PlaceholdersExtension.class)
class DurationPlaceholdersTest {

    @Nested
    @DisplayName("Simple durations")
    class SimpleDurations {

        @Test
        void shouldFormatDays(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofDays(1))
                .apply();

            assertThat(result).isEqualTo("1d");
        }

        @Test
        void shouldFormatHours(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofHours(2))
                .apply();

            assertThat(result).isEqualTo("2h");
        }

        @Test
        void shouldFormatMinutes(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofMinutes(30))
                .apply();

            assertThat(result).isEqualTo("30m");
        }

        @Test
        void shouldFormatSeconds(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofSeconds(45))
                .apply();

            assertThat(result).isEqualTo("45s");
        }

        @Test
        void shouldFormatMilliseconds(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofMillis(500))
                .apply();

            assertThat(result).isEqualTo("500ms");
        }
    }

    @Nested
    @DisplayName("Combined durations")
    class CombinedDurations {

        @Test
        void shouldFormatDaysAndHours(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofHours(25))
                .apply();

            assertThat(result).isEqualTo("1d1h");
        }

        @Test
        void shouldFormatHoursAndMinutes(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofMinutes(90))
                .apply();

            assertThat(result).isEqualTo("1h30m");
        }

        @Test
        void shouldFormatHoursMinutesSeconds(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofSeconds(3661))
                .apply();

            assertThat(result).isEqualTo("1h1m1s");
        }

        @Test
        void shouldFormatLargeDuration(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofDays(30).plusHours(12))
                .apply();

            assertThat(result).isEqualTo("30d12h");
        }
    }

    @Nested
    @DisplayName("Zero duration")
    class ZeroDuration {

        @Test
        void shouldFormatZeroAsSeconds(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ZERO)
                .apply();

            assertThat(result).isEqualTo("0s");
        }
    }

    @Nested
    @DisplayName("Negative duration")
    class NegativeDuration {

        @Test
        void shouldFormatNegativeDuration(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofHours(-2))
                .apply();

            assertThat(result).startsWith("-");
        }
    }

    @Nested
    @DisplayName("Large values")
    class LargeValues {

        @Test
        void shouldFormatYearEquivalent(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofDays(365))
                .apply();

            assertThat(result).isEqualTo("365d");
        }

        @Test
        void shouldFormatWeekEquivalent(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.ofDays(7))
                .apply();

            assertThat(result).isEqualTo("7d");
        }
    }

    @Nested
    @DisplayName("Parameterized tests")
    class ParameterizedTests {

        @ParameterizedTest
        @CsvSource({
            "PT1H, 1h",
            "PT2H30M, 2h30m",
            "PT24H, 1d",
            "PT25H, 1d1h",
            "PT0S, 0s",
            "PT1M30S, 1m30s",
            "P1D, 1d",
            "P1DT12H, 1d12h"
        })
        void shouldFormatVariousDurations(String isoDuration, String expected, Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{d}"))
                .with("d", Duration.parse(isoDuration))
                .apply();

            assertThat(result).isEqualTo(expected);
        }
    }
}
