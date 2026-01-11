package eu.okaeri.placeholders.ast;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.ast.node.*;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.resolver.PlaceholderResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Evaluates AST expressions using the Placeholders resolver infrastructure.
 * <p>
 * This bridges AST-based parsing with PlaceholderResolver, allowing all existing
 * resolvers to work unchanged. Evaluation is demand-driven: values are only
 * resolved when accessed.
 */
@RequiredArgsConstructor
public class ExpressionEvaluator implements AstVisitor<Object>, EvaluationContext {

    private final Map<String, Object> values;
    private final Placeholders placeholders;
    @Getter private final Locale locale;
    @Nullable private final PlaceholderContext legacyContext;

    /**
     * Creates an evaluator with values and placeholder resolvers.
     */
    public static ExpressionEvaluator of(Map<String, Object> values, Placeholders placeholders, Locale locale) {
        return new ExpressionEvaluator(values, placeholders, locale, null);
    }

    /**
     * Creates an evaluator with values, resolvers, and context.
     */
    public static ExpressionEvaluator of(Map<String, Object> values, Placeholders placeholders, Locale locale, PlaceholderContext context) {
        return new ExpressionEvaluator(values, placeholders, locale, context);
    }

    // === AstVisitor implementation ===

    @Override
    public Object visitRef(Ref node) {
        return this.values.get(node.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitCall(Call node) {
        Object target = node.getTarget().accept(this);
        String name = node.getName();

        if (this.placeholders == null) {
            return null;
        }

        PlaceholderResolver resolver;
        if (target != null) {
            resolver = this.placeholders.getResolver(target, name);
        } else {
            resolver = this.placeholders.getResolver(Object.class, name);
        }

        if (resolver == null) {
            resolver = this.placeholders.getFallbackResolver();
            if (resolver == null) {
                return null;
            }
        }

        FieldParams params = FieldParams.of(name, node.getArgs(), node.isHasParens(), this);
        return resolver.resolve(target, params, this.legacyContext);
    }

    @Override
    public Object visitStringLiteral(StringLiteral node) {
        return new LiteralValue(node.getValue());
    }

    @Override
    public Object visitNumberLiteral(NumberLiteral node) {
        return new LiteralValue(node.getValue());
    }

    @Override
    public Object visitWithDefault(WithDefault node) {
        Object value = node.getExpression().accept(this);
        if (value == null) {
            return node.getDefaultValue().accept(this);
        }
        return value;
    }

    // === EvaluationContext implementation ===

    @Override
    @Nullable
    public Object evaluate(AstNode node) {
        return node.accept(this);
    }

    @Override
    @Nullable
    public Object getValue(String name) {
        return this.values.get(name);
    }

    @Override
    public boolean hasValue(String name) {
        return this.values.containsKey(name);
    }

    @Override
    public Locale locale() {
        return this.locale;
    }

    /**
     * Evaluates an AST expression and returns a typed result indicating
     * success, null, or missing field.
     *
     * @param ast        The AST to evaluate
     * @param expression The original expression string (for error messages)
     * @return EvaluationResult indicating the outcome
     */
    @SuppressWarnings("unchecked")
    public EvaluationResult evaluateToResult(AstNode ast, String expression) {
        String rootField = this.getRootFieldName(ast);
        boolean rootMissing = (rootField != null) && !this.values.containsKey(rootField);

        Object result = ast.accept(this);

        // Check if result came from a literal before any further processing
        boolean isLiteral = LiteralValue.isLiteral(result);
        result = LiteralValue.unwrap(result);

        // Apply default renderer if available
        if ((result != null) && (this.placeholders != null)) {
            Object unwrapped = LiteralValue.unwrap(result);
            PlaceholderResolver resolver = this.placeholders.getResolver(unwrapped, null);
            if ((resolver != null) && (resolver != this.placeholders.getFallbackResolver())) {
                FieldParams params = FieldParams.of("", Collections.emptyList(), this);
                result = resolver.resolve(unwrapped, params, this.legacyContext);
                // After resolver processing, it's no longer a raw literal
                isLiteral = false;
            }
        }

        if (result == null) {
            if (rootMissing) {
                return new EvaluationResult.MissingValue(rootField, expression);
            }
            return new EvaluationResult.NullValue(expression);
        }

        return new EvaluationResult.Value(result, expression, isLiteral);
    }

    @Nullable
    private String getRootFieldName(AstNode node) {
        if (node instanceof Ref) {
            return ((Ref) node).getName();
        } else if (node instanceof Call) {
            return this.getRootFieldName(((Call) node).getTarget());
        } else if (node instanceof WithDefault) {
            return this.getRootFieldName(((WithDefault) node).getExpression());
        }
        return null;
    }
}
