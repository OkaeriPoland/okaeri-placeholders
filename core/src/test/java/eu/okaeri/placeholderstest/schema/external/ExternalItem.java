package eu.okaeri.placeholderstest.schema.external;

import lombok.Data;

@Data
public class ExternalItem {

    private String type;
    private int amount;
    private short damage;
    private byte data;
    private ExternalMeta meta;
    private Type typeEnum;

    public enum Type {
        STONE_AXE,
        DIAMOND_PICKAXE
    }
}
