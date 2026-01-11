package eu.okaeri.placeholders.ast.visitor;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.EvaluationContext;
import eu.okaeri.placeholders.ast.Resolver;
import eu.okaeri.placeholders.ast.node.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * Evaluates an AST to produce a value.
 * <p>
 * This visitor walks the AST and resolves references, applies method calls,
 * and returns the final value. It implements {@link EvaluationContext} so
 * resolvers can use it to evaluate their arguments.
 */
@RequiredArgsConstructor
public class EvaluationVisitor implements AstVisitor<Object>, EvaluationContext {

    private final Map<String, Object> values;
    private final ResolverRegistry resolvers;
    private final Locale locale;

    /**
     * Functional interface for looking up resolvers.
     */
    @FunctionalInterface
    public interface ResolverRegistry {
        /**
         * Gets a resolver for the given target class and method name.
         *
         * @param targetClass the class of the target object (or Object.class for null)
         * @param methodName  the method/field name
         * @return the resolver, or null if not found
         */
        @Nullable
        Resolver<Object> getResolver(Class<?> targetClass, String methodName);
    }

    // === AstVisitor implementation ===

    @Override
    public Object visitRef(Ref node) {
        return this.values.get(node.getName());
    }

    @Override
    public Object visitCall(Call node) {
        Object target = node.getTarget().accept(this);
        String name = node.getName();

        // Find resolver
        Class<?> targetClass = (target != null) ? target.getClass() : Object.class;
        Resolver<Object> resolver = this.resolvers.getResolver(targetClass, name);

        if (resolver == null) {
            // No resolver found - return null
            return null;
        }

        // Invoke resolver with AST args directly
        return resolver.resolve(target, node.getArgs(), this);
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
}
