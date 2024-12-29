package org.va.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to validate that a field contains only numeric strings.
 * <p>
 * It can be applied to fields, and during validation, the specified message
 * can be displayed if the field does not meet the numeric requirement.
 * </p>
 * @Retention(RetentionPolicy.RUNTIME) Indicates the annotation is retained at runtime.
 * @Target(ElementType.FIELD) Specifies that this annotation can only be applied to fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IsNumber {
    String message() default "Field should have be Number";
}
