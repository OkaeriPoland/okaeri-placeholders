package eu.okaeri.placeholders.ast.visitor;

import eu.okaeri.placeholders.ast.node.*;

/**
 * Visitor interface for AST nodes.
 *
 * @param <T> the return type of visit methods
 */
public interface AstVisitor<T> {

    /**
     * Visit a reference node (root identifier like player, $).
     */
    T visitRef(Ref node);

    /**
     * Visit a call node (field access or method call).
     */
    T visitCall(Call node);

    /**
     * Visit a string literal node.
     */
    T visitStringLiteral(StringLiteral node);

    /**
     * Visit a number literal node.
     */
    T visitNumberLiteral(NumberLiteral node);

    /**
     * Visit a with-default node ({expr|default}).
     */
    T visitWithDefault(WithDefault node);
}
