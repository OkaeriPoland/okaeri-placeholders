package eu.okaeri.placeholders.ast.node;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * A reference to a root identifier (e.g., player, $, active).
 * <p>
 * This is the starting point of an expression chain.
 * Examples: {player}, {$}, {name}
 */
@Value
public class Ref implements AstNode {

    @NonNull String name;
    SourceSpan sourceSpan;

    public static Ref of(String name) {
        return new Ref(name, null);
    }

    public static Ref of(String name, SourceSpan span) {
        return new Ref(name, span);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitRef(this);
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
        return "Ref(" + this.name + ")";
    }
}
