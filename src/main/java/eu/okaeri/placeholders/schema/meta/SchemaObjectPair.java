package eu.okaeri.placeholders.schema.meta;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaObjectPair {
    private SchemaMeta schema;
    private Object object;
}
