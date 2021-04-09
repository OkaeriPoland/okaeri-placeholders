package eu.okaeri.placeholders.schema.resolver;

import eu.okaeri.placeholders.message.part.FieldParams;

// resolves object fields to objects
// single element of the PlaceholderPack
public interface PlaceholderResolver<T> {
    Object resolve(T from, FieldParams params);
}
