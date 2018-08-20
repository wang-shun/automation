package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.bo.MenuBo;
import com.gome.test.api.ide.model.Message;
import com.gome.test.api.ide.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/workbench/menu")
public class KeyWordController {

    @Autowired
    private MenuBo menuBo;

    private static final Logger LOG = LoggerFactory.getLogger(
            KeyWordController.class);

    @RequestMapping(value = "/setup/query", method = RequestMethod.GET)
    @ResponseBody
    public Result getSetUpMenu(
            @RequestParam(value = "setUpClass", required = true) String setUpClass) {
        try {
            return new Result(menuBo.getSetUpMenu(setUpClass));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.format(Message.CLASS_NOT_FOUND, setUpClass));
        }
    }

    @RequestMapping(value = "/verify/query", method = RequestMethod.GET)
    @ResponseBody
    public Result getVerifyMenu(
            @RequestParam(value = "verifyClass", required = true) String verifyClass) {
        try {
            return new Result(menuBo.getVerifyMenu(verifyClass));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.format(Message.CLASS_NOT_FOUND, verifyClass));
        }
    }

    @RequestMapping(value = "/teardown/query", method = RequestMethod.GET)
    @ResponseBody
    public Result getTearDownMenu(
            @RequestParam(value = "tearDownClass", required = true) String tearDownClass) {
        try {
            return new Result(menuBo.getTearDownMenu(tearDownClass));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.format(Message.CLASS_NOT_FOUND, tearDownClass));
        }
    }
}
