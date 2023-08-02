package net.binis.codegen.annotation.augment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Set;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeAugment {

    AugmentType adds();
    AugmentTargetType targets() default AugmentTargetType.EVERYTHING;
    AugmentTargetTypeSeverity severity() default AugmentTargetTypeSeverity.ERROR;
    String name() default "";
    String type() default "";
    long modifier() default Modifier.PUBLIC;
    CodeAugmentParameters parameters() default @CodeAugmentParameters;

}
