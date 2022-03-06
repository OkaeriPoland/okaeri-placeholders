package eu.okaeri.placeholderstest.schema.own;

import eu.okaeri.placeholders.schema.annotation.Placeholder;
import lombok.Data;

@Data
@Placeholder
public class Meta {
    private String name;
    private String lore;
}
