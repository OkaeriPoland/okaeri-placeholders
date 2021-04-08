package eu.okaeri.placeholders.schema.annotation;

import eu.okaeri.placeholders.schema.resolver.DefaultSchemaResolver;
import eu.okaeri.placeholders.schema.resolver.SchemaResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Placeholder {
    String name() default "";
    Class<? extends SchemaResolver> resolver() default DefaultSchemaResolver.class;
}
