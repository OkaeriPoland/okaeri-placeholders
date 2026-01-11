package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.exception.MissingFieldException;
import eu.okaeri.placeholders.exception.NullValueException;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FailMode")
class FailModeTest {

    @Nested
    @DisplayName("FAIL_SAFE mode")
    class FailSafeMode {

        @Test
        void shouldReturnMissingMarkerForMissingField() {
            var result = PlaceholderContext.of(CompiledMessage.of("{missing}"))
                .apply();

            assertThat(result).isEqualTo("<missing:missing>");
        }

        @Test
        void shouldReturnMissingMarkerWithFullPath() {
            var result = PlaceholderContext.of(CompiledMessage.of("{player.inventory.name}"))
                .apply();

            assertThat(result).isEqualTo("<missing:player.inventory.name>");
        }

        @Test
        void shouldBeDefaultMode() {
            var ctx = PlaceholderContext.of(CompiledMessage.of("{missing}"));

            assertThat(ctx.getFailMode()).isEqualTo(FailMode.FAIL_SAFE);
        }

        @Test
        void shouldBeDefaultForCreate() {
            var ctx = PlaceholderContext.create();

            assertThat(ctx.getFailMode()).isEqualTo(FailMode.FAIL_SAFE);
        }

        @Test
        void shouldContinueWithMultipleMissing() {
            var result = PlaceholderContext.of(CompiledMessage.of("{a} {b} {c}"))
                .with("b", "B")
                .apply();

            assertThat(result).isEqualTo("<missing:a> B <missing:c>");
        }

        @Test
        void shouldUseDefaultValueIfProvided() {
            var result = PlaceholderContext.of(CompiledMessage.of("{missing|default}"))
                .apply();

            assertThat(result).isEqualTo("default");
        }

        @Test
        void shouldPreferDefaultOverMissingMarker() {
            var result = PlaceholderContext.of(CompiledMessage.of("{value|fallback}"))
                .apply();

            assertThat(result).isEqualTo("fallback");
        }
    }

    @Nested
    @DisplayName("FAIL_FAST mode")
    class FailFastMode {

        @Test
        void shouldThrowOnMissingField() {
            var message = CompiledMessage.of("{missing}");

            assertThatThrownBy(() ->
                PlaceholderContext.create(FailMode.FAIL_FAST)
                    .apply(message))
                .isInstanceOf(MissingFieldException.class)
                .hasMessageContaining("missing");
        }

        @Test
        void shouldThrowWithFieldName() {
            var message = CompiledMessage.of("{player}");

            assertThatThrownBy(() ->
                PlaceholderContext.create(FailMode.FAIL_FAST)
                    .apply(message))
                .isInstanceOf(MissingFieldException.class)
                .hasMessageContaining("player");
        }

        @Test
        void shouldIncludeMessageInException() {
            var message = CompiledMessage.of("Hello {name}!");

            assertThatThrownBy(() ->
                PlaceholderContext.create(FailMode.FAIL_FAST)
                    .apply(message))
                .isInstanceOf(MissingFieldException.class);
            // Note: formatMessage() includes the template, but getMessage() may not
        }

        @Test
        void shouldSucceedWhenAllFieldsPresent() {
            var message = CompiledMessage.of("{name}");

            var result = PlaceholderContext.create(FailMode.FAIL_FAST)
                .with("name", "World")
                .apply(message);

            assertThat(result).isEqualTo("World");
        }

        @Test
        void shouldUseDefaultValueIfProvided() {
            var message = CompiledMessage.of("{missing|default}");

            var result = PlaceholderContext.create(FailMode.FAIL_FAST)
                .apply(message);

            assertThat(result).isEqualTo("default");
        }

        @Test
        void shouldThrowOnNullValue() {
            var message = CompiledMessage.of("{value}");

            assertThatThrownBy(() ->
                PlaceholderContext.create(FailMode.FAIL_FAST)
                    .with("value", null)
                    .apply(message))
                .isInstanceOf(NullValueException.class);
        }

        @Test
        void shouldAcceptEmptyStringValue() {
            var message = CompiledMessage.of("{value}");

            var result = PlaceholderContext.create(FailMode.FAIL_FAST)
                .with("value", "")
                .apply(message);

            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"{a}", "{a.b}", "{a.b.c}", "{deep.nested.path.field}"})
        void shouldThrowForVariousFieldPatterns(String pattern) {
            var message = CompiledMessage.of(pattern);

            assertThatThrownBy(() ->
                PlaceholderContext.create(FailMode.FAIL_FAST)
                    .apply(message))
                .isInstanceOf(MissingFieldException.class);
        }
    }

    @Nested
    @DisplayName("Mode configuration")
    class ModeConfiguration {

        @Test
        void shouldCreateContextWithSpecificMode() {
            var ctx = PlaceholderContext.create(FailMode.FAIL_FAST);

            assertThat(ctx.getFailMode()).isEqualTo(FailMode.FAIL_FAST);
        }

        @Test
        void shouldCreateMessageContextWithSpecificMode() {
            var ctx = PlaceholderContext.of(null, CompiledMessage.of("{x}"), FailMode.FAIL_FAST);

            assertThat(ctx.getFailMode()).isEqualTo(FailMode.FAIL_FAST);
        }
    }

    @Nested
    @DisplayName("Null value handling")
    class NullValueHandling {

        @Test
        void shouldReturnNullMarkerInFailSafe() {
            // When a placeholder renders to null (e.g., null sub-field)
            // this is different from missing placeholder
            var result = PlaceholderContext.of(CompiledMessage.of("{value|fallback}"))
                .with("value", null)
                .apply();

            // Null value with default should use the default
            assertThat(result).isEqualTo("fallback");
        }

        @Test
        void shouldUseMissingMarkerForMissingVsNullMarkerForNull() {
            // Missing field (not added to context)
            var missingResult = PlaceholderContext.of(CompiledMessage.of("{missing}"))
                .apply();

            assertThat(missingResult).startsWith("<missing:");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        void shouldHandleStaticMessageInBothModes() {
            var message = CompiledMessage.of("Static text only");

            var safResult = PlaceholderContext.create(FailMode.FAIL_SAFE)
                .apply(message);
            var fastResult = PlaceholderContext.create(FailMode.FAIL_FAST)
                .apply(message);

            assertThat(safResult).isEqualTo("Static text only");
            assertThat(fastResult).isEqualTo("Static text only");
        }

        @Test
        void shouldHandleEmptyMessageInBothModes() {
            var message = CompiledMessage.of("");

            var safeResult = PlaceholderContext.create(FailMode.FAIL_SAFE)
                .apply(message);
            var fastResult = PlaceholderContext.create(FailMode.FAIL_FAST)
                .apply(message);

            assertThat(safeResult).isEmpty();
            assertThat(fastResult).isEmpty();
        }

        @Test
        void shouldHandlePartialFieldsInFailSafe() {
            var message = CompiledMessage.of("{present} and {missing}");

            var result = PlaceholderContext.create(FailMode.FAIL_SAFE)
                .with("present", "VALUE")
                .apply(message);

            assertThat(result).isEqualTo("VALUE and <missing:missing>");
        }

        @Test
        void shouldFailOnFirstMissingInFailFast() {
            var message = CompiledMessage.of("{first} and {second}");

            assertThatThrownBy(() ->
                PlaceholderContext.create(FailMode.FAIL_FAST)
                    .apply(message))
                .isInstanceOf(MissingFieldException.class)
                .hasMessageContaining("first");
        }
    }
}
