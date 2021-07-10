package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.meta.SchemaMeta;
import eu.okaeri.placeholders.schema.resolver.DefaultSchemaResolver;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholder {

    public static Placeholder of(Object value) {
        return new Placeholder(value);
    }

    public static Placeholder of(Placeholders placeholders, Object value) {
        Placeholder placeholder = new Placeholder(value);
        placeholder.setPlaceholders(placeholders);
        return placeholder;
    }

    private Placeholders placeholders;
    private final Object value;

    @SuppressWarnings("unchecked")
    public String render(@NonNull MessageField field) {
        return this.render(this.value, field);
    }

    @SuppressWarnings("unchecked")
    private String render(Object object, @NonNull MessageField field) {

        if (object == null) {
            return null;
        }

        if (this.placeholders != null) {
            if (field.hasSub()) {
                MessageField fieldSub = field.getSub();
                PlaceholderResolver resolver = this.placeholders.getResolver(object, fieldSub.getName());
                if (resolver == null) {
                    return ("<noresolver:" + field.getName() + "@" + fieldSub.getName() + ">");
                }
                Object value = resolver.resolve(object, fieldSub.getParams());
                return this.render(value, fieldSub);
            } else {
                PlaceholderResolver resolver = this.placeholders.getResolver(object, null);
                if (resolver != null) {
                    Object value = resolver.resolve(object, field.getParams());
                    return this.render(value, field);
                }
            }
        }

        if (DefaultSchemaResolver.INSTANCE.supports(object.getClass())) {
            return DefaultSchemaResolver.INSTANCE.resolve(object, field);
        }

        if (object instanceof PlaceholderSchema) {

            SchemaMeta meta = SchemaMeta.of((Class<? extends PlaceholderSchema>) object.getClass());
            if (!field.hasSub()) {
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

        return "<norenderer:" + field.getLastSubPath() + "(" + object.getClass().getSimpleName() + ")>";
    }
}
