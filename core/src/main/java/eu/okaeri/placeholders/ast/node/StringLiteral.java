package eu.okaeri.placeholders.ast.node;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * A string literal node.
 * <p>
 * Examples: "hello", 'world'
 */
@Value
public class StringLiteral implements AstNode {

    @NonNull String value;
    SourceSpan sourceSpan;

    public static StringLiteral of(String value) {
        return new StringLiteral(value, null);
    }

    public static StringLiteral of(String value, SourceSpan span) {
        return new StringLiteral(value, span);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitStringLiteral(this);
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
        return "StringLiteral(\"" + this.value + "\")";
    }
}
