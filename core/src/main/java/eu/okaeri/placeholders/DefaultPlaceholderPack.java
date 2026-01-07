package eu.okaeri.placeholders;

import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;

public class DefaultPlaceholderPack implements PlaceholderPack {

    private static Number asIntIfWhole(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return (int) value;
        }
        return value;
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }

    private static String capitalizeFully(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String[] words = text.split(" ");
        StringBuilder buf = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            buf.append(Character.toUpperCase(word.charAt(0)));
            buf.append(word.substring(1).toLowerCase(Locale.ROOT)).append(" ");
        }
        return buf.toString().trim();
    }

    private static String simpleDuration(Duration duration, SimpleDurationAccuracy accuracy) {

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

        return (builder.toString().isEmpty() || "-".equals(builder.toString()))
            ? simpleDuration(duration, SimpleDurationAccuracy.values()[accuracy.ordinal() - 1])
            : builder.toString();
    }

    @SuppressWarnings("StandardVariableNames")
    private enum SimpleDurationAccuracy {
        ns, ms, s, m, h, d
    }

    @Override
    public void register(Placeholders placeholders) {

        // Duration
        placeholders.registerPlaceholder(Duration.class, (dur, a, o) -> simpleDuration(dur, SimpleDurationAccuracy.valueOf(a.params().strAt(0, "s"))));
        placeholders.registerPlaceholder(Duration.class, "days", (dur, a, o) -> dur.getSeconds() / 86400L);
        placeholders.registerPlaceholder(Duration.class, "hours", (dur, a, o) -> dur.toHours() % 24L);
        placeholders.registerPlaceholder(Duration.class, "minutes", (dur, a, o) -> dur.toMinutes() % 60L);
        placeholders.registerPlaceholder(Duration.class, "seconds", (dur, a, o) -> dur.getSeconds() % 60L);
        placeholders.registerPlaceholder(Duration.class, "millis", (dur, a, o) -> dur.getNano() / 1_000_000L);
        placeholders.registerPlaceholder(Duration.class, "nanos", (dur, a, o) -> (dur.getNano() >= 1_000_000L) ? 0L : dur.getNano());
        placeholders.registerPlaceholder(Duration.class, "format", (dur, a, o) -> {

            String format = String.join(",", a.params().getParams());
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

            StringBuilder out = new StringBuilder();
            long lastValue = 0;

            for (int i = 0; i < format.length(); i++) {
                char c = format.charAt(i);
                // unit blocks [h], (m), etc.
                if ((c == '[') || (c == '(')) {
                    char closing = (c == '[') ? ']' : ')';
                    int end = format.indexOf(closing, i);
                    if (end != -1) {
                        String unit = format.substring(i + 1, end);
                        long value = 0;
                        if ("d".equals(unit)) value = days;
                        else if ("h".equals(unit)) value = hours;
                        else if ("m".equals(unit)) value = minutes;
                        else if ("s".equals(unit)) value = seconds;
                        else if ("ms".equals(unit)) value = millis;
                        else if ("ns".equals(unit)) value = nanos;
                        boolean optional = (c == '[');
                        if (!optional || (value > 0)) {
                            out.append(value);
                        }
                        lastValue = value;
                        i = end;
                        continue;
                    }
                }
                // pluralization blocks <...>
                if (c == '<') {
                    int end = format.indexOf('>', i);
                    if (end != -1) {
                        boolean optional = (format.charAt(i - 1) == ']');
                        if (!optional || (lastValue > 0)) {
                            String inside = format.substring(i + 1, end);
                            String[] forms = inside.split(",");
                            String chosen = Placeholders.pluralize(a.locale(), (int) lastValue, forms);
                            out.append(chosen);
                        }
                        i = end;
                        continue;
                    }
                }

                // additional characters
                out.append(c);
            }

            if (negative) {
                return "-" + out.toString().trim();
            }

            return out.toString().trim();
        });

        // Enum
        placeholders.registerPlaceholder(Enum.class, "name", (e, a, o) -> e.name());
        placeholders.registerPlaceholder(Enum.class, "ordinal", (e, a, o) -> e.ordinal());
        placeholders.registerPlaceholder(Enum.class, "pretty", (e, a, o) -> capitalizeFully(e.name().replace("_", " ")));

        // Integer
        placeholders.registerPlaceholder(Integer.class, "divide", (num, a, o) -> num / a.params().intAt(0));
        placeholders.registerPlaceholder(Integer.class, "multiply", (num, a, o) -> num * a.params().intAt(0));
        placeholders.registerPlaceholder(Integer.class, "minus", (num, a, o) -> num - a.params().intAt(0));
        placeholders.registerPlaceholder(Integer.class, "subtract", (num, a, o) -> num - a.params().intAt(0));
        placeholders.registerPlaceholder(Integer.class, "plus", (num, a, o) -> num + a.params().intAt(0));
        placeholders.registerPlaceholder(Integer.class, "add", (num, a, o) -> num + a.params().intAt(0));

        // Double
        placeholders.registerPlaceholder(Double.class, "divide", (num, a, o) -> asIntIfWhole(num / a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Double.class, "multiply", (num, a, o) -> asIntIfWhole(num * a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Double.class, "minus", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Double.class, "subtract", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Double.class, "plus", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Double.class, "add", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));

        // Float
        placeholders.registerPlaceholder(Float.class, "divide", (num, a, o) -> asIntIfWhole(num / a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Float.class, "multiply", (num, a, o) -> asIntIfWhole(num * a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Float.class, "minus", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Float.class, "subtract", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Float.class, "plus", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Float.class, "add", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));

        // Short
        placeholders.registerPlaceholder(Short.class, "divide", (num, a, o) -> asIntIfWhole(num / a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Short.class, "multiply", (num, a, o) -> asIntIfWhole(num * a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Short.class, "minus", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Short.class, "subtract", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Short.class, "plus", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Short.class, "add", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));

        // Byte
        placeholders.registerPlaceholder(Byte.class, "divide", (num, a, o) -> asIntIfWhole(num / a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Byte.class, "multiply", (num, a, o) -> asIntIfWhole(num * a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Byte.class, "minus", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Byte.class, "subtract", (num, a, o) -> asIntIfWhole(num - a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Byte.class, "plus", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));
        placeholders.registerPlaceholder(Byte.class, "add", (num, a, o) -> asIntIfWhole(num + a.params().doubleAt(0)));

        // String
        placeholders.registerPlaceholder(String.class, "toLowerCase", (str, a, o) -> str.toLowerCase(Locale.ROOT));
        placeholders.registerPlaceholder(String.class, "toUpperCase", (str, a, o) -> str.toUpperCase(Locale.ROOT));
        placeholders.registerPlaceholder(String.class, "replace", (str, a, o) -> {
            String search = a.params().strAt(0, "");
            String replacement = a.params().strAt(1, "");
            return str.replace(search, replacement);
        });
        placeholders.registerPlaceholder(String.class, "capitalize", (str, a, o) -> capitalize(str));
        placeholders.registerPlaceholder(String.class, "capitalizeFully", (str, a, o) -> capitalizeFully(str));
        placeholders.registerPlaceholder(String.class, "prepend", (str, a, o) -> a.params().strAt(0, "") + str);
        placeholders.registerPlaceholder(String.class, "append", (str, a, o) -> str + a.params().strAt(0, ""));

        // Map
        placeholders.registerPlaceholder(Map.class, "localized", (map, a, o) -> {

            if (map.isEmpty()) {
                return null;
            }

            Locale locale = a.locale();
            if (locale == null) {
                locale = Locale.ENGLISH;
            }

            Object result = map.get(locale);
            if (result == null) {
                result = map.get(Locale.forLanguageTag(locale.getLanguage()));
            }

            if (result == null) {
                //noinspection unchecked
                result = map.get(map.keySet().stream().findFirst().get());
            }

            return result;
        });

        // Object - or (default/fallback with field ref support)
        // Usage: {player.name.or("Anonymous")} or {player.name.or(player.nickname)}
        placeholders.registerPlaceholder(Object.class, "or", (obj, a, ctx) -> {
            // If current value is non-null, return it
            if (obj != null) {
                return obj;
            }
            // Otherwise resolve the fallback argument (may be field ref or literal)
            return a.params().resolveArg(0, a.locale(), ctx);
        });

        // Number - pluralization
        // Usage: {amount.plural("apple", "apples")}
        placeholders.registerPlaceholder(Number.class, "plural", (num, a, o) -> {
            String[] forms = a.params().strArr();
            return Placeholders.pluralize(a.locale(), num.intValue(), forms);
        });

        // Boolean - formatting
        // Usage: {status.bool("yes", "no")} or {status.format("yes", "no")}
        placeholders.registerPlaceholder(Boolean.class, "bool", (bool, a, o) ->
            bool ? a.params().strAt(0, "true") : a.params().strAt(1, "false"));
        placeholders.registerPlaceholder(Boolean.class, "format", (bool, a, o) ->
            bool ? a.params().strAt(0, "true") : a.params().strAt(1, "false"));

        // Instant - datetime formatting (readable names)
        // Usage: {time.time("medium", "UTC")} - localized time
        placeholders.registerPlaceholder(Instant.class, "time", (inst, a, o) ->
            formatInstant(inst, DateTimeFormatter::ofLocalizedTime, a.params().strAt(0, "short"), a.params().strAt(1, "UTC"), a.locale()));

        // Usage: {time.date("long", "Europe/Paris")} - localized date
        placeholders.registerPlaceholder(Instant.class, "date", (inst, a, o) ->
            formatInstant(inst, DateTimeFormatter::ofLocalizedDate, a.params().strAt(0, "short"), a.params().strAt(1, "UTC"), a.locale()));

        // Usage: {time.datetime("medium", "UTC")} - localized datetime
        placeholders.registerPlaceholder(Instant.class, "datetime", (inst, a, o) ->
            formatInstant(inst, DateTimeFormatter::ofLocalizedDateTime, a.params().strAt(0, "short"), a.params().strAt(1, "UTC"), a.locale()));

        // Usage: {time.format("yyyy-MM-dd HH:mm", "UTC")} - pattern-based format
        placeholders.registerPlaceholder(Instant.class, "format", (inst, a, o) -> {
            String pattern = a.params().strAt(0, "yyyy-MM-dd'T'HH:mm:ss");
            String zoneId = a.params().strAt(1, "UTC");
            Locale locale = a.locale() != null ? a.locale() : Locale.ROOT;
            return DateTimeFormatter.ofPattern(pattern)
                .withLocale(locale)
                .withZone(ZoneId.of(zoneId))
                .format(inst);
        });

        // Short aliases for backward compatibility
        placeholders.registerPlaceholder(Instant.class, "lt", (inst, a, o) ->
            formatInstant(inst, DateTimeFormatter::ofLocalizedTime, a.params().strAt(0, "short"), a.params().strAt(1, "UTC"), a.locale()));
        placeholders.registerPlaceholder(Instant.class, "ld", (inst, a, o) ->
            formatInstant(inst, DateTimeFormatter::ofLocalizedDate, a.params().strAt(0, "short"), a.params().strAt(1, "UTC"), a.locale()));
        placeholders.registerPlaceholder(Instant.class, "ldt", (inst, a, o) ->
            formatInstant(inst, DateTimeFormatter::ofLocalizedDateTime, a.params().strAt(0, "short"), a.params().strAt(1, "UTC"), a.locale()));

        // =====================
        // Global Functions ($.)
        // =====================

        // $.env(VAR) - environment variable
        // Usage: {$.env(HOME)} or {$.env("PATH")}
        placeholders.registerGlobalFunction("env", (gf, a, ctx) -> {
            String varName = a.params().strAt(0, "");
            String value = System.getenv(varName);
            return value != null ? value : "";
        });

        // $.now() - current timestamp
        // Usage: {$.now()} or {$.now().datetime("medium", "UTC")}
        placeholders.registerGlobalFunction("now", (gf, a, ctx) -> Instant.now());

        // $.coalesce(a, b, c, ...) or $.or(a, b, c, ...) - first non-null value
        // Usage: {$.coalesce(player.nickname, player.name, "Anonymous")}
        // Usage: {or(a, b, "default")} - short syntax, preferred over chained .or()
        PlaceholderResolver<GlobalFunctions> coalesceResolver = (gf, a, ctx) -> {
            for (int i = 0; i < a.params().length(); i++) {
                Object val = a.params().resolveArg(i, a.locale(), ctx);
                if (val != null) {
                    return val;
                }
            }
            return null;
        };
        placeholders.registerGlobalFunction("coalesce", coalesceResolver);
        placeholders.registerGlobalFunction("or", coalesceResolver);  // alias

        // $.if(condition, thenValue, elseValue) - conditional
        // Usage: {$.if(player.online, "Online", "Offline")}
        placeholders.registerGlobalFunction("if", (gf, a, ctx) -> {
            Object condition = a.params().resolveArg(0, a.locale(), ctx);
            boolean isTrue = isTruthy(condition);
            return isTrue
                ? a.params().resolveArg(1, a.locale(), ctx)
                : a.params().resolveArg(2, a.locale(), ctx);
        });

        // $.random(min, max) - random integer in range
        // Usage: {$.random(1, 100)}
        placeholders.registerGlobalFunction("random", (gf, a, ctx) -> {
            int min = a.params().intAt(0, 0);
            int max = a.params().intAt(1, 100);
            return min + (int) (Math.random() * (max - min + 1));
        });

        // $.concat(a, b, c, ...) - concatenate values
        // Usage: {$.concat(first, " ", last)}
        placeholders.registerGlobalFunction("concat", (gf, a, ctx) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < a.params().length(); i++) {
                Object val = a.params().resolveArg(i, a.locale(), ctx);
                if (val != null) {
                    sb.append(val);
                }
            }
            return sb.toString();
        });
    }

    /**
     * Determines if a value is "truthy" for conditional purposes.
     */
    private static boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) {
            String s = (String) value;
            return !s.isEmpty() && !s.equalsIgnoreCase("false") && !s.equals("0");
        }
        return true; // Non-null objects are truthy
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

        ZoneId zone;
        try {
            zone = ZoneId.of(zoneStr);
        } catch (Exception e) {
            zone = ZoneId.of("UTC");
        }

        Locale effectiveLocale = locale != null ? locale : Locale.ROOT;
        return factory.create(style)
            .withLocale(effectiveLocale)
            .withZone(zone)
            .format(instant);
    }
}
