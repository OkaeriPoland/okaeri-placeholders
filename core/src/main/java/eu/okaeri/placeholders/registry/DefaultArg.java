package eu.okaeri.placeholders.registry;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.ast.FieldParams;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

/**
 * Default implementation of {@link Arg} that wraps a FieldParams at a specific index.
 */
@RequiredArgsConstructor
class DefaultArg implements Arg {

    private final FieldParams fieldParams;
    private final int index;
    private final Locale locale;
    private final PlaceholderContext context;

    @Override
    public Object raw() {
        if (this.index >= this.fieldParams.length()) return null;
        return this.fieldParams.strAt(this.index, null);
    }

    @Override
    public String asString() {
        return this.fieldParams.strAt(this.index, "");
    }

    @Override
    public int asInt() {
        return this.fieldParams.intAt(this.index);
    }

    @Override
    public int asInt(int defaultValue) {
        return this.fieldParams.intAt(this.index, defaultValue);
    }

    @Override
    public double asDouble() {
        return this.fieldParams.doubleAt(this.index);
    }

    @Override
    public double asDouble(double defaultValue) {
        return this.fieldParams.doubleAt(this.index, defaultValue);
    }

    @Override
    public boolean asBool() {
        return this.asBool(false);
    }

    @Override
    public boolean asBool(boolean defaultValue) {
        if (this.index >= this.fieldParams.length()) return defaultValue;
        String str = this.fieldParams.strAt(this.index, "");
        if (str.isEmpty()) return defaultValue;
        return "true".equalsIgnoreCase(str) || "1".equals(str) || "yes".equalsIgnoreCase(str);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T orElse(T defaultValue) {
        if (this.index >= this.fieldParams.length()) return defaultValue;

        Object value;
        if (defaultValue instanceof Integer) {
            value = this.fieldParams.intAt(this.index, (Integer) defaultValue);
        } else if (defaultValue instanceof Double) {
            value = this.fieldParams.doubleAt(this.index, (Double) defaultValue);
        } else if (defaultValue instanceof String) {
            value = this.fieldParams.strAt(this.index, (String) defaultValue);
        } else {
            String str = this.fieldParams.strAt(this.index, null);
            if ((str == null) || str.isEmpty()) return defaultValue;
            value = str;
        }
        return (T) value;
    }

    @Override
    public Object resolve(PlaceholderContext ctx) {
        PlaceholderContext effectiveCtx = (ctx != null) ? ctx : this.context;
        return this.fieldParams.resolveArg(this.index, this.locale, effectiveCtx);
    }
}
