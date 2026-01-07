package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.meta.SchemaMeta;
import eu.okaeri.placeholders.schema.resolver.DefaultSchemaResolver;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@ToString(exclude = "context")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholder {

    private final Object value;
    private Placeholders placeholders;
    private PlaceholderContext context;

    public static Placeholder of(@Nullable Object value) {
        return new Placeholder(value);
    }

    public static Placeholder of(@Nullable Placeholders placeholders, @Nullable Object value) {
        return of(placeholders, value, null);
    }

    public static Placeholder of(@Nullable Placeholders placeholders, @Nullable Object value, @Nullable PlaceholderContext context) {
        Placeholder placeholder = new Placeholder(value);
        placeholder.setPlaceholders(placeholders);
        placeholder.setContext(context);
        return placeholder;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public String render(@NonNull MessageField field) {
        return this.render(this.value, field);
    }

    /**
     * Resolves the placeholder value without converting to String.
     * <p>
     * This is similar to {@link #render(MessageField)} but returns the raw Object
     * instead of converting to String. Useful for field reference resolution
     * in arguments like {@code {$.coalesce(a, b, "default")}}.
     *
     * @param field The field to resolve
     * @return The resolved object value, or null if resolution fails
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public Object resolveValue(@NonNull MessageField field) {
        return this.resolveValue(this.value, field);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Object resolveValue(@Nullable Object object, @NonNull MessageField field) {

        // Handle null case: if there's a sub-field (method like .or()), try to resolve it
        // This allows methods registered on Object.class to provide fallbacks for null values
        if (object == null) {
            if ((this.placeholders != null) && (field.getSub() != null)) {
                MessageField fieldSub = field.getSub();
                // Try to find a resolver for Object.class (e.g., .or() method)
                PlaceholderResolver resolver = this.placeholders.getResolver(Object.class, fieldSub.getName());
                if (resolver != null) {
                    object = resolver.resolve(null, fieldSub, this.context);
                    // Advance field to fieldSub so we don't re-process the same method below
                    field = fieldSub;
                    if (field.hasSub()) {
                        return this.resolveValue(object, field);
                    }
                    return object;
                }
            }
            return null;
        }

        if (this.placeholders != null) {
            if (field.getSub() != null) {
                MessageField fieldSub = field.getSub();
                PlaceholderResolver resolver = this.placeholders.getResolver(object, fieldSub.getName());
                if (resolver == null) {
                    if (object.getClass().getAnnotation(eu.okaeri.placeholders.schema.annotation.Placeholder.class) != null) {
                        return this.resolveValueUsingPlaceholderSchema(object, field);
                    }
                    return null; // No resolver found
                }
                object = resolver.resolve(object, fieldSub, this.context);
                if (fieldSub.hasSub()) {
                    return this.resolveValue(object, fieldSub);
                }
            } else {
                PlaceholderResolver resolver = this.placeholders.getResolver(object, null);
                if (resolver != null) {
                    object = resolver.resolve(object, field, this.context);
                }
            }
        }

        return object; // Return the raw object without String conversion
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Object resolveValueUsingPlaceholderSchema(@NonNull Object object, @NonNull MessageField field) {
        SchemaMeta meta = SchemaMeta.of(object.getClass());
        if (field.getSub() == null) {
            return object; // Return the schema object itself
        }

        MessageField fieldSub = field.getSub();
        Map<String, PlaceholderResolver> placeholders = meta.getPlaceholders();
        PlaceholderResolver resolver = placeholders.get(fieldSub.getName());

        if (resolver == null) {
            return null;
        }

        Object resolved = resolver.resolve(object, fieldSub, this.context);
        return this.resolveValue(resolved, fieldSub);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private String render(@Nullable Object object, @NonNull MessageField field) {

        // Handle null case: if there's a sub-field (method like .or()), try to resolve it
        // This allows methods registered on Object.class to provide fallbacks for null values
        if (object == null) {
            if ((this.placeholders != null) && (field.getSub() != null)) {
                MessageField fieldSub = field.getSub();
                // Try to find a resolver for Object.class (e.g., .or() method)
                PlaceholderResolver resolver = this.placeholders.getResolver(Object.class, fieldSub.getName());
                if (resolver != null) {
                    object = resolver.resolve(null, fieldSub, this.context);
                    // Advance field to fieldSub so we don't re-process the same method below
                    field = fieldSub;
                    if (field.hasSub()) {
                        return this.render(object, field);
                    }
                    // Fall through to render the result (field.getSub() is now null)
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        if ((this.placeholders != null) && (object != null)) {
            if (field.getSub() != null) {
                MessageField fieldSub = field.getSub();
                PlaceholderResolver resolver = this.placeholders.getResolver(object, fieldSub.getName());
                if (resolver == null) {
                    if (object.getClass().getAnnotation(eu.okaeri.placeholders.schema.annotation.Placeholder.class) != null) {
                        return this.renderUsingPlaceholderSchema(object, field);
                    }
                    return ("<noresolver:" + field.getName() + "@" + fieldSub.getName() + ">");
                }
                object = resolver.resolve(object, fieldSub, this.context);
                if (fieldSub.hasSub()) {
                    return this.render(object, fieldSub);
                }
            }
            else {
                PlaceholderResolver resolver = this.placeholders.getResolver(object, null);
                if (resolver != null) {
                    object = resolver.resolve(object, field, this.context);
                }
            }
        }

        if (object == null) {
            return null;
        }

        if (DefaultSchemaResolver.INSTANCE.supports(object.getClass())) {
            return DefaultSchemaResolver.INSTANCE.resolve(object, field);
        }

        if (object.getClass().getAnnotation(eu.okaeri.placeholders.schema.annotation.Placeholder.class) != null) {
            return this.renderUsingPlaceholderSchema(object, field);
        }

        return "<norenderer:" + field.getLastSubPath() + "(" + object.getClass().getSimpleName() + ")>";
    }

    @SuppressWarnings("unchecked")
    private String renderUsingPlaceholderSchema(@NonNull Object object, @NonNull MessageField field) {

        SchemaMeta meta = SchemaMeta.of(object.getClass());
        if (field.getSub() == null) {
            throw new RuntimeException("rendering PlaceholderSchema itself not supported at the moment");
        }

        MessageField fieldSub = field.getSub();
        Map<String, PlaceholderResolver> placeholders = meta.getPlaceholders();
        PlaceholderResolver resolver = placeholders.get(fieldSub.getName());

        if (resolver == null) {
            throw new RuntimeException("resolver cannot be null: " + fieldSub.getName());
        }

        Object resolved = resolver.resolve(object, fieldSub, this.context);
        return this.render(resolved, fieldSub);
    }
}
