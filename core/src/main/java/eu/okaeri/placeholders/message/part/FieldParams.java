package eu.okaeri.placeholders.message.part;

import eu.okaeri.placeholders.context.Placeholder;
import eu.okaeri.placeholders.context.PlaceholderContext;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Locale;

@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FieldParams {

    private final String field;
    private final String[] params;
    private final ParsedArg[] parsedParams;

    /**
     * Creates FieldParams from raw string array (legacy/backward compat).
     * All params are treated as FIELD_REF_OR_LITERAL for backward compatibility.
     */
    public static FieldParams of(String field, @NonNull String[] params) {
        ParsedArg[] parsed = new ParsedArg[params.length];
        for (int i = 0; i < params.length; i++) {
            parsed[i] = ParsedArg.fieldRefOrLiteral(params[i]);
        }
        return new FieldParams(field, params, parsed);
    }

    /**
     * Creates FieldParams from parsed arguments (new style with quote detection).
     * Uses rawValue for backward compatibility with code that expects quoted strings.
     */
    public static FieldParams ofParsed(String field, @NonNull ParsedArg[] parsedParams) {
        String[] params = new String[parsedParams.length];
        for (int i = 0; i < parsedParams.length; i++) {
            params[i] = parsedParams[i].getRawValue();
        }
        return new FieldParams(field, params, parsedParams);
    }

    public static FieldParams empty(String field) {
        return new FieldParams(field, new String[]{}, new ParsedArg[]{});
    }

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
     * Resolves an argument at the specified position, handling field references.
     * <p>
     * Resolution logic:
     * <ul>
     *   <li>If the argument is a LITERAL (was quoted), returns the literal string value</li>
     *   <li>If the argument is FIELD_REF_OR_LITERAL (unquoted):
     *     <ul>
     *       <li>First tries to resolve as a field reference from the context</li>
     *       <li>If field not found, falls back to treating as literal string (backward compat)</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param pos     The argument position (0-indexed)
     * @param locale  The locale for field resolution
     * @param context The placeholder context for field lookup
     * @return The resolved value (may be any Object type), or null if resolution fails
     */
    @Nullable
    public Object resolveArg(int pos, @Nullable Locale locale, @Nullable PlaceholderContext context) {
        if (pos >= this.parsedParams.length) return null;

        ParsedArg arg = this.parsedParams[pos];

        // Explicit literal (was quoted) - return as-is
        if (arg.isLiteral()) {
            return arg.getValue();
        }

        // Try as field reference first
        if ((context != null) && arg.mayBeFieldRef()) {
            String fieldPath = arg.getValue().trim();  // Trim for field resolution (allows spaces after commas)

            // Parse the field path and try to resolve
            MessageField field = MessageField.of((locale != null) ? locale : Locale.ROOT, fieldPath);
            Placeholder placeholder = context.getFields().get(field.getName());

            if (placeholder != null) {
                // Found a matching field - resolve it (may return null if field value is null)
                return placeholder.resolveValue(field);
            }
        }

        // Fallback: treat as literal string (backward compat for unrecognized field names)
        return arg.getValue();
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
