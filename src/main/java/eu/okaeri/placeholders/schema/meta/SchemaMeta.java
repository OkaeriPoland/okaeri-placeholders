package eu.okaeri.placeholders.schema.meta;

import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.annotation.Placeholder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaMeta {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, SchemaMeta> SCHEMA_CACHE = new ConcurrentHashMap<>();
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

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static SchemaMeta of(Class<? extends PlaceholderSchema> clazz) {

        SchemaMeta cached = SCHEMA_CACHE.get(clazz);
        if (cached != null) {
            return cached;
        }

        Map<String, PlaceholderResolver> placeholders = new LinkedHashMap<>();
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
                MethodHandle handle = toHandle(field);
                placeholders.put(name, from -> handleholder(handle, from));
                continue;
            }

            // FIXME: use resolver
            if (canConvert(fieldType)) {
                MethodHandle handle = toHandle(field);
                placeholders.put(name, from -> String.valueOf(handleholder(handle, from)));
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
                MethodHandle handle = toHandle(method);
                placeholders.put(name, from -> handleholder(handle, from));
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

            MethodHandle handle = toHandle(method);
            placeholders.put(name, from -> String.valueOf(handleholder(handle, from)));
        }

        SchemaMeta meta = new SchemaMeta(clazz, placeholders);
        SCHEMA_CACHE.put(clazz, meta);

        return meta;
    }

    @SneakyThrows
    private static Object handleholder(MethodHandle handle, Object from) {
        return handle.invoke(from);
    }

    @SneakyThrows
    private static MethodHandle toHandle(Method method) {
        method.setAccessible(true);
        return LOOKUP.unreflect(method);
    }

    @SneakyThrows
    private static MethodHandle toHandle(Field field) {
        field.setAccessible(true);
        return LOOKUP.unreflectGetter(field);
    }

    private static boolean canConvert(Class<?> type) {
        return SUPPORTED_TOSTRING_TYPES.contains(type);
    }

    private final Class<?> type;
    private final Map<String, PlaceholderResolver> placeholders;
}
