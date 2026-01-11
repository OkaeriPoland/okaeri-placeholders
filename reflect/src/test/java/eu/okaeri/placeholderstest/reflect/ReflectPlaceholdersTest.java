package eu.okaeri.placeholderstest.reflect;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.message.CompiledMessage;
import eu.okaeri.placeholders.reflect.ReflectPlaceholders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for ReflectPlaceholders.
 * Tests the full placeholder resolution pipeline from template to result.
 */
@DisplayName("ReflectPlaceholders E2E")
class ReflectPlaceholdersTest {

    private Placeholders placeholders;

    @BeforeEach
    void setUp() {
        this.placeholders = ReflectPlaceholders.create();
    }

    // Test domain objects
    @SuppressWarnings("unused")
    public static class Person {
        public String name;
        private int age;
        protected String email;
        public Address address;
        public List<String> tags;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return this.name;
        }

        public int getAge() {
            return this.age;
        }

        public String greet() {
            return "Hello, " + this.name + "!";
        }

        public String greet(String greeting) {
            return greeting + ", " + this.name + "!";
        }

        public String formatAge(String prefix, String suffix) {
            return prefix + this.age + suffix;
        }

        public boolean isAdult() {
            return this.age >= 18;
        }

        public Person withAge(int newAge) {
            return new Person(this.name, newAge);
        }

