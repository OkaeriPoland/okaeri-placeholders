package eu.okaeri.placeholders;

import java.time.Duration;
import java.util.Locale;

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

        return builder.toString();
    }

    @SuppressWarnings("StandardVariableNames")
    private enum SimpleDurationAccuracy {
        ns, ms, s, m, h, d
    }

    @Override
    public void register(Placeholders placeholders) {

        // Duration
        placeholders.registerPlaceholder(Duration.class, "days", (dur, p) -> dur.getSeconds() / 86400L);
        placeholders.registerPlaceholder(Duration.class, "hours", (dur, p) -> dur.toHours() % 24L);
        placeholders.registerPlaceholder(Duration.class, "minutes", (dur, p) -> dur.toMinutes() % 60L);
        placeholders.registerPlaceholder(Duration.class, "seconds", (dur, p) -> dur.getSeconds() % 60L);
        placeholders.registerPlaceholder(Duration.class, "millis", (dur, p) -> dur.getNano() / 1_000_000L);
        placeholders.registerPlaceholder(Duration.class, "nanos", (dur, p) -> (dur.getNano() >= 1_000_000L) ? 0L : dur.getNano());
        placeholders.registerPlaceholder(Duration.class, (dur, p) -> simpleDuration(dur, SimpleDurationAccuracy.valueOf(p.strAt(0, "s"))));

        // Enum
        placeholders.registerPlaceholder(Enum.class, "name", (e, p) -> e.name());
        placeholders.registerPlaceholder(Enum.class, "ordinal", (e, p) -> e.ordinal());
        placeholders.registerPlaceholder(Enum.class, "pretty", (e, p) -> capitalizeFully(e.name().replace("_", " ")));

        // Integer
        placeholders.registerPlaceholder(Integer.class, "divide", (num, p) -> num / p.intAt(0));
        placeholders.registerPlaceholder(Integer.class, "multiply", (num, p) -> num * p.intAt(0));
        placeholders.registerPlaceholder(Integer.class, "minus", (num, p) -> num - p.intAt(0));
        placeholders.registerPlaceholder(Integer.class, "subtract", (num, p) -> num - p.intAt(0));
        placeholders.registerPlaceholder(Integer.class, "plus", (num, p) -> num + p.intAt(0));
        placeholders.registerPlaceholder(Integer.class, "add", (num, p) -> num + p.intAt(0));

        // Double
        placeholders.registerPlaceholder(Double.class, "divide", (num, p) -> num / p.doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "multiply", (num, p) -> num * p.doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "minus", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "subtract", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "plus", (num, p) -> num + p.doubleAt(0));
        placeholders.registerPlaceholder(Double.class, "add", (num, p) -> num + p.doubleAt(0));

        // Float
        placeholders.registerPlaceholder(Float.class, "divide", (num, p) -> num / p.doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "multiply", (num, p) -> num * p.doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "minus", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "subtract", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "plus", (num, p) -> num + p.doubleAt(0));
        placeholders.registerPlaceholder(Float.class, "add", (num, p) -> num + p.doubleAt(0));

        // Short
        placeholders.registerPlaceholder(Short.class, "divide", (num, p) -> num / p.doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "multiply", (num, p) -> num * p.doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "minus", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "subtract", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "plus", (num, p) -> num + p.doubleAt(0));
        placeholders.registerPlaceholder(Short.class, "add", (num, p) -> num + p.doubleAt(0));

        // Byte
        placeholders.registerPlaceholder(Byte.class, "divide", (num, p) -> num / p.doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "multiply", (num, p) -> num * p.doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "minus", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "subtract", (num, p) -> num - p.doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "plus", (num, p) -> num + p.doubleAt(0));
        placeholders.registerPlaceholder(Byte.class, "add", (num, p) -> num + p.doubleAt(0));

        // String
        placeholders.registerPlaceholder(String.class, "toLowerCase", (str, p) -> str.toLowerCase(Locale.ROOT));
        placeholders.registerPlaceholder(String.class, "toUpperCase", (str, p) -> str.toUpperCase(Locale.ROOT));
        placeholders.registerPlaceholder(String.class, "replace", (str, p) -> {
            String search = p.strAt(0, "");
            String replacement = p.strAt(1, "");
            return str.replace(search, replacement);
        });
        placeholders.registerPlaceholder(String.class, "capitalize", (str, p) -> capitalize(str));
        placeholders.registerPlaceholder(String.class, "capitalizeFully", (str, p) -> capitalizeFully(str));
    }
}
