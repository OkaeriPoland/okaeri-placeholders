package eu.okaeri.placeholders.ast.bridge;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.EvaluationContext;
import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.message.part.MessageFieldAccessor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

/**
 * Adapts AST call information to the {@link MessageFieldAccessor} interface.
 * <p>
 * This allows {@link eu.okaeri.placeholders.schema.resolver.PlaceholderResolver} implementations
 * to access method name, arguments, and locale during AST evaluation.
 */
@RequiredArgsConstructor
public class AstMessageFieldAccessor implements MessageFieldAccessor {

    private final String methodName;
    private final List<AstNode> args;
    private final boolean hasParens;
    private final EvaluationContext ctx;
    private AstFieldParams cachedParams;

    /**
     * Creates an accessor for an AST Call node (with parentheses).
     */
    public static AstMessageFieldAccessor of(String methodName, List<AstNode> args, EvaluationContext ctx) {
        return new AstMessageFieldAccessor(methodName, args, true, ctx);
    }

    /**
     * Creates an accessor for an AST Call node with explicit parens flag.
     *
     * @param hasParens true for method calls like foo(), false for field access like foo.bar
     */
    public static AstMessageFieldAccessor of(String methodName, List<AstNode> args, boolean hasParens, EvaluationContext ctx) {
        return new AstMessageFieldAccessor(methodName, args, hasParens, ctx);
    }

    @Override
    public Locale locale() {
        return this.ctx.locale();
    }

    @Override
    public FieldParams params() {
        if (this.cachedParams == null) {
            this.cachedParams = AstFieldParams.of(this.methodName, this.args, this.hasParens, this.ctx);
        }
        return this.cachedParams;
    }
}
