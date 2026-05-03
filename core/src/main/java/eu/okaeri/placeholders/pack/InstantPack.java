package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Placeholder methods for Instant (datetime) values.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code time(style, zone)} / {@code lt} - localized time format</li>
 *   <li>{@code date(style, zone)} / {@code ld} - localized date format</li>
 *   <li>{@code datetime(style, zone)} / {@code ldt} - localized datetime format</li>
 *   <li>{@code format(pattern, zone)} - custom pattern format</li>
 *   <li>{@code relative(reference)} - signed Duration from reference to this instant</li>
 * </ul>
 * <p>
 * Style can be: short, medium, long, full
 * <p>
 * {@code relative} returns a {@link Duration} so the existing DurationPack
 * formatting methods compose naturally:
 * <pre>
 * {expiry.relative(now()).format("[d]d [h]h")}     // "3d 5h" (or "-2d -3h" if past)
 * {birthday.relative(now()).days}                  // -10957
 * </pre>
 */
public class InstantPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Instant.class)
            // Localized time
            .add("time", (inst, p, ctx) -> formatInstant(
                inst,
                DateTimeFormatter::ofLocalizedTime,
                p.arg(0).orElse("short"),
                p.arg(1).orElse("UTC"),
                ctx.getLocale()))
            .alias("time", "lt")

            // Localized date
            .add("date", (inst, p, ctx) -> formatInstant(
                inst,
                DateTimeFormatter::ofLocalizedDate,
                p.arg(0).orElse("short"),
                p.arg(1).orElse("UTC"),
                ctx.getLocale()))
            .alias("date", "ld")

            // Localized datetime
            .add("datetime", (inst, p, ctx) -> formatInstant(
                inst,
                DateTimeFormatter::ofLocalizedDateTime,
                p.arg(0).orElse("short"),
                p.arg(1).orElse("UTC"),
                ctx.getLocale()))
            .alias("datetime", "ldt")

            // Pattern-based format
            .add("format", (inst, p, ctx) -> DateTimeFormatter.ofPattern(p.arg(0).orElse("yyyy-MM-dd'T'HH:mm:ss"))
                .withLocale(ctx.getLocale())
                .withZone(parseZone(p.arg(1).orElse("UTC")))
                .format(inst))

            // Signed Duration from reference to this instant (positive if this > reference)
            .add("relative", (inst, p, ctx) -> {
                Object ref = p.arg(0).resolve(ctx);
                if (!(ref instanceof Instant)) return null;
                return Duration.between((Instant) ref, inst);
            });
    }

    @FunctionalInterface
    private interface FormatterFactory {
        DateTimeFormatter create(FormatStyle style);
    }

    private static String formatInstant(Instant instant, FormatterFactory factory, String styleStr, String zoneStr, Locale locale) {
        FormatStyle style;
        try {
            style = FormatStyle.valueOf(styleStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            style = FormatStyle.SHORT;
        }

        return factory.create(style)
            .withLocale(locale)
            .withZone(parseZone(zoneStr))
            .format(instant);
    }

    private static ZoneId parseZone(String zoneStr) {
        try {
            return ZoneId.of(zoneStr);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }
}
