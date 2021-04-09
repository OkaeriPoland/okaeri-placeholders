package eu.okaeri.placeholders;

import java.util.Locale;

public class DefaultPlaceholderPack implements PlaceholderPack {

    @Override
    public void register(Placeholders placeholders) {

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

    private static String capitalize(String text) {
        text = text.replace("_", " ");
        text = text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
        return text;
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
}
