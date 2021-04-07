package eu.okaeri.placeholders.schema.meta;

public interface PlaceholderResolver<T> {
    String resolve(T from);
}
