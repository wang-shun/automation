package com.gome.test.mock.controller;


import com.gome.test.mock.dao.HostDao;
import com.gome.test.mock.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2015/10/14.
 */
@Controller
@ComponentScan
@RequestMapping(value = "")
public class HomeController {

    @Autowired
    private HostDao hostDao;

    @RequestMapping(value = "/")
    public ModelAndView home() {

        return new ModelAndView("index");
    }


    @RequestMapping(value = "/left")
    public ModelAndView leftpage() {

        return new ModelAndView("left");
    }
}
