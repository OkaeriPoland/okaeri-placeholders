package eu.okaeri.placeholders.ast;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Resolves a method/field call on a target object.
 * <p>
 * This is the AST-native resolver interface. Unlike the legacy interface,
 * it receives AST nodes directly and can evaluate them as needed.
 *
 * @param <T> the type of target this resolver handles
 */
@FunctionalInterface
public interface Resolver<T> {

    /**
     * Resolves a call on the target object.
     *
     * @param target the target object (may be null for methods like .or())
     * @param args   the argument AST nodes (may be empty)
     * @param ctx    the evaluation context for evaluating args
     * @return the resolved value, may be null
     */
    @Nullable
    Object resolve(@Nullable T target, List<AstNode> args, EvaluationContext ctx);
}
