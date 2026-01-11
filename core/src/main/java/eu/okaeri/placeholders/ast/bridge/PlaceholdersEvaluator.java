package eu.okaeri.placeholders.ast.bridge;

import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.EvaluationContext;
import eu.okaeri.placeholders.ast.EvaluationResult;
import eu.okaeri.placeholders.ast.node.*;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Evaluates AST expressions using the existing Placeholders resolver infrastructure.
 * <p>
 * This class bridges the new AST-based parsing with the existing PlaceholderResolver
 * interface, allowing all existing resolvers to work unchanged.
 * <p>
 * The evaluation is demand-driven: values are only resolved when accessed.
 * This eliminates the need for fast mode (usedFields tracking).
 */
@RequiredArgsConstructor
public class PlaceholdersEvaluator implements AstVisitor<Object>, EvaluationContext {

    private final Map<String, Object> values;
    private final Placeholders placeholders;
    @Getter private final Locale locale;
    @Nullable private final PlaceholderContext legacyContext;

    /**
     * Creates an evaluator with values and placeholder resolvers.
     */
    public static PlaceholdersEvaluator of(Map<String, Object> values, Placeholders placeholders, Locale locale) {
        return new PlaceholdersEvaluator(values, placeholders, locale, null);
    }

    /**
     * Creates an evaluator with values, resolvers, and legacy context (for resolver compatibility).
     */
    public static PlaceholdersEvaluator of(Map<String, Object> values, Placeholders placeholders, Locale locale, PlaceholderContext legacyContext) {
        return new PlaceholdersEvaluator(values, placeholders, locale, legacyContext);
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

        // If no placeholders instance, can't resolve methods
        if (this.placeholders == null) {
            return null;
        }

        // Find resolver
        PlaceholderResolver resolver;
        if (target != null) {
            resolver = this.placeholders.getResolver(target, name);
        } else {
            // For null target, try Object.class (e.g., .or() method)
            resolver = this.placeholders.getResolver(Object.class, name);
        }

        if (resolver == null) {
            // Try fallback resolver
            resolver = this.placeholders.getFallbackResolver();
            if (resolver == null) {
                return null;
            }
        }

        // Create bridge accessor for old resolver interface
        AstMessageFieldAccessor accessor = AstMessageFieldAccessor.of(name, node.getArgs(), node.isHasParens(), this);

        // Invoke resolver
        return resolver.resolve(target, accessor, this.legacyContext);
    }

    @Override
    public Object visitStringLiteral(StringLiteral node) {
        return node.getValue();
    }

    @Override
    public Object visitNumberLiteral(NumberLiteral node) {
        return node.getValue();
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
        // Check if root field is missing (for reporting purposes)
        String rootField = this.getRootFieldName(ast);
        boolean rootMissing = (rootField != null) && !this.values.containsKey(rootField);

        // Evaluate the expression (this allows .or() and other methods to handle null/missing)
        Object result = ast.accept(this);

        // Apply default renderer if available (for types like Duration)
        // Exclude fallback resolver - it's for method calls, not default rendering
        if ((result != null) && (this.placeholders != null)) {
            PlaceholderResolver resolver = this.placeholders.getResolver(result, null);
            if ((resolver != null) && (resolver != this.placeholders.getFallbackResolver())) {
                AstMessageFieldAccessor accessor = AstMessageFieldAccessor.of("", Collections.emptyList(), this);
                result = resolver.resolve(result, accessor, this.legacyContext);
            }
        }

        if (result == null) {
            // Distinguish between "field missing" and "field exists but resolved to null"
            if (rootMissing) {
                return new EvaluationResult.MissingValue(rootField, expression);
            }
            return new EvaluationResult.NullValue(expression);
        }

        return new EvaluationResult.Value(result, expression);
    }

    /**
     * Extracts the root field name from an AST node.
     * For example: {player.name} → "player", {value} → "value"
     */
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
