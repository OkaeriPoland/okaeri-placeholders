package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Enum placeholders")
@ExtendWith(PlaceholdersExtension.class)
class EnumPlaceholdersTest {

    enum Color {
        RED, GREEN, BLUE
    }

    enum Status {
        PENDING_APPROVAL,
        IN_PROGRESS,
        COMPLETED
    }

    @Nested
    @DisplayName("name operation")
    class NameOperation {

        @Test
        void shouldReturnEnumName(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.name}"))
                .with("e", Color.RED)
                .apply();

            assertThat(result).isEqualTo("RED");
        }

        @ParameterizedTest
        @EnumSource(Color.class)
        void shouldReturnNameForAllColors(Color color, Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.name}"))
                .with("e", color)
                .apply();

            assertThat(result).isEqualTo(color.name());
        }

        @Test
        void shouldReturnUnderscoreName(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.name}"))
                .with("e", Status.PENDING_APPROVAL)
                .apply();

            assertThat(result).isEqualTo("PENDING_APPROVAL");
        }
    }

    @Nested
    @DisplayName("ordinal operation")
    class OrdinalOperation {

        @Test
        void shouldReturnOrdinalAsString(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.ordinal}"))
                .with("e", Color.RED)
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void shouldReturnCorrectOrdinalForSecond(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.ordinal}"))
                .with("e", Color.GREEN)
                .apply();

            assertThat(result).isEqualTo("1");
        }

        @Test
        void shouldReturnCorrectOrdinalForThird(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.ordinal}"))
                .with("e", Color.BLUE)
                .apply();

            assertThat(result).isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("pretty operation")
    class PrettyOperation {

        @Test
        void shouldCapitalizeSimpleName(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.pretty}"))
                .with("e", Color.RED)
                .apply();

            assertThat(result).isEqualTo("Red");
        }

        @Test
        void shouldConvertUnderscoresToSpacesAndCapitalize(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.pretty}"))
                .with("e", Status.PENDING_APPROVAL)
                .apply();

            assertThat(result).isEqualTo("Pending Approval");
        }

        @Test
        void shouldCapitalizeEachWord(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.pretty}"))
                .with("e", Status.IN_PROGRESS)
                .apply();

            assertThat(result).isEqualTo("In Progress");
        }
    }

    @Nested
    @DisplayName("Default toString")
    class DefaultToString {

        @Test
        void shouldUseToStringByDefault(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e}"))
                .with("e", Color.GREEN)
                .apply();

            assertThat(result).isEqualTo("GREEN");
        }
    }

    @Nested
    @DisplayName("Chaining with enum")
    class Chaining {

        @Test
        void shouldChainNameWithLowerCase(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.name.toLowerCase}"))
                .with("e", Color.RED)
                .apply();

            assertThat(result).isEqualTo("red");
        }

        @Test
        void shouldChainPrettyWithReplace(Placeholders placeholders) {
            var result = placeholders.contextOf(CompiledMessage.of("{e.pretty.replace( ,_)}"))
                .with("e", Status.PENDING_APPROVAL)
                .apply();

            assertThat(result).isEqualTo("Pending_Approval");
        }
    }
}
