package com.gome.test.mock.controller;

import com.gome.test.mock.bo.LeftTreeService;
import com.gome.test.mock.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Administrator on 2015/10/14.
 */
@Controller
@ComponentScan
@RequestMapping(value = "/leftTree")
public class LefTreeController {



    @Autowired
    private LeftTreeService leftTreeService;

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    @ResponseBody
    public Result getHelperTreeData(
            @RequestParam(value = "pid", required = true) String pid,
            @RequestParam(value = "pvalue", required = true) String pvalue
    ) {
        try {

            return new Result(leftTreeService.getNodes(pid,pvalue));

        } catch (Exception ex) {

            return new Result(true, String.valueOf(ex));
        }
    }
}
