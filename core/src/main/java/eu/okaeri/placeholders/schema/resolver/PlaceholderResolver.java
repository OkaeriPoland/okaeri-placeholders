package eu.okaeri.placeholders.schema.resolver;

public interface PlaceholderResolver<T> {
    Object resolve(T from);
}
