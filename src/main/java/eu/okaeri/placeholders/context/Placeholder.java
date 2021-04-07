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
        return render(this.value, field);
    }

    @SuppressWarnings("unchecked")
    private static String render(Object object, MessageField field) {

        if (object instanceof String) {
            return (String) object;
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
            return render(resolved, fieldSub);
        }

        // FIXME: this fallback should be illegal lol
        return String.valueOf(object);
    }
}
