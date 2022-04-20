package eu.okaeri.placeholders.schema.resolver;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.part.FieldParams;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

// resolves object fields to objects
// single element of the PlaceholderPack
public interface PlaceholderResolver<T> {
    Object resolve(@NonNull T from, @NonNull FieldParams params, @Nullable PlaceholderContext context);
}
