package eu.okaeri.placeholders.fixture;

import org.junit.jupiter.params.provider.Arguments;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Parameterized test data for comprehensive test coverage.
 * Methods return Stream<Arguments> for use with @MethodSource.
 */
public final class TestData {

    private TestData() {
    }

    // ========== Syntax Edge Cases ==========

    /**
     * Edge cases for placeholder syntax parsing.
     * Each case: (input, description)
     */
    public static Stream<Arguments> syntaxEdgeCases() {
        return Stream.of(
            arguments("{}", "empty placeholder"),
            arguments("{field}{field}", "adjacent fields"),
            arguments("{field} {field}", "same field twice"),
            arguments("{{field}}", "double braces around field"),
            arguments("{a#b#c}", "multiple hash characters"),
            arguments("{field|a|b}", "multiple pipe characters"),
            arguments("{field|}", "empty default value"),
            // Note: {|default} causes NPE - empty field name not supported
            arguments("{a.b.c.d.e.f.g}", "deep nesting 7 levels"),
            arguments("{a.b.c.d.e.f.g.h.i.j}", "deep nesting 10 levels"),
            arguments("{field|default value with spaces}", "default with spaces"),
            arguments("{field|default|with|pipes}", "default containing pipes"),
            arguments("{очень}", "cyrillic field name"),
            arguments("{日本語}", "japanese field name"),
            arguments("{emoji_🎉}", "field with emoji"),
            arguments("{CamelCase}", "camel case field"),
            arguments("{snake_case}", "snake case field"),
            arguments("{SCREAMING_CASE}", "screaming case field"),
            arguments("{field123}", "field with numbers"),
            arguments("{123field}", "field starting with number"),
            arguments("Hello {name}!", "field at middle"),
            arguments("{name} World!", "field at start"),
            arguments("Hello {name}", "field at end"),
            arguments("{a}{b}{c}", "multiple adjacent fields"),
            arguments("no placeholders here", "no fields at all")
        );
    }

    /**
     * Invalid or potentially problematic syntax patterns.
     * Each case: (input, description)
     */
    public static Stream<Arguments> problematicSyntax() {
        return Stream.of(
            arguments("{unclosed", "unclosed brace"),
            arguments("unclosed}", "unmatched closing brace"),
            arguments("{nested{field}}", "nested braces"),
            arguments("{}", "empty placeholder"),
            arguments("{  }", "whitespace only"),
            arguments("{\t}", "tab character"),
            arguments("{\n}", "newline character")
        );
    }

    // ========== Pluralization ==========

    /**
     * Pluralization test cases for various locales.
     * Each case: (locale, count, forms, expected)
     */
    public static Stream<Arguments> pluralizationCases() {
        return Stream.of(
            // English (2 forms: singular, plural)
            arguments(Locale.ENGLISH, 0, new String[]{"apple", "apples"}, "apples"),
            arguments(Locale.ENGLISH, 1, new String[]{"apple", "apples"}, "apple"),
            arguments(Locale.ENGLISH, 2, new String[]{"apple", "apples"}, "apples"),
            arguments(Locale.ENGLISH, 5, new String[]{"apple", "apples"}, "apples"),
            arguments(Locale.ENGLISH, 100, new String[]{"apple", "apples"}, "apples"),
            arguments(Locale.ENGLISH, -1, new String[]{"apple", "apples"}, "apples"),

            // Polish (3 forms: 1, 2-4/22-24/etc, rest)
            arguments(Locale.forLanguageTag("pl"), 1, new String[]{"pies", "psy", "psów"}, "pies"),
            arguments(Locale.forLanguageTag("pl"), 2, new String[]{"pies", "psy", "psów"}, "psy"),
            arguments(Locale.forLanguageTag("pl"), 3, new String[]{"pies", "psy", "psów"}, "psy"),
            arguments(Locale.forLanguageTag("pl"), 4, new String[]{"pies", "psy", "psów"}, "psy"),
            arguments(Locale.forLanguageTag("pl"), 5, new String[]{"pies", "psy", "psów"}, "psów"),
            arguments(Locale.forLanguageTag("pl"), 11, new String[]{"pies", "psy", "psów"}, "psów"),
            arguments(Locale.forLanguageTag("pl"), 22, new String[]{"pies", "psy", "psów"}, "psy"),

            // Russian (3 forms: similar to Polish)
            arguments(Locale.forLanguageTag("ru"), 1, new String[]{"яблоко", "яблока", "яблок"}, "яблоко"),
            arguments(Locale.forLanguageTag("ru"), 2, new String[]{"яблоко", "яблока", "яблок"}, "яблока"),
            arguments(Locale.forLanguageTag("ru"), 5, new String[]{"яблоко", "яблока", "яблок"}, "яблок"),
            arguments(Locale.forLanguageTag("ru"), 21, new String[]{"яблоко", "яблока", "яблок"}, "яблоко"),

            // German (2 forms: like English)
            arguments(Locale.GERMAN, 1, new String[]{"Apfel", "Äpfel"}, "Apfel"),
            arguments(Locale.GERMAN, 2, new String[]{"Apfel", "Äpfel"}, "Äpfel"),

            // French (2 forms but 0 is singular)
            arguments(Locale.FRENCH, 0, new String[]{"pomme", "pommes"}, "pomme"),
            arguments(Locale.FRENCH, 1, new String[]{"pomme", "pommes"}, "pomme"),
            arguments(Locale.FRENCH, 2, new String[]{"pomme", "pommes"}, "pommes")
        );
    }

