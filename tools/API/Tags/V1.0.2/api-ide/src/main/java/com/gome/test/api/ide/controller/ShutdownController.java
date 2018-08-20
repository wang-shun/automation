package com.gome.test.api.ide.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ShutdownController {

    @Autowired
    private ApplicationContext context;

    @RequestMapping(value = "/shutdown", method = RequestMethod.POST)
    public void shutdown(HttpServletResponse response) {
        ((AbstractApplicationContext) context).close();
    }
}
