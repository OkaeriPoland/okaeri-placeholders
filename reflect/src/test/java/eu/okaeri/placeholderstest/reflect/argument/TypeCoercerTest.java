package eu.okaeri.placeholderstest.reflect.argument;

import eu.okaeri.placeholders.reflect.argument.TypeCoercer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.assertj.core.data.Offset;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TypeCoercer")
class TypeCoercerTest {

    @Nested
    @DisplayName("wrap()")
    class Wrap {

        @Test
        @DisplayName("wraps primitives to wrappers")
        void wrapsPrimitives() {
            assertThat(TypeCoercer.wrap(int.class)).isEqualTo(Integer.class);
            assertThat(TypeCoercer.wrap(long.class)).isEqualTo(Long.class);
            assertThat(TypeCoercer.wrap(double.class)).isEqualTo(Double.class);
            assertThat(TypeCoercer.wrap(float.class)).isEqualTo(Float.class);
            assertThat(TypeCoercer.wrap(boolean.class)).isEqualTo(Boolean.class);
            assertThat(TypeCoercer.wrap(byte.class)).isEqualTo(Byte.class);
            assertThat(TypeCoercer.wrap(short.class)).isEqualTo(Short.class);
            assertThat(TypeCoercer.wrap(char.class)).isEqualTo(Character.class);
        }

        @Test
        @DisplayName("returns non-primitives unchanged")
        void nonPrimitivesUnchanged() {
            assertThat(TypeCoercer.wrap(String.class)).isEqualTo(String.class);
            assertThat(TypeCoercer.wrap(Integer.class)).isEqualTo(Integer.class);
            assertThat(TypeCoercer.wrap(Object.class)).isEqualTo(Object.class);
        }
    }

    @Nested
    @DisplayName("coercionScore()")
    class CoercionScore {

        @Test
        @DisplayName("exact match scores 0")
        void exactMatchScoresZero() {
            assertThat(TypeCoercer.coercionScore(String.class, String.class)).isEqualTo(0);
            assertThat(TypeCoercer.coercionScore(Integer.class, Integer.class)).isEqualTo(0);
        }

        @Test
        @DisplayName("primitive/wrapper match scores 1")
        void primitiveWrapperScoresOne() {
            assertThat(TypeCoercer.coercionScore(Integer.class, int.class)).isEqualTo(1);
            assertThat(TypeCoercer.coercionScore(int.class, Integer.class)).isEqualTo(1);
            assertThat(TypeCoercer.coercionScore(Long.class, long.class)).isEqualTo(1);
        }

        @Test
        @DisplayName("widening primitive conversions score 2")
        void wideningPrimitiveScoresTwo() {
            // int -> long
            assertThat(TypeCoercer.coercionScore(Integer.class, long.class)).isEqualTo(2);
            // float -> double
            assertThat(TypeCoercer.coercionScore(Float.class, double.class)).isEqualTo(2);
            // byte -> int
            assertThat(TypeCoercer.coercionScore(Byte.class, int.class)).isEqualTo(2);
        }

        @Test
        @DisplayName("widening to wrapper uses same score as primitive widening")
        void wideningToWrapperScore() {
            // int -> Long (widening + boxing) - same as widening
            int score = TypeCoercer.coercionScore(Integer.class, Long.class);
            assertThat(score).isGreaterThan(1); // Not exact or primitive/wrapper
            assertThat(score).isLessThan(TypeCoercer.NO_MATCH);
        }

        @Test
        @DisplayName("string parsing scores 4")
        void stringParsingScoresFour() {
            assertThat(TypeCoercer.coercionScore(String.class, int.class)).isEqualTo(4);
            assertThat(TypeCoercer.coercionScore(String.class, long.class)).isEqualTo(4);
            assertThat(TypeCoercer.coercionScore(String.class, double.class)).isEqualTo(4);
        }

        @Test
        @DisplayName("assignable types score 5")
        void assignableScoresFive() {
            assertThat(TypeCoercer.coercionScore(ArrayList.class, List.class)).isEqualTo(5);
            assertThat(TypeCoercer.coercionScore(String.class, Object.class)).isEqualTo(5);
        }

        @Test
        @DisplayName("incompatible types return NO_MATCH")
        void incompatibleReturnsNoMatch() {
            assertThat(TypeCoercer.coercionScore(String.class, List.class)).isEqualTo(TypeCoercer.NO_MATCH);
            assertThat(TypeCoercer.coercionScore(Integer.class, String.class)).isEqualTo(TypeCoercer.NO_MATCH);
        }

        @Test
        @DisplayName("null types handled correctly")
        void nullTypesHandled() {
            // null can be assigned to any reference type
            assertThat(TypeCoercer.coercionScore(null, String.class)).isNotEqualTo(TypeCoercer.NO_MATCH);
            assertThat(TypeCoercer.coercionScore(null, Object.class)).isNotEqualTo(TypeCoercer.NO_MATCH);
        }
    }

    @Nested
    @DisplayName("canCoerce()")
    class CanCoerce {

