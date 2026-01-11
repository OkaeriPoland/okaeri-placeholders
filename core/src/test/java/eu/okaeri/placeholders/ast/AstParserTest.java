package eu.okaeri.placeholders.ast;

import eu.okaeri.placeholders.ast.node.Call;
import eu.okaeri.placeholders.ast.node.Ref;
import eu.okaeri.placeholders.ast.node.StringLiteral;
import eu.okaeri.placeholders.ast.node.WithDefault;
import eu.okaeri.placeholders.ast.parser.ExpressionParser;
import eu.okaeri.placeholders.ast.visitor.EvaluationVisitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AST Parser")
class AstParserTest {

    @Nested
    @DisplayName("Parsing")
    class Parsing {

        @Test
        void shouldParseSimpleRef() {
            AstNode ast = new ExpressionParser("player").parse();

            assertThat(ast).isInstanceOf(Ref.class);
            assertThat(((Ref) ast).getName()).isEqualTo("player");
        }

        @Test
        void shouldParseFieldAccess() {
            AstNode ast = new ExpressionParser("player.name").parse();

            assertThat(ast).isInstanceOf(Call.class);
            Call call = (Call) ast;
            assertThat(call.getName()).isEqualTo("name");
            assertThat(call.getTarget()).isInstanceOf(Ref.class);
            assertThat(((Ref) call.getTarget()).getName()).isEqualTo("player");
            assertThat(call.getArgs()).isEmpty();
        }

        @Test
        void shouldParseMethodCall() {
            AstNode ast = new ExpressionParser("name.toUpperCase()").parse();

            assertThat(ast).isInstanceOf(Call.class);
            Call call = (Call) ast;
            assertThat(call.getName()).isEqualTo("toUpperCase");
            assertThat(call.getArgs()).isEmpty();
        }

        @Test
        void shouldParseMethodWithArgs() {
            AstNode ast = new ExpressionParser("str.replace(\"a\", \"b\")").parse();

            assertThat(ast).isInstanceOf(Call.class);
            Call call = (Call) ast;
            assertThat(call.getName()).isEqualTo("replace");
            assertThat(call.getArgs()).hasSize(2);
            assertThat(call.getArgs().get(0)).isInstanceOf(StringLiteral.class);
            assertThat(((StringLiteral) call.getArgs().get(0)).getValue()).isEqualTo("a");
        }

        @Test
        void shouldParseChainedCalls() {
            AstNode ast = new ExpressionParser("player.name.toUpperCase").parse();

            assertThat(ast).isInstanceOf(Call.class);
            Call outer = (Call) ast;
            assertThat(outer.getName()).isEqualTo("toUpperCase");

            assertThat(outer.getTarget()).isInstanceOf(Call.class);
            Call inner = (Call) outer.getTarget();
            assertThat(inner.getName()).isEqualTo("name");

            assertThat(inner.getTarget()).isInstanceOf(Ref.class);
            assertThat(((Ref) inner.getTarget()).getName()).isEqualTo("player");
        }

        @Test
        void shouldParseNestedArgs() {
            // $.if(active, primary.or(secondary), fallback)
            AstNode ast = new ExpressionParser("$.if(active, primary.or(secondary), fallback)").parse();

            assertThat(ast).isInstanceOf(Call.class);
            Call ifCall = (Call) ast;
            assertThat(ifCall.getName()).isEqualTo("if");
            assertThat(ifCall.getArgs()).hasSize(3);

            // First arg: active (Ref)
            assertThat(ifCall.getArgs().get(0)).isInstanceOf(Ref.class);

            // Second arg: primary.or(secondary) (Call)
            assertThat(ifCall.getArgs().get(1)).isInstanceOf(Call.class);
            Call orCall = (Call) ifCall.getArgs().get(1);
            assertThat(orCall.getName()).isEqualTo("or");

            // Third arg: fallback (Ref)
            assertThat(ifCall.getArgs().get(2)).isInstanceOf(Ref.class);
        }

        @Test
        void shouldParseWithDefault() {
            AstNode ast = new ExpressionParser("player.name|Guest").parse();

            assertThat(ast).isInstanceOf(WithDefault.class);
            WithDefault wd = (WithDefault) ast;
            assertThat(wd.getExpression()).isInstanceOf(Call.class);
            assertThat(wd.getDefaultValue()).isInstanceOf(Ref.class);
        }

        @Test
        void shouldParseStringLiteral() {
            AstNode ast = new ExpressionParser("\"hello world\"").parse();

            assertThat(ast).isInstanceOf(StringLiteral.class);
            assertThat(((StringLiteral) ast).getValue()).isEqualTo("hello world");
        }

        @Test
        void shouldParseNumericIdentifier() {
            // Numbers are parsed as identifiers (Ref) - evaluated at runtime
            AstNode ast = new ExpressionParser("42").parse();

            assertThat(ast).isInstanceOf(Ref.class);
            assertThat(((Ref) ast).getName()).isEqualTo("42");
        }

        @Test
        void shouldParseDecimalIdentifier() {
            // Decimal numbers are also identifiers - evaluated at runtime
            AstNode ast = new ExpressionParser("3.14").parse();

            assertThat(ast).isInstanceOf(Ref.class);
            assertThat(((Ref) ast).getName()).isEqualTo("3.14");
        }

