package com.gome.test.mock.controller;


import com.gome.test.mock.bo.LeftTreeService;
import com.gome.test.mock.dao.ApiDao;
import com.gome.test.mock.dao.HostDao;
import com.gome.test.mock.model.bean.Api;
import com.gome.test.mock.model.bean.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2015/10/14.
 */

@Controller
@ComponentScan
@RequestMapping(value = "/help")
public class helpController {
//    @Autowired
//    public  hostsDao;
    @Autowired
    public ApiDao apiDao;

    @Autowired
    public HostDao hostDao;

    @Autowired
    public LeftTreeService leftTreeService;

    @RequestMapping(value = "/jsonpath")
    public ModelAndView getJsonpath() {
        try
        {
            Api api=new Api();
            api.setApiName("aaa");
            api.setDescript("");
            api.setKeyWords("");

        apiDao.save(api);
         List lst= apiDao.getAll();
         List<Host> list=hostDao.getAll();
            leftTreeService.getTreeNode("0");

        }catch (Exception ex)
        {

        }
        return new ModelAndView("jsonpath");
    }

    @RequestMapping(value = "/tree")
    public ModelAndView gethelp() {

        return new ModelAndView("help");
    }
}
