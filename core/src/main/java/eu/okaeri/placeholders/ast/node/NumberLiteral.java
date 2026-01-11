package eu.okaeri.placeholders.ast.node;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * A number literal node.
 * <p>
 * Examples: 123, 45.67, -10
 */
@Value
public class NumberLiteral implements AstNode {

    @NonNull Number value;
    SourceSpan sourceSpan;

    public static NumberLiteral of(int value) {
        return new NumberLiteral(value, null);
    }

    public static NumberLiteral of(double value) {
        return new NumberLiteral(value, null);
    }

    public static NumberLiteral of(Number value) {
        return new NumberLiteral(value, null);
    }

    public static NumberLiteral of(Number value, SourceSpan span) {
        return new NumberLiteral(value, span);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitNumberLiteral(this);
    }

    @Override
    public List<AstNode> children() {
        return Collections.emptyList();
    }

    @Override
    public SourceSpan sourceSpan() {
        return this.sourceSpan;
    }

    @Override
    public String toString() {
        return "NumberLiteral(" + this.value + ")";
    }
}
