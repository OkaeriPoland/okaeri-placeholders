package eu.okaeri.placeholders.ast;

import eu.okaeri.placeholders.ast.visitor.AstVisitor;

import java.util.List;

/**
 * Base interface for all AST nodes in the placeholder expression language.
 */
public interface AstNode {

    /**
     * Accepts a visitor for processing this node.
     *
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitor
     * @return the result of visiting this node
     */
    <T> T accept(AstVisitor<T> visitor);

    /**
     * Returns the child nodes of this node.
     *
     * @return list of child nodes (may be empty, never null)
     */
    List<AstNode> children();

    /**
     * Returns the source span of this node (position in original source).
     *
     * @return the source span, or null if not available
     */
    SourceSpan sourceSpan();
}
