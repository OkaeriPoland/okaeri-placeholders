package eu.okaeri.placeholders.resolver.annotation;

import eu.okaeri.placeholders.resolver.annotation.Placeholder;
import eu.okaeri.placeholders.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Scans classes annotated with {@link Placeholder} and builds resolvers for their getters, methods, and fields.
 * <p>
 * This enables annotation-based placeholder resolution where you can define a class like:
 * <pre>{@code
 * @Placeholder
 * class Player {
 *     public String getName() { return "John"; }
 *     public int getHealth() { return 100; }
 * }
 * }</pre>
 * <p>
 * And use it directly in templates: {@code {player.name}: {player.health}}
 *
 * <h2>Scanning Behavior</h2>
 * <ul>
 *   <li>Class with {@code @Placeholder}: auto-discovers all public {@code getXxx()} and {@code isXxx()} methods</li>
 *   <li>Class with {@code @Placeholder(scan = false)}: only exposes explicitly annotated methods/fields</li>
 *   <li>Method with {@code @Placeholder(name = "custom")}: exposes with custom name (overrides auto-scan)</li>
 *   <li>Field with {@code @Placeholder}: exposes the field directly</li>
 * </ul>
 * <p>
 * Results are cached per-class for performance.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnotationResolver {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, AnnotationResolver> CACHE = new ConcurrentHashMap<>();

    @Getter
    private final Class<?> type;
    private final Map<String, PlaceholderResolver> resolvers;

    /**
     * Gets or creates an AnnotationResolver for the given class.
     * Results are cached for performance.
     *
     * @param clazz The class to scan for @Placeholder annotations
     * @return The resolver containing all discovered placeholder methods
     */
    public static AnnotationResolver of(@NonNull Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, AnnotationResolver::scan);
    }

    /**
     * Gets the resolver for a placeholder name.
     *
     * @param name The placeholder name (e.g., "name" for getName())
     * @return The resolver, or null if not found
     */
    @Nullable
    public PlaceholderResolver getResolver(@Nullable String name) {
        return this.resolvers.get(name);
    }

    /**
     * Returns an unmodifiable view of all resolvers.
     */
    public Map<String, PlaceholderResolver> getResolvers() {
        return Collections.unmodifiableMap(this.resolvers);
    }

    /**
     * Scans a class for @Placeholder annotations and builds resolvers.
     */
    private static AnnotationResolver scan(Class<?> clazz) {
        Map<String, PlaceholderResolver> resolvers = new LinkedHashMap<>();
        Placeholder classAnnotation = clazz.getAnnotation(Placeholder.class);

        // Auto-scan getters if @Placeholder on class with scan=true (default)
        if ((classAnnotation != null) && classAnnotation.scan()) {
            if (!classAnnotation.name().isEmpty()) {
                throw new IllegalArgumentException("@Placeholder on class " + clazz.getName() +
                    " cannot have a name; names are only supported on methods and fields");
            }
            scanGetters(clazz, resolvers);
        }

        // Scan annotated fields (can override auto-scanned)
        scanAnnotatedFields(clazz, resolvers);

        // Scan annotated methods (can override auto-scanned)
        scanAnnotatedMethods(clazz, resolvers);

        return new AnnotationResolver(clazz, resolvers);
    }

    /**
     * Scans public getXxx() and isXxx() methods as placeholders.
     */
    private static void scanGetters(Class<?> clazz, Map<String, PlaceholderResolver> resolvers) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }

            String methodName = method.getName();
            String placeholderName;

            if (methodName.startsWith("get") && methodName.length() > 3) {
                placeholderName = decapitalize(methodName.substring(3));
            } else if (methodName.startsWith("is") && methodName.length() > 2) {
                placeholderName = decapitalize(methodName.substring(2));
            } else {
                continue;
            }

            MethodHandle handle = createMethodHandle(method);
            resolvers.put(placeholderName, (target, params, context) -> invokeHandle(handle, target));
        }
    }

    /**
     * Scans fields annotated with @Placeholder.
     */
    private static void scanAnnotatedFields(Class<?> clazz, Map<String, PlaceholderResolver> resolvers) {
        for (Field field : clazz.getDeclaredFields()) {
            Placeholder annotation = field.getAnnotation(Placeholder.class);
            if (annotation == null) {
                continue;
            }

            String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
            MethodHandle handle = createFieldHandle(field);
            resolvers.put(name, (target, params, context) -> invokeHandle(handle, target));
        }
    }

    /**
     * Scans methods annotated with @Placeholder.
     */
    private static void scanAnnotatedMethods(Class<?> clazz, Map<String, PlaceholderResolver> resolvers) {
        for (Method method : clazz.getDeclaredMethods()) {
            Placeholder annotation = method.getAnnotation(Placeholder.class);
            if (annotation == null) {
                continue;
            }

            String name;
            if (!annotation.name().isEmpty()) {
                // Custom name specified - use it and remove any auto-scanned version
                name = annotation.name();
                String derivedName = deriveGetterName(method.getName());
                if (derivedName != null) {
                    resolvers.remove(derivedName); // Remove auto-scanned version
                }
            } else {
                // No custom name - derive from method name (strip get/is)
                name = deriveGetterName(method.getName());
                if (name == null) {
                    name = method.getName(); // Non-getter, use raw name
                }
            }
            MethodHandle handle = createMethodHandle(method);
            resolvers.put(name, (target, params, context) -> invokeHandle(handle, target));
        }
    }

    /**
     * Invokes a MethodHandle with the given target object.
     */
    private static Object invokeHandle(MethodHandle handle, Object target) {
        try {
            return handle.invoke(target);
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to invoke method handle on " + target, e);
        }
    }

    /**
     * Creates a MethodHandle for a method.
     */
    private static MethodHandle createMethodHandle(Method method) {
        try {
            method.setAccessible(true);
            return LOOKUP.unreflect(method);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to create method handle for " + method, e);
        }
    }

    /**
     * Creates a MethodHandle for a field getter.
     */
    private static MethodHandle createFieldHandle(Field field) {
        try {
            field.setAccessible(true);
            return LOOKUP.unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to create field handle for " + field, e);
        }
    }

    /**
     * Decapitalizes a string (e.g., "Name" → "name").
     */
    private static String decapitalize(String str) {
        if (str.isEmpty()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * Derives a placeholder name from a getter method name.
     * Returns null if the method is not a getter (doesn't start with get/is).
     */
    @Nullable
    private static String deriveGetterName(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return decapitalize(methodName.substring(3));
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return decapitalize(methodName.substring(2));
        }
        return null;
    }
}
