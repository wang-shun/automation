package com.gome.test.gui.execute;


import com.gome.test.context.ContextUtils;
import com.gome.test.gui.BusinessBase;
import com.gome.test.gui.annotation.Given;
import com.gome.test.gui.annotation.Then;
import com.gome.test.gui.annotation.When;
import com.gome.test.gui.helper.BasePage;
import com.gome.test.utils.JsonUtils;
import com.gome.test.utils.Logger;
import org.apache.http.impl.io.ContentLengthInputStream;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GuiProxy {

    static void process(BusinessBase business, Method method, Object param, String desc) throws Throwable  {
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
        }
        catch (InvocationTargetException ex) {

            if(ex.getTargetException().getMessage().contains("Timed out waiting for page load"))
            {
                boolean isHasTimeError=false;
                String key ="gui_HasTimeOutError";

                if(ContextUtils.getContext().keySet().contains(key))
                    isHasTimeError =(Boolean)ContextUtils.getContext().get(key);
                try {
                    WebDriverWait wait = new WebDriverWait(BasePage.driver, 60);
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                    BasePage.driver.findElement(By.tagName("body")).sendKeys("Keys.ESCAPE");
                }catch (Exception e)
                {
                    Logger.error("执行 [%s %s:%s] 异常:%s", annotation, desc, func, "页面加载超时,body获取失败");
                }

                try {
                    Actions actions = new Actions(BasePage.driver);
                    actions.sendKeys(Keys.ESCAPE).perform();
                    Logger.warn("执行 [%s %s:%s] 异常:%s", annotation, desc, func,"页面加载超时,停止页面加载.url:"+ BasePage.driver.getCurrentUrl());
                    isHasTimeError=false;
                    ContextUtils.getContext().put(key,isHasTimeError);

                }catch (Exception e)
                {
                    Logger.error("执行 [%s %s:%s] 异常:%s", annotation, desc, func,"停止加载页面异常:"+e.getMessage());
                    if (ex.getCause().getClass() != null && ex.getCause().getClass().equals(AssertionError.class))
                        throw new AssertionError(e.getMessage());
                    else
                        throw e;
                }


            }else {
                Logger.error("执行 [%s %s:%s] 异常:%s", annotation, desc, func, ex.getTargetException().getMessage());
                if (ex.getCause().getClass() != null && ex.getCause().getClass().equals(AssertionError.class))
                    throw new AssertionError(ex.getTargetException().getMessage());
                else
                    throw ex.getTargetException();
            }
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
