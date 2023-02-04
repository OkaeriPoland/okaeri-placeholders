package eu.okaeri.placeholders.schema.resolver;

import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.pluralize.Pluralize;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class DefaultSchemaResolver implements SchemaResolver {

    public static final SchemaResolver INSTANCE = new DefaultSchemaResolver();
    private static final Set<Class<?>> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(
        BigDecimal.class,
        BigInteger.class,
        Boolean.class, boolean.class,
        Byte.class, byte.class,
        Character.class, char.class,
        Double.class, double.class,
        Float.class, float.class,
        Integer.class, int.class,
        Long.class, long.class,
        Short.class, short.class,
        String.class,
        UUID.class,
        Instant.class));

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return SUPPORTED_TYPES.contains(type) || type.isEnum();
    }

    @Override
    public String resolve(@NonNull Object object, @NonNull MessageField field) {

        if ((field.getMetadataOptions() != null) && (object instanceof Number) && (field.getMetadataOptions().length == Pluralize.plurals(field.getLocale()))) {
            int intValue = new BigDecimal(String.valueOf(object)).intValueExact();
            try {
                return Pluralize.pluralize(field.getLocale(), intValue, field.getMetadataOptions());
            }
            catch (IllegalArgumentException exception) {
                try {
                    return Pluralize.pluralize(Locale.ENGLISH, intValue, field.getMetadataOptions());
                }
                catch (IllegalArgumentException exception1) {
                    return field.getMetadataOptions()[0];
                }
            }
        }

        if ((field.getMetadataOptions() != null) && (object instanceof Boolean) && (field.getMetadataOptions().length == 2)) {
            return ((Boolean) object) ? field.getMetadataOptions()[0] : field.getMetadataOptions()[1];
        }

        if ((field.getMetadataRaw() != null) && (object instanceof Number) && (field.getMetadataRaw().length() > 1) && (field.getMetadataRaw().charAt(0) == '%')) {
            double doubleValue = new BigDecimal(String.valueOf(object)).doubleValue();
            return String.format(field.getLocale(), field.getMetadataRaw(), doubleValue);
        }

        if ((field.getMetadataRaw() != null) && (object instanceof Instant)) {

            String[] metadataOptions = field.getMetadataOptions();
            String rawFormat = metadataOptions[0].toUpperCase(Locale.ROOT);

            FormatStyle style = null;
            String pattern = null;

            if ("P".equals(rawFormat)) {
                if (metadataOptions.length < 2) {
                    throw new IllegalArgumentException("The pattern formatter ('P') requires a pattern as a second metadata option.");
                }
                pattern = metadataOptions[1];
            } else {
                style = (metadataOptions.length >= 2)
                    ? FormatStyle.valueOf(metadataOptions[1].toUpperCase(Locale.ROOT))
                    : FormatStyle.SHORT;
            }

            DateTimeFormatter formatter;
            switch (rawFormat) {
                case "LT":
                    formatter = DateTimeFormatter.ofLocalizedTime(style);
                    break;
                case "LDT":
                    formatter = DateTimeFormatter.ofLocalizedDateTime(style);
                    break;
                case "LD":
                    formatter = DateTimeFormatter.ofLocalizedDate(style);
                    break;
                case "P":
                    formatter = DateTimeFormatter.ofPattern(pattern);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown time formatter: " + rawFormat);
            }

            ZoneId zone = ((field.getMetadataRaw() == null) || (field.getMetadataOptions().length < 3))
                ? ZoneId.systemDefault()
                : ZoneId.of(metadataOptions[2]);

            return formatter
                .withLocale(field.getLocale())
                .withZone(zone)
                .format((TemporalAccessor) object);
        }

        return this.resolve(object);
    }

    @Override
    @SuppressWarnings("MalformedFormatString")
    public String resolve(@NonNull Object object) {

        if (object instanceof Enum) {
            return ((Enum<?>) object).name();
        }

        if ((object instanceof Float) || (object instanceof Double)) {
            return String.format("%.2f", object);
        }

        return object.toString();
    }
}
