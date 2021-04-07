package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.meta.PlaceholderResolver;
import eu.okaeri.placeholders.schema.meta.SchemaMeta;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Placeholder {

    public static Placeholder of(Object value) {
        return new Placeholder(value);
    }

    private final Object value;
    private final Map<MessageField, String> savedResults = new HashMap<>();

    @SuppressWarnings("unchecked")
    public String render(MessageField field) {

        String savedResult = this.savedResults.get(field);
        if (savedResult != null) {
            return savedResult;
        }

        if (this.value instanceof String) {
            String result = (String) this.value;
            this.savedResults.put(field, result);
            return result;
        }

        if (this.value instanceof PlaceholderSchema) {

            SchemaMeta meta = SchemaMeta.of((Class<? extends PlaceholderSchema>) this.value.getClass());
            if (!field.hasSub()) {
                throw new RuntimeException("rendering PlaceholderSchema itself not supported at the moment");
            }

            MessageField fieldSub = field.getSub();
            Map<String, PlaceholderResolver> placeholders = meta.getPlaceholders();
            PlaceholderResolver resolver = placeholders.get(fieldSub.getName());
            if (resolver == null) {
                throw new RuntimeException("resolver cannot be null: " + fieldSub.getName());
            }

            Object resolved = resolver.resolve(this.value);
            Placeholder placeholder = Placeholder.of(resolved);
            String result = placeholder.render(fieldSub);
            this.savedResults.put(field, result);

            return result;
        }

        // FIXME: this fallback should be illegal lol
        String result = String.valueOf(this.value);
        this.savedResults.put(field, result);
        return result;
    }
}
