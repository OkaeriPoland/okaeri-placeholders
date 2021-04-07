package eu.okaeri.placeholders.schema.meta;

public interface PlaceholderResolver<T> {
    Object resolve(T from);
}
