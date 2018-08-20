package com.gome.test.gui.execute;


import com.gome.test.gui.BusinessBase;
import com.gome.test.gui.annotation.Given;
import com.gome.test.gui.annotation.Then;
import com.gome.test.gui.annotation.When;
import com.gome.test.utils.JsonUtils;
import com.gome.test.utils.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GuiProxy {

    static void process(BusinessBase business, Method method, Object param, String desc) throws Throwable {
        if (business == null)
            throw new Exception("business is null");

        if (method == null)
            throw new Exception("method is null");

        String annotation = getAnnotationName(method);
        long begin = System.currentTimeMillis();
        String func = String.format("%s(%s)", method.getName(), param == null ? "" : JsonUtils.toJson(param));
        Logger.info("执行 [%s %s:%s] 开始", annotation, desc, func);

        try {
            if (param == null)
                method.invoke(business);
            else
                method.invoke(business, param);
        } catch (InvocationTargetException ex) {
            Logger.error("执行 [%s %s:%s] 异常:%s", annotation, desc, func, ex.getTargetException().getMessage());
            if (ex.getCause().getClass() != null && ex.getCause().getClass().equals(AssertionError.class))
                throw new AssertionError(ex.getTargetException().getMessage());
            else
                throw ex.getTargetException();
        } catch (Throwable ex) {
            Logger.error("执行 [%s %s:%s] 异常:%s", annotation, desc, func, ex.getMessage());
            throw ex;
        }

        Logger.info("执行 [%s %s:%s] 结束,耗时 %s ms", annotation, desc, func, System.currentTimeMillis() - begin);
    }

    private static String getAnnotationName(Method method) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            Class cls = annotation.annotationType();
            if (cls == Given.class || cls == Then.class || cls == When.class)
                return cls.getSimpleName();
        }

        return "";
    }
}