    // ========== Boolean Translation ==========

    /**
     * Boolean translation test cases.
     * Each case: (value, trueText, falseText, expected)
     */
    public static Stream<Arguments> booleanTranslationCases() {
        return Stream.of(
            arguments(true, "yes", "no", "yes"),
            arguments(false, "yes", "no", "no"),
            arguments(true, "enabled", "disabled", "enabled"),
            arguments(false, "enabled", "disabled", "disabled"),
            arguments(true, "✓", "✗", "✓"),
            arguments(false, "✓", "✗", "✗"),
            arguments(true, "on", "off", "on"),
            arguments(false, "on", "off", "off"),
            arguments(true, "true", "false", "true"),
            arguments(false, "true", "false", "false")
        );
    }

    // ========== Number Formatting ==========

    /**
     * Printf-style number formatting cases.
     * Each case: (format, value, expected) - using English locale
     */
    public static Stream<Arguments> numberFormattingCases() {
        return Stream.of(
            // Basic decimal
            arguments("%.2f", 3.14159, "3.14"),
            arguments("%.0f", 3.7, "4"),
            arguments("%.4f", 0.1, "0.1000"),
            arguments("%.1f", 2.55, "2.6"),

            // Integer formatting
            arguments("%d", 42, "42"),
            arguments("%d", -42, "-42"),
            arguments("%d", 0, "0"),

            // Padding
            arguments("%05d", 42, "00042"),
            arguments("%5d", 42, "   42"),
            arguments("%-5d", 42, "42   "),

            // Signs
            arguments("%+d", 42, "+42"),
            arguments("%+d", -42, "-42"),
            arguments("%+.2f", 3.14, "+3.14"),

            // Grouping (locale dependent)
            arguments("%,d", 1234567, "1,234,567"),
            arguments("%,.2f", 1234567.89, "1,234,567.89")
        );
    }

    // ========== Duration Formatting ==========

    /**
     * Duration formatting test cases.
     * Each case: (duration, pattern, expected)
     */
    public static Stream<Arguments> durationFormattingCases() {
        return Stream.of(
            // Simple durations
            arguments(Duration.ofDays(1), "{d}", "1d"),
            arguments(Duration.ofHours(2), "{d}", "2h"),
            arguments(Duration.ofMinutes(30), "{d}", "30m"),
            arguments(Duration.ofSeconds(45), "{d}", "45s"),

            // Combined durations
            arguments(Duration.ofHours(25), "{d}", "1d1h"),
            arguments(Duration.ofMinutes(90), "{d}", "1h30m"),
            arguments(Duration.ofSeconds(3661), "{d}", "1h1m1s"),

            // With accuracy parameter
            arguments(Duration.ofSeconds(3661), "{d(h)}", "1h"),
            arguments(Duration.ofSeconds(3661), "{d(m)}", "1h1m"),
            arguments(Duration.ofSeconds(3661), "{d(s)}", "1h1m1s"),

            // Zero duration
            arguments(Duration.ZERO, "{d}", "0s"),
            arguments(Duration.ZERO, "{d(h)}", "0h"),

            // Large durations
            arguments(Duration.ofDays(365), "{d}", "365d"),
            arguments(Duration.ofDays(30).plusHours(12), "{d}", "30d12h")
        );
    }

    // ========== Instant/DateTime Formatting ==========

