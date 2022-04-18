package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.meta.SchemaMeta;
import eu.okaeri.placeholders.schema.resolver.DefaultSchemaResolver;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholder {

    private final Object value;
    private Placeholders placeholders;

    public static Placeholder of(@Nullable Object value) {
        return new Placeholder(value);
    }

    public static Placeholder of(@Nullable Placeholders placeholders, @Nullable Object value) {
        Placeholder placeholder = new Placeholder(value);
        placeholder.setPlaceholders(placeholders);
        return placeholder;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public String render(@NonNull MessageField field) {
        return this.render(this.value, field);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private String render(@Nullable Object object, @NonNull MessageField field) {

        if (object == null) {
            return null;
        }

        if (this.placeholders != null) {
            if (field.getSub() != null) {
                MessageField fieldSub = field.getSub();
                PlaceholderResolver resolver = this.placeholders.getResolver(object, fieldSub.getName());
                if (resolver == null) {
                    if (object.getClass().getAnnotation(eu.okaeri.placeholders.schema.annotation.Placeholder.class) != null) {
                        return this.renderUsingPlaceholderSchema(object, field);
                    }
                    return ("<noresolver:" + field.getName() + "@" + fieldSub.getName() + ">");
                }
                object = resolver.resolve(object, fieldSub.getParams());
                if (fieldSub.hasSub()) {
                    return this.render(object, fieldSub);
                }
            }
            else {
                PlaceholderResolver resolver = this.placeholders.getResolver(object, null);
                if (resolver != null) {
                    object = resolver.resolve(object, field.getParams());
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

        Object resolved = resolver.resolve(object, fieldSub.getParams());
        return this.render(resolved, fieldSub);
    }
}
