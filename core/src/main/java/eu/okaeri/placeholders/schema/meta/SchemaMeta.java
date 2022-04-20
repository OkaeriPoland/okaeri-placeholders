package eu.okaeri.placeholders.schema.meta;

import eu.okaeri.placeholders.schema.annotation.Placeholder;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaMeta {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, SchemaMeta> SCHEMA_CACHE = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, PlaceholderResolver> placeholders;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static SchemaMeta of(@NonNull Class<?> clazz) {

        SchemaMeta cached = SCHEMA_CACHE.get(clazz);
        if (cached != null) {
            return cached;
        }

        Map<String, PlaceholderResolver> placeholders = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();

        // annotated classes
        Placeholder clazzAnnotation = clazz.getAnnotation(Placeholder.class);
        if ((clazzAnnotation != null) && clazzAnnotation.scan()) {
            for (Method method : methods) {

                if (!Modifier.isPublic(method.getModifiers())) {
                    continue;
                }

                if (!clazzAnnotation.name().isEmpty()) {
                    throw new RuntimeException("@Placeholder for " + clazz + " has name set, names are not supported here");
                }

                String name = method.getName();
                if (name.startsWith("get")) {
                    name = name.substring(3);
                } else if (name.startsWith("is")) {
                    name = name.substring(2);
                } else {
                    continue;
                }

                char nameArr[] = name.toCharArray();
                nameArr[0] = Character.toLowerCase(nameArr[0]);
                name = new String(nameArr);

                Class<?> returnType = method.getReturnType();
                MethodHandle handle = toHandle(method);
                placeholders.put(name, (from, params, context) -> handleholder(handle, from));
            }
        }

        // annotated fields
        for (Field field : fields) {

            Class<?> fieldType = field.getType();
            Placeholder placeholder = field.getAnnotation(Placeholder.class);
            if (placeholder == null) {
                continue;
            }

            String name = placeholder.name().isEmpty() ? field.getName() : placeholder.name();
            MethodHandle handle = toHandle(field);
            placeholders.put(name, (from, params, context) -> handleholder(handle, from));
        }

        // annotated methods
        for (Method method : methods) {

            Placeholder placeholder = method.getAnnotation(Placeholder.class);
            if (placeholder == null) {
                continue;
            }

            String name = placeholder.name().isEmpty() ? method.getName() : placeholder.name();
            MethodHandle handle = toHandle(method);
            placeholders.put(name, (from, params, context) -> handleholder(handle, from));
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
}
