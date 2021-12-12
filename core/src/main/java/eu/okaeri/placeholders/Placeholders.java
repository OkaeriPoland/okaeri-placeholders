package eu.okaeri.placeholders;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholders {

    @Setter private Map<Class<?>, Map<String, PlaceholderResolver>> resolvers = new HashMap<>();

    public static Placeholders create() {
        return create(false);
    }

    public static Placeholders create(boolean registerDefaults) {
        Placeholders placeholders = new Placeholders();
        if (registerDefaults) placeholders.registerPlaceholders(new DefaultPlaceholderPack());
        return placeholders;
    }

    public PlaceholderContext contextOf(@NonNull CompiledMessage message) {
        return PlaceholderContext.of(this, message);
    }

    public Placeholders registerPlaceholders(@NonNull PlaceholderPack pack) {
        pack.register(this);
        return this;
    }

    public <T> Placeholders registerPlaceholder(@NonNull Class<T> type, @NonNull PlaceholderResolver<T> resolver) {
        Map<String, PlaceholderResolver> resolverMap = this.resolvers.computeIfAbsent(type, kk -> new HashMap<>());
        resolverMap.put(null, resolver);
        return this;
    }

    public <T> Placeholders registerPlaceholder(@NonNull Class<T> type, @NonNull String name, @NonNull PlaceholderResolver<T> resolver) {
        Map<String, PlaceholderResolver> resolverMap = this.resolvers.computeIfAbsent(type, kk -> new HashMap<>());
        resolverMap.put(name, resolver);
        return this;
    }

    @SuppressWarnings("unchecked")
    public Object readValue(@NonNull Object from) {
        PlaceholderResolver placeholderResolver = this.getResolver(from, null);
        if (placeholderResolver != null) {
            return placeholderResolver.resolve(from, FieldParams.empty());
        }
        throw new IllegalArgumentException("cannot find resolver for " + from.getClass());
    }

    @SuppressWarnings("unchecked")
    public Object readValue(@NonNull Object from, @Nullable String param) {
        PlaceholderResolver placeholderResolver = this.getResolver(from, param);
        if (placeholderResolver != null) {
            return placeholderResolver.resolve(from, FieldParams.empty());
        }
        throw new IllegalArgumentException("cannot find resolver for " + from.getClass() + ": " + param);
    }

    public PlaceholderResolver getResolver(@NonNull Object from, @Nullable String param) {

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

    public int getResolversCount() {
        return Math.toIntExact(this.resolvers.values().stream()
            .mapToLong(map -> map.entrySet().size())
            .sum());
    }

    public Placeholders copy() {
        Placeholders placeholders = new Placeholders();
        placeholders.resolvers = this.getResolversCopy();
        return placeholders;
    }

    public Map<Class<?>, Map<String, PlaceholderResolver>> getResolversCopy() {
        Map<Class<?>, Map<String, PlaceholderResolver>> resolvers = new HashMap<>();
        for (Map.Entry<Class<?>, Map<String, PlaceholderResolver>> entry : this.resolvers.entrySet()) {
            Map<String, PlaceholderResolver> map = new HashMap<>();
            map.putAll(entry.getValue());
            resolvers.put(entry.getKey(), map);
        }
        return resolvers;
    }
}
