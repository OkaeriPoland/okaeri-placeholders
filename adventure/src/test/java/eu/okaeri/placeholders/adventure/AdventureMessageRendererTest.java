package eu.okaeri.placeholders.adventure;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AdventureMessageRenderer")
class AdventureMessageRendererTest {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private AdventureMessageRenderer renderer;
    private Placeholders placeholders;

    @BeforeEach
    void setUp() {
        this.renderer = new AdventureMessageRenderer();
        this.placeholders = Placeholders.create(true);
    }

    @Nested
    @DisplayName("Basic rendering")
    class BasicRendering {

        @Test
        void shouldRenderSimplePlaceholder() {
            var message = CompiledMessage.of("Hello {name}!");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "World");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Hello World!");
        }

        @Test
        void shouldRenderMultiplePlaceholders() {
            var message = CompiledMessage.of("{greeting} {name}!");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message)
                .with("greeting", "Hello")
                .with("name", "World");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Hello World!");
        }

        @Test
        void shouldRenderStaticText() {
            var message = CompiledMessage.of("No placeholders here");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message);

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("No placeholders here");
        }

        @Test
        void shouldShowMissingPlaceholder() {
            var message = CompiledMessage.of("Hello {name}!");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message);

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Hello <missing:name>!");
        }
    }

    @Nested
    @DisplayName("MiniMessage styling")
    class MiniMessageStyling {

        @Test
        void shouldApplyBold() {
            var message = CompiledMessage.of("<bold>Hello</bold> {name}!");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "World");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&lHello&r World!");
        }

        @Test
        void shouldApplyColor() {
            var message = CompiledMessage.of("<red>Error:</red> {message}");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("message", "failed");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&cError:&r failed");
        }

        @Test
        void shouldApplyColorToPlaceholder() {
            var message = CompiledMessage.of("<gold>{name}</gold>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Steve");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&6Steve");
        }

        @Test
        void shouldApplyBoldToPlaceholder() {
            var message = CompiledMessage.of("<bold>{name}</bold>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Steve");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&lSteve");
        }

        @Test
        void shouldApplyColorAndBoldToPlaceholder() {
            var message = CompiledMessage.of("<red><bold>{name}</bold></red>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Steve");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&c&lSteve");
        }

        @Test
        void shouldInheritStyleForStringPlaceholder() {
            var message = CompiledMessage.of("<aqua>Hello {name}!</aqua>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "World");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&bHello World!");
        }

        @Test
        void shouldApplyGradientToPlaceholder() {
            var message = CompiledMessage.of("<gradient:red:blue>Hi {name}!</gradient>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "AB");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&cH&ci&d &dA&9B&9!");
        }
    }

    @Nested
    @DisplayName("Component preservation")
    class ComponentPreservation {

        @Test
        void shouldPreserveComponentColor() {
            var goldComponent = Component.text("Gold").color(NamedTextColor.GOLD);

            var message = CompiledMessage.of("Color: {comp}");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("comp", goldComponent);

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Color: &6Gold");
        }

        @Test
        void shouldPreserveComponentDecoration() {
            var boldComponent = Component.text("Bold").decorate(TextDecoration.BOLD);

            var message = CompiledMessage.of("Style: {comp}");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("comp", boldComponent);

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Style: &lBold");
        }

        @Test
        void shouldPreserveComponentColorAndDecoration() {
            var styledComponent = Component.text("Styled")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);

            var message = CompiledMessage.of("Hello {comp}!");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("comp", styledComponent);

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Hello &6&lStyled&r!");
        }

        @Test
        void shouldNotOverrideComponentStyleWithSurrounding() {
            var goldComponent = Component.text("Gold").color(NamedTextColor.GOLD);

            var message = CompiledMessage.of("<red>Error: {comp}</red>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("comp", goldComponent);

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&cError: &6Gold");
        }

        @Test
        void shouldMixComponentAndStringValues() {
            var goldComponent = Component.text("Gold").color(NamedTextColor.GOLD);

            var message = CompiledMessage.of("{comp} and {str}");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message)
                .with("comp", goldComponent)
                .with("str", "plain");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&6Gold&r and plain");
        }
    }

    @Nested
    @DisplayName("Legacy color codes")
    class LegacyColorCodes {

        @Test
        void shouldParseSectionCodes() {
            var message = CompiledMessage.of("§cRed {name}");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Text");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&cRed &rText");
        }

        @Test
        void shouldParseAmpersandCodes() {
            var message = CompiledMessage.of("&6Gold {name}");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Text");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&6Gold &rText");
        }

        @Test
        void shouldMixLegacyAndMiniMessage() {
            var message = CompiledMessage.of("&6Gold <bold>{name}</bold>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Steve");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("&6Gold &r&lSteve");
        }
    }

    @Nested
    @DisplayName("Click events")
    class ClickEvents {

        @Test
        void shouldReplacePlaceholderInRunCommand() {
            var message = CompiledMessage.of("<click:run_command:'/give {name} diamond'>Click</click>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Steve");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(MINI_MESSAGE.serialize(result)).isEqualTo("<click:run_command:'/give Steve diamond'>Click");
        }

        @Test
        void shouldReplacePlaceholderInSuggestCommand() {
            var message = CompiledMessage.of("<click:suggest_command:'/msg {name} '>Message</click>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "Player");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(MINI_MESSAGE.serialize(result)).isEqualTo("<click:suggest_command:'/msg Player '>Message");
        }

        @Test
        void shouldReplacePlaceholderInOpenUrl() {
            var message = CompiledMessage.of("<click:open_url:'https://example.com/{id}'>Link</click>");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("id", "123");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(MINI_MESSAGE.serialize(result)).isEqualTo("<click:open_url:'https://example.com/123'>Link");
        }
    }

    @Nested
    @DisplayName("Resolver integration")
    class ResolverIntegration {

        @Test
        void shouldApplyResolverChain() {
            var message = CompiledMessage.of("Hello {name.toUpperCase}!");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "world");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Hello WORLD!");
        }

        @Test
        void shouldHandleOrFallback() {
            var message = CompiledMessage.of("Hello {name.or(\"Guest\")}!");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message);

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Hello Guest!");
        }

        @Test
        void shouldChainMultipleResolvers() {
            var message = CompiledMessage.of("{name.toLowerCase.capitalize}");
            var context = AdventureMessageRendererTest.this.placeholders.contextOf(message).with("name", "HELLO WORLD");

            var result = AdventureMessageRendererTest.this.renderer.render(message, context);

            assertThat(LEGACY.serialize(result)).isEqualTo("Hello world");
        }
    }
}
