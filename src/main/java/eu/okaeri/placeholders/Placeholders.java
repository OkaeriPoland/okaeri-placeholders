package eu.okaeri.placeholders;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.schema.meta.ParameterMeta;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
        this.resolvers.put(ParameterMeta.of(type, null), resolver);
        return this;
    }

    public <T> Placeholders registerPlaceholder(Class<T> type, String name, PlaceholderResolver<T> resolver) {
        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        if (resolver == null) throw new IllegalArgumentException("resolver cannot be null");
        this.resolvers.put(ParameterMeta.of(type, name), resolver);
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
        ParameterMeta parameter = ParameterMeta.of(from.getClass(), param);
        return this.resolvers.get(parameter);
    }

    private Map<ParameterMeta, PlaceholderResolver> resolvers = new ConcurrentHashMap<>();
}
