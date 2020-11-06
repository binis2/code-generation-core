package net.binis.codegen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CodeAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Ignore {

    boolean forField() default false;
    boolean forClass() default false;
    boolean forInterface() default false;
    boolean forModifier() default false;

}
