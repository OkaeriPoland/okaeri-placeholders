package eu.okaeri.placeholders.ast.node;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import lombok.NonNull;
import lombok.Value;

import java.util.Arrays;
import java.util.List;

/**
 * A node representing an expression with a default value.
 * <p>
 * Example: {player.name|Guest} produces WithDefault(Call(...), StringLiteral("Guest"))
 */
@Value
public class WithDefault implements AstNode {

    @NonNull AstNode expression;
    @NonNull AstNode defaultValue;
    SourceSpan sourceSpan;

    public static WithDefault of(AstNode expression, AstNode defaultValue) {
        return new WithDefault(expression, defaultValue, null);
    }

    public static WithDefault of(AstNode expression, AstNode defaultValue, SourceSpan span) {
        return new WithDefault(expression, defaultValue, span);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitWithDefault(this);
    }

    @Override
    public List<AstNode> children() {
        return Arrays.asList(this.expression, this.defaultValue);
    }

    @Override
    public SourceSpan sourceSpan() {
        return this.sourceSpan;
    }

    @Override
    public String toString() {
        return "WithDefault(" + this.expression + " | " + this.defaultValue + ")";
    }
}
