package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.model.Result;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

@Controller
@RequestMapping(value = "/help")
public class HelperController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Result getHelper() {
        Map<String, AbstractUrlHandlerMapping> beans
                = appContext.getBeansOfType(AbstractUrlHandlerMapping.class);
        if (null != beans) {
            for (AbstractUrlHandlerMapping bean : beans.values()) {
                Map<String, Object> m = bean.getHandlerMap();
            }
        }
        return new Result();
    }

    @Autowired
    private ApplicationContext appContext;
}
