package eu.okaeri.placeholders.ast.node;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.SourceSpan;
import eu.okaeri.placeholders.ast.visitor.AstVisitor;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A call node representing field access or method call.
 * <p>
 * In this AST, there's no distinction between field access and method calls.
 * Both {player.name} and {player.name()} produce the same AST:
 * Call(target=Ref("player"), name="name", args=[])
 * <p>
 * The resolver decides at runtime whether it's a field or method.
 * <p>
 * Examples:
 * - {player.name} → Call(Ref("player"), "name", [])
 * - {str.replace(a,b)} → Call(Ref("str"), "replace", [Ref("a"), Ref("b")])
 * - {$.if(cond, a, b)} → Call(Ref("$"), "if", [Ref("cond"), Ref("a"), Ref("b")])
 */
@Value
public class Call implements AstNode {

    @NonNull AstNode target;
    @NonNull String name;
    @NonNull List<AstNode> args;
    SourceSpan sourceSpan;

    public static Call of(AstNode target, String name) {
        return new Call(target, name, Collections.emptyList(), null);
    }

    public static Call of(AstNode target, String name, List<AstNode> args) {
        return new Call(target, name, (args != null) ? args : Collections.emptyList(), null);
    }

    public static Call of(AstNode target, String name, List<AstNode> args, SourceSpan span) {
        return new Call(target, name, (args != null) ? args : Collections.emptyList(), span);
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitCall(this);
    }

    @Override
    public List<AstNode> children() {
        List<AstNode> children = new ArrayList<>(1 + this.args.size());
        children.add(this.target);
        children.addAll(this.args);
        return children;
    }

    @Override
    public SourceSpan sourceSpan() {
        return this.sourceSpan;
    }

    @Override
    public String toString() {
        if (this.args.isEmpty()) {
            return "Call(" + this.target + "." + this.name + ")";
        }
        return "Call(" + this.target + "." + this.name + "(" + this.args + "))";
    }
}
