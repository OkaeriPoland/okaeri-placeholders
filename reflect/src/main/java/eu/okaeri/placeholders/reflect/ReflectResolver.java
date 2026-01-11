package eu.okaeri.placeholders.reflect;

import eu.okaeri.placeholders.context.PlaceholderContext;
import eu.okaeri.placeholders.message.part.FieldParams;
import eu.okaeri.placeholders.message.part.MessageFieldAccessor;
import eu.okaeri.placeholders.reflect.argument.ArgumentParser;
import eu.okaeri.placeholders.reflect.argument.ParsedArgument;
import eu.okaeri.placeholders.reflect.exception.ReflectException;
import eu.okaeri.placeholders.reflect.invoke.FieldInvoker;
import eu.okaeri.placeholders.reflect.invoke.MethodInvoker;
import eu.okaeri.placeholders.reflect.lookup.MemberLookup;
import eu.okaeri.placeholders.schema.resolver.PlaceholderResolver;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Reflection-based placeholder resolver that can access fields and invoke methods.
 * <p>
 * Supports:
 * <ul>
 *   <li>Field access: {@code {obj.field}}</li>
 *   <li>No-arg method: {@code {obj.method()}}</li>
 *   <li>Method with args: {@code {obj.method(123, 'hello')}}</li>
 *   <li>Static members when target is a Class</li>
 * </ul>
 * <p>
 * Type coercion is automatically applied when matching method overloads.
 */
public class ReflectResolver implements PlaceholderResolver {

    private final MemberLookup lookup = new MemberLookup();

    @Override
    public Object resolve(@NotNull Object object, @NonNull MessageFieldAccessor accessor, @Nullable PlaceholderContext context) {
        Class<?> clazz = object.getClass();

        // Try static resolution if object is a Class
        if (object instanceof Class) {
            Object result = this.resolveOn(object, (Class<?>) object, accessor, context);
            if (result != null) {
                return result;
            }
        }

        // Try instance resolution
        Object result = this.resolveOn(object, clazz, accessor, context);
        if (result != null) {
            return result;
        }

        // Nothing found
        throw ReflectException.memberNotFound(clazz, accessor.params().getField());
    }

    /**
     * Resolves a field or method on the given class.
     *
     * @param target  The object instance (or null for static)
     * @param clazz   The class to search in
     * @param accessor The field accessor with name and params
     * @param context The placeholder context for resolving argument references
     * @return The resolved value, or null if not found
     */
    @Nullable
    private Object resolveOn(@NonNull Object target, @NonNull Class<?> clazz, @NonNull MessageFieldAccessor accessor, @Nullable PlaceholderContext context) {
        FieldParams params = accessor.params();
        String name = params.getField();
        String[] rawArgs = params.getParams();

        // Field access: no params at all
        if (rawArgs.length == 0) {
            Optional<Field> field = this.lookup.findField(clazz, name);
            if (field.isPresent()) {
                return FieldInvoker.getValue(field.get(), target);
            }
        }

        // Method call
        if (rawArgs.length > 0) {
            // No-arg method: empty params string "" signals method() syntax
            if ((rawArgs.length == 1) && rawArgs[0].isEmpty()) {
                Optional<Method> method = this.lookup.findMethod(clazz, name);
                if (method.isPresent()) {
                    return MethodInvoker.invoke(method.get(), target);
                }
            } else {
                // Method with arguments
                ParsedArgument[] parsedArgs = ArgumentParser.parseAndResolve(rawArgs, context);
                Class<?>[] argTypes = ArgumentParser.extractTypes(parsedArgs);
                Object[] argValues = ArgumentParser.extractValues(parsedArgs);

                // Find method with coercion support
                Optional<Method> method = this.lookup.findMethodWithCoercion(clazz, name, argTypes);
                if (method.isPresent()) {
                    return MethodInvoker.invoke(method.get(), target, argValues);
                }

                // Method not found
                throw ReflectException.methodNotFound(clazz, name, argTypes);
            }
        }

        return null;
    }
}
