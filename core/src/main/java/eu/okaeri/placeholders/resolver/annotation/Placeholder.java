package eu.okaeri.placeholders.resolver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class, method, or field for automatic placeholder resolution.
 * <p>
 * When applied to a class, all public getters (getXxx/isXxx methods) are
 * automatically discovered and exposed as placeholders:
 * <pre>{@code
 * @Placeholder
 * public class Player {
 *     public String getName() { return "John"; }  // exposes {player.name}
 *     public boolean isOnline() { return true; }  // exposes {player.online}
 * }
 * }</pre>
 *
 * <h2>Custom Names</h2>
 * Apply to a method or field to expose with a custom name:
 * <pre>{@code
 * @Placeholder(name = "hp")
 * public int getHealth() { return 100; }  // exposes {player.hp} instead of {player.health}
 * }</pre>
 *
 * <h2>Disabling Auto-Scan</h2>
 * Use {@code scan = false} on the class to only expose explicitly annotated members:
 * <pre>{@code
 * @Placeholder(scan = false)
 * public class Config {
 *     @Placeholder public String getPublicValue() { ... }  // exposed
 *     public String getPrivateValue() { ... }              // NOT exposed
 * }
 * }</pre>
 *
 * @see AnnotationResolver
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Placeholder {

    /**
     * Custom name for this placeholder.
     * <p>
     * When empty (default), the name is derived from the method/field name:
     * <ul>
     *   <li>{@code getName()} → {@code name}</li>
     *   <li>{@code isActive()} → {@code active}</li>
     *   <li>{@code count} (field) → {@code count}</li>
     * </ul>
     * <p>
     * Not applicable when annotating a class.
     *
     * @return The custom placeholder name, or empty to derive automatically
     */
    String name() default "";

    /**
     * Whether to auto-scan all public getters when annotating a class.
     * <p>
     * When {@code true} (default), all public {@code getXxx()} and {@code isXxx()}
     * methods are automatically exposed as placeholders.
     * <p>
     * When {@code false}, only explicitly {@code @Placeholder}-annotated methods
     * and fields are exposed.
     * <p>
     * Only applicable when annotating a class (ignored on methods/fields).
     *
     * @return Whether to auto-scan getters (default true)
     */
    boolean scan() default true;
}
