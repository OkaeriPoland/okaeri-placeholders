package eu.okaeri.placeholders.reflect;

import eu.okaeri.placeholders.context.Placeholder;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.message.part.MessageFieldAccessor;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ReflectResolver implements PlaceholderResolver {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();

    static {
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
    }

    @Override
    public Object resolve(@NotNull Object object, @NonNull MessageFieldAccessor accessor, @Nullable PlaceholderContext context) {

        Class<?> clazz = object.getClass();
        String name = accessor.params().getField();

        Object[] resolved = new Object[0];
        if (object instanceof Class) {
            resolved = this.resolve(object, (Class<?>) object, accessor, context);
        }

        if (resolved.length == 0) {
            resolved = this.resolve(object, clazz, accessor, context);
        }

        if (resolved.length > 0) {
            return resolved[0];
        }

        throw new RuntimeException("Cannot reflect " + accessor + " for " + object + " [" + clazz + "]");
    }

    @SneakyThrows
    private Object[] resolve(@NonNull Object object, @NonNull Class<?> clazz, @NonNull MessageFieldAccessor accessor, @Nullable PlaceholderContext context) {

        FieldParams params = accessor.params();

        // field
        Field field = this.getField(clazz, params.getField());
        if ((field != null) && (params.getParams().length == 0)) {
            return new Object[]{field.get(object)};
        }

        // method
        if (params.getParams().length > 0) {

            // no args
            if ((params.getParams().length == 1) && "".equals(params.getParams()[0])) {
                Method method = this.getMethod(clazz, params.getField());
                if (method != null) {
                    return new Object[]{method.invoke(object)};
                }
            }

            // args
            else {
                String[] args = params.getParams();
                Class<?>[] argTypes = new Class[args.length];
                Object[] call = new Object[args.length];

                for (int i = 0; i < args.length; i++) {
                    String param = args[i];
                    if (param.startsWith("'") && param.endsWith("'")) {
                        argTypes[i] = String.class;
                        call[i] = param.substring(1, param.length() - 1);
                    } else if (param.startsWith("c'") && param.endsWith("'") && (param.length() == 4)) {
                        argTypes[i] = Character.class;
                        call[i] = param.substring(2, param.length() - 1).charAt(0);
                    } else if ("true".equals(param) || "false".equals(param)) {
                        argTypes[i] = Boolean.class;
                        call[i] = Boolean.valueOf(param);
                    } else if (param.matches("-?\\d+")) {
                        argTypes[i] = Integer.class;
                        call[i] = Integer.valueOf(param);
                    } else if (param.matches("-?\\d+L")) {
                        argTypes[i] = Long.class;
                        call[i] = Long.valueOf(param.substring(0, param.length() - 1));
                    } else if (param.matches("-?\\d+\\.\\d+")) {
                        argTypes[i] = Double.class;
                        call[i] = Double.valueOf(param);
                    } else if (param.matches("-?\\d+\\.\\d+f")) {
                        argTypes[i] = Float.class;
                        call[i] = Float.valueOf(param.substring(0, param.length() - 1));
                    } else if (param.matches("-?\\d+b")) {
                        argTypes[i] = Byte.class;
                        call[i] = Byte.valueOf(param.substring(0, param.length() - 1));
                    } else if (param.matches("-?\\d+s")) {
                        argTypes[i] = Short.class;
                        call[i] = Short.valueOf(param.substring(0, param.length() - 1));
                    } else if ((context != null) && (param.contains(".") || param.contains("("))) {
                        String result = context.getPlaceholders()
                            .contextOf(CompiledMessage.of(param))
                            .apply(); // TODO: types other than string [?]
                        argTypes[i] = result.getClass();
                        call[i] = result;
                    } else if ((context != null) && context.getFields().containsKey(param)) {
                        Placeholder placeholder = context.getFields().get(param);
                        argTypes[i] = placeholder.getValue().getClass();
                        call[i] = placeholder.getValue();
                    } else {
                        throw new IllegalArgumentException("Unknown argument '" + param + "' in " + params + " [" + clazz + "] " +
                            "(argTypes: " + Arrays.toString(argTypes) + ", " +
                            "call: " + Arrays.toString(call) + ", " +
                            "context: " + (context != null ? context.getFields().keySet() : null) + ")");
                    }
                }

                Method targetMethod = Stream.concat(Arrays.stream(clazz.getMethods()), Arrays.stream(clazz.getDeclaredMethods()))
                    .filter(method -> method.getName().equals(params.getField()))
                    .filter(method -> method.getParameterCount() == args.length)
                    .filter(method -> this.compareSignature(method.getParameterTypes(), argTypes))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Cannot resolve method for " + params + " [" + clazz + "] " +
                        "(argTypes: " + Arrays.toString(argTypes) + ", " +
                        "call: " + Arrays.toString(call) + ", " +
                        "context: " + (context != null ? context.getFields().keySet() : null) + ")"));

                return new Object[]{targetMethod.invoke(object, call)};
            }
        }

        return new Object[0];
    }

    private Field getField(@NonNull Class<?> clazz, @NonNull String name) {
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

    private Method getMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>... parameterTypes) {
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

    private boolean compareSignature(@NonNull Class<?>[] signature1, @NonNull Class<?>[] signature2) {

        if (signature1.length != signature2.length) {
            return false;
        }

        for (int i = 0; i < signature1.length; i++) {

            Class<?> type1 = signature1[i];
            Class<?> type2 = signature2[i];

            if (this.comparePrimitiveType(type1, type2)) {
                continue;
            }

            if (!type1.isAssignableFrom(type2)) {
                return false;
            }
        }

        return true;
    }

    private boolean comparePrimitiveType(@NonNull Class<?> clazz1, @NonNull Class<?> clazz2) {
        return (PRIMITIVE_TO_WRAPPER.get(clazz1) == clazz2) || (clazz2 == PRIMITIVE_TO_WRAPPER.get(clazz1));
    }
}