        public String repeat(String text, int times) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < times; i++) {
                sb.append(text);
            }
            return sb.toString();
        }

        public double calculateScore(double base, double multiplier) {
            return base * multiplier * this.age;
        }
    }

    @SuppressWarnings("unused")
    public static class Address {
        public String city;
        public String country;
        public int zipCode;

        public Address(String city, String country, int zipCode) {
            this.city = city;
            this.country = country;
            this.zipCode = zipCode;
        }

        public String format() {
            return this.city + ", " + this.country + " " + this.zipCode;
        }
    }

    @SuppressWarnings("unused")
    public static class Calculator {
        public static int add(int a, int b) {
            return a + b;
        }

        public static String concat(String a, String b) {
            return a + b;
        }

        public static final double PI = 3.14159;
        public static final String VERSION = "1.0.0";
    }

    @SuppressWarnings("unused")
    public static class TypeTester {
        public String stringMethod(String s) {
            return "string:" + s;
        }

        public String charMethod(char c) {
            return "char:" + c;
        }

        public String intMethod(int i) {
            return "int:" + i;
        }

        public String longMethod(long l) {
            return "long:" + l;
        }

        public String doubleMethod(double d) {
            return "double:" + d;
        }

        public String floatMethod(float f) {
            return "float:" + f;
        }

        public String boolMethod(boolean b) {
            return "bool:" + b;
        }

        public String byteMethod(byte b) {
            return "byte:" + b;
        }

        public String shortMethod(short s) {
            return "short:" + s;
        }

        // Overloaded methods for coercion testing
        public String overloaded(int x) {
            return "int:" + x;
        }

        public String overloaded(long x) {
            return "long:" + x;
        }

        public String overloaded(double x) {
            return "double:" + x;
        }

        public String overloaded(String x) {
            return "string:" + x;
        }
    }

    @Nested
    @DisplayName("Field access")
    class FieldAccess {

        @Test
        @DisplayName("accesses public field")
        void accessesPublicField() {
            Person person = new Person("Alice", 30);
            String result = ReflectPlaceholdersTest.this.apply("{person.name}", "person", person);
            assertThat(result).isEqualTo("Alice");
        }

        @Test
        @DisplayName("accesses nested object field")
        void accessesNestedField() {
            Person person = new Person("Bob", 25);
            person.address = new Address("Paris", "France", 75001);

            String result = ReflectPlaceholdersTest.this.apply("{person.address.city}", "person", person);
            assertThat(result).isEqualTo("Paris");
        }

        @Test
        @DisplayName("chains multiple field accesses")
        void chainsFieldAccesses() {
            Person person = new Person("Carol", 35);
            person.address = new Address("Berlin", "Germany", 10115);

            String result = ReflectPlaceholdersTest.this.apply("{person.address.country}", "person", person);
            assertThat(result).isEqualTo("Germany");
        }
    }

    @Nested
    @DisplayName("No-arg methods")
    class NoArgMethods {

        @Test
        @DisplayName("calls no-arg method")
        void callsNoArgMethod() {
            Person person = new Person("Dave", 40);
            String result = ReflectPlaceholdersTest.this.apply("{person.greet()}", "person", person);
            assertThat(result).isEqualTo("Hello, Dave!");
        }

        @Test
        @DisplayName("calls getter method")
        void callsGetterMethod() {
            Person person = new Person("Eve", 28);
            String result = ReflectPlaceholdersTest.this.apply("{person.getName()}", "person", person);
            assertThat(result).isEqualTo("Eve");
        }

        @Test
        @DisplayName("calls boolean method")
        void callsBooleanMethod() {
            Person adult = new Person("Frank", 21);
            Person minor = new Person("Grace", 15);

            assertThat(ReflectPlaceholdersTest.this.apply("{person.isAdult()}", "person", adult)).isEqualTo("true");
            assertThat(ReflectPlaceholdersTest.this.apply("{person.isAdult()}", "person", minor)).isEqualTo("false");
        }

        @Test
        @DisplayName("chains method calls")
        void chainsMethodCalls() {
            String result = ReflectPlaceholdersTest.this.apply("{text.toUpperCase().toLowerCase()}", "text", "Hello");
            assertThat(result).isEqualTo("hello");
        }

        @Test
        @DisplayName("chains field and method")
        void chainsFieldAndMethod() {
            Person person = new Person("Henry", 45);
            person.address = new Address("Tokyo", "Japan", 100);

            String result = ReflectPlaceholdersTest.this.apply("{person.address.format()}", "person", person);
            assertThat(result).isEqualTo("Tokyo, Japan 100");
        }
    }

    @Nested
    @DisplayName("Methods with arguments")
    class MethodsWithArgs {

        @Test
        @DisplayName("calls method with string argument")
        void callsWithStringArg() {
            Person person = new Person("Ivy", 32);
            String result = ReflectPlaceholdersTest.this.apply("{person.greet('Hi')}", "person", person);
            assertThat(result).isEqualTo("Hi, Ivy!");
        }

        @Test
        @DisplayName("calls method with multiple string arguments")
        void callsWithMultipleStringArgs() {
            Person person = new Person("Jack", 50);
            String result = ReflectPlaceholdersTest.this.apply("{person.formatAge('Age: ', ' years')}", "person", person);
            assertThat(result).isEqualTo("Age: 50 years");
        }

        @Test
        @DisplayName("calls method with integer arguments")
        void callsWithIntArgs() {
            String result = ReflectPlaceholdersTest.this.apply("{text.substring(0, 5)}", "text", "Hello World");
            assertThat(result).isEqualTo("Hello");
        }

        @Test
        @DisplayName("calls method with mixed arguments")
        void callsWithMixedArgs() {
            Person person = new Person("Kate", 25);
            String result = ReflectPlaceholdersTest.this.apply("{person.repeat('*', 3)}", "person", person);
            assertThat(result).isEqualTo("***");
        }

        @Test
        @DisplayName("calls method with double arguments")
        void callsWithDoubleArgs() {
            Person person = new Person("Leo", 10);
            String result = ReflectPlaceholdersTest.this.apply("{person.calculateScore(2.5, 1.5)}", "person", person);
            // 2.5 * 1.5 * 10 = 37.5
            assertThat(result).startsWith("37.5");
        }

        @Test
        @DisplayName("calls String.replace with string arguments")
        void callsReplaceWithStrings() {
            String result = ReflectPlaceholdersTest.this.apply("{text.replace('hello', 'hi')}", "text", "hello world hello");
            assertThat(result).isEqualTo("hi world hi");
        }
    }

    @Nested
    @DisplayName("Argument types")
    class ArgumentTypes {

        private TypeTester tester;

        @BeforeEach
        void setUp() {
            this.tester = new TypeTester();
        }

        @Test
        @DisplayName("passes string literal")
        void passesStringLiteral() {
            String result = ReflectPlaceholdersTest.this.apply("{t.stringMethod('hello')}", "t", this.tester);
            assertThat(result).isEqualTo("string:hello");
        }

        @Test
        @DisplayName("passes integer literal")
        void passesIntegerLiteral() {
            String result = ReflectPlaceholdersTest.this.apply("{t.intMethod(42)}", "t", this.tester);
            assertThat(result).isEqualTo("int:42");
        }

        @Test
        @DisplayName("passes negative integer")
        void passesNegativeInteger() {
            String result = ReflectPlaceholdersTest.this.apply("{t.intMethod(-100)}", "t", this.tester);
            assertThat(result).isEqualTo("int:-100");
        }

        @Test
        @DisplayName("passes double literal")
        void passesDoubleLiteral() {
            String result = ReflectPlaceholdersTest.this.apply("{t.doubleMethod(3.14)}", "t", this.tester);
            assertThat(result).startsWith("double:3.14");
        }

        @Test
        @DisplayName("passes boolean true")
        void passesBooleanTrue() {
            String result = ReflectPlaceholdersTest.this.apply("{t.boolMethod(true)}", "t", this.tester);
            assertThat(result).isEqualTo("bool:true");
        }

        @Test
        @DisplayName("passes boolean false")
        void passesBooleanFalse() {
            String result = ReflectPlaceholdersTest.this.apply("{t.boolMethod(false)}", "t", this.tester);
            assertThat(result).isEqualTo("bool:false");
        }
    }

    @Nested
    @DisplayName("Context variable resolution")
    class ContextResolution {

        @Test
        @DisplayName("resolves context variable as method argument")
        void resolvesContextVariable() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{person.greet(greeting)}"))
                .with("person", new Person("Mike", 30))
                .with("greeting", "Welcome")
                .apply();
            assertThat(result).isEqualTo("Welcome, Mike!");
        }

        @Test
        @DisplayName("resolves multiple context variables")
        void resolvesMultipleContextVariables() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{text.replace(from, to)}"))
                .with("text", "hello world")
                .with("from", "world")
                .with("to", "universe")
                .apply();
            assertThat(result).isEqualTo("hello universe");
        }
    }

    @Nested
    @DisplayName("Type coercion")
    class TypeCoercion {

        private TypeTester tester;

        @BeforeEach
        void setUp() {
            this.tester = new TypeTester();
        }

        @Test
        @DisplayName("widens int to long parameter")
        void widensIntToLong() {
            // Call longMethod with int literal - should widen
            String result = ReflectPlaceholdersTest.this.apply("{t.longMethod(42)}", "t", this.tester);
            assertThat(result).isEqualTo("long:42");
        }

        @Test
        @DisplayName("selects correct overload for int")
        void selectsOverloadForInt() {
            String result = ReflectPlaceholdersTest.this.apply("{t.overloaded(42)}", "t", this.tester);
            assertThat(result).isEqualTo("int:42");
        }

        @Test
        @DisplayName("selects correct overload for long")
        void selectsOverloadForLong() {
            String result = ReflectPlaceholdersTest.this.apply("{t.overloaded(42L)}", "t", this.tester);
            assertThat(result).isEqualTo("long:42");
        }

        @Test
        @DisplayName("selects correct overload for double")
        void selectsOverloadForDouble() {
            String result = ReflectPlaceholdersTest.this.apply("{t.overloaded(3.14)}", "t", this.tester);
            assertThat(result).isEqualTo("double:3.14");
        }

        @Test
        @DisplayName("selects correct overload for string")
        void selectsOverloadForString() {
            String result = ReflectPlaceholdersTest.this.apply("{t.overloaded('hello')}", "t", this.tester);
            assertThat(result).isEqualTo("string:hello");
        }
    }

    @Nested
    @DisplayName("Static members")
    class StaticMembers {

        @Test
        @DisplayName("calls static method")
        void callsStaticMethod() {
            String result = ReflectPlaceholdersTest.this.apply("{calc.add(10, 20)}", "calc", Calculator.class);
            assertThat(result).isEqualTo("30");
        }

        @Test
        @DisplayName("calls static method with strings")
        void callsStaticMethodWithStrings() {
            String result = ReflectPlaceholdersTest.this.apply("{calc.concat('foo', 'bar')}", "calc", Calculator.class);
            assertThat(result).isEqualTo("foobar");
        }

        @Test
        @DisplayName("accesses static field")
        void accessesStaticField() {
            String result = ReflectPlaceholdersTest.this.apply("{calc.VERSION}", "calc", Calculator.class);
            assertThat(result).isEqualTo("1.0.0");
        }

        @Test
        @DisplayName("accesses static double field")
        void accessesStaticDoubleField() {
            String result = ReflectPlaceholdersTest.this.apply("{calc.PI}", "calc", Calculator.class);
            assertThat(result).startsWith("3.14");
        }
    }

    @Nested
    @DisplayName("Standard library methods")
    class StandardLibMethods {

        @Test
        @DisplayName("calls String.length()")
        void callsStringLength() {
            String result = ReflectPlaceholdersTest.this.apply("{text.length()}", "text", "hello");
            assertThat(result).isEqualTo("5");
        }

        @Test
        @DisplayName("calls String.toUpperCase()")
        void callsStringToUpperCase() {
            String result = ReflectPlaceholdersTest.this.apply("{text.toUpperCase()}", "text", "hello");
            assertThat(result).isEqualTo("HELLO");
        }

        @Test
        @DisplayName("calls String.toLowerCase()")
        void callsStringToLowerCase() {
            String result = ReflectPlaceholdersTest.this.apply("{text.toLowerCase()}", "text", "HELLO");
            assertThat(result).isEqualTo("hello");
        }

        @Test
        @DisplayName("calls String.trim()")
        void callsStringTrim() {
            String result = ReflectPlaceholdersTest.this.apply("{text.trim()}", "text", "  hello  ");
            assertThat(result).isEqualTo("hello");
        }

        @Test
        @DisplayName("calls String.substring()")
        void callsStringSubstring() {
            String result = ReflectPlaceholdersTest.this.apply("{text.substring(0, 5)}", "text", "hello world");
            assertThat(result).isEqualTo("hello");
        }

        @Test
        @DisplayName("calls String.startsWith()")
        void callsStringStartsWith() {
            String result = ReflectPlaceholdersTest.this.apply("{text.startsWith('hello')}", "text", "hello world");
            assertThat(result).isEqualTo("true");
        }

        @Test
        @DisplayName("calls String.contains()")
        void callsStringContains() {
            String result = ReflectPlaceholdersTest.this.apply("{text.contains('world')}", "text", "hello world");
            assertThat(result).isEqualTo("true");
        }

        @Test
        @DisplayName("calls List.size()")
        void callsListSize() {
            List<String> list = new java.util.ArrayList<>(Arrays.asList("a", "b", "c"));
            String result = ReflectPlaceholdersTest.this.apply("{list.size()}", "list", list);
            assertThat(result).isEqualTo("3");
        }

        @Test
        @DisplayName("calls List.get()")
        void callsListGet() {
            List<String> list = new java.util.ArrayList<>(Arrays.asList("first", "second", "third"));
            String result = ReflectPlaceholdersTest.this.apply("{list.get(1)}", "list", list);
            assertThat(result).isEqualTo("second");
        }

        @Test
        @DisplayName("calls List.isEmpty()")
        void callsListIsEmpty() {
            List<String> empty = new java.util.ArrayList<>();
            List<String> nonEmpty = new java.util.ArrayList<>(List.of("item"));

            assertThat(ReflectPlaceholdersTest.this.apply("{list.isEmpty()}", "list", empty)).isEqualTo("true");
            assertThat(ReflectPlaceholdersTest.this.apply("{list.isEmpty()}", "list", nonEmpty)).isEqualTo("false");
        }

        @Test
        @DisplayName("calls Map.size()")
        void callsMapSize() {
            Map<String, String> map = new HashMap<>();
            map.put("a", "1");
            map.put("b", "2");
            String result = ReflectPlaceholdersTest.this.apply("{map.size()}", "map", map);
            assertThat(result).isEqualTo("2");
        }

        @Test
        @DisplayName("calls Integer methods")
        void callsIntegerMethods() {
            Integer num = 42;
            assertThat(ReflectPlaceholdersTest.this.apply("{n.intValue()}", "n", num)).isEqualTo("42");
            assertThat(ReflectPlaceholdersTest.this.apply("{n.doubleValue()}", "n", num)).startsWith("42.0");
        }

        @Test
        @DisplayName("calls getClass().getSimpleName()")
        void callsGetClassSimpleName() {
            String result = ReflectPlaceholdersTest.this.apply("{obj.getClass().getSimpleName()}", "obj", "test");
            assertThat(result).isEqualTo("String");
        }

        @Test
        @DisplayName("calls getClass().getName()")
        void callsGetClassName() {
            String result = ReflectPlaceholdersTest.this.apply("{obj.getClass().getName()}", "obj", "test");
            assertThat(result).isEqualTo("java.lang.String");
        }
    }

    @Nested
    @DisplayName("Complex scenarios")
    class ComplexScenarios {

        @Test
        @DisplayName("deep method chaining")
        void deepMethodChaining() {
            String result = ReflectPlaceholdersTest.this.apply("{text.trim().toUpperCase().substring(0, 3)}", "text", "  hello  ");
            assertThat(result).isEqualTo("HEL");
        }

        @Test
        @DisplayName("method returning object then field access")
        void methodReturningObjectThenFieldAccess() {
            Person person = new Person("Nancy", 25);
            String result = ReflectPlaceholdersTest.this.apply("{person.withAge(30).getAge()}", "person", person);
            assertThat(result).isEqualTo("30");
        }

        @Test
        @DisplayName("multiple placeholders in template")
        void multiplePlaceholders() {
            Person person = new Person("Oscar", 35);
            person.address = new Address("Rome", "Italy", 100);

            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{person.name} lives in {person.address.city}, {person.address.country}"))
                .with("person", person)
                .apply();
            assertThat(result).isEqualTo("Oscar lives in Rome, Italy");
        }

        @Test
        @DisplayName("placeholder with surrounding text")
        void placeholderWithSurroundingText() {
            Person person = new Person("Paul", 28);
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("Hello, {person.name}! You are {person.getAge()} years old."))
                .with("person", person)
                .apply();
            assertThat(result).isEqualTo("Hello, Paul! You are 28 years old.");
        }

        @Test
        @DisplayName("LocalDate methods")
        void localDateMethods() {
            LocalDate date = LocalDate.of(2024, 6, 15);
            assertThat(ReflectPlaceholdersTest.this.apply("{d.getYear()}", "d", date)).isEqualTo("2024");
            assertThat(ReflectPlaceholdersTest.this.apply("{d.getMonthValue()}", "d", date)).isEqualTo("6");
            assertThat(ReflectPlaceholdersTest.this.apply("{d.getDayOfMonth()}", "d", date)).isEqualTo("15");
        }

        @Test
        @DisplayName("BigDecimal methods")
        void bigDecimalMethods() {
            BigDecimal bd = new BigDecimal("123.456");
            assertThat(ReflectPlaceholdersTest.this.apply("{n.intValue()}", "n", bd)).isEqualTo("123");
            assertThat(ReflectPlaceholdersTest.this.apply("{n.scale()}", "n", bd)).isEqualTo("3");
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("empty string value")
        void emptyStringValue() {
            String result = ReflectPlaceholdersTest.this.apply("{text.length()}", "text", "");
            assertThat(result).isEqualTo("0");
        }

        @Test
        @DisplayName("string with special characters")
        void stringWithSpecialChars() {
            String result = ReflectPlaceholdersTest.this.apply("{text.length()}", "text", "hello\nworld\ttab");
            assertThat(result).isEqualTo("15");
        }

        @Test
        @DisplayName("zero integer")
        void zeroInteger() {
            TypeTester tester = new TypeTester();
            String result = ReflectPlaceholdersTest.this.apply("{t.intMethod(0)}", "t", tester);
            assertThat(result).isEqualTo("int:0");
        }

        @Test
        @DisplayName("string with unicode")
        void stringWithUnicode() {
            String result = ReflectPlaceholdersTest.this.apply("{text.length()}", "text", "héllo wörld");
            assertThat(result).isEqualTo("11");
        }
    }

    @Nested
    @DisplayName("Built-in methods mixed with reflection")
    class BuiltInMethodsMixed {

        @Test
        @DisplayName("field access then capitalize")
        void fieldThenCapitalize() {
            Person person = new Person("alice", 25);
            String result = ReflectPlaceholdersTest.this.apply("{person.name.capitalize}", "person", person);
            assertThat(result).isEqualTo("Alice");
        }

        @Test
        @DisplayName("method returning string then capitalize")
        void methodThenCapitalize() {
            Person person = new Person("bob", 30);
            String result = ReflectPlaceholdersTest.this.apply("{person.getName().capitalize}", "person", person);
            assertThat(result).isEqualTo("Bob");
        }

        @Test
        @DisplayName("reflection method then capitalizeFully")
        void methodThenCapitalizeFully() {
            String result = ReflectPlaceholdersTest.this.apply("{text.toLowerCase().capitalizeFully}", "text", "HELLO WORLD");
            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("method returning int then plus")
        void methodReturningIntThenPlus() {
            Person person = new Person("Carol", 20);
            String result = ReflectPlaceholdersTest.this.apply("{person.getAge().plus(5)}", "person", person);
            assertThat(result).isEqualTo("25");
        }

        @Test
        @DisplayName("method returning int then minus")
        void methodReturningIntThenMinus() {
            Person person = new Person("Dave", 30);
            String result = ReflectPlaceholdersTest.this.apply("{person.getAge().minus(10)}", "person", person);
            assertThat(result).isEqualTo("20");
        }

        @Test
        @DisplayName("method returning int then multiply")
        void methodReturningIntThenMultiply() {
            Person person = new Person("Eve", 10);
            String result = ReflectPlaceholdersTest.this.apply("{person.getAge().multiply(3)}", "person", person);
            assertThat(result).isEqualTo("30");
        }

        @Test
        @DisplayName("method returning int then divide")
        void methodReturningIntThenDivide() {
            Person person = new Person("Frank", 50);
            String result = ReflectPlaceholdersTest.this.apply("{person.getAge().divide(5)}", "person", person);
            assertThat(result).isEqualTo("10");
        }

        @Test
        @DisplayName("chained arithmetic: plus then multiply")
        void chainedArithmetic() {
            Person person = new Person("Grace", 10);
            String result = ReflectPlaceholdersTest.this.apply("{person.getAge().plus(5).multiply(2)}", "person", person);
            assertThat(result).isEqualTo("30");
        }

        @Test
        @DisplayName("String length() then plus")
        void stringLengthThenPlus() {
            String result = ReflectPlaceholdersTest.this.apply("{text.length().plus(10)}", "text", "hello");
            assertThat(result).isEqualTo("15");
        }

        @Test
        @DisplayName("List size() then multiply")
        void listSizeThenMultiply() {
            List<String> list = new java.util.ArrayList<>(Arrays.asList("a", "b", "c"));
            String result = ReflectPlaceholdersTest.this.apply("{list.size().multiply(10)}", "list", list);
            assertThat(result).isEqualTo("30");
        }

        @Test
        @DisplayName("nested field access then capitalize")
        void nestedFieldThenCapitalize() {
            Person person = new Person("Henry", 40);
            person.address = new Address("paris", "france", 75000);
            String result = ReflectPlaceholdersTest.this.apply("{person.address.city.capitalize}", "person", person);
            assertThat(result).isEqualTo("Paris");
        }

        @Test
        @DisplayName("nested method then built-in")
        void nestedMethodThenBuiltIn() {
            Person person = new Person("Ivy", 25);
            person.address = new Address("london", "uk", 10000);
            String result = ReflectPlaceholdersTest.this.apply("{person.address.format().capitalize}", "person", person);
            assertThat(result).isEqualTo("London, uk 10000");
        }

        @Test
        @DisplayName("trim then capitalize")
        void trimThenCapitalize() {
            String result = ReflectPlaceholdersTest.this.apply("{text.trim().capitalize}", "text", "  hello  ");
            assertThat(result).isEqualTo("Hello");
        }

        @Test
        @DisplayName("Integer field plus context variable")
        void fieldPlusContextVariable() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{person.getAge().plus(bonus)}"))
                .with("person", new Person("Jack", 20))
                .with("bonus", 5)
                .apply();
            assertThat(result).isEqualTo("25");
        }

        @Test
        @DisplayName("uses add alias for plus")
        void usesAddAlias() {
            Person person = new Person("Kate", 15);
            String result = ReflectPlaceholdersTest.this.apply("{person.getAge().add(10)}", "person", person);
            assertThat(result).isEqualTo("25");
        }

        @Test
        @DisplayName("uses subtract alias for minus")
        void usesSubtractAlias() {
            Person person = new Person("Leo", 40);
            String result = ReflectPlaceholdersTest.this.apply("{person.getAge().subtract(15)}", "person", person);
            assertThat(result).isEqualTo("25");
        }

        @Test
        @DisplayName("adds two fields from different objects")
        void addsTwoFields() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{a.getAge().plus(b.getAge())}"))
                .with("a", new Person("Alice", 20))
                .with("b", new Person("Bob", 30))
                .apply();
            assertThat(result).isEqualTo("50");
        }

        @Test
        @DisplayName("subtracts field from field")
        void subtractsFieldFromField() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{older.getAge().minus(younger.getAge())}"))
                .with("older", new Person("Senior", 50))
                .with("younger", new Person("Junior", 20))
                .apply();
            assertThat(result).isEqualTo("30");
        }

        @Test
        @DisplayName("multiplies two fields")
        void multipliesTwoFields() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{a.getAge().multiply(b.getAge())}"))
                .with("a", new Person("A", 5))
                .with("b", new Person("B", 6))
                .apply();
            assertThat(result).isEqualTo("30");
        }

        @Test
        @DisplayName("divides field by field")
        void dividesFieldByField() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{a.getAge().divide(b.getAge())}"))
                .with("a", new Person("A", 100))
                .with("b", new Person("B", 4))
                .apply();
            assertThat(result).isEqualTo("25");
        }

        @Test
        @DisplayName("adds string lengths from two fields")
        void addsStringLengths() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{a.name.length().plus(b.name.length())}"))
                .with("a", new Person("Alice", 20))
                .with("b", new Person("Bob", 30))
                .apply();
            // "Alice".length() + "Bob".length() = 5 + 3 = 8
            assertThat(result).isEqualTo("8");
        }

        @Test
        @DisplayName("adds nested field to another field")
        void addsNestedFields() {
            Person a = new Person("A", 10);
            a.address = new Address("City", "Country", 1000);
            Person b = new Person("B", 20);
            b.address = new Address("Town", "Nation", 2000);

            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{a.address.zipCode.plus(b.address.zipCode)}"))
                .with("a", a)
                .with("b", b)
                .apply();
            assertThat(result).isEqualTo("3000");
        }

        @Test
        @DisplayName("chains operations with multiple field references")
        void chainsWithMultipleFields() {
            String result = ReflectPlaceholdersTest.this.placeholders
                .context(CompiledMessage.of("{a.getAge().plus(b.getAge()).multiply(factor)}"))
                .with("a", new Person("A", 10))
                .with("b", new Person("B", 10))
                .with("factor", 3)
                .apply();
            // (10 + 10) * 3 = 60
            assertThat(result).isEqualTo("60");
        }
    }

    // Helper method to apply placeholders
    private String apply(String template, String varName, Object value) {
        return this.placeholders
            .context(CompiledMessage.of(template))
            .with(varName, value)
            .apply();
    }
}
