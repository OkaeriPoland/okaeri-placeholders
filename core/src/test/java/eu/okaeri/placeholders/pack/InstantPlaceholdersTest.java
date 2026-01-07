package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Instant placeholders")
@ExtendWith(PlaceholdersExtension.class)
class InstantPlaceholdersTest {

    private static final Instant EPOCH = Instant.ofEpochSecond(0);
    private static final Instant Y2K = Instant.parse("2000-01-01T12:00:00Z");

    @Nested
    @DisplayName("Localized time (lt)")
    class LocalizedTime {

        @Test
        void shouldFormatShortTimeEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{lt,short,UTC#time}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).contains(":00");
        }

        @Test
        void shouldFormatMediumTimeEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{lt,medium,UTC#time}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).contains("12:00:00");
        }

        @Test
        void shouldFormatTimeWithParisTimezone(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{lt,short,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("1:00");
        }

        @Test
        void shouldFormatTimeWithPolishLocale(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.forLanguageTag("pl"), "{lt,short,Europe/Warsaw#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("01:00");
        }

        @Test
        void shouldFormatTimeWithJapaneseLocale(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.JAPAN, "{lt,short,Asia/Tokyo#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("9:00");
        }
    }

    @Nested
    @DisplayName("Localized date-time (ldt)")
    class LocalizedDateTime {

        @Test
        void shouldFormatShortDateTimeEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ldt,short,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            // Contains date and time
            assertThat(result).contains("1/1/70");
            assertThat(result).contains(":00");
        }

        @Test
        void shouldFormatMediumDateTimeEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ldt,medium,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("Jan");
            assertThat(result).contains("1970");
        }

        @Test
        void shouldFormatLongDateTimeEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ldt,long,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("January");
            assertThat(result).contains("1970");
        }

        @Test
        void shouldFormatDateTimeWithPolishLocale(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.forLanguageTag("pl"), "{ldt,short,Europe/Warsaw#time}"))
                .with("time", EPOCH)
                .apply();

            // Note: Polish format may or may not pad day with zeros depending on JDK version
            assertThat(result).contains("1970");
            assertThat(result).contains("01");
        }

        @Test
        void shouldFormatDateTimeWithJapaneseLocale(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.JAPAN, "{ldt,short,Asia/Tokyo#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("1970");
        }
    }

    @Nested
    @DisplayName("Localized date (ld)")
    class LocalizedDate {

        @Test
        void shouldFormatShortDateEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ld,short,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("1/1/70");
        }

        @Test
        void shouldFormatMediumDateEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ld,medium,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("Jan 1, 1970");
        }

        @Test
        void shouldFormatLongDateEnglish(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ld,long,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("January 1, 1970");
        }

        @Test
        void shouldFormatDateWithPolishLocale(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.forLanguageTag("pl"), "{ld,short,Europe/Warsaw#time}"))
                .with("time", EPOCH)
                .apply();

            // Note: Polish format may or may not pad day with zeros depending on JDK version
            assertThat(result).contains("1970");
            assertThat(result).contains(".01.");
        }
    }

    @Nested
    @DisplayName("Custom pattern (p)")
    class CustomPattern {

        @Test
        void shouldFormatWithCustomPattern(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{p,yyyy-MM-dd,UTC#time}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).isEqualTo("2000-01-01");
        }

        @Test
        void shouldFormatWithTimePattern(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{p,HH:mm:ss,UTC#time}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).isEqualTo("12:00:00");
        }

        @Test
        void shouldFormatWithEscapedComma(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{p,yyyy/MM/dd\\, HH:mm G,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("1970/01/01, 01:00 AD");
        }

        @Test
        void shouldRespectLocaleInPattern(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.forLanguageTag("pl"), "{p,yyyy/MM/dd\\, HH:mm G,Europe/Paris#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("1970/01/01, 01:00 n.e.");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        void shouldHandleEpoch(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ld,short,UTC#time}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isNotEmpty();
        }

        @Test
        void shouldHandleFutureDate(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{ld,short,UTC#time}"))
                .with("time", Instant.parse("2099-12-31T23:59:59Z"))
                .apply();

            assertThat(result).contains("12/31/99");
        }
    }

    @Nested
    @DisplayName("Method-style .time() syntax")
    class MethodStyleTime {

        @Test
        void shouldFormatTimeWithDefaultStyle(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.time(\"short\",\"UTC\")}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).contains(":00");
        }

        @Test
        void shouldFormatMediumTime(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.time(\"medium\",\"UTC\")}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).contains("12:00:00");
        }

        @Test
        void shouldFormatTimeWithTimezone(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.time(\"short\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("1:00");
        }
    }

    @Nested
    @DisplayName("Method-style .date() syntax")
    class MethodStyleDate {

        @Test
        void shouldFormatShortDate(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.date(\"short\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("1/1/70");
        }

        @Test
        void shouldFormatMediumDate(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.date(\"medium\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("Jan 1, 1970");
        }

        @Test
        void shouldFormatLongDate(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.date(\"long\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("January 1, 1970");
        }
    }

    @Nested
    @DisplayName("Method-style .datetime() syntax")
    class MethodStyleDateTime {

        @Test
        void shouldFormatShortDateTime(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.datetime(\"short\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("1/1/70");
            assertThat(result).contains(":00");
        }

        @Test
        void shouldFormatMediumDateTime(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.datetime(\"medium\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("Jan");
            assertThat(result).contains("1970");
        }

        @Test
        void shouldFormatLongDateTime(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.datetime(\"long\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).contains("January");
            assertThat(result).contains("1970");
        }
    }

    @Nested
    @DisplayName("Method-style .format() with custom pattern")
    class MethodStyleFormat {

        @Test
        void shouldFormatWithCustomPattern(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.format(\"yyyy-MM-dd\",\"UTC\")}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).isEqualTo("2000-01-01");
        }

        @Test
        void shouldFormatWithTimePattern(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.format(\"HH:mm:ss\",\"UTC\")}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).isEqualTo("12:00:00");
        }

        @Test
        void shouldFormatWithFullPattern(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.format(\"yyyy-MM-dd HH:mm:ss\",\"UTC\")}"))
                .with("time", Y2K)
                .apply();

            assertThat(result).isEqualTo("2000-01-01 12:00:00");
        }

        @Test
        void shouldRespectTimezone(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, "{time.format(\"yyyy-MM-dd HH:mm\",\"Europe/Paris\")}"))
                .with("time", EPOCH)
                .apply();

            assertThat(result).isEqualTo("1970-01-01 01:00");
        }
    }
}
