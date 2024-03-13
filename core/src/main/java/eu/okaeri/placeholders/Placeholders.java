package eu.okaeri.placeholders;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholders {

    private Map<Class<?>, Map<String, PlaceholderResolver>> resolvers = new LinkedHashMap<>();
    private List<Class<?>> resolversOrdered = new ArrayList<>();
    @Getter private PlaceholderResolver fallbackResolver = null;
    @Getter private boolean fastMode = true;

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

    public Placeholders fallbackResolver(PlaceholderResolver fallbackResolver) {
        this.fallbackResolver = fallbackResolver;
        return this;
    }

    /**
     * Sets fast mode state.
     *
     * Fast mode applies to the non-shared {@link PlaceholderContext} instances.
     *
     * When {@code fastMode} is set to {@code true}, fields added to the context
     * and not present in the specific message would be discarded immediately.
     *
     * When {@code fastMode} is set to {@code false}, all fields added to the
     * context are preserved regardless of the message contents.
     *
     * @param fastMode Target fast mode state
     * @return This instance
     */
    public Placeholders fastMode(boolean fastMode) {
        this.fastMode = fastMode;
        return this;
    }

    public Placeholders registerPlaceholders(@NonNull PlaceholderPack pack) {
        pack.register(this);
        return this;
    }

    public void setResolvers(@NonNull Map<Class<?>, Map<String, PlaceholderResolver>> resolvers) {
        this.resolvers = resolvers;
        ArrayList<Class<?>> keys = new ArrayList<>(resolvers.keySet());
        Collections.reverse(keys);
        this.resolversOrdered = keys;
    }

    public <T> Placeholders registerPlaceholder(@NonNull Class<T> type, @NonNull PlaceholderResolver<T> resolver) {

        if (!this.resolvers.containsKey(type)) {
            this.resolvers.put(type, new HashMap<>());
            this.resolversOrdered.add(0, type);
        }

        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(type);
        resolverMap.put(null, resolver);
        return this;
    }

    public <T> Placeholders registerPlaceholder(@NonNull Class<T> type, @NonNull String name, @NonNull PlaceholderResolver<T> resolver) {

        if (!this.resolvers.containsKey(type)) {
            this.resolvers.put(type, new HashMap<>());
            this.resolversOrdered.add(0, type);
        }

        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(type);
        resolverMap.put(name, resolver);
        return this;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public Object readValue(@NonNull Object from) {
        PlaceholderResolver placeholderResolver = this.getResolver(from, null);
        if (placeholderResolver != null) {
            return placeholderResolver.resolve(from, MessageField.unknown(), null);
        }
        throw new IllegalArgumentException("cannot find resolver for " + from.getClass());
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public Object readValue(@NonNull Object from, @Nullable String param) {
        PlaceholderResolver placeholderResolver = this.getResolver(from, param);
        if (placeholderResolver != null) {
            return placeholderResolver.resolve(from, MessageField.unknown(), null);
        }
        throw new IllegalArgumentException("cannot find resolver for " + from.getClass() + ": " + param);
    }

    public PlaceholderResolver getResolver(@NonNull Object from, @Nullable String param) {

        Class<?> fromClass = from.getClass();
        Map<String, PlaceholderResolver> resolverMap = this.resolvers.get(fromClass);

        if (resolverMap == null) {

            for (Class<?> potentialType : this.resolversOrdered) {
                if (potentialType.isAssignableFrom(fromClass)) {

                    resolverMap = this.resolvers.get(potentialType);
                    PlaceholderResolver resolver = resolverMap.get(param);

                    if (resolver != null) {
                        return resolver;
                    }
                }
            }

            return this.fallbackResolver;
        }

        PlaceholderResolver resolver = resolverMap.get(param);
        if (resolver == null) {
            return this.fallbackResolver;
        }

        return resolver;
    }

    public int getResolversCount() {
        return Math.toIntExact(this.resolvers.values().stream()
            .mapToLong(map -> map.entrySet().size())
            .sum());
    }

    public Placeholders copy() {
        Placeholders placeholders = new Placeholders();
        placeholders.setResolvers(this.getResolversCopy());
        placeholders.fallbackResolver = this.getFallbackResolver();
        return placeholders;
    }

    public Map<Class<?>, Map<String, PlaceholderResolver>> getResolversCopy() {
        Map<Class<?>, Map<String, PlaceholderResolver>> resolvers = new LinkedHashMap<>();
        for (Map.Entry<Class<?>, Map<String, PlaceholderResolver>> entry : this.resolvers.entrySet()) {
            Map<String, PlaceholderResolver> map = new HashMap<>();
            map.putAll(entry.getValue());
            resolvers.put(entry.getKey(), map);
        }
        return resolvers;
    }
}
