package eu.okaeri.placeholders.schema.meta;

import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.annotation.Placeholder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaMeta {

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

    @SuppressWarnings("unchecked")
    public static SchemaMeta of(Class<? extends PlaceholderSchema> clazz) {

        Map<String, PlaceholderResolver> placeholders = new LinkedHashMap<>();
        Map<String, SchemaMetaResolver> subschemas = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();

        for (Field field : fields) {

            Class<?> fieldType = field.getType();
            Placeholder placeholder = field.getAnnotation(Placeholder.class);
            if (placeholder == null) {
                continue;
            }

            // submeta
            String name = placeholder.name().isEmpty() ? field.getName() : placeholder.name();
            if (PlaceholderSchema.class.isAssignableFrom(fieldType)) {
                SchemaMeta submeta = SchemaMeta.of((Class<? extends PlaceholderSchema>) fieldType);
                subschemas.put(name, from -> new SchemaObjectPair(submeta, fieldPlaceholder(field, from)));;
                continue;
            }

            // FIXME: use resolver
            if (canConvert(fieldType)) {
                placeholders.put(name, from -> String.valueOf(fieldPlaceholder(field, from)));
                continue;
            }

            throw new RuntimeException("cannot convert fields with type " + fieldType + ", use resolver=CustomResolver.class or define local conversion method with @Placeholder");
        }

        for (Method method : methods) {

            Class<?> returnType = method.getReturnType();
            Placeholder placeholder = method.getAnnotation(Placeholder.class);
            if (placeholder == null) {
                continue;
            }

            // submeta
            String name = placeholder.name().isEmpty() ? method.getName() : placeholder.name();
            if (PlaceholderSchema.class.isAssignableFrom(returnType)) {
                SchemaMeta submeta = SchemaMeta.of((Class<? extends PlaceholderSchema>) returnType);
                subschemas.put(name, from -> new SchemaObjectPair(submeta, methodPlaceholder(method, from)));
                continue;
            }

            // FIXME: use resolver
            if (!canConvert(returnType)) {
                throw new RuntimeException("cannot convert using method with return type of " + returnType + ", use resolver=CustomResolver.class or supported type: " + method);
            }

            // FIXME: support for schema mappers?
            if (method.getParameters().length > 0) {
                throw new RuntimeException("cannot convert using method with arguments: " + method);
            }

            placeholders.put(name, from -> String.valueOf(methodPlaceholder(method, from)));
        }

        return new SchemaMeta(clazz, placeholders, subschemas);
    }

    @SneakyThrows
    private static Object fieldPlaceholder(Field field, Object from) {
        field.setAccessible(true);
        return field.get(from);
    }

    @SneakyThrows
    private static Object methodPlaceholder(Method method, Object from) {
        method.setAccessible(true);
        return method.invoke(from);
    }

    private static boolean canConvert(Class<?> type) {
        return SUPPORTED_TOSTRING_TYPES.contains(type);
    }

    private final Class<?> type;
    private final Map<String, PlaceholderResolver> placeholders;
    private final Map<String, SchemaMetaResolver> subschemas;
}
