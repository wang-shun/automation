package com.gome.test.mq.model;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by zhangjiadi on 15/12/22.
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)

public @interface DictionaryMap {

    String parent() default "";

    String keyColName() default "";
}
