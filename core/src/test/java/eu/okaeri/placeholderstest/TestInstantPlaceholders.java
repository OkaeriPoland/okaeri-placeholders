package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestInstantPlaceholders {

    private final Placeholders placeholders = Placeholders.create(true);

    private void test(Instant time, String expect, String pattern, Locale locale) {
        assertEquals(expect, this.placeholders.contextOf(CompiledMessage.of(locale, pattern))
            .with("time", time)
            .apply());
    }

    @Test
    public void test_format_lt() {
        this.test(Instant.ofEpochSecond(0), "1:00 AM", "{lt,short,Europe/Paris#time}", Locale.ENGLISH);
        this.test(Instant.ofEpochSecond(0), "1:00:00 AM", "{lt,medium,Europe/Paris#time}", Locale.ENGLISH);
        this.test(Instant.ofEpochSecond(0), "1:00:00 AM CET", "{lt,long,Europe/Paris#time}", Locale.ENGLISH);

        this.test(Instant.ofEpochSecond(0), "01:00", "{lt,short,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));
        this.test(Instant.ofEpochSecond(0), "01:00:00", "{lt,medium,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));
        this.test(Instant.ofEpochSecond(0), "01:00:00 CET", "{lt,long,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));

        this.test(Instant.ofEpochSecond(0), "9:00", "{lt,short,Asia/Tokyo#time}", Locale.JAPAN);
        this.test(Instant.ofEpochSecond(0), "9:00:00", "{lt,medium,Asia/Tokyo#time}", Locale.JAPAN);
        this.test(Instant.ofEpochSecond(0), "9:00:00 JST", "{lt,long,Asia/Tokyo#time}", Locale.JAPAN);
    }

    @Test
    public void test_format_ldt() {
        this.test(Instant.ofEpochSecond(0), "1/1/70, 1:00 AM", "{ldt,short,Europe/Paris#time}", Locale.ENGLISH);
        this.test(Instant.ofEpochSecond(0), "Jan 1, 1970, 1:00:00 AM", "{ldt,medium,Europe/Paris#time}", Locale.ENGLISH);
        this.test(Instant.ofEpochSecond(0), "January 1, 1970 at 1:00:00 AM CET", "{ldt,long,Europe/Paris#time}", Locale.ENGLISH);

        this.test(Instant.ofEpochSecond(0), "01.01.1970, 01:00", "{ldt,short,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));
        this.test(Instant.ofEpochSecond(0), "1 sty 1970, 01:00:00", "{ldt,medium,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));
        this.test(Instant.ofEpochSecond(0), "1 stycznia 1970 01:00:00 CET", "{ldt,long,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));

        this.test(Instant.ofEpochSecond(0), "1970/01/01 9:00", "{ldt,short,Asia/Tokyo#time}", Locale.JAPAN);
        this.test(Instant.ofEpochSecond(0), "1970/01/01 9:00:00", "{ldt,medium,Asia/Tokyo#time}", Locale.JAPAN);
        this.test(Instant.ofEpochSecond(0), "1970年1月1日 9:00:00 JST", "{ldt,long,Asia/Tokyo#time}", Locale.JAPAN);
    }

    @Test
    public void test_format_ld() {
        this.test(Instant.ofEpochSecond(0), "1/1/70", "{ld,short,Europe/Paris#time}", Locale.ENGLISH);
        this.test(Instant.ofEpochSecond(0), "Jan 1, 1970", "{ld,medium,Europe/Paris#time}", Locale.ENGLISH);
        this.test(Instant.ofEpochSecond(0), "January 1, 1970", "{ld,long,Europe/Paris#time}", Locale.ENGLISH);

        this.test(Instant.ofEpochSecond(0), "01.01.1970", "{ld,short,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));
        this.test(Instant.ofEpochSecond(0), "1 sty 1970", "{ld,medium,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));
        this.test(Instant.ofEpochSecond(0), "1 stycznia 1970", "{ld,long,Europe/Warsaw#time}", Locale.forLanguageTag("pl"));

        this.test(Instant.ofEpochSecond(0), "1970/01/01", "{ld,short,Asia/Tokyo#time}", Locale.JAPAN);
        this.test(Instant.ofEpochSecond(0), "1970/01/01", "{ld,medium,Asia/Tokyo#time}", Locale.JAPAN);
        this.test(Instant.ofEpochSecond(0), "1970年1月1日", "{ld,long,Asia/Tokyo#time}", Locale.JAPAN);
    }

    @Test
    public void test_format_p() {
        this.test(Instant.ofEpochSecond(0), "1970/01/01, 01:00 AD", "{p,yyyy/MM/dd\\, HH:mm G,Europe/Paris#time}", Locale.ENGLISH);
        this.test(Instant.ofEpochSecond(0), "1970/01/01, 01:00 n.e.", "{p,yyyy/MM/dd\\, HH:mm G,Europe/Paris#time}", Locale.forLanguageTag("pl"));
        this.test(Instant.ofEpochSecond(0), "1970/01/01, 01:00 西暦", "{p,yyyy/MM/dd\\, HH:mm G,Europe/Paris#time}", Locale.JAPAN);
    }
}
