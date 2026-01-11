package eu.okaeri.placeholders.message.part;

import eu.okaeri.placeholders.context.PlaceholderContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Provides access to method call parameters.
 * <p>
 * This is the base class extended by {@link eu.okaeri.placeholders.ast.bridge.AstFieldParams}
 * for AST-based parameter access.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FieldParams {

    private final String field;
    private final String[] params;
    private final ParsedArg[] parsedParams;

    public int length() {
        return this.params.length;
    }

    public String[] strArr() {
        String[] arr = new String[this.parsedParams.length];
        for (int i = 0; i < this.parsedParams.length; i++) {
            arr[i] = this.parsedParams[i].getValue();
        }
        return arr;
    }

    public int[] intArr() {
        int[] arr = new int[this.params.length];
        for (int i = 0; i < this.params.length; i++) {
            arr[i] = this.intAt(i, 0);
        }
        return arr;
    }

    public double[] doubleArr() {
        double[] arr = new double[this.params.length];
        for (int i = 0; i < this.params.length; i++) {
            arr[i] = this.doubleAt(i, 0);
        }
        return arr;
    }

    public String strAt(int pos) {
        return this.strAt(pos, "");
    }

    public String strAt(int pos, String def) {
        if (pos >= this.parsedParams.length) return def;
        return this.parsedParams[pos].getValue();
    }

    public double doubleAt(int pos) {
        return this.doubleAt(pos, 0);
    }

    public double doubleAt(int pos, double def) {
        String str = this.strAt(pos, String.valueOf(def));
        try {
            return new BigDecimal(str).doubleValue();
        } catch (NumberFormatException exception) {
            return def;
        }
    }

    public int intAt(int pos) {
        return this.intAt(pos, 0);
    }

    public int intAt(int pos, int def) {
        String str = this.strAt(pos, String.valueOf(def));
        try {
            return new BigDecimal(str).intValue();
        } catch (NumberFormatException exception) {
            return def;
        }
    }

    /**
     * Gets the parsed argument at the specified position.
     *
     * @param pos The argument position (0-indexed)
     * @return The parsed argument, or null if position is out of bounds
     */
    @Nullable
    public ParsedArg parsedAt(int pos) {
        if (pos >= this.parsedParams.length) return null;
        return this.parsedParams[pos];
    }

    /**
     * Resolves an argument at the specified position, evaluating field references.
     * <p>
     * Subclasses override this to provide AST-based evaluation.
     *
     * @param pos     The argument position (0-indexed)
     * @param locale  The locale for evaluation
     * @param context The placeholder context for field lookup
     * @return The resolved value, or null if position is out of bounds
     */
    @Nullable
    public Object resolveArg(int pos, @Nullable Locale locale, @Nullable PlaceholderContext context) {
        if (pos >= this.parsedParams.length) return null;
        return this.parsedParams[pos].getValue();
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
}
