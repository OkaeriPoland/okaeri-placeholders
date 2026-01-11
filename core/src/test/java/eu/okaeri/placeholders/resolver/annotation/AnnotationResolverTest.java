package eu.okaeri.placeholders.resolver.annotation;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.TestSchemas;
import eu.okaeri.placeholders.fixture.TestSchemas.*;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for @Placeholder annotation-based auto-discovery.
 */
class AnnotationResolverTest {

    private final Placeholders placeholders = Placeholders.create();

    // === Basic getter auto-discovery ===

    @Test
    void shouldDiscoverGetters() {
        SchemaItem item = TestSchemas.sampleSchemaItem();

        String result = this.placeholders
            .context(CompiledMessage.of("{item.type} x{item.amount}"))
            .with("item", item)
            .apply();

        assertThat(result).isEqualTo("DIAMOND_SWORD x1");
    }

    @Test
    void shouldDiscoverNumericGetters() {
        SchemaItem item = new SchemaItem("TEST", 42, (short) 5, (byte) 3, null);

        String result = this.placeholders
            .context(CompiledMessage.of("amount={item.amount}, damage={item.damage}, data={item.data}"))
            .with("item", item)
            .apply();

        assertThat(result).isEqualTo("amount=42, damage=5, data=3");
    }

    // === Boolean getter (isXxx) ===

    @Placeholder
    static class StatusHolder {
        private final boolean active;

        StatusHolder(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return this.active;
        }
    }

    @Test
    void shouldDiscoverBooleanGetters() {
        String result = this.placeholders
            .context(CompiledMessage.of("active={status.active}"))
            .with("status", new StatusHolder(true))
            .apply();

        assertThat(result).isEqualTo("active=true");
    }

    // === Custom named methods ===

    @Test
    void shouldUseCustomPlaceholderNames() {
        CustomNamedSchema schema = TestSchemas.sampleCustomNamedSchema();

        String result = this.placeholders
            .context(CompiledMessage.of("{s.custom} (len={s.length})"))
            .with("s", schema)
            .apply();

        assertThat(result).isEqualTo("Hello World (len=11)");
    }

    @Test
    void shouldOverrideAutoScannedWithCustomName() {
        CustomNamedSchema schema = TestSchemas.sampleCustomNamedSchema();

        // {s.value} should NOT work because getValue() has @Placeholder(name = "custom")
        String result = this.placeholders
            .context(CompiledMessage.of("{s.value|not-found}"))
            .with("s", schema)
            .apply();

        assertThat(result).isEqualTo("not-found");
    }

    // === scan=false mode ===

    @Test
    void shouldRespectScanFalse() {
        ExplicitOnlySchema schema = TestSchemas.sampleExplicitOnlySchema();

        // visible should work (explicitly annotated)
        String visible = this.placeholders
            .context(CompiledMessage.of("{s.visible}"))
            .with("s", schema)
            .apply();
        assertThat(visible).isEqualTo("I am visible");

        // hidden should NOT work (not annotated, scan=false)
        String hidden = this.placeholders
            .context(CompiledMessage.of("{s.hidden|default}"))
            .with("s", schema)
            .apply();
        assertThat(hidden).isEqualTo("default");
    }

    // === Nested @Placeholder objects ===

    @Test
    void shouldResolveNestedPlaceholders() {
        SchemaItem item = TestSchemas.sampleSchemaItem();

        String result = this.placeholders
            .context(CompiledMessage.of("{item.meta.name}: {item.meta.lore}"))
            .with("item", item)
            .apply();

        assertThat(result).isEqualTo("Excalibur: A legendary sword");
    }

    // === Method chaining with packs ===

    @Test
    void shouldChainWithStringPack() {
        SchemaItem item = TestSchemas.sampleSchemaItem();

        String result = this.placeholders
            .context(CompiledMessage.of("{item.type.toLowerCase()}"))
            .with("item", item)
            .apply();

        assertThat(result).isEqualTo("diamond_sword");
    }

    @Test
    void shouldChainMultipleMethods() {
        SchemaMeta meta = TestSchemas.sampleSchemaMeta();

        String result = this.placeholders
            .context(CompiledMessage.of("{meta.name.toUpperCase().replace(\"EXCALIBUR\", \"Sword\")}"))
            .with("meta", meta)
            .apply();

        assertThat(result).isEqualTo("Sword");
    }

    // === Null handling ===

    @Test
    void shouldHandleNullNestedWithDefault() {
        SchemaItem item = TestSchemas.schemaItemWithNullMeta();

        String result = this.placeholders
            .context(CompiledMessage.of("{item.meta.name|no-meta}"))
            .with("item", item)
            .apply();

        assertThat(result).isEqualTo("no-meta");
    }

    @Test
    void shouldHandleNullWithOr() {
        SchemaItem item = TestSchemas.schemaItemWithNullMeta();

        String result = this.placeholders
            .context(CompiledMessage.of("{item.meta.or(\"fallback\")}"))
            .with("item", item)
            .apply();

        assertThat(result).isEqualTo("fallback");
    }

    // === Priority: explicit registration wins ===

    @Test
    void shouldPreferExplicitResolverOverAnnotation() {
        Placeholders customPlaceholders = Placeholders.create()
            .register(SchemaItem.class, "type", (item, params, ctx) -> "OVERRIDDEN");

        SchemaItem item = TestSchemas.sampleSchemaItem();

        String result = customPlaceholders
            .context(CompiledMessage.of("{item.type}"))
            .with("item", item)
            .apply();

        // Explicit registration should win over @Placeholder annotation
        assertThat(result).isEqualTo("OVERRIDDEN");
    }

    // === Non-annotated class ===

    @Test
    void shouldNotDiscoverNonAnnotatedClass() {
        ExternalItem item = TestSchemas.sampleExternalItem();

        // ExternalItem has no @Placeholder, so getType() should not be discovered
        String result = this.placeholders
            .context(CompiledMessage.of("{item.type|not-found}"))
            .with("item", item)
            .apply();

        assertThat(result).isEqualTo("not-found");
    }

    // === Enum handling ===

    @Test
    void shouldHandleEnumGetters() {
        SchemaWithEnum schema = TestSchemas.sampleSchemaWithEnum();

        String result = this.placeholders
            .context(CompiledMessage.of("{s.type} x{s.count}"))
            .with("s", schema)
            .apply();

        assertThat(result).isEqualTo("DIAMOND_PICKAXE x1");
    }

    // === AnnotationResolver direct API ===

    @Test
    void shouldCacheResolvers() {
        AnnotationResolver resolver1 = AnnotationResolver.of(SchemaItem.class);
        AnnotationResolver resolver2 = AnnotationResolver.of(SchemaItem.class);

        assertThat(resolver1).isSameAs(resolver2);
    }

    @Test
    void shouldReturnNullForUnknownPlaceholder() {
        AnnotationResolver resolver = AnnotationResolver.of(SchemaItem.class);

        assertThat(resolver.getResolver("nonexistent")).isNull();
    }

    @Test
    void shouldListAllResolvers() {
        AnnotationResolver resolver = AnnotationResolver.of(SchemaItem.class);

        assertThat(resolver.getResolvers())
            .containsKeys("type", "amount", "damage", "data", "meta");
    }
}
