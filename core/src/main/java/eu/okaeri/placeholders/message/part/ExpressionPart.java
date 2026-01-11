package eu.okaeri.placeholders.message.part;

import eu.okaeri.placeholders.ast.AstNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A message part containing a parsed AST expression.
 * <p>
 * This is the AST-based replacement for MessageField.
 * The AST is parsed once at compile time and evaluated on demand.
 */
@Getter
@RequiredArgsConstructor
public class ExpressionPart implements MessageElement {

    /**
     * The original raw expression string (without braces, before transformation).
     * Used as the key in renderFieldValues() for backward compatibility.
     */
    private final String originalRaw;

    /**
     * The transformed raw expression string (after legacy syntax transformation).
     */
    private final String raw;

    /**
     * The parsed AST for this expression.
     */
    private final AstNode ast;

    /**
     * Optional default value (from | syntax).
     */
    private final String defaultValue;

    public static ExpressionPart of(String raw, AstNode ast) {
        return new ExpressionPart(raw, raw, ast, null);
    }

    public static ExpressionPart of(String raw, AstNode ast, String defaultValue) {
        return new ExpressionPart(raw, raw, ast, defaultValue);
    }

    public static ExpressionPart of(String originalRaw, String raw, AstNode ast, String defaultValue) {
        return new ExpressionPart(originalRaw, raw, ast, defaultValue);
    }

    @Override
    public String toString() {
        return "ExpressionPart{" + this.raw + "}";
    }
}
