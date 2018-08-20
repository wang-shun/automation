package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.bo.OrderCaseBo;
import com.gome.test.api.ide.model.OrderCase;
import com.gome.test.api.ide.model.Result;
import com.gome.test.api.ide.model.editor.OrderCaseEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/workbench/order/case")
public class OrderCaseController {

    @Autowired
    private OrderCaseBo orderCaseBo;

    @Value(value = "${svn.username}")
    private String username;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(OrderCase.class, new OrderCaseEditor());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "ordercase", required = true) OrderCase orderCase) {
        try {
            orderCase.setOwner(username);
            return new Result(orderCaseBo.add(node, orderCase));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(
            @RequestParam(value = "node", required = true) String node) {
        try {
            orderCaseBo.delete(node);
            return new Result();
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public Result query(
            @RequestParam(value = "node", required = true) String node) {
        try {
            return new Result(orderCaseBo.query(node));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "ordercase", required = true) OrderCase orderCase) {
        try {
            orderCase.setOwner(username);
            return new Result(orderCaseBo.update(node, orderCase));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/update/order/case/name", method = RequestMethod.POST)
    @ResponseBody
    public Result updateCaseName(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "ordercase", required = true) OrderCase orderCase) {
        try {
            orderCase.setOwner(username);
            return new Result(orderCaseBo.updateCaseName(node, orderCase));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }
//
//    @RequestMapping(value = "/id", method = RequestMethod.GET)
//    @ResponseBody
//    public Result getId(
//            @RequestParam(value = "node", required = true) String node) {
//        try {
//            String part1 = node.split("#")[0];
//            part1 = part1.substring(0, part1.length() - 5);
//            String part2 = node.split("#")[2];
//            String caseId = String.format("test_%s_%s", part1.replaceAll("\\/", "_"), part2);
//            return new Result(caseId);
//        } catch (Exception ex) {
//            LOG.error("Exception:", ex);
//            return new Result(true, String.valueOf(ex));
//        }
//    }

    private static final Logger LOG = LoggerFactory.getLogger(OrderCaseController.class);
}
