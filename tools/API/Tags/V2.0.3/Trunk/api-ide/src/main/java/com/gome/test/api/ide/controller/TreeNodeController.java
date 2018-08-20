package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.bo.TreeNodeBo;
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
@RequestMapping("/workbench/tree/node")
public class TreeNodeController {

    @Autowired
    private TreeNodeBo treeNodeBo;

    @RequestMapping(value = "/children", method = RequestMethod.GET)
    @ResponseBody
    public Result getChildren(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "type", required = true) String type) {
        try {
            return new Result(treeNodeBo.getChildren(node, type));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteNode(
            @RequestParam(value = "node", required = true) String node) {
        try {
            treeNodeBo.deleteNode(node);
            return new Result();
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/folder/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addFolder(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "folder", required = true) String name) {
        try {
            return new Result(treeNodeBo.addFolder(node, name));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/file/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addFile(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "file", required = true) String name) {
        try {
            return new Result(treeNodeBo.addFile(node, name));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/suite/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addSuite(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "suite", required = true) String name) {
        try {
            return new Result(treeNodeBo.addSuite(node, name));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/psuite/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addPSuite(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "suite", required = true) String name) {
        try {
            return new Result(treeNodeBo.addPSuite(node, name));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/psuitetpl/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addPSuiteTpl(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "suite", required = true) String name) {
        try {
            return new Result(treeNodeBo.addPSuiteTpl(node, name));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    //added by zonglin.li
    @RequestMapping(value = "/devsuite/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addDevSuite(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "suite", required = true) String name) {
        try {
            return new Result(treeNodeBo.addDevSuite(node, name));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }


    @RequestMapping(value = "/order/suite/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addOrderSuite(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "ordersuite", required = true) String name) {
        try {
            return new Result(treeNodeBo.addOrderSuite(node, name));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/suite/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateSuite(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "suite", required = true) String name,
            @RequestParam(value = "newsuite", required = true) String newsuite) {
        try {
            return new Result(treeNodeBo.updateSuite(node, name, newsuite));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/psuitetpl/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updatePSuiteTpl(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "suite", required = true) String name,
            @RequestParam(value = "newsuite", required = true) String newsuite) {
        try {
            return new Result(treeNodeBo.updatePSuiteTpl(node, name, newsuite));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/psuite/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updatePSuite(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "suite", required = true) String name,
            @RequestParam(value = "newsuite", required = true) String newsuite) {
        try {
            return new Result(treeNodeBo.updatePSuite(node, name, newsuite));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/order/suite/update", method = RequestMethod.POST)
    @ResponseBody
    public Result updateOrderSuite(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "ordersuite", required = true) String name,
            @RequestParam(value = "newordersuite", required = true) String newsuite) {
        try {
            return new Result(treeNodeBo.updateOrderSuite(node, name, newsuite));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(TreeNodeController.class);
}
