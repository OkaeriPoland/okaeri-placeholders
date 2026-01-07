package eu.okaeri.placeholders.fixture;

import eu.okaeri.placeholders.Placeholders;
import org.junit.jupiter.api.extension.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JUnit 5 extension for injecting Placeholders instances into test methods.
 * <p>
 * Usage:
 * <pre>{@code
 * @ExtendWith(PlaceholdersExtension.class)
 * class MyTest {
 *     @Test
 *     void testSomething(Placeholders placeholders) {
 *         // placeholders with defaults registered
 *     }
 *
 *     @Test
 *     @WithPlaceholders(defaults = false)
 *     void testWithoutDefaults(Placeholders placeholders) {
 *         // placeholders without defaults
 *     }
 * }
 * }</pre>
 */
public class PlaceholdersExtension implements ParameterResolver, BeforeEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE =
        ExtensionContext.Namespace.create(PlaceholdersExtension.class);

    private static final String PLACEHOLDERS_KEY = "placeholders";

    @Override
    public void beforeEach(ExtensionContext context) {
        // Create placeholders instance before each test
        var config = findConfiguration(context);
        var placeholders = Placeholders.create(config.defaults());

        // Store in context for parameter resolution
        context.getStore(NAMESPACE).put(PLACEHOLDERS_KEY, placeholders);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == Placeholders.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(PLACEHOLDERS_KEY, Placeholders.class);
    }

    private WithPlaceholders findConfiguration(ExtensionContext context) {
        // Check method first, then class
        return context.getTestMethod()
            .map(method -> method.getAnnotation(WithPlaceholders.class))
            .or(() -> context.getTestClass().map(clazz -> clazz.getAnnotation(WithPlaceholders.class)))
            .orElse(DefaultConfig.INSTANCE);
    }

    /**
     * Annotation to customize Placeholders configuration for a test method or class.
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WithPlaceholders {
        /**
         * Whether to register default placeholders (DefaultPlaceholderPack).
         * Default is true.
         */
        boolean defaults() default true;
    }

    /**
     * Default configuration when no annotation is present.
     */
    @WithPlaceholders(defaults = true)
    private static final class DefaultConfig {
        static final WithPlaceholders INSTANCE =
            DefaultConfig.class.getAnnotation(WithPlaceholders.class);
    }
}
