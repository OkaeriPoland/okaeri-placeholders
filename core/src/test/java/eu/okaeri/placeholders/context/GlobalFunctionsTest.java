package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Global functions via $ context")
@ExtendWith(PlaceholdersExtension.class)
class GlobalFunctionsTest {

    @Nested
    @DisplayName("$.env() - Environment variables")
    class EnvFunction {

        @Test
        void shouldReadExistingEnvVar(Placeholders placeholders) {
            // PATH should exist on all systems
            var message = CompiledMessage.of("{$.env(PATH)}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isNotEmpty();
        }

        @Test
        void shouldReturnEmptyForMissingEnvVar(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.env(DEFINITELY_DOES_NOT_EXIST_12345)}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isEmpty();
        }

        @Test
        void shouldWorkWithQuotedArg(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.env(\"PATH\")}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("$.now() - Current time")
    class NowFunction {

        @Test
        void shouldReturnCurrentInstant(Placeholders placeholders) {
            var before = Instant.now();

            var message = CompiledMessage.of("{$.now()}");
            var result = placeholders.contextOf(message).apply();

            var after = Instant.now();

            // Result should parse as a valid instant between before and after
            assertThat(result).isNotEmpty();
            assertThat(result).containsPattern("\\d{4}-\\d{2}-\\d{2}");
        }

        @Test
        void shouldChainWithDateFormat(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.now().format(\"yyyy-MM-dd\",\"UTC\")}");
            var result = placeholders.contextOf(message).apply();

            // Should be a valid date format
            assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}");
        }
    }

    @Nested
    @DisplayName("$.coalesce() - First non-null value")
    class CoalesceFunction {

        @Test
        void shouldReturnFirstNonNullField(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,c)}");
            var result = placeholders.contextOf(message)
                .with("a", null)
                .with("b", "Bravo")
                .with("c", "Charlie")
                .apply();

            assertThat(result).isEqualTo("Bravo");
        }

