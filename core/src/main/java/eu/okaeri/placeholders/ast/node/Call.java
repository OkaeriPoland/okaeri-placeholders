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
 * The {@code hasParens} flag distinguishes between field access and method calls:
 * - {player.name} → Call(target=Ref("player"), name="name", args=[], hasParens=false)
 * - {player.name()} → Call(target=Ref("player"), name="name", args=[], hasParens=true)
 * <p>
 * This is needed for backward compatibility with ReflectResolver which checks
 * params.length to distinguish field access from no-arg method calls.
 * <p>
 * Examples:
 * - {player.name} → Call(Ref("player"), "name", [], false)
 * - {player.name()} → Call(Ref("player"), "name", [], true)
 * - {str.replace(a,b)} → Call(Ref("str"), "replace", [Ref("a"), Ref("b")], true)
 * - {$.if(cond, a, b)} → Call(Ref("$"), "if", [Ref("cond"), Ref("a"), Ref("b")], true)
 */
@Value
public class Call implements AstNode {

    @NonNull AstNode target;
    @NonNull String name;
    @NonNull List<AstNode> args;
    boolean hasParens;
    SourceSpan sourceSpan;

    public static Call of(AstNode target, String name) {
        return new Call(target, name, Collections.emptyList(), false, null);
    }

    public static Call of(AstNode target, String name, List<AstNode> args) {
        return new Call(target, name, (args != null) ? args : Collections.emptyList(), args != null, null);
    }

    public static Call of(AstNode target, String name, boolean hasParens) {
        return new Call(target, name, Collections.emptyList(), hasParens, null);
    }

    public static Call of(AstNode target, String name, List<AstNode> args, boolean hasParens) {
        return new Call(target, name, (args != null) ? args : Collections.emptyList(), hasParens, null);
    }

    public static Call of(AstNode target, String name, List<AstNode> args, SourceSpan span) {
        return new Call(target, name, (args != null) ? args : Collections.emptyList(), args != null, span);
    }

    public static Call of(AstNode target, String name, List<AstNode> args, boolean hasParens, SourceSpan span) {
        return new Call(target, name, (args != null) ? args : Collections.emptyList(), hasParens, span);
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
