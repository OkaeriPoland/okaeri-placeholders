package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.registry.Registry;

import java.time.Duration;
import java.util.Locale;

/**
 * Placeholder methods for Duration values.
 * <p>
 * Provides:
 * <ul>
 *   <li>Default rendering with accuracy: {@code {duration}} or {@code {duration(m)}}</li>
 *   <li>Component access: {@code days}, {@code hours}, {@code minutes}, {@code seconds}, {@code millis}, {@code nanos}</li>
 *   <li>Custom format: {@code format("[d]d [h]h [m]m [s]s")}</li>
 * </ul>
 * <p>
 * <b>Custom format syntax:</b>
 * <ul>
 *   <li>{@code (unit)} - Required: always displays the value (e.g., {@code (h)} → "5")</li>
 *   <li>{@code [unit]} - Optional: only displays if value &gt; 0 (e.g., {@code [d]} → "" if days=0)</li>
 *   <li>{@code <singular,plural>} - Pluralization: picks form based on previous value</li>
 *   <li>{@code <one,few,many>} - Locale-aware pluralization for languages with multiple forms</li>
 * </ul>
 * <p>
 * Supported units: {@code d} (days), {@code h} (hours), {@code m} (minutes),
 * {@code s} (seconds), {@code ms} (milliseconds), {@code ns} (nanoseconds)
 * <p>
 * <b>Examples:</b>
 * <pre>
 * format("[d]d [h]h [m]m (s)s")             → "2d 5h 30m 15s" or "30m 15s" (no days/hours if 0)
 * format("(h) hour&lt;,s&gt; (m) minute&lt;,s&gt;")  → "5 hours 30 minutes"
 * format("[d] day&lt;,s&gt; [h] hour&lt;,s&gt;")     → "2 days 5 hours" or "5 hours" (days hidden if 0)
 * </pre>
 */
public class DurationPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Duration.class)
            // Default rendering with simple format
            .self((dur, p) -> simpleDuration(dur, p.arg(0).orElse("s")))

            // Component accessors
            .add("days", dur -> dur.getSeconds() / 86400L)
            .add("hours", dur -> dur.toHours() % 24L)
            .add("minutes", dur -> dur.toMinutes() % 60L)
            .add("seconds", dur -> dur.getSeconds() % 60L)
            .add("millis", dur -> dur.getNano() / 1_000_000L)
            .add("nanos", dur -> (dur.getNano() >= 1_000_000L) ? 0L : dur.getNano())

            // Custom format
            .add("format", (dur, p, ctx) -> {
                String format = p.arg(0).orElse("");
                if (format.isEmpty()) {
                    return dur.toString();
                }

                boolean negative = dur.isNegative();
                Duration abs = dur.abs();

                long days = abs.getSeconds() / 86400L;
                long hours = abs.toHours() % 24L;
                long minutes = abs.toMinutes() % 60L;
                long seconds = abs.getSeconds() % 60L;
                long millis = abs.toMillis() % 1000L;
                long nanos = (abs.getNano() >= 1_000_000L) ? 0L : abs.getNano();

                Locale locale = ctx.getLocale();

                StringBuilder out = new StringBuilder();
                long lastValue = 0;

                for (int i = 0; i < format.length(); i++) {
                    char c = format.charAt(i);
                    // Unit blocks [h], (m), etc.
                    if ((c == '[') || (c == '(')) {
                        char closing = (c == '[') ? ']' : ')';
                        int end = format.indexOf(closing, i);
                        if (end != -1) {
                            String unit = format.substring(i + 1, end);
                            long value;
                            switch (unit) {
                                case "d":
                                    value = days;
                                    break;
                                case "h":
                                    value = hours;
                                    break;
                                case "m":
                                    value = minutes;
                                    break;
                                case "s":
                                    value = seconds;
                                    break;
                                case "ms":
                                    value = millis;
                                    break;
                                case "ns":
                                    value = nanos;
                                    break;
                                default:
                                    value = 0;
                            }
                            boolean optional = c == '[';
                            if (!optional || (value > 0)) {
                                out.append(value);
                            }
                            lastValue = value;
                            i = end;
                            continue;
                        }
                    }
                    // Pluralization blocks <...>
                    if (c == '<') {
                        int end = format.indexOf('>', i);
                        if (end != -1) {
                            boolean optional = (i > 0) && (format.charAt(i - 1) == ']');
                            if (!optional || (lastValue > 0)) {
                                String inside = format.substring(i + 1, end);
                                String[] forms = inside.split(",");
                                String chosen = Placeholders.pluralize(locale, (int) lastValue, forms);
                                out.append(chosen);
                            }
                            i = end;
                            continue;
                        }
                    }
                    out.append(c);
                }

                if (negative) {
                    return "-" + out.toString().trim();
                }
                return out.toString().trim();
            });
    }

    @SuppressWarnings("StandardVariableNames")
    private enum Accuracy {ns, ms, s, m, h, d}

    private static String simpleDuration(Duration duration, String accuracyStr) {
        Accuracy accuracy;
        try {
            accuracy = Accuracy.valueOf(accuracyStr);
        } catch (IllegalArgumentException e) {
            accuracy = Accuracy.s;
        }

        if (duration.isZero()) {
            return "0" + accuracy.name();
        }

        StringBuilder builder = new StringBuilder();
        if (duration.isNegative()) builder.append("-");
        duration = duration.abs();

        long days = duration.getSeconds() / 86400L;
        if ((accuracy.ordinal() <= 5) && (days > 0)) builder.append(days).append("d");

        long hours = duration.toHours() % 24L;
        if ((accuracy.ordinal() <= 4) && (hours > 0)) builder.append(hours).append("h");

        long minutes = duration.toMinutes() % 60L;
        if ((accuracy.ordinal() <= 3) && (minutes > 0)) builder.append(minutes).append("m");

        long seconds = duration.getSeconds() % 60L;
        if ((accuracy.ordinal() <= 2) && (seconds > 0)) builder.append(seconds).append("s");

        long millis = duration.getNano() / 1_000_000L;
        if ((accuracy.ordinal() <= 1) && (millis > 0)) builder.append(millis).append("ms");

        long nanos = (duration.getNano() >= 1_000_000L) ? 0L : duration.getNano();
        if ((accuracy.ordinal() <= 0) && (nanos > 0)) builder.append(nanos).append("ns");

        String result = builder.toString();
        if (result.isEmpty() || "-".equals(result)) {
            // Recurse with lower accuracy
            int lowerOrdinal = accuracy.ordinal() - 1;
            if (lowerOrdinal >= 0) {
                return simpleDuration(duration, Accuracy.values()[lowerOrdinal].name());
            }
        }
        return result;
    }
}
