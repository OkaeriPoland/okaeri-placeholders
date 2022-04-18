package eu.okaeri.placeholders.schema.resolver;

import eu.okaeri.placeholders.message.part.FieldParams;
import lombok.NonNull;

// resolves object fields to objects
// single element of the PlaceholderPack
public interface PlaceholderResolver<T> {
    Object resolve(@NonNull T from, @NonNull FieldParams params);
}
