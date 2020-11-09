package net.binis.codegen.annotation;

import java.lang.annotation.*;

@CodeAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CodeFieldAnnotations {

    String[] value();

}