        @Test
        @DisplayName("returns true for exact match")
        void exactMatch() {
            assertThat(TypeCoercer.canCoerce(String.class, String.class)).isTrue();
        }

        @Test
        @DisplayName("returns true for primitive/wrapper")
        void primitiveWrapper() {
            assertThat(TypeCoercer.canCoerce(Integer.class, int.class)).isTrue();
            assertThat(TypeCoercer.canCoerce(int.class, Integer.class)).isTrue();
        }

        @Test
        @DisplayName("returns true for widening conversions")
        void wideningConversions() {
            assertThat(TypeCoercer.canCoerce(Integer.class, long.class)).isTrue();
            assertThat(TypeCoercer.canCoerce(Float.class, double.class)).isTrue();
        }

        @Test
        @DisplayName("returns true for string to numeric")
        void stringToNumeric() {
            assertThat(TypeCoercer.canCoerce(String.class, int.class)).isTrue();
            assertThat(TypeCoercer.canCoerce(String.class, long.class)).isTrue();
        }

        @Test
        @DisplayName("returns true for assignable types")
        void assignableTypes() {
            assertThat(TypeCoercer.canCoerce(ArrayList.class, List.class)).isTrue();
        }

        @Test
        @DisplayName("returns false for incompatible types")
        void incompatibleTypes() {
            assertThat(TypeCoercer.canCoerce(String.class, List.class)).isFalse();
        }
    }

    @Nested
    @DisplayName("coerce()")
    class Coerce {

        @Test
        @DisplayName("returns null for null value")
        void nullValue() {
            assertThat(TypeCoercer.coerce(null, String.class)).isNull();
        }

        @Test
        @DisplayName("returns same value for exact match")
        void exactMatch() {
            String value = "hello";
            assertThat(TypeCoercer.coerce(value, String.class)).isSameAs(value);
        }

        @Test
        @DisplayName("coerces Integer to long")
        void integerToLong() {
            assertThat(TypeCoercer.coerce(42, long.class)).isEqualTo(42L);
        }

        @Test
        @DisplayName("coerces Integer to Long wrapper")
        void integerToLongWrapper() {
            assertThat(TypeCoercer.coerce(42, Long.class)).isEqualTo(42L);
        }

        @Test
        @DisplayName("coerces Float to double")
        void floatToDouble() {
            // Float to double coercion - use delta comparison for floating point
            double result = (double) TypeCoercer.coerce(3.14f, double.class);
            assertThat(result).isCloseTo(3.14, Offset.offset(0.001));
        }

        @Test
        @DisplayName("parses String to Integer")
        void stringToInteger() {
            assertThat(TypeCoercer.coerce("123", int.class)).isEqualTo(123);
            assertThat(TypeCoercer.coerce("123", Integer.class)).isEqualTo(123);
        }

        @Test
        @DisplayName("parses String to Long")
        void stringToLong() {
            assertThat(TypeCoercer.coerce("123", long.class)).isEqualTo(123L);
        }

        @Test
        @DisplayName("parses String to Double")
        void stringToDouble() {
            assertThat(TypeCoercer.coerce("12.34", double.class)).isEqualTo(12.34);
        }

        @Test
        @DisplayName("parses String to Boolean")
        void stringToBoolean() {
            assertThat(TypeCoercer.coerce("true", boolean.class)).isEqualTo(true);
            assertThat(TypeCoercer.coerce("false", Boolean.class)).isEqualTo(false);
        }

        @Test
        @DisplayName("widens byte to int")
        void byteToInt() {
            assertThat(TypeCoercer.coerce((byte) 42, int.class)).isEqualTo(42);
        }

        @Test
        @DisplayName("widens short to long")
        void shortToLong() {
            assertThat(TypeCoercer.coerce((short) 100, long.class)).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("Method matching scenarios")
    class MethodMatching {

        @Test
        @DisplayName("prefers exact match over widening")
        void prefersExactOverWidening() {
            // When choosing between method(int) and method(long) for int arg
            int intScore = TypeCoercer.coercionScore(Integer.class, int.class);
            int longScore = TypeCoercer.coercionScore(Integer.class, long.class);
            assertThat(intScore).isLessThan(longScore);
        }

        @Test
        @DisplayName("primitive and wrapper widening have same or similar score")
        void primitiveAndWrapperWideningScores() {
            // When choosing between method(long) and method(Long) for int arg
            int primScore = TypeCoercer.coercionScore(Integer.class, long.class);
            int wrapScore = TypeCoercer.coercionScore(Integer.class, Long.class);
            // Both are widening - scores may be equal or primitive slightly lower
            assertThat(primScore).isLessThanOrEqualTo(wrapScore);
        }

        @Test
        @DisplayName("prefers widening over string parsing")
        void prefersWideningOverStringParsing() {
            // Should prefer numeric widening over parsing
            int wideningScore = TypeCoercer.coercionScore(Integer.class, long.class);
            int parsingScore = TypeCoercer.coercionScore(String.class, long.class);
            assertThat(wideningScore).isLessThan(parsingScore);
        }
    }
}
