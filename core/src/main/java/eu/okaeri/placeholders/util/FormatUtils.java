package eu.okaeri.placeholders.util;

/**
 * Utility methods for formatting placeholder values.
 */
public final class FormatUtils {

    private FormatUtils() {
    }

    /**
     * Converts an object to its string representation using standard placeholder rules:
     * <ul>
     *   <li>null → "null"</li>
     *   <li>Enum → enum.name()</li>
     *   <li>Float/Double → formatted with 2 decimal places</li>
     *   <li>Other → toString()</li>
     * </ul>
     */
    public static String objectToString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }
        if ((value instanceof Float) || (value instanceof Double)) {
            return String.format("%.2f", value);
        }
        return value.toString();
    }
}
