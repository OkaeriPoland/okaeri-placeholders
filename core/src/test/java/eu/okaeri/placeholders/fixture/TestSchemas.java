package eu.okaeri.placeholders.fixture;

import eu.okaeri.placeholders.schema.annotation.Placeholder;

/**
 * Test classes with @Placeholder annotation for schema resolution tests.
 * These test the auto-discovery and custom naming features.
 */
public final class TestSchemas {

    private TestSchemas() {
    }

    /**
     * Basic schema with auto-scan enabled (default).
     * All public getters should be discovered automatically.
     */
    @Placeholder
    public static final class SchemaItem {
        private final String type;
        private final int amount;
        private final short damage;
        private final byte data;
        private final SchemaMeta meta;

        public SchemaItem(String type, int amount, short damage, byte data, SchemaMeta meta) {
            this.type = type;
            this.amount = amount;
            this.damage = damage;
            this.data = data;
            this.meta = meta;
        }

        public String getType() {
            return type;
        }

        public int getAmount() {
            return amount;
        }

        public short getDamage() {
            return damage;
        }

        public byte getData() {
            return data;
        }

        public SchemaMeta getMeta() {
            return meta;
        }
    }

    /**
     * Nested schema for testing chained placeholder resolution.
     */
    @Placeholder
    public static final class SchemaMeta {
        private final String name;
        private final String lore;

        public SchemaMeta(String name, String lore) {
            this.name = name;
            this.lore = lore;
        }

        public String getName() {
            return name;
        }

        public String getLore() {
            return lore;
        }
    }

    /**
     * Schema with custom placeholder names on methods.
     */
    @Placeholder
    public static final class CustomNamedSchema {
        private final String value;

        public CustomNamedSchema(String value) {
            this.value = value;
        }

        @Placeholder(name = "custom")
        public String getValue() {
            return value;
        }

        @Placeholder(name = "upper")
        public String getUpperCase() {
            return value.toUpperCase();
        }

        @Placeholder(name = "lower")
        public String getLowerCase() {
            return value.toLowerCase();
        }

        @Placeholder(name = "length")
        public int getLength() {
            return value.length();
        }
    }

    /**
     * Schema with scan disabled - only explicitly annotated methods are exposed.
     */
    @Placeholder(scan = false)
    public static final class ExplicitOnlySchema {
        private final String visible;
        private final String hidden;

        public ExplicitOnlySchema(String visible, String hidden) {
            this.visible = visible;
            this.hidden = hidden;
        }

        @Placeholder
        public String getVisible() {
            return visible;
        }

        // This should NOT be discoverable since scan=false
        public String getHidden() {
            return hidden;
        }
    }

    /**
     * Non-annotated class for external registration tests.
     * Placeholders must be registered manually for this class.
     */
    public static final class ExternalItem {
        private final String type;
        private final int amount;
        private final ExternalMeta meta;

        public ExternalItem(String type, int amount, ExternalMeta meta) {
            this.type = type;
            this.amount = amount;
            this.meta = meta;
        }

        public String getType() {
            return type;
        }

        public int getAmount() {
            return amount;
        }

        public ExternalMeta getMeta() {
            return meta;
        }
    }

    /**
     * Non-annotated nested class for external registration tests.
     */
    public static final class ExternalMeta {
        private final String name;
        private final String lore;

        public ExternalMeta(String name, String lore) {
            this.name = name;
            this.lore = lore;
        }

        public String getName() {
            return name;
        }

        public String getLore() {
            return lore;
        }
    }

    /**
     * Schema with enum field for enum placeholder tests.
     */
    @Placeholder
    public static final class SchemaWithEnum {
        private final Type type;
        private final int count;

        public SchemaWithEnum(Type type, int count) {
            this.type = type;
            this.count = count;
        }

        public Type getType() {
            return type;
        }

        public int getCount() {
            return count;
        }

        public enum Type {
            STONE_AXE,
            DIAMOND_PICKAXE,
            IRON_SWORD
        }
    }

    // Factory methods for test data
    public static SchemaMeta sampleSchemaMeta() {
        return new SchemaMeta("Excalibur", "A legendary sword");
    }

    public static SchemaItem sampleSchemaItem() {
        return new SchemaItem("DIAMOND_SWORD", 1, (short) 0, (byte) 0, sampleSchemaMeta());
    }

    public static SchemaItem schemaItemWithNullMeta() {
        return new SchemaItem("STONE", 64, (short) 0, (byte) 0, null);
    }

    public static CustomNamedSchema sampleCustomNamedSchema() {
        return new CustomNamedSchema("Hello World");
    }

    public static ExplicitOnlySchema sampleExplicitOnlySchema() {
        return new ExplicitOnlySchema("I am visible", "I am hidden");
    }

    public static ExternalMeta sampleExternalMeta() {
        return new ExternalMeta("Test Item", "Some lore text");
    }

    public static ExternalItem sampleExternalItem() {
        return new ExternalItem("STONE_AXE", 5, sampleExternalMeta());
    }

    public static ExternalItem externalItemWithNullMeta() {
        return new ExternalItem("COBBLESTONE", 64, null);
    }

    public static SchemaWithEnum sampleSchemaWithEnum() {
        return new SchemaWithEnum(SchemaWithEnum.Type.DIAMOND_PICKAXE, 1);
    }
}
