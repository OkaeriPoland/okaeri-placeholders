package eu.okaeri.placeholderstest.schema;

import eu.okaeri.placeholders.schema.PlaceholderSchema;
import eu.okaeri.placeholders.schema.annotation.Placeholder;
import lombok.Data;

@Data
public class Item implements PlaceholderSchema {
    @Placeholder private String type;
    @Placeholder private int amount;
    @Placeholder private Meta meta;
}
