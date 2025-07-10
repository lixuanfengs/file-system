package net.cactus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to define table column properties on a POJO field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableColumn {

    /**
     * The key/property name to be used in the template.
     * If empty, the field name will be used.
     */
    String key() default "";

    /**
     * The display label for the column header.
     */
    String label();

    /**
     * The type of the column for special rendering (e.g., 'date', 'size', 'badge').
     * Defaults to 'default'.
     */
    String type() default "default";

    /**
     * The display order of the column, lower numbers first.
     */
    int order();

    /**
     * Whether the column is visible by default.
     */
    boolean visible() default true;
}
