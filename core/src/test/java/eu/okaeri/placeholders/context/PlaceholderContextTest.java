package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import lombok.Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PlaceholderContext")
class PlaceholderContextTest {

    @Nested
    @DisplayName("Basic placeholder replacement")
    class BasicReplacement {

        @Test
        void shouldReplaceSimplePlaceholder() {
            var result = PlaceholderContext.of(CompiledMessage.of("Hello {name}!"))
                .with("name", "World")
                .apply();

            assertThat(result).isEqualTo("Hello World!");
        }

        @Test
        void shouldReplaceMultiplePlaceholders() {
            var result = PlaceholderContext.of(CompiledMessage.of("{greeting} {name}!"))
                .with("greeting", "Hello")
                .with("name", "World")
                .apply();

            assertThat(result).isEqualTo("Hello World!");
        }

        @Test
        void shouldHandleSamePlaceholderMultipleTimes() {
            var result = PlaceholderContext.of(CompiledMessage.of("{name} meets {name}"))
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Alice meets Alice");
        }

        @Test
        void shouldPreserveStaticText() {
            var result = PlaceholderContext.of(CompiledMessage.of("Static only"))
                .apply();

            assertThat(result).isEqualTo("Static only");
        }

        @Test
        void shouldHandleEmptyMessage() {
            var result = PlaceholderContext.of(CompiledMessage.of(""))
                .apply();

            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
            "Hello {name}!, Steve, Hello Steve!",
            "{x}, value, value",
            "a{b}c, B, aBc",
            "{a}{b}{c}, x, xxx"
        })
        void shouldReplaceVariousPatterns(String pattern, String value, String expected) {
            var result = PlaceholderContext.of(CompiledMessage.of(pattern))
                .with("name", value)
                .with("x", value)
                .with("b", value)
                .with("a", value)
                .with("c", value)
                .apply();

            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Bulk field addition")
    class BulkFieldAddition {

        @Test
        void shouldAddFieldsFromMap() {
            var fields = Map.<String, Object>of("a", "1", "b", "2", "c", "3");
            var result = PlaceholderContext.of(CompiledMessage.of("{a}{b}{c}"))
                .with(fields)
                .apply();

            assertThat(result).isEqualTo("123");
        }

        @Test
        void shouldOverrideExistingFields() {
            var result = PlaceholderContext.of(CompiledMessage.of("{name}"))
                .with("name", "first")
                .with(Map.<String, Object>of("name", "second"))
                .apply();

            assertThat(result).isEqualTo("second");
        }

        @Test
        void shouldCombineIndividualAndBulkFields() {
            var result = PlaceholderContext.of(CompiledMessage.of("{a} {b} {c}"))
                .with("a", "A")
                .with(Map.<String, Object>of("b", "B", "c", "C"))
                .apply();

            assertThat(result).isEqualTo("A B C");
        }
    }

    @Nested
    @DisplayName("Shared context")
    class SharedContext {

        @Test
        void shouldCreateSharedContext() {
            var ctx = PlaceholderContext.create()
                .with("name", "World");

            var result1 = ctx.apply(CompiledMessage.of("Hello {name}!"));
            var result2 = ctx.apply(CompiledMessage.of("Goodbye {name}!"));

            assertThat(result1).isEqualTo("Hello World!");
            assertThat(result2).isEqualTo("Goodbye World!");
        }

        @Test
        void shouldAllowAddingFieldsToSharedContext() {
            var ctx = PlaceholderContext.create()
                .with("greeting", "Hello");

            var result1 = ctx.apply(CompiledMessage.of("{greeting}!"));

            ctx.with("name", "World");
            var result2 = ctx.apply(CompiledMessage.of("{greeting} {name}!"));

            assertThat(result1).isEqualTo("Hello!");
            assertThat(result2).isEqualTo("Hello World!");
        }

        @Test
        void shouldRejectDifferentMessageOnNonSharedContext() {
            var message1 = CompiledMessage.of("Message 1: {name}");
            var message2 = CompiledMessage.of("Message 2: {name}");

            var ctx = PlaceholderContext.of(message1)
                .with("name", "value");

            assertThatThrownBy(() -> ctx.apply(message2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot apply another message");
        }
    }

    @Nested
    @DisplayName("Default values")
    class DefaultValues {

        @Test
        void shouldUseDefaultForMissingField() {
            var result = PlaceholderContext.of(CompiledMessage.of("{name|Anonymous}"))
                .apply();

            assertThat(result).isEqualTo("Anonymous");
        }

        @Test
        void shouldUseProvidedValueOverDefault() {
            var result = PlaceholderContext.of(CompiledMessage.of("{name|Anonymous}"))
                .with("name", "Steve")
                .apply();

            assertThat(result).isEqualTo("Steve");
        }

        @Test
        void shouldUseDefaultForNullValue() {
            var result = PlaceholderContext.of(CompiledMessage.of("{name|Anonymous}"))
                .with("name", null)
                .apply();

            assertThat(result).isEqualTo("Anonymous");
        }

        @Test
        void shouldSupportEmptyDefault() {
            var result = PlaceholderContext.of(CompiledMessage.of("{name|}"))
                .apply();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("renderFields method")
    class RenderFields {

        @Test
        void shouldReturnRenderedFieldsMap() {
            var message = CompiledMessage.of("Hello {name}!");
            var rendered = PlaceholderContext.of(message)
                .with("name", "World")
                .renderFields();

            assertThat(rendered).hasSize(1);
            assertThat(rendered.values()).containsExactly("World");
        }

        @Test
        void shouldReturnEmptyMapForStaticMessage() {
            var message = CompiledMessage.of("No placeholders here");
            var rendered = PlaceholderContext.of(message)
                .renderFields();

            assertThat(rendered).isEmpty();
        }

        @Test
        void shouldRenderMultipleFields() {
            var message = CompiledMessage.of("{a} and {b}");
            var rendered = PlaceholderContext.of(message)
                .with("a", "A")
                .with("b", "B")
                .renderFields();

            assertThat(rendered).hasSize(2);
            assertThat(rendered.values()).containsExactlyInAnyOrder("A", "B");
        }
    }

    @Nested
    @DisplayName("With Placeholders instance")
    @ExtendWith(PlaceholdersExtension.class)
    class WithPlaceholders {

        @Test
        void shouldUseRegisteredResolvers(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.toUpperCase}");
            var result = placeholders.contextOf(message)
                .with("name", "hello")
                .apply();

            assertThat(result).isEqualTo("HELLO");
        }

        @Test
        void shouldSupportChainedOperations(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.toLowerCase.capitalize}");
            var result = placeholders.contextOf(message)
                .with("name", "HELLO WORLD")
                .apply();

            assertThat(result).isEqualTo("Hello world");
        }
    }

    @Nested
    @DisplayName("Type conversions")
    class TypeConversions {

        @Test
        void shouldConvertIntegerToString() {
            var result = PlaceholderContext.of(CompiledMessage.of("Count: {count}"))
                .with("count", 42)
                .apply();

            assertThat(result).isEqualTo("Count: 42");
        }

        @Test
        void shouldConvertDoubleToString() {
            var result = PlaceholderContext.of(CompiledMessage.of("Value: {value}"))
                .with("value", 3.14)
                .apply();

            assertThat(result).contains("3.14");
        }

        @Test
        void shouldConvertBooleanToString() {
            var result = PlaceholderContext.of(CompiledMessage.of("Active: {active}"))
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("Active: true");
        }

        @Test
        void shouldConvertEnumToString() {
            var result = PlaceholderContext.of(CompiledMessage.of("Status: {status}"))
                .with("status", Thread.State.RUNNABLE)
                .apply();

            assertThat(result).isEqualTo("Status: RUNNABLE");
        }
    }

    @Nested
    @DisplayName("Fluent API")
    class FluentApi {

        @Test
        void shouldSupportMethodChaining() {
            var result = PlaceholderContext.of(CompiledMessage.of("{a} {b} {c}"))
                .with("a", "1")
                .with("b", "2")
                .with("c", "3")
                .apply();

            assertThat(result).isEqualTo("1 2 3");
        }

        @Test
        void shouldReturnSameContextFromWith() {
            var ctx = PlaceholderContext.of(CompiledMessage.of("{name}"));
            var returned = ctx.with("name", "value");

            assertThat(returned).isSameAs(ctx);
        }
    }

    @Nested
    @DisplayName("renderFieldValues method")
    class RenderFieldValues {

        @Test
        void shouldReturnTypedValuesKeyedByRaw() {
            var message = CompiledMessage.of("Hello {name}!");
            var values = PlaceholderContext.of(message)
                .with("name", "World")
                .renderFieldValues();

            assertThat(values).hasSize(1);
            assertThat(values.get("name")).isEqualTo("World");
        }

        @Test
        void shouldReturnEmptyMapForStaticMessage() {
            var message = CompiledMessage.of("No placeholders here");
            var values = PlaceholderContext.of(message)
                .renderFieldValues();

            assertThat(values).isEmpty();
        }

        @Test
        void shouldPreserveIntegerType() {
            var message = CompiledMessage.of("Count: {count}");
            var values = PlaceholderContext.of(message)
                .with("count", 42)
                .renderFieldValues();

            assertThat(values.get("count")).isInstanceOf(Integer.class);
            assertThat(values.get("count")).isEqualTo(42);
        }

        @Test
        void shouldPreserveCustomObjectType() {
            var customObject = new CustomValue("test");
            var message = CompiledMessage.of("Value: {obj}");
            var values = PlaceholderContext.of(message)
                .with("obj", customObject)
                .renderFieldValues();

            assertThat(values.get("obj")).isSameAs(customObject);
        }

        @Test
        void shouldRenderMultipleFieldsWithTypes() {
            var message = CompiledMessage.of("{str} {num} {bool}");
            var values = PlaceholderContext.of(message)
                .with("str", "hello")
                .with("num", 123)
                .with("bool", true)
                .renderFieldValues();

            assertThat(values).hasSize(3);
            assertThat(values.get("str")).isEqualTo("hello");
            assertThat(values.get("num")).isEqualTo(123);
            assertThat(values.get("bool")).isEqualTo(true);
        }

        @Test
        void shouldHandleDefaultForMissingField() {
            var message = CompiledMessage.of("{name|Anonymous}");
            var values = PlaceholderContext.of(message)
                .renderFieldValues();

            assertThat(values.get("name|Anonymous")).isEqualTo("Anonymous");
        }

        @Test
        void shouldUseProvidedValueOverDefault() {
            var message = CompiledMessage.of("{name|Anonymous}");
            var values = PlaceholderContext.of(message)
                .with("name", "Steve")
                .renderFieldValues();

            assertThat(values.get("name|Anonymous")).isEqualTo("Steve");
        }

        @Test
        void shouldDeduplicateSameFieldInMessage() {
            var message = CompiledMessage.of("{name} and {name}");
            var values = PlaceholderContext.of(message)
                .with("name", "Alice")
                .renderFieldValues();

            assertThat(values).hasSize(1);
            assertThat(values.get("name")).isEqualTo("Alice");
        }

        @Test
        @ExtendWith(PlaceholdersExtension.class)
        void shouldApplyResolverChain(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.toUpperCase}");
            var values = placeholders.contextOf(message)
                .with("name", "hello")
                .renderFieldValues();

            assertThat(values.get("name.toUpperCase")).isEqualTo("HELLO");
        }

        @Test
        @ExtendWith(PlaceholdersExtension.class)
        void shouldApplyPrintfFormatViaResolverChain(Placeholders placeholders) {
            var message = CompiledMessage.of("{%.2f#value}");
            var values = placeholders.contextOf(message)
                .with("value", 3.14159)
                .renderFieldValues();

            assertThat(values.get("%.2f#value")).isEqualTo("3.14");
        }

        // Helper class for type preservation tests
        @Value
        static class CustomValue {
            String value;
        }
    }
}
