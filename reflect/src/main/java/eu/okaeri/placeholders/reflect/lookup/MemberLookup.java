package eu.okaeri.placeholders.reflect.lookup;

import eu.okaeri.placeholders.reflect.argument.TypeCoercer;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Looks up fields and methods using reflection with caching support.
 * <p>
 * For methods, uses type coercion scoring to find the best matching overload.
 */
public final class MemberLookup {

    private final MemberCache cache;

    /**
     * Creates a lookup with the global shared cache.
     */
    public MemberLookup() {
        this(MemberCache.global());
    }

    /**
     * Creates a lookup with a custom cache.
     */
    public MemberLookup(@NonNull MemberCache cache) {
        this.cache = cache;
    }

    /**
     * Finds a field by name.
     *
     * @param clazz The class to search
     * @param name  The field name
     * @return Optional containing the field if found
     */
    public Optional<Field> findField(@NonNull Class<?> clazz, @NonNull String name) {
        // Check cache first
        Optional<Optional<Field>> cached = this.cache.getField(clazz, name);
        if (cached.isPresent()) {
            return cached.get();
        }

        // Try declared fields first (includes private)
        Field field = null;
        try {
            field = clazz.getDeclaredField(name);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            // Try public fields (includes inherited)
            try {
                field = clazz.getField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }

        // Cache the result (including null for negative cache)
        this.cache.putField(clazz, name, field);
        return Optional.ofNullable(field);
    }

    /**
     * Finds a no-argument method by name.
     *
     * @param clazz The class to search
     * @param name  The method name
     * @return Optional containing the method if found
     */
    public Optional<Method> findMethod(@NonNull Class<?> clazz, @NonNull String name) {
        return this.findMethod(clazz, name, new Class<?>[0]);
    }

    /**
     * Finds a method by name and exact argument types.
     *
     * @param clazz    The class to search
     * @param name     The method name
     * @param argTypes The exact argument types
     * @return Optional containing the method if found
     */
    public Optional<Method> findMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>[] argTypes) {
        // Check cache first
        Optional<Optional<Method>> cached = this.cache.getMethod(clazz, name, argTypes);
        if (cached.isPresent()) {
            return cached.get();
        }

        // Try declared methods first (includes private)
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(name, argTypes);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            // Try public methods (includes inherited)
            try {
                method = clazz.getMethod(name, argTypes);
            } catch (NoSuchMethodException ignored) {
            }
        }

        // Cache the result
        this.cache.putMethod(clazz, name, argTypes, method);
        return Optional.ofNullable(method);
    }

    /**
     * Finds the best matching method using type coercion.
     * <p>
     * Scores all candidate methods and returns the one with the lowest coercion cost.
     * This allows calling {@code method(long)} with an int argument.
     *
     * @param clazz    The class to search
     * @param name     The method name
     * @param argTypes The argument types to match (may be coerced)
     * @return Optional containing the best matching method if found
     */
    public Optional<Method> findMethodWithCoercion(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>[] argTypes) {
        // First try exact match
        Optional<Method> exact = this.findMethod(clazz, name, argTypes);
        if (exact.isPresent()) {
            return exact;
        }

        // Find all methods with matching name and parameter count
        return this.getAllMethods(clazz)
            .filter(m -> m.getName().equals(name))
            .filter(m -> m.getParameterCount() == argTypes.length)
            .map(m -> new ScoredMethod(m, this.scoreMethod(m.getParameterTypes(), argTypes)))
            .filter(sm -> sm.score != TypeCoercer.NO_MATCH)
            .min(Comparator.comparingInt(sm -> sm.score))
            .map(sm -> {
                sm.method.setAccessible(true);
                return sm.method;
            });
    }

    /**
     * Calculates the total coercion score for a method signature.
     * Lower scores indicate better matches.
     *
     * @param paramTypes The method's parameter types
     * @param argTypes   The argument types being passed
     * @return Total coercion score, or TypeCoercer.NO_MATCH if any param can't be coerced
     */
    private int scoreMethod(Class<?>[] paramTypes, Class<?>[] argTypes) {
        if (paramTypes.length != argTypes.length) {
            return TypeCoercer.NO_MATCH;
        }

        int totalScore = 0;
        for (int i = 0; i < paramTypes.length; i++) {
            int score = TypeCoercer.coercionScore(argTypes[i], paramTypes[i]);
            if (score == TypeCoercer.NO_MATCH) {
                return TypeCoercer.NO_MATCH;
            }
            totalScore += score;
        }
        return totalScore;
    }

    /**
     * Gets all methods (declared + public inherited) from a class.
     */
    private Stream<Method> getAllMethods(Class<?> clazz) {
        return Stream.concat(
            Arrays.stream(clazz.getDeclaredMethods()),
            Arrays.stream(clazz.getMethods())
        ).distinct();
    }

    /**
     * Checks if a field or method is static.
     */
    public static boolean isStatic(java.lang.reflect.Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * Helper class for scoring method candidates.
     */
    private static class ScoredMethod {
        final Method method;
        final int score;

        ScoredMethod(Method method, int score) {
            this.method = method;
            this.score = score;
        }
    }
}
