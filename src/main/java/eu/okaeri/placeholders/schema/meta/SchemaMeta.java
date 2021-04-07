package eu.okaeri.placeholders.schema.meta;

import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.annotation.Placeholder;
import eu.okaeri.placeholders.schema.resolver.SchemaResolver;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaMeta {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<? extends PlaceholderSchema>, SchemaMeta> SCHEMA_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<? extends SchemaResolver>, SchemaResolver> RESOLVER_CACHE = new ConcurrentHashMap<>();

    @SneakyThrows
    private static SchemaResolver resolver(Class<? extends SchemaResolver> clazz) {

        SchemaResolver resolver = RESOLVER_CACHE.get(clazz);
        if (resolver != null) {
            return resolver;
        }

        SchemaResolver instance = clazz.newInstance();
        RESOLVER_CACHE.put(clazz, instance);
        return instance;
    }

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
                placeholders.put(name, from -> handleholder(handle, from, null));
                continue;
            }

            SchemaResolver resolver = resolver(placeholder.resolver());
            if (resolver.supports(fieldType)) {
                MethodHandle handle = toHandle(field);
                placeholders.put(name, from -> handleholder(handle, from, resolver));
                continue;
            }

            throw new RuntimeException("cannot convert field with type " + fieldType + " using " + resolver.getClass());
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
                placeholders.put(name, from -> handleholder(handle, from, null));
                continue;
            }

            SchemaResolver resolver = resolver(placeholder.resolver());
            if (!resolver.supports(returnType)) {
                throw new RuntimeException("cannot convert method with return type " + returnType + " using " + resolver.getClass());
            }

            // FIXME: support for schema mappers?
            if (method.getParameters().length > 0) {
                throw new RuntimeException("cannot convert using method with arguments: " + method);
            }

            MethodHandle handle = toHandle(method);
            placeholders.put(name, from -> handleholder(handle, from, resolver));
        }

        SchemaMeta meta = new SchemaMeta(clazz, placeholders);
        SCHEMA_CACHE.put(clazz, meta);

        return meta;
    }

    @SneakyThrows
    private static Object handleholder(MethodHandle handle, Object from, SchemaResolver resolver) {
        Object object = handle.invoke(from);
        if (resolver == null) return object;
        return resolver.resolve(object);
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

    private final Class<?> type;
    private final Map<String, PlaceholderResolver> placeholders;
}