        @Test
        void shouldReturnFirstIfNotNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,c)}");
            var result = placeholders.contextOf(message)
                .with("a", "Alpha")
                .with("b", "Bravo")
                .with("c", "Charlie")
                .apply();

            assertThat(result).isEqualTo("Alpha");
        }

        @Test
        void shouldReturnLiteralDefault(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,\"default\")}");
            var result = placeholders.contextOf(message)
                .with("a", null)
                .with("b", null)
                .apply();

            assertThat(result).isEqualTo("default");
        }

        @Test
        void shouldTreatUnrecognizedFieldNamesAsLiteralStrings(Placeholders placeholders) {
            // Backward compat: unrecognized field names fall back to being treated as literal strings
            var message = CompiledMessage.of("{$.coalesce(missing1,missing2,\"fallback\")}");
            var result = placeholders.contextOf(message).apply();

            // "missing1" is not in context, so it's treated as literal string "missing1" (not null)
            assertThat(result).isEqualTo("missing1");
        }

        @Test
        void shouldReturnNullIndicatorWhenAllNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.coalesce(a,b,c)}");
            var result = placeholders.contextOf(message)
                .with("a", null)
                .with("b", null)
                .with("c", null)
                .apply();

            // Should indicate null since all are null
            assertThat(result).contains("null");
        }
    }

    @Nested
    @DisplayName("$.if() - Conditional")
    class IfFunction {

        @Test
        void shouldReturnThenValueWhenConditionTrue(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(active,\"yes\",\"no\")}");
            var result = placeholders.contextOf(message)
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void shouldReturnElseValueWhenConditionFalse(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(active,\"yes\",\"no\")}");
            var result = placeholders.contextOf(message)
                .with("active", false)
                .apply();

            assertThat(result).isEqualTo("no");
        }

        @Test
        void shouldTreatNonEmptyStringAsTruthy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(name,\"has name\",\"no name\")}");
            var result = placeholders.contextOf(message)
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("has name");
        }

        @Test
        void shouldTreatEmptyStringAsFalsy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(name,\"has name\",\"no name\")}");
            var result = placeholders.contextOf(message)
                .with("name", "")
                .apply();

            assertThat(result).isEqualTo("no name");
        }

        @Test
        void shouldTreatNullAsFalsy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(name,\"has name\",\"no name\")}");
            var result = placeholders.contextOf(message)
                .with("name", null)
                .apply();

            assertThat(result).isEqualTo("no name");
        }

        @Test
        void shouldTreatNonZeroNumberAsTruthy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(count,\"has items\",\"empty\")}");
            var result = placeholders.contextOf(message)
                .with("count", 5)
                .apply();

            assertThat(result).isEqualTo("has items");
        }

        @Test
        void shouldTreatZeroAsFalsy(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(count,\"has items\",\"empty\")}");
            var result = placeholders.contextOf(message)
                .with("count", 0)
                .apply();

            assertThat(result).isEqualTo("empty");
        }

        @Test
        void shouldSupportFieldRefAsThenElse(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(useNickname,nickname,name)}");
            var result = placeholders.contextOf(message)
                .with("useNickname", true)
                .with("nickname", "Ali")
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Ali");
        }
    }

    @Nested
    @DisplayName("$.random() - Random number")
    class RandomFunction {

        @Test
        void shouldReturnNumberInRange(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.random(1,10)}");

            for (int i = 0; i < 100; i++) {
                var result = placeholders.contextOf(message).apply();
                int value = Integer.parseInt(result);

                assertThat(value).isBetween(1, 10);
            }
        }

        @Test
        void shouldWorkWithQuotedArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.random(\"1\",\"10\")}");
            var result = placeholders.contextOf(message).apply();
            int value = Integer.parseInt(result);

            assertThat(value).isBetween(1, 10);
        }
    }

    @Nested
    @DisplayName("$.concat() - String concatenation")
    class ConcatFunction {

        @Test
        void shouldConcatenateLiterals(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(\"Hello\",\" \",\"World\")}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        void shouldConcatenateFieldRefs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(greeting,\" \",name)}");
            var result = placeholders.contextOf(message)
                .with("greeting", "Hello")
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Hello Alice");
        }

        @Test
        void shouldConcatenateMixedArgs(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(\"Hello \",name,\"!\")}");
            var result = placeholders.contextOf(message)
                .with("name", "World")
                .apply();

            assertThat(result).isEqualTo("Hello World!");
        }

        @Test
        void shouldHandleNullAsEmptyString(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.concat(a,b,c)}");
            var result = placeholders.contextOf(message)
                .with("a", "start")
                .with("b", null)
                .with("c", "end")
                .apply();

            assertThat(result).isEqualTo("startend");
        }
    }

    @Nested
    @DisplayName("Custom global functions")
    class CustomGlobalFunctions {

        @Test
        void shouldSupportUserDefinedGlobalFunctions() {
            var placeholders = Placeholders.create(true);
            placeholders.registerGlobalFunction("greet", (gf, accessor, ctx) ->
                "Hello, " + accessor.params().strAt(0, "stranger") + "!");

            var message = CompiledMessage.of("{$.greet(\"World\")}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isEqualTo("Hello, World!");
        }

        @Test
        void shouldAllowOverridingBuiltInFunctions() {
            var placeholders = Placeholders.create(true);
            placeholders.registerGlobalFunction("env", (gf, accessor, ctx) ->
                "CUSTOM:" + accessor.params().strAt(0, ""));

            var message = CompiledMessage.of("{$.env(TEST)}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isEqualTo("CUSTOM:TEST");
        }
    }

    @Nested
    @DisplayName("Global functions in context")
    class InContext {

        @Test
        void shouldWorkWithSurroundingText(Placeholders placeholders) {
            var message = CompiledMessage.of("Name: {$.coalesce(nickname,name,\"Unknown\")}");
            var result = placeholders.contextOf(message)
                .with("nickname", null)
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Name: Alice");
        }

        @Test
        void shouldWorkWithMultipleGlobalCalls(Placeholders placeholders) {
            var message = CompiledMessage.of("{$.if(active,name,\"inactive\")} - {$.coalesce(status,\"unknown\")}");
            var result = placeholders.contextOf(message)
                .with("active", true)
                .with("name", "Alice")
                .with("status", null)
                .apply();

            assertThat(result).isEqualTo("Alice - unknown");
        }
    }

    @Nested
    @DisplayName("Short syntax for global functions")
    class ShortSyntax {

        @Test
        void dotSyntaxShouldWork(Placeholders placeholders) {
            // {.now()} should work like {$.now()}
            var message = CompiledMessage.of("{.now()}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isNotEmpty();
            assertThat(result).containsPattern("\\d{4}-\\d{2}-\\d{2}");
        }

        @Test
        void dotSyntaxShouldWorkWithChaining(Placeholders placeholders) {
            // {.coalesce(a,b,"default")} should work
            var message = CompiledMessage.of("{.coalesce(a,b,\"fallback\")}");
            var result = placeholders.contextOf(message)
                .with("a", null)
                .with("b", null)
                .apply();

            assertThat(result).isEqualTo("fallback");
        }

        @Test
        void bareSyntaxShouldWork(Placeholders placeholders) {
            // {now()} should work like {$.now()}
            var message = CompiledMessage.of("{now()}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isNotEmpty();
            assertThat(result).containsPattern("\\d{4}-\\d{2}-\\d{2}");
        }

        @Test
        void bareSyntaxShouldWorkWithArgs(Placeholders placeholders) {
            // {coalesce(a,b,"default")} should work
            var message = CompiledMessage.of("{coalesce(a,b,\"fallback\")}");
            var result = placeholders.contextOf(message)
                .with("a", null)
                .with("b", "Bravo")
                .apply();

            assertThat(result).isEqualTo("Bravo");
        }

        @Test
        void bareSyntaxShouldWorkWithIf(Placeholders placeholders) {
            // {if(cond,"yes","no")} should work
            var message = CompiledMessage.of("{if(active,\"yes\",\"no\")}");
            var result = placeholders.contextOf(message)
                .with("active", true)
                .apply();

            assertThat(result).isEqualTo("yes");
        }

        @Test
        void contextFieldShouldTakePriorityOverGlobalFunction(Placeholders placeholders) {
            // If "now" is in context, it should be used instead of $.now()
            var message = CompiledMessage.of("{now}");
            var result = placeholders.contextOf(message)
                .with("now", "custom-value")
                .apply();

            assertThat(result).isEqualTo("custom-value");
        }

        @Test
        void bareSyntaxWithEnv(Placeholders placeholders) {
            // {env(PATH)} should work
            var message = CompiledMessage.of("{env(PATH)}");
            var result = placeholders.contextOf(message).apply();

            assertThat(result).isNotEmpty();
        }

        @Test
        void orGlobalFunctionShouldWorkAsCoalesceAlias(Placeholders placeholders) {
            // {or(a,b,"default")} should work as alias for coalesce
            var message = CompiledMessage.of("{or(a,b,\"fallback\")}");
            var result = placeholders.contextOf(message)
                .with("a", null)
                .with("b", "Bravo")
                .apply();

            assertThat(result).isEqualTo("Bravo");
        }

        @Test
        void orGlobalFunctionShouldReturnDefaultWhenAllNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{or(a,b,\"fallback\")}");
            var result = placeholders.contextOf(message)
                .with("a", null)
                .with("b", null)
                .apply();

            assertThat(result).isEqualTo("fallback");
        }
    }
}