        @Test
        void shouldParseDeeplyNested() {
            // $.if(active, primary.or(secondary.replace("_", "-")), fallback)
            AstNode ast = new ExpressionParser("$.if(active, primary.or(secondary.replace(\"_\", \"-\")), fallback)").parse();

            assertThat(ast).isInstanceOf(Call.class);
            Call ifCall = (Call) ast;

            // Get the .or() call
            Call orCall = (Call) ifCall.getArgs().get(1);
            assertThat(orCall.getName()).isEqualTo("or");

            // Get the .replace() call inside .or()
            Call replaceCall = (Call) orCall.getArgs().get(0);
            assertThat(replaceCall.getName()).isEqualTo("replace");
            assertThat(replaceCall.getArgs()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Evaluation")
    class Evaluation {

        @Test
        void shouldEvaluateSimpleRef() {
            Map<String, Object> values = Map.of("name", "Alice");
            EvaluationVisitor visitor = new EvaluationVisitor(values, (c, n) -> null, Locale.ROOT);

            AstNode ast = new ExpressionParser("name").parse();
            Object result = ast.accept(visitor);

            assertThat(result).isEqualTo("Alice");
        }

        @Test
        void shouldEvaluateStringLiteral() {
            EvaluationVisitor visitor = new EvaluationVisitor(Map.of(), (c, n) -> null, Locale.ROOT);

            AstNode ast = new ExpressionParser("\"hello\"").parse();
            Object result = ast.accept(visitor);

            assertThat(result).isEqualTo("hello");
        }

        @Test
        void shouldEvaluateWithDefault() {
            Map<String, Object> values = new HashMap<>();
            values.put("name", null);
            values.put("Guest", "Guest");

            EvaluationVisitor visitor = new EvaluationVisitor(values, (c, n) -> null, Locale.ROOT);

            AstNode ast = new ExpressionParser("name|Guest").parse();
            Object result = ast.accept(visitor);

            assertThat(result).isEqualTo("Guest");
        }

        @Test
        void shouldEvaluateMethodCall() {
            Map<String, Object> values = Map.of("name", "alice");

            // Register toUpperCase resolver
            Resolver<String> toUpperCase = (target, args, ctx) -> target.toUpperCase();

            EvaluationVisitor.ResolverRegistry registry = (targetClass, methodName) -> {
                if ((targetClass == String.class) && "toUpperCase".equals(methodName)) {
                    return (Resolver<Object>) (Object) toUpperCase;
                }
                return null;
            };

            EvaluationVisitor visitor = new EvaluationVisitor(values, registry, Locale.ROOT);

            AstNode ast = new ExpressionParser("name.toUpperCase").parse();
            Object result = ast.accept(visitor);

            assertThat(result).isEqualTo("ALICE");
        }

        @Test
        void shouldEvaluateMethodWithArgs() {
            Map<String, Object> values = Map.of("name", "hello_world");

            // Register replace resolver
            Resolver<String> replace = (target, args, ctx) -> {
                String search = ctx.evaluateString(args.get(0), "");
                String replacement = ctx.evaluateString(args.get(1), "");
                return target.replace(search, replacement);
            };

            EvaluationVisitor.ResolverRegistry registry = (targetClass, methodName) -> {
                if ((targetClass == String.class) && "replace".equals(methodName)) {
                    return (Resolver<Object>) (Object) replace;
                }
                return null;
            };

            EvaluationVisitor visitor = new EvaluationVisitor(values, registry, Locale.ROOT);

            AstNode ast = new ExpressionParser("name.replace(\"_\", \"-\")").parse();
            Object result = ast.accept(visitor);

            assertThat(result).isEqualTo("hello-world");
        }

        @Test
        void shouldEvaluateNestedCalls() {
            Map<String, Object> values = Map.of("name", "hello_world");

            Resolver<String> replace = (target, args, ctx) -> {
                String search = ctx.evaluateString(args.get(0), "");
                String replacement = ctx.evaluateString(args.get(1), "");
                return target.replace(search, replacement);
            };

            Resolver<String> toUpperCase = (target, args, ctx) -> target.toUpperCase();

            EvaluationVisitor.ResolverRegistry registry = (targetClass, methodName) -> {
                if (targetClass == String.class) {
                    if ("replace".equals(methodName)) return (Resolver<Object>) (Object) replace;
                    if ("toUpperCase".equals(methodName)) return (Resolver<Object>) (Object) toUpperCase;
                }
                return null;
            };

            EvaluationVisitor visitor = new EvaluationVisitor(values, registry, Locale.ROOT);

            AstNode ast = new ExpressionParser("name.replace(\"_\", \"-\").toUpperCase").parse();
            Object result = ast.accept(visitor);

            assertThat(result).isEqualTo("HELLO-WORLD");
        }

        @Test
        void shouldEvaluateArgAsFieldRef() {
            Map<String, Object> values = new HashMap<>();
            values.put("primary", null);
            values.put("secondary", "backup");

            Resolver<Object> or = (target, args, ctx) -> {
                if (target != null) return target;
                return ctx.evaluate(args.get(0));
            };

            EvaluationVisitor.ResolverRegistry registry = (targetClass, methodName) -> {
                if ("or".equals(methodName)) return or;
                return null;
            };

            EvaluationVisitor visitor = new EvaluationVisitor(values, registry, Locale.ROOT);

            AstNode ast = new ExpressionParser("primary.or(secondary)").parse();
            Object result = ast.accept(visitor);

            assertThat(result).isEqualTo("backup");
        }
    }
}