    /**
     * DateTime formatting test cases.
     * Each case: (instant, pattern, locale, zoneId, expectedContains)
     */
    public static Stream<Arguments> dateTimeFormattingCases() {
        var epoch = Instant.EPOCH;
        var y2k = Instant.parse("2000-01-01T12:00:00Z");

        return Stream.of(
            // Localized time (LT)
            arguments(y2k, "{lt,short#time}", Locale.ENGLISH, ZoneId.of("UTC"), ":00"),

            // Localized date-time (LDT)
            arguments(y2k, "{ldt,medium#time}", Locale.ENGLISH, ZoneId.of("UTC"), "2000"),

            // Localized date (LD)
            arguments(y2k, "{ld,short#time}", Locale.ENGLISH, ZoneId.of("UTC"), "1"),

            // Custom pattern (P)
            arguments(y2k, "{p,yyyy-MM-dd#time}", Locale.ENGLISH, ZoneId.of("UTC"), "2000-01-01"),
            arguments(y2k, "{p,HH:mm:ss#time}", Locale.ENGLISH, ZoneId.of("UTC"), "12:00:00")
        );
    }

    // ========== String Operations ==========

    /**
     * String placeholder operation test cases.
     * Each case: (input, operation, expected)
     */
    public static Stream<Arguments> stringOperationCases() {
        return Stream.of(
            // Case conversion
            arguments("hello", "toUpperCase", "HELLO"),
            arguments("HELLO", "toLowerCase", "hello"),
            arguments("hElLo", "toUpperCase", "HELLO"),
            arguments("hElLo", "toLowerCase", "hello"),

            // Capitalization
            arguments("hello world", "capitalize", "Hello world"),
            arguments("HELLO WORLD", "capitalize", "HELLO WORLD"),
            arguments("hello world", "capitalizeFully", "Hello World"),
            arguments("HELLO WORLD", "capitalizeFully", "Hello World"),

            // Empty/null handling
            arguments("", "toUpperCase", ""),
            arguments("", "toLowerCase", ""),
            arguments("", "capitalize", ""),

            // Unicode
            arguments("cześć", "toUpperCase", "CZEŚĆ"),
            arguments("CZEŚĆ", "toLowerCase", "cześć")
        );
    }

    // ========== Integer Operations ==========

    /**
     * Integer arithmetic operation test cases.
     * Each case: (input, operation, operand, expected)
     */
    public static Stream<Arguments> integerOperationCases() {
        return Stream.of(
            // Addition
            arguments(10, "add", 5, 15),
            arguments(10, "plus", 5, 15),
            arguments(-5, "add", 10, 5),
            arguments(0, "add", 0, 0),

            // Subtraction
            arguments(10, "minus", 3, 7),
            arguments(10, "subtract", 3, 7),
            arguments(5, "minus", 10, -5),

            // Multiplication
            arguments(10, "multiply", 3, 30),
            arguments(-5, "multiply", 3, -15),
            arguments(10, "multiply", 0, 0),

            // Division
            arguments(10, "divide", 2, 5),
            arguments(15, "divide", 4, 3), // integer division
            arguments(-10, "divide", 2, -5)
        );
    }

    // ========== Field Chaining ==========

    /**
     * Field chaining test cases.
     * Each case: (path, description)
     */
    public static Stream<Arguments> fieldChainingCases() {
        return Stream.of(
            arguments("name", "single field"),
            arguments("player.name", "two levels"),
            arguments("player.inventory.size", "three levels"),
            arguments("player.inventory.first.type", "four levels"),
            arguments("player.inventory.first.meta.name", "five levels")
        );
    }

    // ========== Locales for Testing ==========

    /**
     * Various locales for internationalization tests.
     */
    public static Stream<Arguments> testLocales() {
        return Stream.of(
            arguments(Locale.ENGLISH, "English"),
            arguments(Locale.GERMAN, "German"),
            arguments(Locale.FRENCH, "French"),
            arguments(Locale.JAPANESE, "Japanese"),
            arguments(Locale.forLanguageTag("pl"), "Polish"),
            arguments(Locale.forLanguageTag("ru"), "Russian"),
            arguments(Locale.forLanguageTag("ar"), "Arabic"),
            arguments(Locale.forLanguageTag("zh"), "Chinese")
        );
    }

    // ========== Unicode Test Strings ==========

    /**
     * Unicode strings for testing international character support.
     * Each case: (string, description)
     */
    public static Stream<Arguments> unicodeStrings() {
        return Stream.of(
            arguments("Hello", "ASCII only"),
            arguments("Cześć", "Polish"),
            arguments("Привет", "Russian"),
            arguments("你好", "Chinese"),
            arguments("こんにちは", "Japanese"),
            arguments("مرحبا", "Arabic"),
            arguments("שלום", "Hebrew"),
            arguments("🎉🎊🎈", "Emoji"),
            arguments("Héllo Wörld", "Accented Latin"),
            arguments("Καλημέρα", "Greek")
        );
    }
}
