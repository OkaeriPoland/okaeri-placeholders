package eu.okaeri.placeholderstest.schema;

import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.annotation.Placeholder;
import lombok.Data;

@Data
public class Meta implements PlaceholderSchema {
    @Placeholder private String name;
    @Placeholder private String lore;
}
