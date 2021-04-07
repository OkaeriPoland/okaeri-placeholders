package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.meta.SchemaMeta;
import eu.okaeri.placeholders.schema.resolver.DefaultSchemaResolver;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.AccessLevel;
import lombok.Data;
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
    public String render(MessageField field) {
        return this.render(this.value, field);
    }

    @SuppressWarnings("unchecked")
    private String render(Object object, MessageField field) {

        if (object == null) {
            return null;
        }

        // FIXME: allow to override with placeholders: e.g. booleans, floating point format can be rendered localized
        if (DefaultSchemaResolver.INSTANCE.supports(object.getClass())) {
            return DefaultSchemaResolver.INSTANCE.resolve(object);
        }

        if (this.placeholders != null) {

            if (!field.hasSub()) {
                throw new RuntimeException("rendering itself not supported at the moment: " + field + " [" + object.getClass().getSimpleName() + "]");
            }

            MessageField fieldSub = field.getSub();
            PlaceholderResolver resolver = this.placeholders.getResolver(object, fieldSub.getName());

            if (resolver != null) {
                Object value = resolver.resolve(object);
                return this.render(value, fieldSub);
            }
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

            Object resolved = resolver.resolve(object);
            return this.render(resolved, fieldSub);
        }

        throw new RuntimeException("cannot render " + object.getClass() + ": " + object);
    }
}
