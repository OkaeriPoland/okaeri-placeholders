package eu.okaeri.placeholders.context;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.fixture.PlaceholdersExtension;
import eu.okaeri.placeholders.message.CompiledMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Composition with .or() and method chaining")
@ExtendWith(PlaceholdersExtension.class)
class CompositionTest {

    @Nested
    @DisplayName(".or() with string literal fallback")
    class OrWithLiteral {

        @Test
        void shouldReturnValueWhenNotNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or(\"Anonymous\")}");
            var result = placeholders.context(message)
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Alice");
        }

        @Test
        void shouldReturnFallbackWhenNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or(\"Anonymous\")}");
            var result = placeholders.context(message)
                .with("name", null)
                .apply();

            assertThat(result).isEqualTo("Anonymous");
        }

        @Test
        void shouldReturnFallbackWhenMissing(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or(\"Anonymous\")}");
            var result = placeholders.context(message)
                .apply();

            assertThat(result).isEqualTo("Anonymous");
        }

        @Test
        void shouldWorkWithSingleQuotes(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or('Guest')}");
            var result = placeholders.context(message)
                .apply();

            assertThat(result).isEqualTo("Guest");
        }

        @Test
        void shouldWorkWithEmptyString(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or(\"\")}");
            var result = placeholders.context(message)
                .with("name", null)
                .apply();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName(".or() with field reference fallback")
    class OrWithFieldRef {

        @Test
        void shouldUseFieldRefAsFallback(Placeholders placeholders) {
            var message = CompiledMessage.of("{nickname.or(name)}");
            var result = placeholders.context(message)
                .with("nickname", null)
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Alice");
        }

        @Test
        void shouldReturnValueIfNotNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{nickname.or(name)}");
            var result = placeholders.context(message)
                .with("nickname", "Ali")
                .with("name", "Alice")
                .apply();

            assertThat(result).isEqualTo("Ali");
        }

        @Test
        void shouldTreatUnknownFieldAsLiteralString(Placeholders placeholders) {
            // backward compat: if the field doesn't exist, treat arg as string literal
            var message = CompiledMessage.of("{name.or(fallback)}");
            var result = placeholders.context(message)
                .apply();

            assertThat(result).isEqualTo("fallback");
        }
    }

    @Nested
    @DisplayName("Chained .or() calls")
    class ChainedOr {

        @Test
        void shouldChainMultipleOrs(Placeholders placeholders) {
            var message = CompiledMessage.of("{a.or(b).or(c).or(\"default\")}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", null)
                .with("c", "Charlie")
                .apply();

            assertThat(result).isEqualTo("Charlie");
        }

        @Test
        void shouldUseFirstNonNullValue(Placeholders placeholders) {
            var message = CompiledMessage.of("{a.or(b).or(c).or(\"default\")}");
            var result = placeholders.context(message)
                .with("a", "Alpha")
                .with("b", "Bravo")
                .with("c", "Charlie")
                .apply();

            assertThat(result).isEqualTo("Alpha");
        }

        @Test
        void shouldUseLiteralDefaultWhenAllNull(Placeholders placeholders) {
            var message = CompiledMessage.of("{a.or(b).or(c).or(\"default\")}");
            var result = placeholders.context(message)
                .with("a", null)
                .with("b", null)
                .with("c", null)
                .apply();

            assertThat(result).isEqualTo("default");
        }
    }

    @Nested
    @DisplayName(".or() combined with other methods")
    class OrWithOtherMethods {

        @Test
        void shouldChainOrWithToUpperCase(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or(\"guest\").toUpperCase()}");
            var result = placeholders.context(message)
                .apply();

            assertThat(result).isEqualTo("GUEST");
        }

        @Test
        void shouldApplyMethodToFallbackValue(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or(fallback).toUpperCase()}");
            var result = placeholders.context(message)
                .with("name", null)
                .with("fallback", "alice")
                .apply();

            assertThat(result).isEqualTo("ALICE");
        }

        @Test
        void shouldApplyToUpperCaseAfterOrWithNull(Placeholders placeholders) {
            // Test chaining another method after .or() when value is null
            var message = CompiledMessage.of("{nickname.or(name).toUpperCase()}");
            var result = placeholders.context(message)
                .with("nickname", null)
                .with("name", "alice")
                .apply();

            assertThat(result).isEqualTo("ALICE");
        }

        @Test
        void shouldApplyMethodInsideOrParam(Placeholders placeholders) {
            // Test method call INSIDE .or() param: {name.or(fallback.toUpperCase)}
            var message = CompiledMessage.of("{name.or(fallback.toUpperCase)}");
            var result = placeholders.context(message)
                .with("name", null)
                .with("fallback", "alice")
                .apply();

            assertThat(result).isEqualTo("ALICE");
        }

        @Test
        void shouldApplyChainedMethodsInsideOrParam(Placeholders placeholders) {
            // Test chained methods INSIDE .or() param: {name.or(fallback.toLowerCase.capitalize)}
            var message = CompiledMessage.of("{name.or(fallback.toLowerCase.capitalize)}");
            var result = placeholders.context(message)
                .with("name", null)
                .with("fallback", "HELLO WORLD")
                .apply();

            assertThat(result).isEqualTo("Hello world");
        }

        @Test
        void shouldApplyMethodWithParamsInsideOrParam(Placeholders placeholders) {
            // Test method with () args INSIDE .or() param: {name.or(fallback.replace(a,b))}
            var message = CompiledMessage.of("{name.or(fallback.replace(o,0))}");
            var result = placeholders.context(message)
                .with("name", null)
                .with("fallback", "hello")
                .apply();

            assertThat(result).isEqualTo("hell0");
        }

        @Test
        void shouldHandleTwoLevelsOfNestedParens(Placeholders placeholders) {
            // 2 levels: {$.if(cond, value.replace(a,b), fallback)}
            var message = CompiledMessage.of("{$.if(active, name.replace(_,-), fallback)}");
            var result = placeholders.context(message)
                .with("active", true)
                .with("name", "hello_world")
                .with("fallback", "default")
                .apply();

            assertThat(result).isEqualTo("hello-world");
        }

        @Test
        void shouldHandleThreeLevelsOfNestedParens(Placeholders placeholders) {
            // 3 levels: {$.if(cond, a.or(b.replace(x,y)), fallback)}
            var message = CompiledMessage.of("{$.if(active, primary.or(secondary.replace(_,-)), fallback)}");
            var result = placeholders.context(message)
                .with("active", true)
                .with("primary", null)
                .with("secondary", "hello_world")
                .with("fallback", "default")
                .apply();

            assertThat(result).isEqualTo("hello-world");
        }
    }

    @Nested
    @DisplayName("Default value syntax comparison")
    class DefaultValueComparison {

        @Test
        void pipeSyntaxShouldStillWork(Placeholders placeholders) {
            var message = CompiledMessage.of("{name|Anonymous}");
            var result = placeholders.context(message)
                .apply();

            assertThat(result).isEqualTo("Anonymous");
        }

        @Test
        void orMethodShouldWorkLikePipeSyntax(Placeholders placeholders) {
            var message = CompiledMessage.of("{name.or(\"Anonymous\")}");
            var result = placeholders.context(message)
                .apply();

            assertThat(result).isEqualTo("Anonymous");
        }

        @Test
        void orMethodAllowsFieldRefWhilePipeDoesNot(Placeholders placeholders) {
            var messageOr = CompiledMessage.of("{nickname.or(name)}");
            var resultOr = placeholders.context(messageOr)
                .with("nickname", null)
                .with("name", "Alice")
                .apply();

            // With .or(), we can use field references
            assertThat(resultOr).isEqualTo("Alice");

            // With |, the default is always a literal string
            var messagePipe = CompiledMessage.of("{nickname|name}");
            var resultPipe = placeholders.context(messagePipe)
                .with("nickname", null)
                .with("name", "Alice")
                .apply();

            assertThat(resultPipe).isEqualTo("name");
        }
    }
}
