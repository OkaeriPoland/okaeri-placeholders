package eu.okaeri.placeholders.schema.resolver;

public interface SchemaResolver {
    boolean supports(Class<?> type);
    String resolve(Object object);
}
