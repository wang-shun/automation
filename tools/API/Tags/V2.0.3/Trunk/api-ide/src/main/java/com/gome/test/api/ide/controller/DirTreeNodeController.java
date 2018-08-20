/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.bo.DirTreeNodeBo;
import com.gome.test.api.ide.model.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Zonglin.Li
 */
@Controller
@RequestMapping(value = "/workbench/dirTree/node")
public class DirTreeNodeController {
    @Autowired
    private DirTreeNodeBo dirTreeNodeBo;

    @RequestMapping(value = "/children", method = RequestMethod.GET)
    @ResponseBody
    public Result getChildren(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "type", required = true) String type) {
        try {
            return new Result(dirTreeNodeBo.getChildren(node, type));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/open", method = RequestMethod.GET)
    @ResponseBody
    public Result openFile(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "parentPath", required = true) String parentPath,
            @RequestParam(value = "type", required = true) String type) {
        try {
            String fullPath = null;
            if ("file".equals(type)) {
                fullPath = String.format("%s/%s", parentPath, node);
            } else if ("folder".equals(type)) {
                fullPath = String.format("%s", node);
            }
            dirTreeNodeBo.openFile(fullPath);
            return new Result(false, fullPath);
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(DirTreeNodeController.class);
}
