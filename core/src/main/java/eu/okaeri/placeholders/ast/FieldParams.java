package eu.okaeri.placeholders.ast;

import eu.okaeri.placeholders.ast.node.NumberLiteral;
import eu.okaeri.placeholders.ast.node.Ref;
import eu.okaeri.placeholders.ast.node.StringLiteral;
import eu.okaeri.placeholders.context.PlaceholderContext;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Provides access to method call parameters for placeholder resolution.
 * <p>
 * This is passed to {@link eu.okaeri.placeholders.resolver.PlaceholderResolver#resolve}
 * and provides:
 * <ul>
 *   <li>The method/field name being accessed</li>
 *   <li>Arguments passed to the method call</li>
 *   <li>The locale for formatting</li>
 * </ul>
 * <p>
 * AST argument nodes are evaluated on-demand when accessed, enabling
 * lazy evaluation and field reference resolution.
 */
public class FieldParams {

    @Getter
    private final String field;
    private final List<AstNode> args;
    private final boolean hasParens;
    private final EvaluationContext ctx;

    private FieldParams(String field, List<AstNode> args, boolean hasParens, EvaluationContext ctx) {
        this.field = field;
        this.args = args;
        this.hasParens = hasParens;
        this.ctx = ctx;
    }

    /**
     * Creates params for a method call with parentheses.
     */
    public static FieldParams of(String field, List<AstNode> args, EvaluationContext ctx) {
        return new FieldParams(field, args, true, ctx);
    }

    /**
     * Creates params with explicit parentheses flag.
     *
     * @param hasParens true for method calls like foo(), false for field access like foo.bar
     */
    public static FieldParams of(String field, List<AstNode> args, boolean hasParens, EvaluationContext ctx) {
        return new FieldParams(field, args, hasParens, ctx);
    }

    /**
     * The locale for this evaluation context.
     */
    public Locale getLocale() {
        return this.ctx.locale();
    }

    /**
     * Number of arguments.
     */
    public int length() {
        return this.args.size();
    }

    /**
     * All arguments as strings.
     */
    public String[] strArr() {
        String[] arr = new String[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.strAt(i, "");
        }
        return arr;
    }

    /**
     * All arguments as integers.
     */
    public int[] intArr() {
        int[] arr = new int[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.intAt(i, 0);
        }
        return arr;
    }

    /**
     * All arguments as doubles.
     */
    public double[] doubleArr() {
        double[] arr = new double[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.doubleAt(i, 0);
        }
        return arr;
    }

    public String strAt(int pos) {
        return this.strAt(pos, "");
    }

    public String strAt(int pos, String def) {
        if (pos >= this.args.size()) return def;

        AstNode arg = this.args.get(pos);
        Object result = LiteralValue.unwrap(this.ctx.evaluate(arg));

        if (result != null) {
            return result.toString();
        }

        // Fallback for unresolved refs: use the source literal (which falls back to the
        // trimmed name) so edge-whitespace cases like {prepend( wrap )} preserve spaces
        // and unquoted literal-style args like {replace(_,-)} still work
        if (arg instanceof Ref) {
            return ((Ref) arg).getLiteral();
        }

        return def;
    }

    public double doubleAt(int pos) {
        return this.doubleAt(pos, 0);
    }

    public double doubleAt(int pos, double def) {
        if (pos >= this.args.size()) return def;
        return this.ctx.evaluateDouble(this.args.get(pos), def);
    }

    public int intAt(int pos) {
        return this.intAt(pos, 0);
    }

    public int intAt(int pos, int def) {
        if (pos >= this.args.size()) return def;
        return this.ctx.evaluateInt(this.args.get(pos), def);
    }

    /**
     * Gets the parsed argument at the specified position.
     */
    @Nullable
    public ParsedArg parsedAt(int pos) {
        if (pos >= this.args.size()) return null;
        Object value = LiteralValue.unwrap(this.ctx.evaluate(this.args.get(pos)));
        String strValue = (value != null) ? value.toString() : null;
        return ParsedArg.fieldRefOrLiteral(strValue);
    }

    /**
     * Resolves an argument by evaluating the AST node.
     * <p>
     * Resolution logic:
     * <ul>
     *   <li>String literals return their value directly</li>
     *   <li>For Ref nodes: if field exists in context, return evaluated value;
     *       otherwise fall back to ref name (backward compat for literals)</li>
     *   <li>Other expressions are evaluated normally</li>
     * </ul>
     */
    @Nullable
    public Object resolveArg(int pos, @Nullable Locale locale, @Nullable PlaceholderContext context) {
        if (pos >= this.args.size()) return null;

        AstNode arg = this.args.get(pos);

        if (arg instanceof StringLiteral) {
            return ((StringLiteral) arg).getValue();
        }

        if (arg instanceof Ref) {
            String refName = ((Ref) arg).getName();
            if (this.ctx.hasValue(refName)) {
                return LiteralValue.unwrap(this.ctx.evaluate(arg));
            }
            return refName;
        }

        return LiteralValue.unwrap(this.ctx.evaluate(arg));
    }

    /**
     * Resolves an argument preserving literal information.
     * The returned value will be wrapped in LiteralValue if it came from a string/number literal.
     */
    @Nullable
    public Object resolveArgRaw(int pos, @Nullable Locale locale, @Nullable PlaceholderContext context) {
        if (pos >= this.args.size()) return null;

        AstNode arg = this.args.get(pos);

        if (arg instanceof StringLiteral) {
            return new LiteralValue(((StringLiteral) arg).getValue());
        }

        if (arg instanceof Ref) {
            String refName = ((Ref) arg).getName();
            if (this.ctx.hasValue(refName)) {
                return this.ctx.evaluate(arg);  // Keep LiteralValue if present
            }
            return refName;
        }

        return this.ctx.evaluate(arg);  // Keep LiteralValue if present
    }

    /**
     * Resolves an argument as a String, with a default value.
     */
    @NonNull
    public String resolveStrAt(int pos, @NonNull String def, @Nullable Locale locale, @Nullable PlaceholderContext context) {
        Object resolved = this.resolveArg(pos, locale, context);
        if (resolved == null) return def;
        return String.valueOf(resolved);
    }

    /**
     * Raw parameters for backward compatibility.
     */
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
        return this.rawStrArr();
    }

    private String[] rawStrArr() {
        String[] arr = new String[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.toRawString(this.args.get(i));
        }
        return arr;
    }

    private String toRawString(AstNode node) {
        if (node instanceof StringLiteral) {
            return "'" + ((StringLiteral) node).getValue() + "'";
        }
        if (node instanceof NumberLiteral) {
            return ((NumberLiteral) node).getValue().toString();
        }
        if (node instanceof Ref) {
            return ((Ref) node).getName();
        }
        Object result = LiteralValue.unwrap(this.ctx.evaluate(node));
        return (result != null) ? result.toString() : "";
    }

    /**
     * All parsed parameters.
     */
    public ParsedArg[] getParsedParams() {
        ParsedArg[] arr = new ParsedArg[this.args.size()];
        for (int i = 0; i < this.args.size(); i++) {
            arr[i] = this.parsedAt(i);
        }
        return arr;
    }
}
