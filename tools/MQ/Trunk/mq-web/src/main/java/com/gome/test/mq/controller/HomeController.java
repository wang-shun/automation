package com.gome.test.mq.controller;



import com.gome.test.mq.bo.LeftTreeService;
import com.gome.test.mq.bo.SvnTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Administrator on 2015/10/14.
 */
@Controller
@ComponentScan
@RequestMapping(value = "")
public class HomeController {




    @RequestMapping(value = "/")
    public ModelAndView home() {

        return new ModelAndView("index");
    }


    @RequestMapping(value = "/left")
    public ModelAndView leftpage() {

        return new ModelAndView("left");
    }

    @RequestMapping(value = "/home")
    public ModelAndView gohome() {

        return new ModelAndView("home");
    }

    @RequestMapping(value = "/clean")
    public ModelAndView clean() {
        SvnTemplateUtil.createTemplate();
        LeftTreeService.clean();
        return home();
    }
}
