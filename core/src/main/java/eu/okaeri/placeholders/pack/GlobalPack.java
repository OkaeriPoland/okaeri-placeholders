package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.registry.Params;
import eu.okaeri.placeholders.registry.Registry;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

/**
 * Global functions accessible via {@code {$.func()}} or {@code {func()}} syntax.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code env(VAR)} - environment variable</li>
 *   <li>{@code now()} - current timestamp</li>
 *   <li>{@code coalesce(a, b, ...)} / {@code or(a, b, ...)} - first non-null</li>
 *   <li>{@code if(cond, then, else)} - conditional</li>
 *   <li>{@code random(min, max)} - random integer</li>
 *   <li>{@code concat(a, b, ...)} - concatenation</li>
 *   <li>{@code min(a, b, ...)} / {@code max(a, b, ...)} - numeric min/max</li>
 *   <li>{@code clamp(value, min, max)} - clamp number to range</li>
 *   <li>{@code len(value)} - length/size of string, array, or collection</li>
 *   <li>{@code default(value, fallback)} - default value</li>
 *   <li>{@code cond(c1, v1, c2, v2, ..., default)} - chained conditionals</li>
 *   <li>{@code switch(value, case1, result1, ..., default)} - switch/case</li>
 * </ul>
 */
public class GlobalPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.globals()
            // Environment variable
            .add("env", p -> {
                String varName = p.arg(0).asString();
                String value = System.getenv(varName);
                return (value != null) ? value : "";
            })

            // Current timestamp
            .add("now", () -> Instant.now())

            // First non-null value
            .add("coalesce", (p, ctx) -> {
                for (int i = 0; i < p.length(); i++) {
                    Object val = p.arg(i).resolve(ctx);
                    if (val != null) return val;
                }
                return null;
            })
            .alias("coalesce", "or")

            // Conditional
            .add("if", (p, ctx) -> {
                Object condition = p.arg(0).resolve(ctx);
                return isTruthy(condition)
                    ? p.arg(1).resolve(ctx)
                    : p.arg(2).resolve(ctx);
            })

            // Random integer
            .add("random", p -> {
                int min = p.arg(0).asInt(0);
                int max = p.arg(1).asInt(100);
                return min + (int) (Math.random() * ((max - min) + 1));
            })

            // Concatenation
            .add("concat", (p, ctx) -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < p.length(); i++) {
                    Object val = p.arg(i).resolve(ctx);
                    if (val != null) sb.append(val);
                }
                return sb.toString();
            })

            // Numeric min
            .add("min", (p, ctx) -> {
                Double min = null;
                for (int i = 0; i < p.length(); i++) {
                    Object val = p.arg(i).resolve(ctx);
                    if (val instanceof Number) {
                        double d = ((Number) val).doubleValue();
                        if ((min == null) || (d < min)) min = d;
                    }
                }
                return (min != null) ? asIntIfWhole(min) : null;
            })

            // Numeric max
            .add("max", (p, ctx) -> {
                Double max = null;
                for (int i = 0; i < p.length(); i++) {
                    Object val = p.arg(i).resolve(ctx);
                    if (val instanceof Number) {
                        double d = ((Number) val).doubleValue();
                        if ((max == null) || (d > max)) max = d;
                    }
                }
                return (max != null) ? asIntIfWhole(max) : null;
            })

            // Clamp value to range
            .add("clamp", (p, ctx) -> {
                Object val = p.arg(0).resolve(ctx);
                if (!(val instanceof Number)) return val;
                double value = ((Number) val).doubleValue();
                double min = p.arg(1).asDouble(Double.MIN_VALUE);
                double max = p.arg(2).asDouble(Double.MAX_VALUE);
                return asIntIfWhole(Math.max(min, Math.min(max, value)));
            })

            // Length/size of string, array, or collection
            .add("len", (p, ctx) -> {
                Object val = p.arg(0).resolve(ctx);
                if (val == null) return 0;
                if (val instanceof String) return ((String) val).length();
                if (val instanceof Collection) return ((Collection<?>) val).size();
                if (val instanceof Map) return ((Map<?, ?>) val).size();
                if (val.getClass().isArray()) return Array.getLength(val);
                return 1;
            })
            .alias("len", "length", "size")

            // Default value (simpler coalesce for 2 args)
            .add("default", (p, ctx) -> {
                Object val = p.arg(0).resolve(ctx);
                if (val != null) {
                    if ((val instanceof String) && ((String) val).isEmpty()) {
                        return p.arg(1).resolve(ctx);
                    }
                    return val;
                }
                return p.arg(1).resolve(ctx);
            })

            // Chained conditionals
            .add("cond", (p, ctx) -> evalCond(p, ctx))

            // Switch/case
            .add("switch", (p, ctx) -> evalSwitch(p, ctx));
    }

    private static Object evalCond(Params p, PlaceholderContext ctx) {
        int len = p.length();
        for (int i = 0; (i + 1) < len; i += 2) {
            Object condition = p.arg(i).resolve(ctx);
            if (isTruthy(condition)) {
                // Use resolveRaw to preserve literal info for the value
                return p.arg(i + 1).resolveRaw(ctx);
            }
        }
        // Odd number of args = last is default
        if ((len % 2) == 1) {
            return p.arg(len - 1).resolveRaw(ctx);
        }
        return null;
    }

    private static Object evalSwitch(Params p, PlaceholderContext ctx) {
        if (p.length() < 1) return null;

        Object value = p.arg(0).resolve(ctx);
        String valueStr = (value != null) ? String.valueOf(value) : "";

        int len = p.length();
        for (int i = 1; (i + 1) < len; i += 2) {
            Object caseVal = p.arg(i).resolve(ctx);
            String caseStr = (caseVal != null) ? String.valueOf(caseVal) : "";
            if (valueStr.equals(caseStr) || ((value != null) && value.equals(caseVal))) {
                // Use resolveRaw to preserve literal info for the value
                return p.arg(i + 1).resolveRaw(ctx);
            }
        }
        // Remaining arg after pairs is default
        if (((len - 1) % 2) == 1) {
            return p.arg(len - 1).resolveRaw(ctx);
        }
        return null;
    }

    private static boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) {
            String s = (String) value;
            return !s.isEmpty() && !"false".equalsIgnoreCase(s) && !"0".equals(s);
        }
        return true;
    }

    private static Number asIntIfWhole(double value) {
        if ((value == Math.floor(value)) && !Double.isInfinite(value)) {
            return (int) value;
        }
        return value;
    }
}
