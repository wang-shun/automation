package com.ofo.test.api.annotation;

import com.ofo.test.Constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CaseOwner {

    public String description() default Constant.CASE_OWNER;

}