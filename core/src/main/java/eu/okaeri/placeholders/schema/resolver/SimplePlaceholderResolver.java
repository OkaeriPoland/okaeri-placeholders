package eu.okaeri.placeholders.schema.resolver;

// resolves object fields to objects
// single element of the PlaceholderPack
public interface SimplePlaceholderResolver<T> {
    Object resolve(T from);
}
