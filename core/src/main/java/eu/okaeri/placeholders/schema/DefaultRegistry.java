package eu.okaeri.placeholders.schema;

import eu.okaeri.placeholders.GlobalFunctions;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Default implementation of {@link Registry} that bridges to {@link Placeholders}.
 */
@RequiredArgsConstructor
public class DefaultRegistry implements Registry {

    private final Placeholders placeholders;
    private final Map<String, String> typeAliases = new HashMap<>();
    private final Map<String, String> globalAliases = new HashMap<>();

    /**
     * Creates a registry for the given Placeholders instance.
     */
    public static Registry of(Placeholders placeholders) {
        return new DefaultRegistry(placeholders);
    }

    @Override
    public <T> TypeMethods<T> type(Class<T> type) {
        return new DefaultTypeMethods<>(this, this.placeholders, type);
    }

    @Override
    public GlobalMethods globals() {
        return new DefaultGlobalMethods(this, this.placeholders);
    }

    void registerTypeAlias(Class<?> type, String alias, String target) {
        String key = type.getName() + ":" + alias;
        this.typeAliases.put(key, target);
    }

    void registerGlobalAlias(String alias, String target) {
        this.globalAliases.put(alias, target);
    }

    String resolveTypeAlias(Class<?> type, String name) {
        String key = type.getName() + ":" + name;
        return this.typeAliases.getOrDefault(key, name);
    }

    String resolveGlobalAlias(String name) {
        return this.globalAliases.getOrDefault(name, name);
    }

    /**
     * Implementation of TypeMethods that registers to Placeholders.
     */
    @RequiredArgsConstructor
    static class DefaultTypeMethods<T> implements TypeMethods<T> {

        private final DefaultRegistry registry;
        private final Placeholders placeholders;
        private final Class<T> type;

        @Override
        public TypeMethods<T> add(String name, Function<T, Object> resolver) {
            this.placeholders.register(this.type, name, (value, field, ctx) -> resolver.apply(value));
            return this;
        }

        @Override
        public TypeMethods<T> add(String name, BiFunction<T, Params, Object> resolver) {
            this.placeholders.register(this.type, name, (PlaceholderResolver<T>) (value, field, ctx) -> {
                Params params = DefaultParams.of(field.params(), field.locale(), ctx);
                return resolver.apply(value, params);
            });
            return this;
        }

        @Override
        public TypeMethods<T> add(String name, TriFunction<T, Params, PlaceholderContext, Object> resolver) {
            this.placeholders.register(this.type, name, (PlaceholderResolver<T>) (value, field, ctx) -> {
                Params params = DefaultParams.of(field.params(), field.locale(), ctx);
                return resolver.apply(value, params, ctx);
            });
            return this;
        }

        @Override
        public TypeMethods<T> self(Function<T, Object> resolver) {
            this.placeholders.register(this.type, (value, field, ctx) -> resolver.apply(value));
            return this;
        }

        @Override
        public TypeMethods<T> self(BiFunction<T, Params, Object> resolver) {
            this.placeholders.register(this.type, (PlaceholderResolver<T>) (value, field, ctx) -> {
                Params params = DefaultParams.of(field.params(), field.locale(), ctx);
                return resolver.apply(value, params);
            });
            return this;
        }

        @Override
        public TypeMethods<T> self(TriFunction<T, Params, PlaceholderContext, Object> resolver) {
            this.placeholders.register(this.type, (PlaceholderResolver<T>) (value, field, ctx) -> {
                Params params = DefaultParams.of(field.params(), field.locale(), ctx);
                return resolver.apply(value, params, ctx);
            });
            return this;
        }

        @Override
        public TypeMethods<T> alias(String target, String... aliases) {
            // Get the resolver for the target and register it under each alias
            PlaceholderResolver<T> targetResolver = (PlaceholderResolver<T>) this.placeholders.getResolver(this.type, target);
            if (targetResolver != null) {
                for (String alias : aliases) {
                    this.placeholders.register(this.type, alias, targetResolver);
                    this.registry.registerTypeAlias(this.type, alias, target);
                }
            }
            return this;
        }

        @Override
        public <U> TypeMethods<U> type(Class<U> newType) {
            return new DefaultTypeMethods<>(this.registry, this.placeholders, newType);
        }

        @Override
        public GlobalMethods globals() {
            return new DefaultGlobalMethods(this.registry, this.placeholders);
        }
    }

    /**
     * Implementation of GlobalMethods that registers to Placeholders.
     */
    @RequiredArgsConstructor
    static class DefaultGlobalMethods implements GlobalMethods {

        private final DefaultRegistry registry;
        private final Placeholders placeholders;

        @Override
        public GlobalMethods add(String name, Supplier<Object> resolver) {
            this.placeholders.register(name, (gf, field, ctx) -> resolver.get());
            return this;
        }

        @Override
        public GlobalMethods add(String name, Function<Params, Object> resolver) {
            this.placeholders.register(name, (gf, field, ctx) -> {
                Params params = DefaultParams.of(field.params(), field.locale(), ctx);
                return resolver.apply(params);
            });
            return this;
        }

        @Override
        public GlobalMethods add(String name, BiFunction<Params, PlaceholderContext, Object> resolver) {
            this.placeholders.register(name, (gf, field, ctx) -> {
                Params params = DefaultParams.of(field.params(), field.locale(), ctx);
                return resolver.apply(params, ctx);
            });
            return this;
        }

        @Override
        public GlobalMethods alias(String target, String... aliases) {
            PlaceholderResolver<GlobalFunctions> targetResolver =
                (PlaceholderResolver<GlobalFunctions>) this.placeholders.getResolver(GlobalFunctions.class, target);
            if (targetResolver != null) {
                for (String alias : aliases) {
                    this.placeholders.register(alias, targetResolver);
                    this.registry.registerGlobalAlias(alias, target);
                }
            }
            return this;
        }

        @Override
        public <T> TypeMethods<T> type(Class<T> type) {
            return new DefaultTypeMethods<>(this.registry, this.placeholders, type);
        }
    }
}
