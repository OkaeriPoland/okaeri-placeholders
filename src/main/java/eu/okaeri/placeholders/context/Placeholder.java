package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.meta.SchemaMeta;
import eu.okaeri.placeholders.schema.resolver.DefaultSchemaResolver;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import eu.okaeri.pluralize.Pluralize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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

        if ((field.getMetadataOptions() != null) && (object instanceof Integer) && (field.getMetadataOptions().length == Pluralize.plurals(field.getLocale()))) {
            return Pluralize.pluralize(field.getLocale(), ((Integer) object), field.getMetadataOptions());
        }

        if ((field.getMetadataOptions() != null) && (object instanceof Boolean) && (field.getMetadataOptions().length == 2)) {
            return ((Boolean) object) ? field.getMetadataOptions()[0] : field.getMetadataOptions()[1];
        }

        if ((field.getMetadataRaw() != null) && (object instanceof Number) && (field.getMetadataRaw().length() > 1) && (field.getMetadataRaw().charAt(0) == '%')) {
            return String.format(field.getMetadataRaw(), new BigDecimal(String.valueOf(object)).doubleValue());
        }

        if (DefaultSchemaResolver.INSTANCE.supports(object.getClass())) {
            return DefaultSchemaResolver.INSTANCE.resolve(object);
        }

        if (this.placeholders != null) {

            if (!field.hasSub()) {
                PlaceholderResolver resolver = this.placeholders.getResolver(object, null);
                if (resolver != null) {
                    Object value = resolver.resolve(object);
                    return this.render(value, field);
                }
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
