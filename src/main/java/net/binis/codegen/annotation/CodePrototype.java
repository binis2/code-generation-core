package net.binis.codegen.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@CodeAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface CodePrototype {

    String name() default "";
    String interfaceName() default "";
    boolean generateConstructor() default true;
    boolean generateInterface() default true;
    boolean generateModifier() default false;
    boolean base() default false;
    boolean interfaceSetters() default true;
    boolean classGetters() default true;
    boolean classSetters() default true;
    Class<?> baseModifierClass() default void.class;
    Class<?> creatorClass() default void.class;
    boolean creatorModifier() default false;
    Class<?> mixInClass() default void.class;
    String implementationPackage() default "";

}
