package eu.okaeri.placeholders;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholders {

    public static Placeholders create() {
        return new Placeholders();
    }

    public PlaceholderContext contextOf(CompiledMessage message) {
        return PlaceholderContext.of(this, message);
    }

    public Placeholders registerPlaceholders(PlaceholderPack pack) {
        pack.register(this);
        return this;
    }

    public <T> Placeholders registerPlaceholder(Class<T> type, PlaceholderResolver<T> resolver) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (resolver == null) throw new IllegalArgumentException("resolver cannot be null");
        Map<String, PlaceholderResolver> resolverMap = this.resolvers.computeIfAbsent(type, kk -> new HashMap<>());
        resolverMap.put(null, resolver);
        return this;
    }

    public <T> Placeholders registerPlaceholder(Class<T> type, String name, PlaceholderResolver<T> resolver) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        if (resolver == null) throw new IllegalArgumentException("resolver cannot be null");
        Map<String, PlaceholderResolver> resolverMap = this.resolvers.computeIfAbsent(type, kk -> new HashMap<>());
        resolverMap.put(name, resolver);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Object readValue(Object from) {
        PlaceholderResolver placeholderResolver = this.getResolver(from, null);
        if (placeholderResolver != null) {
            return placeholderResolver.resolve(from);
        }
        throw new IllegalArgumentException("cannot find resolver for " + from.getClass());
    }

    @SuppressWarnings("unchecked")
    public Object readValue(Object from, String param) {
        PlaceholderResolver placeholderResolver = this.getResolver(from, param);
        if (placeholderResolver != null) {
            return placeholderResolver.resolve(from);
        }
        throw new IllegalArgumentException("cannot find resolver for " + from.getClass() + ": " + param);
    }

    public PlaceholderResolver getResolver(Object from, String param) {

        Class<?> fromClass = from.getClass();
        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(fromClass);

        if (resolverMap == null) {
            for (Class<?> potentialType : this.resolvers.keySet()) {
                if (potentialType.isAssignableFrom(fromClass)) {

                    resolverMap = this.resolvers.get(potentialType);
                    PlaceholderResolver resolver = resolverMap.get(param);

                    if (resolver != null) {
                        return resolver;
                    }
                }
            }
            return null;
        }

        return resolverMap.get(param);
    }

    private Map<Class<?>, Map<String, PlaceholderResolver>> resolvers = new ConcurrentHashMap<>();
}
