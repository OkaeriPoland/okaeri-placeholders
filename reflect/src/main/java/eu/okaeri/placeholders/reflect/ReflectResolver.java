package eu.okaeri.placeholders.reflect;

import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ReflectResolver implements PlaceholderResolver {

    @Override
    public Object resolve(@NotNull Object object, @NotNull FieldParams params) {

        Class<?> clazz = object.getClass();
        String name = params.getField();

        Optional<Object> resolvedOptional = Optional.empty();
        if (object instanceof Class) {
            resolvedOptional = this.resolve(object, (Class<?>) object, params);
        }

        if (!resolvedOptional.isPresent()) {
            resolvedOptional = this.resolve(object, clazz, params);
        }

        if (resolvedOptional.isPresent()) {
            return resolvedOptional.get();
        }

        throw new RuntimeException("Cannot reflect " + params + " for " + object + " [" + clazz + "]");
    }

    @SneakyThrows
    private Optional<Object> resolve(Object object, Class<?> clazz, FieldParams params) {

        // field
        Field field = this.getField(clazz, params.getField());
        if ((field != null) && (params.getParams().length == 0)) {
            return Optional.ofNullable(field.get(object));
        }

        // method (no args)
        if ((params.getParams().length == 1) && "".equals(params.getParams()[0])) {
            Method method = this.getMethod(clazz, params.getField());
            if (method != null) {
                return Optional.ofNullable(method.invoke(object));
            }
        }

        // method (args)
        if (params.getParams().length > 0) {
            Stream.concat(Arrays.stream(clazz.getMethods()), Arrays.stream(clazz.getDeclaredMethods()))
                .filter(method -> method.getName().equals(params.getField()))
                .filter(method -> method.getParameterCount() == params.getParams().length)
                .forEach(System.out::println); // TODO
        }

        return Optional.empty();
    }

    private Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ignored) {
        }
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException ignored) {
            return null;
        }
    }

    private Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException ignored) {
        }
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
