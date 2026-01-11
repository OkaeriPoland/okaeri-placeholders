package eu.okaeri.placeholders;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.schema.DefaultRegistry;
import eu.okaeri.placeholders.schema.GlobalMethods;
import eu.okaeri.placeholders.schema.Params;
import eu.okaeri.placeholders.schema.TypeMethods;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import eu.okaeri.pluralize.Pluralize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholders {

    /**
     * The key used for global functions in the placeholder context.
     * Global functions are accessible via {$.functionName(args)} syntax.
     */
    public static final String GLOBAL_FUNCTIONS_KEY = "$";

    private Map<Class<?>, Map<String, PlaceholderResolver>> resolvers = new LinkedHashMap<>();
    private List<Class<?>> resolversOrdered = new ArrayList<>();
    @Getter private PlaceholderResolver fallbackResolver = null;

    /**
     * Create a new Placeholders instance with default packs registered.
     */
    public static Placeholders create() {
        return new Placeholders().with(new DefaultPlaceholderPack());
    }

    /**
     * Create an empty Placeholders instance with no packs registered.
     */
    public static Placeholders empty() {
        return new Placeholders();
    }

    /**
     * Create a placeholder context for the given message.
     */
    public PlaceholderContext context(@NonNull CompiledMessage message) {
        return PlaceholderContext.of(this, message);
    }

    /**
     * Set a fallback resolver used when no specific resolver is found.
     */
    public Placeholders fallback(PlaceholderResolver fallbackResolver) {
        this.fallbackResolver = fallbackResolver;
        return this;
    }

    /**
     * Register a placeholder pack.
     */
    public Placeholders with(@NonNull PlaceholderPack pack) {
        pack.register(this);
        return this;
    }

    /**
     * Access the fluent API for registering type methods.
     * <p>
     * Example:
     * <pre>
     * placeholders.type(String.class)
     *     .add("reverse", s -> new StringBuilder(s).reverse().toString())
     *     .add("truncate", (s, p) -> s.substring(0, Math.min(s.length(), p.arg(0).asInt(10))));
     * </pre>
     */
    public <T> TypeMethods<T> type(@NonNull Class<T> type) {
        return DefaultRegistry.of(this).type(type);
    }

    /**
     * Access the fluent API for registering global functions.
     * <p>
     * Example:
     * <pre>
     * placeholders.globals()
     *     .add("env", p -> System.getenv(p.arg(0).asString()))
     *     .add("now", () -> Instant.now());
     * </pre>
     */
    public GlobalMethods globals() {
        return DefaultRegistry.of(this).globals();
    }

    /**
     * Register a global function with no parameters.
     * Shorthand for {@code globals().add(name, resolver)}.
     */
    public Placeholders global(@NonNull String name, @NonNull Supplier<Object> resolver) {
        this.globals().add(name, resolver);
        return this;
    }

    /**
     * Register a global function with parameters.
     * Shorthand for {@code globals().add(name, resolver)}.
     */
    public Placeholders global(@NonNull String name, @NonNull Function<Params, Object> resolver) {
        this.globals().add(name, resolver);
        return this;
    }

    /**
     * Register a global function with parameters and context.
     * Shorthand for {@code globals().add(name, resolver)}.
     */
    public Placeholders global(@NonNull String name, @NonNull BiFunction<Params, PlaceholderContext, Object> resolver) {
        this.globals().add(name, resolver);
        return this;
    }

    // Low-level registration - prefer using type() or globals() fluent API

    /**
     * Register a default resolver for a type (when no method name specified).
     * Prefer using {@link #type(Class)} fluent API.
     */
    public <T> Placeholders register(@NonNull Class<T> type, @NonNull PlaceholderResolver<T> resolver) {

        if (!this.resolvers.containsKey(type)) {
            this.resolvers.put(type, new HashMap<>());
            this.resolversOrdered.add(0, type);
        }

        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(type);
        resolverMap.put(null, resolver);
        return this;
    }

    /**
     * Register a named resolver for a type.
     * Prefer using {@link #type(Class)} fluent API.
     */
    public <T> Placeholders register(@NonNull Class<T> type, @NonNull String name, @NonNull PlaceholderResolver<T> resolver) {

        if (!this.resolvers.containsKey(type)) {
            this.resolvers.put(type, new HashMap<>());
            this.resolversOrdered.add(0, type);
        }

        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(type);
        resolverMap.put(name, resolver);
        return this;
    }

    /**
     * Register a global function with a raw resolver.
     * Prefer using {@link #global(String, Supplier)} or {@link #globals()} fluent API.
     */
    public Placeholders register(@NonNull String name, @NonNull PlaceholderResolver<GlobalFunctions> resolver) {
        return this.register(GlobalFunctions.class, name, resolver);
    }

    // Used internally by copy()
    void setResolvers(@NonNull Map<Class<?>, Map<String, PlaceholderResolver>> resolvers) {
        this.resolvers = resolvers;
        ArrayList<Class<?>> keys = new ArrayList<>(resolvers.keySet());
        Collections.reverse(keys);
        this.resolversOrdered = keys;
    }

    public PlaceholderResolver getResolver(@NonNull Object from, @Nullable String param) {

        Class<?> fromClass = from.getClass();
        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(fromClass);

        if (resolverMap != null) {
            PlaceholderResolver resolver = resolverMap.get(param);
            if (resolver != null) {
                return resolver;
            }
        }

        PlaceholderResolver resolver = this.findResolverOrNull(from, param);
        if (resolver != null) {
            return resolver;
        }

        return this.fallbackResolver;
    }

    /**
     * Gets a resolver for a specific class type and parameter.
     * This is useful for looking up resolvers when the actual object is null
     * (e.g., for methods like .or() that handle null values).
     */
    public PlaceholderResolver getResolver(@NonNull Class<?> fromClass, @Nullable String param) {

        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(fromClass);

        if (resolverMap != null) {
            PlaceholderResolver resolver = resolverMap.get(param);
            if (resolver != null) {
                return resolver;
            }
        }

        // Check parent classes in order
        for (Class<?> potentialType : this.resolversOrdered) {
            if (!potentialType.isAssignableFrom(fromClass)) {
                continue;
            }
            resolverMap = this.resolvers.get(potentialType);
            PlaceholderResolver resolver = resolverMap.get(param);
            if (resolver != null) {
                return resolver;
            }
        }

        return null;
    }

    private PlaceholderResolver findResolverOrNull(@NonNull Object from, @Nullable String param) {

        for (Class<?> potentialType : this.resolversOrdered) {

            if (!potentialType.isAssignableFrom(from.getClass())) {
                continue;
            }

            Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(potentialType);
            PlaceholderResolver resolver = resolverMap.get(param);

            if (resolver == null) {
                continue;
            }

            return resolver;
        }

        return null;
    }

    /**
     * Create a copy of this Placeholders instance.
     */
    public Placeholders copy() {
        Placeholders placeholders = new Placeholders();
        placeholders.setResolvers(this.copyResolvers());
        placeholders.fallbackResolver = this.getFallbackResolver();
        return placeholders;
    }

    private Map<Class<?>, Map<String, PlaceholderResolver>> copyResolvers() {
        Map<Class<?>, Map<String, PlaceholderResolver>> resolvers = new LinkedHashMap<>();
        for (Map.Entry<Class<?>, Map<String, PlaceholderResolver>> entry : this.resolvers.entrySet()) {
            Map<String, PlaceholderResolver> map = new HashMap<>();
            map.putAll(entry.getValue());
            resolvers.put(entry.getKey(), map);
        }
        return resolvers;
    }

    public static String pluralize(@NonNull Locale locale, int count, @NonNull String... options) {
        if (options.length == 0) {
            return String.valueOf(count);
        }
        try {
            if (options.length == Pluralize.plurals(locale)) {
                return Pluralize.pluralize(locale, count, options);
            }
        } catch (IllegalArgumentException exception) {
            try {
                return Pluralize.pluralize(Locale.ENGLISH, count, options);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return options[0];
    }
}
