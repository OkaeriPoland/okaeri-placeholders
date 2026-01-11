package eu.okaeri.placeholders.ast;

import eu.okaeri.placeholders.ast.node.Ref;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Context for evaluating AST expressions.
 * <p>
 * This is the primary interface resolvers use to:
 * - Evaluate AST nodes (arguments)
 * - Access context values
 * - Get locale information
 */
public interface EvaluationContext {

    /**
     * Evaluates an AST node and returns the result.
     *
     * @param node the node to evaluate
     * @return the evaluated value, may be null
     */
    @Nullable
    Object evaluate(AstNode node);

    /**
     * Gets a value from the context by name.
     *
     * @param name the value name
     * @return the value, or null if not found
     */
    @Nullable
    Object getValue(String name);

    /**
     * Checks if a value exists in the context.
     *
     * @param name the value name
     * @return true if the value exists (even if null), false if not found
     */
    boolean hasValue(String name);

    /**
     * Returns the locale for this evaluation.
     *
     * @return the locale
     */
    Locale locale();

    /**
     * Evaluates a node and returns it as a String.
     *
     * @param node         the node to evaluate
     * @param defaultValue value to return if result is null
     * @return the string value
     */
    default String evaluateString(AstNode node, String defaultValue) {
        Object result = this.evaluate(node);
        return (result != null) ? result.toString() : defaultValue;
    }

    /**
     * Evaluates a node and returns it as an int.
     * <p>
     * If the node is a Ref that evaluates to null (not a known field),
     * the ref name itself is parsed as a number. This allows unquoted
     * numeric arguments like {n.divide(2)} to work.
     *
     * @param node         the node to evaluate
     * @param defaultValue value to return if result is null or not a number
     * @return the int value
     */
    default int evaluateInt(AstNode node, int defaultValue) {
        Object result = this.evaluate(node);
        if (result instanceof Number) {
            return ((Number) result).intValue();
        }
        if (result != null) {
            try {
                return Integer.parseInt(result.toString());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        // Fallback: if node is Ref and result is null, try parsing ref name as number
        // This handles unquoted numeric arguments like {n.divide(2)}
        if (node instanceof Ref) {
            try {
                return Integer.parseInt(((Ref) node).getName());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Evaluates a node and returns it as a double.
     * <p>
     * If the node is a Ref that evaluates to null (not a known field),
     * the ref name itself is parsed as a number. This allows unquoted
     * numeric arguments like {n.divide(2.5)} to work.
     *
     * @param node         the node to evaluate
     * @param defaultValue value to return if result is null or not a number
     * @return the double value
     */
    default double evaluateDouble(AstNode node, double defaultValue) {
        Object result = this.evaluate(node);
        if (result instanceof Number) {
            return ((Number) result).doubleValue();
        }
        if (result != null) {
            try {
                return Double.parseDouble(result.toString());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        // Fallback: if node is Ref and result is null, try parsing ref name as number
        // This handles unquoted numeric arguments like {n.divide(2.5)}
        if (node instanceof Ref) {
            try {
                return Double.parseDouble(((Ref) node).getName());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Evaluates a node and returns it as a boolean.
     *
     * @param node         the node to evaluate
     * @param defaultValue value to return if result is null
     * @return the boolean value
     */
    default boolean evaluateBoolean(AstNode node, boolean defaultValue) {
        Object result = this.evaluate(node);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result != null) {
            String str = result.toString().toLowerCase();
            if ("true".equals(str) || "yes".equals(str) || "1".equals(str)) return true;
            if ("false".equals(str) || "no".equals(str) || "0".equals(str)) return false;
        }
        return defaultValue;
    }
}
