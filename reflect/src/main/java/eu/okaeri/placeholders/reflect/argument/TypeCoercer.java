package eu.okaeri.placeholders.reflect.argument;

import eu.okaeri.placeholders.exception.CoercionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles type coercion for method parameter matching.
 * <p>
 * Coercion priority (lower = better match):
 * <ol>
 *   <li>Exact match (score 0)</li>
 *   <li>Primitive/wrapper equivalence (score 1)</li>
 *   <li>Widening primitives: byte→short→int→long, float→double (score 2)</li>
 *   <li>Widening to wrapper: int→Long (score 3)</li>
 *   <li>String parsing to numeric (score 4)</li>
 *   <li>Assignable types via isAssignableFrom (score 5)</li>
 * </ol>
 */
public final class TypeCoercer {

    /**
     * Score indicating no coercion is possible.
     */
    public static final int NO_MATCH = Integer.MAX_VALUE;

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE = new HashMap<>();

    static {
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);

        PRIMITIVE_TO_WRAPPER.forEach((prim, wrapper) -> WRAPPER_TO_PRIMITIVE.put(wrapper, prim));
    }

    private TypeCoercer() {
        // Utility class
    }

    /**
     * Calculates the coercion score from source type to target type.
     * Lower scores indicate better matches.
     *
     * @param from The source type
     * @param to   The target type
     * @return Coercion score (0 = exact match, Integer.MAX_VALUE = no match)
     */
    public static int coercionScore(Class<?> from, Class<?> to) {
        if ((from == null) || (to == null)) {
            return ((to == null) || !to.isPrimitive()) ? 5 : NO_MATCH;
        }

        // Exact match
        if (from.equals(to)) {
            return 0;
        }

        // Primitive/wrapper equivalence
        if (isPrimitiveWrapperEquivalent(from, to)) {
            return 1;
        }

        // Widening primitive conversions
        if (isWideningPrimitive(from, to)) {
            return 2;
        }

        // Widening to wrapper (int → Long)
        if (isWideningToWrapper(from, to)) {
            return 3;
        }

        // String to numeric parsing
        if (isStringToNumeric(from, to)) {
            return 4;
        }

        // Assignable types (Object, interfaces, superclasses)
        if (to.isAssignableFrom(from)) {
            return 5;
        }

        return NO_MATCH;
    }

    /**
     * Checks if coercion is possible from source to target type.
     */
    public static boolean canCoerce(Class<?> from, Class<?> to) {
        return coercionScore(from, to) != NO_MATCH;
    }

    /**
     * Coerces a value to the target type.
     *
     * @param value      The value to coerce
     * @param targetType The target type
     * @return The coerced value
     * @throws CoercionException if coercion is not possible
     */
    public static Object coerce(Object value, Class<?> targetType) {
        if (value == null) {
            if (targetType.isPrimitive()) {
                throw new CoercionException(null, null, targetType, "cannot coerce null to primitive type");
            }
            return null;
        }

        Class<?> sourceType = value.getClass();

        // Exact match or assignable - no coercion needed
        if (targetType.isAssignableFrom(sourceType)) {
            return value;
        }

        // Handle primitive/wrapper conversion
        Class<?> targetWrapper = PRIMITIVE_TO_WRAPPER.getOrDefault(targetType, targetType);
        if (targetWrapper.isAssignableFrom(sourceType)) {
            return value;
        }

        // Numeric widening
        if (value instanceof Number) {
            return coerceNumber((Number) value, targetType, value);
        }

        // String to numeric
        if (value instanceof String) {
            return coerceString((String) value, targetType);
        }

        // Character to numeric
        if ((value instanceof Character) && isNumericType(targetType)) {
            return coerceNumber((int) (Character) value, targetType, value);
        }

        throw new CoercionException(value, sourceType, targetType, "no coercion path available");
    }

    /**
     * Coerces a Number to the target numeric type.
     */
    private static Object coerceNumber(Number number, Class<?> targetType, Object originalValue) {
        if ((targetType == byte.class) || (targetType == Byte.class)) {
            return number.byteValue();
        }
        if ((targetType == short.class) || (targetType == Short.class)) {
            return number.shortValue();
        }
        if ((targetType == int.class) || (targetType == Integer.class)) {
            return number.intValue();
        }
        if ((targetType == long.class) || (targetType == Long.class)) {
            return number.longValue();
        }
        if ((targetType == float.class) || (targetType == Float.class)) {
            return number.floatValue();
        }
        if ((targetType == double.class) || (targetType == Double.class)) {
            return number.doubleValue();
        }
        throw new CoercionException(originalValue, originalValue.getClass(), targetType, "not a supported numeric target type");
    }

    /**
     * Coerces a String to the target type.
     */
    private static Object coerceString(String str, Class<?> targetType) {
        try {
            if ((targetType == byte.class) || (targetType == Byte.class)) {
                return Byte.parseByte(str);
            }
            if ((targetType == short.class) || (targetType == Short.class)) {
                return Short.parseShort(str);
            }
            if ((targetType == int.class) || (targetType == Integer.class)) {
                return Integer.parseInt(str);
            }
            if ((targetType == long.class) || (targetType == Long.class)) {
                return Long.parseLong(str);
            }
            if ((targetType == float.class) || (targetType == Float.class)) {
                return Float.parseFloat(str);
            }
            if ((targetType == double.class) || (targetType == Double.class)) {
                return Double.parseDouble(str);
            }
            if ((targetType == boolean.class) || (targetType == Boolean.class)) {
                return Boolean.parseBoolean(str);
            }
            if ((targetType == char.class) || (targetType == Character.class)) {
                if (str.length() == 1) {
                    return str.charAt(0);
                }
                throw new CoercionException(str, String.class, targetType, "string length must be 1 for char conversion");
            }
        } catch (NumberFormatException e) {
            throw new CoercionException(str, String.class, targetType, "failed to parse as number", e);
        }
        throw new CoercionException(str, String.class, targetType, "not a supported target type for string parsing");
    }

    /**
     * Checks if two types are primitive/wrapper equivalents.
     */
    private static boolean isPrimitiveWrapperEquivalent(Class<?> a, Class<?> b) {
        return (PRIMITIVE_TO_WRAPPER.get(a) == b) || (PRIMITIVE_TO_WRAPPER.get(b) == a);
    }

    /**
     * Checks if from can be widened to to (primitive widening).
     */
    private static boolean isWideningPrimitive(Class<?> from, Class<?> to) {
        // Unwrap both to primitives
        Class<?> fromPrim = WRAPPER_TO_PRIMITIVE.getOrDefault(from, from);
        Class<?> toPrim = WRAPPER_TO_PRIMITIVE.getOrDefault(to, to);

        if (!fromPrim.isPrimitive() || !toPrim.isPrimitive()) {
            return false;
        }

        // byte → short → int → long
        // char → int → long
        // int → float → double (with precision loss allowed)
        // float → double

        if (fromPrim == byte.class) {
            return (toPrim == short.class) || (toPrim == int.class) || (toPrim == long.class)
                || (toPrim == float.class) || (toPrim == double.class);
        }
        if (fromPrim == short.class) {
            return (toPrim == int.class) || (toPrim == long.class)
                || (toPrim == float.class) || (toPrim == double.class);
        }
        if (fromPrim == char.class) {
            return (toPrim == int.class) || (toPrim == long.class)
                || (toPrim == float.class) || (toPrim == double.class);
        }
        if (fromPrim == int.class) {
            return (toPrim == long.class) || (toPrim == float.class) || (toPrim == double.class);
        }
        if (fromPrim == long.class) {
            return (toPrim == float.class) || (toPrim == double.class);
        }
        if (fromPrim == float.class) {
            return toPrim == double.class;
        }

        return false;
    }

    /**
     * Checks if from (primitive) can be widened to to (wrapper).
     */
    private static boolean isWideningToWrapper(Class<?> from, Class<?> to) {
        Class<?> fromPrim = WRAPPER_TO_PRIMITIVE.getOrDefault(from, from);
        Class<?> toPrim = WRAPPER_TO_PRIMITIVE.get(to);

        if ((toPrim == null) || !fromPrim.isPrimitive()) {
            return false;
        }

        return isWideningPrimitive(fromPrim, toPrim);
    }

    /**
     * Checks if String can be parsed to the target numeric type.
     */
    private static boolean isStringToNumeric(Class<?> from, Class<?> to) {
        return (from == String.class) && isNumericType(to);
    }

    /**
     * Checks if the type is a numeric type (primitive or wrapper).
     */
    private static boolean isNumericType(Class<?> type) {
        return (type == byte.class) || (type == Byte.class)
            || (type == short.class) || (type == Short.class)
            || (type == int.class) || (type == Integer.class)
            || (type == long.class) || (type == Long.class)
            || (type == float.class) || (type == Float.class)
            || (type == double.class) || (type == Double.class);
    }

    /**
     * Returns the wrapper class for a primitive type, or the type itself if not primitive.
     */
    public static Class<?> wrap(Class<?> type) {
        return PRIMITIVE_TO_WRAPPER.getOrDefault(type, type);
    }

    /**
     * Returns the primitive class for a wrapper type, or the type itself if not a wrapper.
     */
    public static Class<?> unwrap(Class<?> type) {
        return WRAPPER_TO_PRIMITIVE.getOrDefault(type, type);
    }
}
