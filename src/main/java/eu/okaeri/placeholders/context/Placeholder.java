package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.meta.PlaceholderResolver;
import eu.okaeri.placeholders.schema.meta.SchemaMeta;
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

    private final Object value;

    @SuppressWarnings("unchecked")
    public String render(MessageField field) {

        if (this.value instanceof String) {
            return (String) this.value;
        }

        if (this.value instanceof PlaceholderSchema) {

            SchemaMeta meta = SchemaMeta.of((Class<? extends PlaceholderSchema>) this.value.getClass());
            if (!field.hasSub()) {
                throw new RuntimeException("rendering PlaceholderSchema itself not supported at the moment");
            }

            MessageField fieldSub = field.getSub();
            Map<String, PlaceholderResolver> placeholders = meta.getPlaceholders();
            PlaceholderResolver resolver = placeholders.get(fieldSub.getName());
            Object resolved = resolver.resolve(this.value);

            return Placeholder.of(resolved).render(fieldSub);
        }

        return String.valueOf(this.value);
    }
}
