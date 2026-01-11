package eu.okaeri.placeholders.reflect.exception;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Exception thrown when reflection-based placeholder resolution fails.
 * Provides detailed context about what was attempted.
 */
@Getter
public class ReflectException extends RuntimeException {

    private final Class<?> targetClass;
    private final String memberName;
    private final Class<?>[] attemptedArgTypes;

    public ReflectException(String message, Class<?> targetClass, String memberName) {
        this(message, targetClass, memberName, new Class<?>[0], null);
    }

    public ReflectException(String message, Class<?> targetClass, String memberName, Class<?>[] attemptedArgTypes) {
        this(message, targetClass, memberName, attemptedArgTypes, null);
    }

    public ReflectException(String message, Class<?> targetClass, String memberName, Throwable cause) {
        this(message, targetClass, memberName, new Class<?>[0], cause);
    }

    public ReflectException(String message, Class<?> targetClass, String memberName, Class<?>[] attemptedArgTypes, Throwable cause) {
        super(buildMessage(message, targetClass, memberName, attemptedArgTypes), cause);
        this.targetClass = targetClass;
        this.memberName = memberName;
        this.attemptedArgTypes = (attemptedArgTypes != null) ? attemptedArgTypes : new Class<?>[0];
    }

    private static String buildMessage(String message, Class<?> targetClass, String memberName, Class<?>[] argTypes) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(" [target=").append((targetClass != null) ? targetClass.getName() : "null");
        sb.append(", member=").append(memberName);
        if ((argTypes != null) && (argTypes.length > 0)) {
            sb.append(", argTypes=(");
            sb.append(Arrays.stream(argTypes)
                .map(c -> (c != null) ? c.getSimpleName() : "null")
                .collect(Collectors.joining(", ")));
            sb.append(")");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Creates an exception for when a member cannot be found.
     */
    public static ReflectException memberNotFound(Class<?> targetClass, String memberName) {
        return new ReflectException("Member not found", targetClass, memberName);
    }

    /**
     * Creates an exception for when a method cannot be found with the given argument types.
     */
    public static ReflectException methodNotFound(Class<?> targetClass, String methodName, Class<?>[] argTypes) {
        return new ReflectException("Method not found", targetClass, methodName, argTypes);
    }

    /**
     * Creates an exception for when field access fails.
     */
    public static ReflectException fieldAccessFailed(Class<?> targetClass, String fieldName, Throwable cause) {
        return new ReflectException("Failed to access field", targetClass, fieldName, cause);
    }

    /**
     * Creates an exception for when method invocation fails.
     */
    public static ReflectException methodInvocationFailed(Class<?> targetClass, String methodName, Class<?>[] argTypes, Throwable cause) {
        return new ReflectException("Failed to invoke method", targetClass, methodName, argTypes, cause);
    }

    /**
     * Creates an exception for when argument parsing fails.
     */
    public static ReflectException argumentParseFailed(String rawArg, String expectedType, Throwable cause) {
        return new ReflectException("Failed to parse argument '" + rawArg + "' as " + expectedType, null, null, cause);
    }
}
