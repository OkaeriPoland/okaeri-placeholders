package eu.okaeri.placeholders.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import eu.okaeri.placeholders.registry.Registry;

import java.math.BigDecimal;

/**
 * Placeholder methods for numeric values.
 * <p>
 * Registers methods on both {@code Number} (for general operations) and specific
 * types like {@code Integer}, {@code Double}, etc. for type-specific arithmetic.
 * <p>
 * Provides:
 * <ul>
 *   <li>Arithmetic: {@code plus/add}, {@code minus/subtract}, {@code multiply}, {@code divide}, {@code mod}</li>
 *   <li>Math: {@code abs}, {@code round}, {@code floor}, {@code ceil}, {@code clamp}</li>
 *   <li>Comparisons: {@code gt}, {@code gte}, {@code lt}, {@code lte}, {@code between}</li>
 *   <li>Formatting: {@code plural}, {@code format}, {@code signed}</li>
 * </ul>
 */
public class NumberPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        // Integer-specific arithmetic (returns int)
        r.type(Integer.class)
            .add("divide", (num, p) -> num / p.arg(0).asInt(1))
            .add("multiply", (num, p) -> num * p.arg(0).asInt(1))
            .add("minus", (num, p) -> num - p.arg(0).asInt(0))
            .alias("minus", "subtract")
            .add("plus", (num, p) -> num + p.arg(0).asInt(0))
            .alias("plus", "add");

        // Double-specific arithmetic (returns smart int/double)
        r.type(Double.class)
            .add("divide", (num, p) -> asIntIfWhole(num / p.arg(0).asDouble(1)))
            .add("multiply", (num, p) -> asIntIfWhole(num * p.arg(0).asDouble(1)))
            .add("minus", (num, p) -> asIntIfWhole(num - p.arg(0).asDouble(0)))
            .alias("minus", "subtract")
            .add("plus", (num, p) -> asIntIfWhole(num + p.arg(0).asDouble(0)))
            .alias("plus", "add");

        // Float-specific arithmetic
        r.type(Float.class)
            .add("divide", (num, p) -> asIntIfWhole(num / p.arg(0).asDouble(1)))
            .add("multiply", (num, p) -> asIntIfWhole(num * p.arg(0).asDouble(1)))
            .add("minus", (num, p) -> asIntIfWhole(num - p.arg(0).asDouble(0)))
            .alias("minus", "subtract")
            .add("plus", (num, p) -> asIntIfWhole(num + p.arg(0).asDouble(0)))
            .alias("plus", "add");

        // Short-specific arithmetic
        r.type(Short.class)
            .add("divide", (num, p) -> asIntIfWhole(num / p.arg(0).asDouble(1)))
            .add("multiply", (num, p) -> asIntIfWhole(num * p.arg(0).asDouble(1)))
            .add("minus", (num, p) -> asIntIfWhole(num - p.arg(0).asDouble(0)))
            .alias("minus", "subtract")
            .add("plus", (num, p) -> asIntIfWhole(num + p.arg(0).asDouble(0)))
            .alias("plus", "add");

        // Byte-specific arithmetic
        r.type(Byte.class)
            .add("divide", (num, p) -> asIntIfWhole(num / p.arg(0).asDouble(1)))
            .add("multiply", (num, p) -> asIntIfWhole(num * p.arg(0).asDouble(1)))
            .add("minus", (num, p) -> asIntIfWhole(num - p.arg(0).asDouble(0)))
            .alias("minus", "subtract")
            .add("plus", (num, p) -> asIntIfWhole(num + p.arg(0).asDouble(0)))
            .alias("plus", "add");

        // Number base class - math operations
        r.type(Number.class)
            // Math functions
            .add("abs", num -> asIntIfWhole(Math.abs(num.doubleValue())))
            .add("round", (num, p) -> {
                int precision = p.arg(0).asInt(0);
                if (precision == 0) {
                    return (int) Math.round(num.doubleValue());
                }
                double factor = Math.pow(10, precision);
                return Math.round(num.doubleValue() * factor) / factor;
            })
            .add("floor", num -> (int) Math.floor(num.doubleValue()))
            .add("ceil", num -> (int) Math.ceil(num.doubleValue()))

            // Clamp to range
            .add("clamp", (num, p) -> {
                double value = num.doubleValue();
                double min = p.arg(0).asDouble(Double.MIN_VALUE);
                double max = p.arg(1).asDouble(Double.MAX_VALUE);
                return asIntIfWhole(Math.max(min, Math.min(max, value)));
            })

            // Modulo
            .add("mod", (num, p) -> {
                double divisor = p.arg(0).asDouble(1);
                return asIntIfWhole(num.doubleValue() % divisor);
            })

            // Comparisons (return Boolean for use with $.if)
            .add("gt", (num, p) -> num.doubleValue() > p.arg(0).asDouble(0))
            .add("gte", (num, p) -> num.doubleValue() >= p.arg(0).asDouble(0))
            .add("lt", (num, p) -> num.doubleValue() < p.arg(0).asDouble(0))
            .add("lte", (num, p) -> num.doubleValue() <= p.arg(0).asDouble(0))
            .add("between", (num, p) -> {
                double value = num.doubleValue();
                double min = p.arg(0).asDouble(Double.MIN_VALUE);
                double max = p.arg(1).asDouble(Double.MAX_VALUE);
                return value >= min && value <= max;
            })

            // Pluralization
            .add("plural", (num, p, ctx) -> {
                String[] forms = p.toStringArray();
                return Placeholders.pluralize(ctx.getLocale(), num.intValue(), forms);
            })

            // Printf-style formatting
            .add("format", (num, p, ctx) -> {
                String pattern = p.arg(0).orElse("%.2f");
                double doubleValue = new BigDecimal(String.valueOf(num)).doubleValue();
                return String.format(ctx.getLocale(), pattern, doubleValue);
            })

            // Explicit-sign formatting: positive -> "+5", negative -> "-3", zero -> "0"
            .add("signed", num -> {
                double d = num.doubleValue();
                if (d == 0) {
                    Number normalized = asIntIfWhole(d);
                    return normalized.toString();
                }
                Number normalized = asIntIfWhole(d);
                return (d > 0) ? ("+" + normalized) : normalized.toString();
            });
    }

    private static Number asIntIfWhole(double value) {
        if ((value == Math.floor(value)) && !Double.isInfinite(value)) {
            return (int) value;
        }
        return value;
    }
}
