package eu.okaeri.placeholders.reflect.invoke;

import eu.okaeri.placeholders.reflect.argument.TypeCoercer;
import eu.okaeri.placeholders.reflect.exception.ReflectException;
import eu.okaeri.placeholders.reflect.lookup.MemberLookup;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Handles method invocation via reflection with automatic type coercion.
 */
public final class MethodInvoker {

    private MethodInvoker() {
        // Utility class
    }

    /**
     * Invokes a method on an object.
     *
     * @param method The method to invoke
     * @param target The object instance (null for static methods)
     * @param args   The method arguments
     * @return The method return value
     * @throws ReflectException if invocation fails
     */
    public static Object invoke(@NonNull Method method, @Nullable Object target, @Nullable Object[] args) {
        try {
            Object[] coercedArgs = coerceArguments(method, args);

            // Static methods don't need a target
            if (MemberLookup.isStatic(method)) {
                return method.invoke(null, coercedArgs);
            }

            if (target == null) {
                throw ReflectException.methodInvocationFailed(
                    method.getDeclaringClass(),
                    method.getName(),
                    method.getParameterTypes(),
                    new NullPointerException("Target object is null for non-static method")
                );
            }

            return method.invoke(target, coercedArgs);
        } catch (IllegalAccessException e) {
            throw ReflectException.methodInvocationFailed(
                method.getDeclaringClass(),
                method.getName(),
                method.getParameterTypes(),
                e
            );
        } catch (InvocationTargetException e) {
            // Unwrap the target exception for better error messages
            Throwable cause = (e.getCause() != null) ? e.getCause() : e;
            throw ReflectException.methodInvocationFailed(
                method.getDeclaringClass(),
                method.getName(),
                method.getParameterTypes(),
                cause
            );
        }
    }

    /**
     * Coerces arguments to match method parameter types.
     *
     * @param method The target method
     * @param args   The arguments to coerce
     * @return Array of coerced arguments
     */
    private static Object[] coerceArguments(Method method, Object[] args) {
        if ((args == null) || (args.length == 0)) {
            return new Object[0];
        }

        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != args.length) {
            throw new IllegalArgumentException("Argument count mismatch: expected "
                + paramTypes.length + ", got " + args.length);
        }

        Object[] coerced = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            coerced[i] = coerceIfNeeded(args[i], paramTypes[i]);
        }
        return coerced;
    }

    /**
     * Coerces a single argument if needed to match the target type.
     */
    private static Object coerceIfNeeded(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        Class<?> valueType = value.getClass();

        // Already compatible - no coercion needed
        if (targetType.isAssignableFrom(valueType)) {
            return value;
        }

        // Check if primitive/wrapper match
        if (TypeCoercer.wrap(targetType).isAssignableFrom(valueType)) {
            return value;
        }

        // Try coercion
        if (TypeCoercer.canCoerce(valueType, targetType)) {
            return TypeCoercer.coerce(value, targetType);
        }

        // Can't coerce - return as-is and let the JVM handle it
        // (will throw IllegalArgumentException if truly incompatible)
        return value;
    }

    /**
     * Invokes a no-argument method.
     */
    public static Object invoke(@NonNull Method method, @Nullable Object target) {
        return invoke(method, target, new Object[0]);
    }
}
