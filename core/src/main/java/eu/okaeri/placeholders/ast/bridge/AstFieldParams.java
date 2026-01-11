package eu.okaeri.placeholders.ast.bridge;

import eu.okaeri.placeholders.ast.AstNode;
import eu.okaeri.placeholders.ast.EvaluationContext;
import eu.okaeri.placeholders.ast.node.NumberLiteral;
import eu.okaeri.placeholders.ast.node.Ref;
import eu.okaeri.placeholders.ast.node.StringLiteral;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.message.part.ParsedArg;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Bridge class that provides FieldParams-like access to AST arguments.
 * <p>
 * This allows existing PlaceholderResolver implementations to work with the new
 * AST-based system without modification. The AST nodes are evaluated on-demand
 * when arguments are accessed.
 */
public class AstFieldParams extends FieldParams {

    private final List<AstNode> args;
    private final boolean hasParens;
    private final EvaluationContext ctx;

    private AstFieldParams(String fieldName, List<AstNode> args, boolean hasParens, EvaluationContext ctx) {
        super(fieldName, new String[0], new ParsedArg[0]);
        this.args = args;
        this.hasParens = hasParens;
        this.ctx = ctx;
    }

    /**
     * Creates AstFieldParams wrapping AST argument nodes.
     */
    public static AstFieldParams of(String fieldName, List<AstNode> args, boolean hasParens, EvaluationContext ctx) {
        return new AstFieldParams(fieldName, args, hasParens, ctx);
    }

    @Override
    public int length() {
        return this.args.size();
    }

    @Override
    public String[] strArr() {
        String[] arr = new String[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.strAt(i, "");
        }
        return arr;
    }

    @Override
    public int[] intArr() {
        int[] arr = new int[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.intAt(i, 0);
        }
        return arr;
    }

    @Override
    public double[] doubleArr() {
        double[] arr = new double[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.doubleAt(i, 0);
        }
        return arr;
    }

    @Override
    public String strAt(int pos) {
        return this.strAt(pos, "");
    }

    @Override
    public String strAt(int pos, String def) {
        if (pos >= this.args.size()) return def;

        AstNode arg = this.args.get(pos);
        Object result = this.ctx.evaluate(arg);

        // If result is non-null, use it
        if (result != null) {
            return result.toString();
        }

        // Fallback for unresolved refs: use the identifier name itself as the string value
        // This enables backward compat: {s.replace(_,-)} where - is not a field but a literal
        if (arg instanceof Ref) {
            return ((Ref) arg).getName();
        }

        return def;
    }

    @Override
    public double doubleAt(int pos) {
        return this.doubleAt(pos, 0);
    }

    @Override
    public double doubleAt(int pos, double def) {
        if (pos >= this.args.size()) return def;
        return this.ctx.evaluateDouble(this.args.get(pos), def);
    }

    @Override
    public int intAt(int pos) {
        return this.intAt(pos, 0);
    }

    @Override
    public int intAt(int pos, int def) {
        if (pos >= this.args.size()) return def;
        return this.ctx.evaluateInt(this.args.get(pos), def);
    }

    @Override
    public ParsedArg parsedAt(int pos) {
        // For AST args, we create a synthetic ParsedArg on-demand
        if (pos >= this.args.size()) return null;
        Object value = this.ctx.evaluate(this.args.get(pos));
        String strValue = (value != null) ? value.toString() : null;
        // All AST args are effectively field refs (evaluated on demand)
        return ParsedArg.fieldRefOrLiteral(strValue);
    }

    /**
     * Resolves an argument at the specified position by evaluating the AST node.
     * This is the core method used by global functions like coalesce, if, etc.
     * <p>
     * Resolution logic:
     * - String literals return their value directly
     * - For Ref nodes:
     * - If the field exists in context (even if null), return the evaluated value
     * - If the field doesn't exist, fall back to the ref name (backward compat for literals)
     * - Other expressions are evaluated normally
     */
    @Override
    @Nullable
    public Object resolveArg(int pos, @Nullable Locale locale, @Nullable PlaceholderContext context) {
        if (pos >= this.args.size()) return null;

        AstNode arg = this.args.get(pos);

        // String literals return their value directly
        if (arg instanceof StringLiteral) {
            return ((StringLiteral) arg).getValue();
        }

        // For Ref nodes, check if field exists in context
        if (arg instanceof Ref) {
            String refName = ((Ref) arg).getName();
            // If field exists in context (even if null), return evaluated value
            if (this.ctx.hasValue(refName)) {
                return this.ctx.evaluate(arg);
            }
            // Field not found - treat ref name as literal string (backward compat)
            return refName;
        }

        // For other expressions (Call, etc.), evaluate normally
        return this.ctx.evaluate(arg);
    }

    @Override
    public String[] getParams() {
        // For backward compatibility with ReflectResolver:
        // - Empty args with parens (foo()) → return [""] to indicate no-arg method call
        // - Empty args without parens (foo.bar) → return [] for field access
        // - With args → return raw-format strings
        if (this.args.isEmpty()) {
            if (this.hasParens) {
                return new String[]{""};
            }
            return new String[0];
        }
        // Return raw-format strings for ReflectResolver compatibility
        // ReflectResolver uses raw format to detect types: 'a' → String, 123 → int, etc.
        return this.rawStrArr();
    }

    /**
     * Returns arguments in raw format for backward compatibility with ReflectResolver.
     * <ul>
     *   <li>StringLiteral → 'value' (single-quoted)</li>
     *   <li>NumberLiteral → number as string</li>
     *   <li>Ref → identifier name</li>
     *   <li>Other → evaluated string</li>
     * </ul>
     */
    private String[] rawStrArr() {
        String[] arr = new String[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.toRawString(this.args.get(i));
        }
        return arr;
    }

    /**
     * Converts an AST node to its raw string representation for ReflectResolver.
     */
    private String toRawString(AstNode node) {
        if (node instanceof StringLiteral) {
            // Return with single quotes so ReflectResolver detects as String type
            return "'" + ((StringLiteral) node).getValue() + "'";
        }
        if (node instanceof NumberLiteral) {
            Number num = ((NumberLiteral) node).getValue();
            return num.toString();
        }
        if (node instanceof Ref) {
            return ((Ref) node).getName();
        }
        // For complex expressions, evaluate and return as string
        Object result = this.ctx.evaluate(node);
        return (result != null) ? result.toString() : "";
    }

    @Override
    public ParsedArg[] getParsedParams() {
        ParsedArg[] arr = new ParsedArg[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.parsedAt(i);
        }
        return arr;
    }
}
