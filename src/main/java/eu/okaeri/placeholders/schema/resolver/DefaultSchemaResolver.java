package eu.okaeri.placeholders.schema.resolver;

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
    public boolean supports(Class<?> type) {
        return SUPPORTED_TOSTRING_TYPES.contains(type);
    }

    @Override
    @SuppressWarnings("MalformedFormatString")
    public String resolve(Object object) {

        if ((object instanceof Float) || (object instanceof Double)) {
            return String.format("%.2f", object);
        }

        return object.toString();
    }
}
