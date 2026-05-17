package eu.okaeri.placeholders.resolver;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension.WithPlaceholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Resolver lookup through type hierarchy")
@ExtendWith(PlaceholdersExtension.class)
@WithPlaceholders(defaults = false)
class ResolverLookupTest {

    interface Named {
        String name();
    }

    interface Container {
        Named inner();
    }

    static abstract class AbstractWorld implements Named {
        public abstract String biome();
    }

    static class CraftWorld extends AbstractWorld {
        @Override public String name() { return "overworld"; }
        @Override public String biome() { return "plains"; }
    }

    static class Box implements Container {
        @Override public Named inner() { return new CraftWorld(); }
    }

    @Nested
    @DisplayName("self() resolver via interface")
    class SelfViaInterface {

        @Test
        void shouldRenderConcreteImplViaInterfaceSelf(Placeholders placeholders) {
            placeholders.type(Named.class).self(Named::name);

            var result = placeholders.context(CompiledMessage.of("{w}"))
                .with("w", new CraftWorld())
                .apply();

            assertThat(result).isEqualTo("overworld");
        }

        @Test
        void shouldRenderConcreteImplViaAbstractClassSelf(Placeholders placeholders) {
            placeholders.type(AbstractWorld.class).self(AbstractWorld::biome);

            var result = placeholders.context(CompiledMessage.of("{w}"))
                .with("w", new CraftWorld())
                .apply();

            assertThat(result).isEqualTo("plains");
        }

        @Test
        void shouldUseInterfaceSelfAfterNavigatingThroughContainer(Placeholders placeholders) {
            // mirrors `chunk.world` style chains: the outer type returns an inner instance
            // whose runtime class differs from the interface used for registration
            placeholders.type(Named.class).self(Named::name);
            placeholders.type(Container.class).add("inner", Container::inner);

            var result = placeholders.context(CompiledMessage.of("{box.inner}"))
                .with("box", new Box())
                .apply();

            assertThat(result).isEqualTo("overworld");
        }
    }

    @Nested
    @DisplayName("add() resolver via interface")
    class MethodViaInterface {

        @Test
        void shouldResolveInterfaceMethodOnConcreteImpl(Placeholders placeholders) {
            placeholders.type(Named.class).add("name", Named::name);

            var result = placeholders.context(CompiledMessage.of("{w.name}"))
                .with("w", new CraftWorld())
                .apply();

            assertThat(result).isEqualTo("overworld");
        }

        @Test
        void shouldResolveAbstractClassMethodOnConcreteImpl(Placeholders placeholders) {
            placeholders.type(AbstractWorld.class).add("biome", AbstractWorld::biome);

            var result = placeholders.context(CompiledMessage.of("{w.biome}"))
                .with("w", new CraftWorld())
                .apply();

            assertThat(result).isEqualTo("plains");
        }

        @Test
        void shouldChainInterfaceMethodsAcrossNestedTypes(Placeholders placeholders) {
            placeholders.type(Named.class).add("name", Named::name);
            placeholders.type(Container.class).add("inner", Container::inner);

            var result = placeholders.context(CompiledMessage.of("{box.inner.name}"))
                .with("box", new Box())
                .apply();

            assertThat(result).isEqualTo("overworld");
        }
    }

    @Nested
    @DisplayName("Specificity")
    class Specificity {

        @Test
        void shouldPreferConcreteRegistrationOverInterface(Placeholders placeholders) {
            // both interface and concrete have a `self` — concrete wins
            placeholders.type(Named.class).self(n -> "interface:" + n.name());
            placeholders.type(CraftWorld.class).self(w -> "concrete:" + w.name());

            var result = placeholders.context(CompiledMessage.of("{w}"))
                .with("w", new CraftWorld())
                .apply();

            assertThat(result).isEqualTo("concrete:overworld");
        }

        @Test
        void shouldFallBackToInterfaceWhenConcreteHasNoMatchingMethod(Placeholders placeholders) {
            // concrete defines its own `self` but only the interface defines `name` —
            // a method lookup must walk past the concrete entry to find the interface one
            placeholders.type(CraftWorld.class).self(w -> "self");
            placeholders.type(Named.class).add("name", Named::name);

            var result = placeholders.context(CompiledMessage.of("{w.name}"))
                .with("w", new CraftWorld())
                .apply();

            assertThat(result).isEqualTo("overworld");
        }

        @Test
        void shouldPreferAbstractClassOverInterfaceForSameMethod(Placeholders placeholders) {
            // when both Named (interface) and AbstractWorld (intermediate) define `name`,
            // the closer-in-hierarchy registration should win
            placeholders.type(Named.class).add("name", n -> "from-interface:" + n.name());
            placeholders.type(AbstractWorld.class).add("name", w -> "from-abstract:" + w.name());

            var result = placeholders.context(CompiledMessage.of("{w.name}"))
                .with("w", new CraftWorld())
                .apply();

            // AbstractWorld is more specific than Named — registered later, so it sits earlier
            // in the lookup order (LIFO) and wins
            assertThat(result).isEqualTo("from-abstract:overworld");
        }
    }
}
