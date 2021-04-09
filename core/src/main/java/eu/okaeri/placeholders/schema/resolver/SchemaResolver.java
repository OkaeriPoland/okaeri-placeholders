package eu.okaeri.placeholders.schema.resolver;

import eu.okaeri.placeholders.message.part.MessageField;

// resolves objects to strings
public interface SchemaResolver {
    boolean supports(Class<?> type);
    String resolve(Object object, MessageField field);
    String resolve(Object object);
}
