package com.gome.test.gui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Given {
    String description() default "";
}
