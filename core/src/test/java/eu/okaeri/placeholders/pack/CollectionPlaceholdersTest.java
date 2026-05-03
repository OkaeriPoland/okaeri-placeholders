package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Collection placeholders")
@ExtendWith(PlaceholdersExtension.class)
class CollectionPlaceholdersTest {

    @Nested
    @DisplayName("Basics: size, isEmpty, first, last, get, contains")
    class Basics {

        @Test
        void sizeShouldReturnElementCount(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.size}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void lengthShouldAliasSize(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.length}"))
                .with("items", List.of("a", "b"))
                .apply();

            assertThat(result).isEqualTo("2");
        }

        @Test
        void isEmptyShouldReturnTrueForEmptyCollection(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.isEmpty}"))
                .with("items", List.of())
                .apply();

            assertThat(result).isEqualTo("true");
        }

        @Test
        void isEmptyShouldReturnFalseForNonEmpty(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.isEmpty}"))
                .with("items", List.of("a"))
                .apply();

            assertThat(result).isEqualTo("false");
        }

        @Test
        void firstShouldReturnFirstElement(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.first}"))
                .with("items", List.of("alpha", "beta", "gamma"))
                .apply();

            assertThat(result).isEqualTo("alpha");
        }

        @Test
        void lastShouldReturnLastElement(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.last}"))
                .with("items", List.of("alpha", "beta", "gamma"))
                .apply();

            assertThat(result).isEqualTo("gamma");
        }

        @Test
        void firstShouldReturnNullOnEmpty(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{items.first}]"))
                .with("items", List.of())
                .apply();

            assertThat(result).isEqualTo("[null]");
        }

        @Test
        void lastShouldHandleNonListCollection(Placeholders placeholders) {
            // LinkedHashSet preserves insertion order
            Set<String> set = new LinkedHashSet<>(Arrays.asList("x", "y", "z"));
            var result = placeholders.context(CompiledMessage.of("{items.last}"))
                .with("items", set)
                .apply();

            assertThat(result).isEqualTo("z");
        }

        @Test
        void getShouldReturnElementAtIndex(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.get(1)}"))
                .with("items", List.of("alpha", "beta", "gamma"))
                .apply();

            assertThat(result).isEqualTo("beta");
        }

        @Test
        void getShouldReturnNullForOutOfBoundsIndex(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("[{items.get(5)}]"))
                .with("items", List.of("a", "b"))
                .apply();

            assertThat(result).isEqualTo("[null]");
        }

        @Test
        void containsShouldReturnTrueWhenPresent(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.contains(\"b\")}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("true");
        }

        @Test
        void containsShouldReturnFalseWhenAbsent(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.contains(\"z\")}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("false");
        }

        @Test
        void containsShouldMatchNumericElementsViaStringEquality(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.contains(2)}"))
                .with("items", List.of(1, 2, 3))
                .apply();

            assertThat(result).isEqualTo("true");
        }
    }

    @Nested
    @DisplayName("Ordering: reverse, sort, sortDesc, distinct")
    class Ordering {

        @Test
        void reverseShouldFlipOrder(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.reverse.first}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("c");
        }

        @Test
        void reverseShouldNotMutateOriginal(Placeholders placeholders) {
            // Calling reverse twice should give back the original first element
            var result = placeholders.context(CompiledMessage.of("{items.reverse.reverse.first}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("a");
        }

        @Test
        void sortShouldOrderNumbersAscending(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.sort.first}={items.sort.last}"))
                .with("items", List.of(5, 1, 9, 3, 7))
                .apply();

            assertThat(result).isEqualTo("1=9");
        }

        @Test
        void sortShouldOrderStringsAscending(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.sort.first}"))
                .with("items", List.of("charlie", "alpha", "bravo"))
                .apply();

            assertThat(result).isEqualTo("alpha");
        }

        @Test
        void sortDescShouldOrderDescending(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.sortDesc.first}"))
                .with("items", List.of(5, 1, 9, 3, 7))
                .apply();

            assertThat(result).isEqualTo("9");
        }

        @Test
        void distinctShouldDedupePreservingOrder(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.distinct.size}"))
                .with("items", List.of("a", "b", "a", "c", "b", "a"))
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void distinctShouldKeepFirstOccurrenceOrder(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.distinct.first}"))
                .with("items", List.of("c", "a", "c", "b"))
                .apply();

            assertThat(result).isEqualTo("c");
        }
    }

    @Nested
    @DisplayName("Slicing: take, drop")
    class Slicing {

        @Test
        void takeShouldReturnFirstNElements(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.take(3).last}"))
                .with("items", List.of("a", "b", "c", "d", "e"))
                .apply();

            assertThat(result).isEqualTo("c");
        }

        @Test
        void takeShouldReturnEmptyForZero(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.take(0).size}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void takeShouldClampToCollectionSize(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.take(99).size}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void dropShouldSkipFirstNElements(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.drop(2).first}"))
                .with("items", List.of("a", "b", "c", "d", "e"))
                .apply();

            assertThat(result).isEqualTo("c");
        }

        @Test
        void dropShouldReturnAllForZero(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.drop(0).size}"))
                .with("items", List.of("a", "b", "c"))
                .apply();

            assertThat(result).isEqualTo("3");
        }

        @Test
        void dropShouldReturnEmptyWhenSkippingPastEnd(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.drop(99).size}"))
                .with("items", List.of("a", "b"))
                .apply();

            assertThat(result).isEqualTo("0");
        }

        @Test
        void takeAndDropShouldComposeForMiddleSlice(Placeholders placeholders) {
            var result = placeholders.context(CompiledMessage.of("{items.drop(1).take(2).first}={items.drop(1).take(2).last}"))
                .with("items", List.of("a", "b", "c", "d", "e"))
                .apply();

            assertThat(result).isEqualTo("b=c");
        }
    }
}
