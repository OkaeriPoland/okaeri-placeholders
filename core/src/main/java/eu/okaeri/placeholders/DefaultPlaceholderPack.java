package eu.okaeri.placeholders;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

public class DefaultPlaceholderPack implements PlaceholderPack {

    private static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }

    private static String capitalizeFully(String text) {
        String[] words = text.split(" ");
        StringBuilder buf = new StringBuilder();
        for (String word : words) {
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
        placeholders.registerPlaceholder(Duration.class, "days", (dur, a, o) -> dur.getSeconds() / 86400L);
        placeholders.registerPlaceholder(Duration.class, "hours", (dur, a, o) -> dur.toHours() % 24L);
        placeholders.registerPlaceholder(Duration.class, "minutes", (dur, a, o) -> dur.toMinutes() % 60L);
        placeholders.registerPlaceholder(Duration.class, "seconds", (dur, a, o) -> dur.getSeconds() % 60L);
        placeholders.registerPlaceholder(Duration.class, "millis", (dur, a, o) -> dur.getNano() / 1_000_000L);
        placeholders.registerPlaceholder(Duration.class, "nanos", (dur, a, o) -> (dur.getNano() >= 1_000_000L) ? 0L : dur.getNano());
        placeholders.registerPlaceholder(Duration.class, (dur, a, o) -> simpleDuration(dur, SimpleDurationAccuracy.valueOf(a.params().strAt(0, "s"))));

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
        placeholders.registerPlaceholder(Double.class, "divide", (num, a, o) -> num / a.params().doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "multiply", (num, a, o) -> num * a.params().doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "minus", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "subtract", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "plus", (num, a, o) -> num + a.params().doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "add", (num, a, o) -> num + a.params().doubleAt(0));

        // Float
        placeholders.registerPlaceholder(Float.class, "divide", (num, a, o) -> num / a.params().doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "multiply", (num, a, o) -> num * a.params().doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "minus", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "subtract", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "plus", (num, a, o) -> num + a.params().doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "add", (num, a, o) -> num + a.params().doubleAt(0));

        // Short
        placeholders.registerPlaceholder(Short.class, "divide", (num, a, o) -> num / a.params().doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "multiply", (num, a, o) -> num * a.params().doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "minus", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "subtract", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "plus", (num, a, o) -> num + a.params().doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "add", (num, a, o) -> num + a.params().doubleAt(0));

        // Byte
        placeholders.registerPlaceholder(Byte.class, "divide", (num, a, o) -> num / a.params().doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "multiply", (num, a, o) -> num * a.params().doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "minus", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "subtract", (num, a, o) -> num - a.params().doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "plus", (num, a, o) -> num + a.params().doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "add", (num, a, o) -> num + a.params().doubleAt(0));

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
    }
}
