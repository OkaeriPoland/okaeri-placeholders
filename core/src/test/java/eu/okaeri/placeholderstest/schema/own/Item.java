package eu.okaeri.placeholderstest.schema.own;

import eu.okaeri.placeholders.schema.annotation.Placeholder;
import lombok.Data;

@Data
@Placeholder
public class Item {
    private String type;
    private int amount;
    private short damage;
    private byte data;
    private Meta meta;
}
