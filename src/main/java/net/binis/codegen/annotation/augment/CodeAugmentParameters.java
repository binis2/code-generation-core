package net.binis.codegen.annotation.augment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeAugmentParameters {
        CodeAugmentParameter[] params() default {};

        String filter() default "";

}
