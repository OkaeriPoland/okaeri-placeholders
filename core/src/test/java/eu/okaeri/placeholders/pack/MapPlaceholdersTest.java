package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Map placeholders")
@ExtendWith(PlaceholdersExtension.class)
class MapPlaceholdersTest {

    @Nested
    @DisplayName("Basics: size, isEmpty, keys, values")
    class Basics {

        @Test
        void sizeShouldReturnEntryCount(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{m.size}"))
                .with("m", Map.of("a", 1, "b", 2, "c", 3))
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void lengthShouldAliasSize(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{m.length}"))
                .with("m", Map.of("a", 1))
                .apply();

            assertThat(result).isEqualTo("1");
        }

        @Test
        void isEmptyShouldReturnTrueForEmptyMap(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{m.isEmpty}"))
                .with("m", Map.of())
                .apply();

            assertThat(result).isEqualTo("true");
        }

        @Test
        void isEmptyShouldReturnFalseForNonEmpty(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{m.isEmpty}"))
                .with("m", Map.of("a", 1))
                .apply();

            assertThat(result).isEqualTo("false");
        }

        @Test
        void keysShouldExposeCollectionForChaining(Placeholders placeholders) {
            // LinkedHashMap preserves insertion order so the test is deterministic
            Map<String, Integer> m = new LinkedHashMap<>();
            m.put("alpha", 1);
            m.put("beta", 2);
            m.put("gamma", 3);
            var result = placeholders.context(CompiledMessage.of("{m.keys.size}={m.keys.first}"))
                .with("m", m)
                .apply();

            assertThat(result).isEqualTo("3=alpha");
        }

        @Test
        void valuesShouldExposeCollectionForAggregation(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{m.values.sum}"))
                .with("m", Map.of("a", 10, "b", 20, "c", 30))
                .apply();

            assertThat(result).isEqualTo("60");
        }
    }

    @Nested
    @DisplayName("get(key) - explicit lookup")
    class Get {

        @Test
        void shouldReturnValueForKnownKey(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{m.get(\"alpha\")}"))
                .with("m", Map.of("alpha", "first", "beta", "second"))
                .apply();

            assertThat(result).isEqualTo("first");
        }

        @Test
        void shouldReturnNullForUnknownKey(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{m.get(\"missing\")}]"))
                .with("m", Map.of("alpha", "first"))
                .apply();

            assertThat(result).isEqualTo("[null]");
        }

        @Test
        void shouldFindNumericKeyViaStringFallback(Placeholders placeholders) {
            // {m.get(2)} resolves as a literal -> null -> string fallback "2"
            // The map's key 2 (Integer) toStringifies to "2" so it matches.
            var result = placeholders.context(CompiledMessage.of("{m.get(2)}"))
                .with("m", Map.of(1, "one", 2, "two", 3, "three"))
                .apply();

            assertThat(result).isEqualTo("two");
        }

        @Test
        void shouldResolveDynamicKeyFromField(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{m.get(k)}"))
                .with("m", Map.of("alpha", "first", "beta", "second"))
                .with("k", "beta")
                .apply();

            assertThat(result).isEqualTo("second");
        }
    }
}
