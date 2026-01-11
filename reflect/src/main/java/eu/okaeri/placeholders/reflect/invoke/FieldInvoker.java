package eu.okaeri.placeholders.reflect.invoke;

import eu.okaeri.placeholders.reflect.exception.ReflectException;
import eu.okaeri.placeholders.reflect.lookup.MemberLookup;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Handles field value retrieval via reflection.
 */
public final class FieldInvoker {

    private FieldInvoker() {
        // Utility class
    }

    /**
     * Gets the value of a field from an object.
     *
     * @param field  The field to read
     * @param target The object instance (null for static fields)
     * @return The field value
     * @throws ReflectException if field access fails
     */
    public static Object getValue(@NonNull Field field, @Nullable Object target) {
        try {
            // Static fields don't need a target
            if (MemberLookup.isStatic(field)) {
                return field.get(null);
            }

            if (target == null) {
                throw ReflectException.fieldAccessFailed(
                    field.getDeclaringClass(),
                    field.getName(),
                    new NullPointerException("Target object is null for non-static field")
                );
            }

            return field.get(target);
        } catch (IllegalAccessException e) {
            throw ReflectException.fieldAccessFailed(field.getDeclaringClass(), field.getName(), e);
        }
    }

    /**
     * Sets the value of a field on an object.
     *
     * @param field  The field to write
     * @param target The object instance (null for static fields)
     * @param value  The value to set
     * @throws ReflectException if field access fails
     */
    public static void setValue(@NonNull Field field, @Nullable Object target, @Nullable Object value) {
        try {
            // Static fields don't need a target
            if (MemberLookup.isStatic(field)) {
                field.set(null, value);
                return;
            }

            if (target == null) {
                throw ReflectException.fieldAccessFailed(
                    field.getDeclaringClass(),
                    field.getName(),
                    new NullPointerException("Target object is null for non-static field")
                );
            }

            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw ReflectException.fieldAccessFailed(field.getDeclaringClass(), field.getName(), e);
        }
    }
}
