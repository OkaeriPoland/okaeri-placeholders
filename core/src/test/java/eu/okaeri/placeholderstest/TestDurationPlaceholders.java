package eu.okaeri.placeholderstest;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDurationPlaceholders {

    private final Placeholders placeholders = Placeholders.create(true);

    private void test(Duration duration, String expect, String pattern) {
        assertEquals(expect, this.placeholders.contextOf(CompiledMessage.of(Locale.ENGLISH, pattern))
            .with("d", duration)
            .apply());
    }

    @Test
    public void test_duration_days() {
        this.test(Duration.ofDays(-1), "-1 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(-1), "-1d", "{d}");
        this.test(Duration.ofDays(0), "0 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(1), "1 day", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(1), "1d", "{d}");
        this.test(Duration.ofDays(2), "2 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(30), "30 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(365), "365 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(1000), "1000 days", "{d.days} {day,days#d.days}");
    }

    @Test
    public void test_duration_days_reminder() {
        this.test(Duration.ofDays(-1).minus(Duration.ofHours(12)), "-1 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(-1).minus(Duration.ofHours(12)), "-1d12h", "{d}");
        this.test(Duration.ofDays(0).plus(Duration.ofHours(12)), "0 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(1).plus(Duration.ofHours(12)), "1 day", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(1).plus(Duration.ofHours(12)), "1d12h", "{d}");
        this.test(Duration.ofDays(2).plus(Duration.ofHours(12)), "2 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(30).plus(Duration.ofHours(12)), "30 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(365).plus(Duration.ofHours(12)), "365 days", "{d.days} {day,days#d.days}");
        this.test(Duration.ofDays(1000).plus(Duration.ofHours(12)), "1000 days", "{d.days} {day,days#d.days}");
    }

    @Test
    public void test_duration_hours() {
        this.test(Duration.ofHours(-1), "-1 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(-1), "-1h", "{d}");
        this.test(Duration.ofHours(0), "0 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(1), "1 hour", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(1), "1h", "{d}");
        this.test(Duration.ofHours(2), "2 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(24), "0 hours", "{d.hours} {hour,hours#d.hours}"); // 1 day
        this.test(Duration.ofHours(30), "6 hours", "{d.hours} {hour,hours#d.hours}"); // 1 day + 6 hours
        this.test(Duration.ofHours(48), "0 hours", "{d.hours} {hour,hours#d.hours}"); // 2 days
    }

    @Test
    public void test_duration_hours_reminder() {
        this.test(Duration.ofHours(-1).minus(Duration.ofMinutes(30)), "-1 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(-1).minus(Duration.ofMinutes(30)), "-1h30m", "{d}");
        this.test(Duration.ofHours(0).plus(Duration.ofMinutes(30)), "0 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(1).plus(Duration.ofMinutes(30)), "1 hour", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(1).plus(Duration.ofMinutes(30)), "1h30m", "{d}");
        this.test(Duration.ofHours(2).plus(Duration.ofMinutes(30)), "2 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(24).plus(Duration.ofMinutes(30)), "0 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(30).plus(Duration.ofMinutes(30)), "6 hours", "{d.hours} {hour,hours#d.hours}");
        this.test(Duration.ofHours(48).plus(Duration.ofMinutes(30)), "0 hours", "{d.hours} {hour,hours#d.hours}");
    }

    @Test
    public void test_duration_minutes() {
        this.test(Duration.ofMinutes(-1), "-1 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(-1), "-1m", "{d}");
        this.test(Duration.ofMinutes(0), "0 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(1), "1 minute", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(1), "1m", "{d}");
        this.test(Duration.ofMinutes(2), "2 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(60), "0 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(90), "30 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(120), "0 minutes", "{d.minutes} {minute,minutes#d.minutes}");
    }

    @Test
    public void test_duration_minutes_reminder() {
        this.test(Duration.ofMinutes(-1).minus(Duration.ofSeconds(30)), "-1 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(-1).minus(Duration.ofSeconds(30)), "-1m30s", "{d}");
        this.test(Duration.ofMinutes(0).plus(Duration.ofSeconds(30)), "0 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(1).plus(Duration.ofSeconds(30)), "1 minute", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(1).plus(Duration.ofSeconds(30)), "1m30s", "{d}");
        this.test(Duration.ofMinutes(2).plus(Duration.ofSeconds(30)), "2 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(60).plus(Duration.ofSeconds(30)), "0 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(90).plus(Duration.ofSeconds(30)), "30 minutes", "{d.minutes} {minute,minutes#d.minutes}");
        this.test(Duration.ofMinutes(120).plus(Duration.ofSeconds(30)), "0 minutes", "{d.minutes} {minute,minutes#d.minutes}");
    }

    @Test
    public void test_duration_complex() {

        String pattern = "{d.days} {day,days#d.days}, " +
            "{d.hours} {hour,hours#d.hours}, " +
            "{d.minutes} {minute,minutes#d.minutes}, " +
            "{d.seconds} {second,seconds#d.seconds}, and " +
            "{d.millis} {millisecond,milliseconds#d.millis}";

        Duration dur1 = Duration.ofDays(88)
            .plus(Duration.ofHours(21))
            .plus(Duration.ofMinutes(37))
            .plus(Duration.ofSeconds(4))
            .plus(Duration.ofMillis(200));
        this.test(dur1, "88 days, 21 hours, 37 minutes, 4 seconds, and 200 milliseconds", pattern);
        this.test(dur1, "88d21h37m4s200ms", "{d(ms)}");
        this.test(dur1, "88d21h37m4s", "{d}");
        this.test(dur1, "88d21h37m", "{d(m)}");
        this.test(dur1, "88d21h", "{d(h)}");

        Duration dur2 = Duration.ofDays(1)
            .plus(Duration.ofHours(21))
            .plus(Duration.ofMinutes(37))
            .plus(Duration.ofSeconds(1))
            .plus(Duration.ofMillis(1));
        this.test(dur2, "1 day, 21 hours, 37 minutes, 1 second, and 1 millisecond", pattern);
        this.test(dur2, "1d21h37m1s1ms", "{d(ms)}");
    }

    @Test
    public void test_duration_too_short() {
        this.test(Duration.ofMinutes(5), "5m", "{d(h)}");
        this.test(Duration.ofSeconds(5), "5s", "{d(h)}");
        this.test(Duration.ofSeconds(3), "3s", "{d(m)}");
        this.test(Duration.ofMillis(23), "23ms", "{d(s)}");
        this.test(Duration.ofNanos(350), "350ns", "{d(s)}");
    }
}
