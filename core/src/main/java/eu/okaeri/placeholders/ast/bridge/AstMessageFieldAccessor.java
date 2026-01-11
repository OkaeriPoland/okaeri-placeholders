package eu.okaeri.placeholders.ast.bridge;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.EvaluationContext;
import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.message.part.MessageField;
import eu.okaeri.placeholders.message.part.MessageFieldAccessor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

/**
 * Bridge class that adapts AST call information to the MessageFieldAccessor interface.
 * <p>
 * This allows existing PlaceholderResolver implementations that expect MessageFieldAccessor
 * to work with the new AST-based evaluation system.
 */
@RequiredArgsConstructor
public class AstMessageFieldAccessor implements MessageFieldAccessor {

    private final String methodName;
    private final List<AstNode> args;
    private final boolean hasParens;
    private final EvaluationContext ctx;
    private AstFieldParams cachedParams;

    /**
     * Creates an accessor for an AST Call node.
     */
    public static AstMessageFieldAccessor of(String methodName, List<AstNode> args, EvaluationContext ctx) {
        return new AstMessageFieldAccessor(methodName, args, true, ctx);
    }

    /**
     * Creates an accessor for an AST Call node with explicit parens flag.
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

    @Override
    public MessageField unsafe() {
        // AST-based evaluation doesn't use MessageField
        // Return null or throw - resolvers that need raw MessageField won't work with AST
        throw new UnsupportedOperationException(
            "MessageField.unsafe() is not available in AST-based evaluation. " +
                "Resolver should use params() instead."
        );
    }
}
