package eu.okaeri.placeholders.schema.resolver;

import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.pluralize.Pluralize;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DefaultSchemaResolver implements SchemaResolver {

    public static final SchemaResolver INSTANCE = new DefaultSchemaResolver();
    private static final Set<Class<?>> SUPPORTED_TOSTRING_TYPES = new HashSet<>(Arrays.asList(
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
            UUID.class));

    @Override
    public boolean supports(@NonNull Class<?> type) {
        return SUPPORTED_TOSTRING_TYPES.contains(type) || type.isEnum();
    }

    @Override
    public String resolve(@NonNull Object object, @NonNull MessageField field) {

        if ((field.getMetadataOptions() != null) && (object instanceof Integer) && (field.getMetadataOptions().length == Pluralize.plurals(field.getLocale()))) {
            return Pluralize.pluralize(field.getLocale(), ((Integer) object), field.getMetadataOptions());
        }

        if ((field.getMetadataOptions() != null) && (object instanceof Boolean) && (field.getMetadataOptions().length == 2)) {
            return ((Boolean) object) ? field.getMetadataOptions()[0] : field.getMetadataOptions()[1];
        }

        if ((field.getMetadataRaw() != null) && (object instanceof Number) && (field.getMetadataRaw().length() > 1) && (field.getMetadataRaw().charAt(0) == '%')) {
            return String.format(field.getMetadataRaw(), new BigDecimal(String.valueOf(object)).doubleValue());
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
