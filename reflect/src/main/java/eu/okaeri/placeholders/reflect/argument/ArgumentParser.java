package eu.okaeri.placeholders.reflect.argument;

import eu.okaeri.placeholders.context.FieldValue;
import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.CompiledMessage;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parses raw string arguments into typed {@link ParsedArgument} objects.
 * <p>
 * Supports literal values (strings, numbers, booleans) and context references.
 */
public final class ArgumentParser {

    private ArgumentParser() {
        // Utility class
    }

    /**
     * Parses a single raw argument string.
     *
     * @param raw The raw argument string
     * @return The parsed argument
     */
    public static ParsedArgument parse(@NonNull String raw) {
        ArgumentType type = ArgumentType.detect(raw);
        Object value = type.parse(raw);
        return ParsedArgument.of(type, value, raw);
    }

    /**
     * Parses multiple raw argument strings.
     *
     * @param raws The raw argument strings
     * @return Array of parsed arguments
     */
    public static ParsedArgument[] parseAll(@NonNull String[] raws) {
        ParsedArgument[] result = new ParsedArgument[raws.length];
        for (int i = 0; i < raws.length; i++) {
            result[i] = parse(raws[i]);
        }
        return result;
    }

    /**
     * Parses and resolves arguments, looking up context references.
     *
     * @param raws    The raw argument strings
     * @param context The placeholder context for resolving references (may be null)
     * @return Array of parsed and resolved arguments
     */
    public static ParsedArgument[] parseAndResolve(@NonNull String[] raws, @Nullable PlaceholderContext context) {
        ParsedArgument[] result = new ParsedArgument[raws.length];
        for (int i = 0; i < raws.length; i++) {
            result[i] = parseAndResolve(raws[i], context);
        }
        return result;
    }

    /**
     * Parses and resolves a single argument, looking up context references.
     *
     * @param raw     The raw argument string
     * @param context The placeholder context for resolving references (may be null)
     * @return The parsed and resolved argument
     */
    public static ParsedArgument parseAndResolve(@NonNull String raw, @Nullable PlaceholderContext context) {
        ArgumentType type = ArgumentType.detect(raw);

        // For context refs or unknown types, try to resolve from context
        if (((type == ArgumentType.CONTEXT_REF) || (type == ArgumentType.UNKNOWN)) && (context != null)) {
            Object resolved = resolveFromContext(raw, context);
            if (resolved != null) {
                return ParsedArgument.contextRef(resolved, raw);
            }
            // Check if it's a simple field reference (not a path)
            FieldValue fieldValue = context.getFields().get(raw);
            if (fieldValue != null) {
                return ParsedArgument.contextRef(fieldValue.getValue(), raw);
            }
        }

        // Parse as literal
        Object value = type.parse(raw);
        return ParsedArgument.of(type, value, raw);
    }

    /**
     * Resolves a field path or expression from the placeholder context.
     *
     * @param path    The field path (e.g., "user.name" or "value.toString()")
     * @param context The placeholder context
     * @return The resolved value, or null if resolution fails
     */
    @Nullable
    private static Object resolveFromContext(String path, PlaceholderContext context) {
        // Check for method chain or complex expression
        if (path.contains(".") || path.contains("(")) {
            try {
                // Use the placeholder system to resolve the path
                if (context.getPlaceholders() != null) {
                    return context.getPlaceholders()
                        .context(CompiledMessage.of("{" + path + "}"))
                        .with(context.getValues())
                        .apply();
                }
            } catch (Exception e) {
                // Resolution failed, fall through to return null
            }
        }
        return null;
    }

    /**
     * Extracts Java types from parsed arguments for method matching.
     *
     * @param args The parsed arguments
     * @return Array of Java types
     */
    public static Class<?>[] extractTypes(@NonNull ParsedArgument[] args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getJavaType();
        }
        return types;
    }

    /**
     * Extracts values from parsed arguments for method invocation.
     *
     * @param args The parsed arguments
     * @return Array of values
     */
    public static Object[] extractValues(@NonNull ParsedArgument[] args) {
        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = args[i].getValue();
        }
        return values;
    }
}
