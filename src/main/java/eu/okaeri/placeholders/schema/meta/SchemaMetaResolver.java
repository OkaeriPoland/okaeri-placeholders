package eu.okaeri.placeholders.schema.meta;

public interface SchemaMetaResolver<T> {
    SchemaObjectPair resolve(T from);
}