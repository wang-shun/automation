package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.bo.TestCaseBo;
import com.gome.test.api.ide.model.Result;
import com.gome.test.api.ide.model.TestCase;
import com.gome.test.api.ide.model.editor.TestCaseEditor;
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
@RequestMapping(value = "/workbench/case")
public class TestCaseController {

    @Autowired
    private TestCaseBo testCaseBo;

    @Value(value = "${svn.username}")
    private String username;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(TestCase.class, new TestCaseEditor());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "case", required = true) TestCase testCase) {
        try {
            testCase.setOwner(username);
            return new Result(testCaseBo.add(node, testCase));
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
            testCaseBo.delete(node);
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
            return new Result(testCaseBo.query(node));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "p/query", method = RequestMethod.GET)
    @ResponseBody
    public Result pquery(
            @RequestParam(value = "node", required = true) String node) {
        try {
            return new Result(testCaseBo.pquery(node));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }


    @RequestMapping(value = "/query/response", method = RequestMethod.GET)
    @ResponseBody
    public Result queryResponse(
            @RequestParam(value = "node", required = true) String node) {
        try {
            return new Result(testCaseBo.queryResponse(node));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/move", method = RequestMethod.GET)
    @ResponseBody
    public Result move(
            @RequestParam(value = "moved_node", required = true) String moved_node,
            @RequestParam(value = "target_node", required = true) String target_node,
            @RequestParam(value = "position", required = true) String position,
            @RequestParam(value = "parent_node", required = true) String parent_node) {
        try {
            return new Result(testCaseBo.move(moved_node, target_node, parent_node, position));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "case", required = true) TestCase testCase) {
        try {
            LOG.info(String.format("update %s owner: %s", node, username));
            testCase.setOwner(username);
            return new Result(testCaseBo.update(node, testCase));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/update/case/name", method = RequestMethod.POST)
    @ResponseBody
    public Result updateCaseName(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "case", required = true) TestCase testCase) {
        try {
            testCase.setOwner(username);
            return new Result(testCaseBo.updateCaseName(node, testCase));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/id", method = RequestMethod.GET)
    @ResponseBody
    public Result getId(
            @RequestParam(value = "node", required = true) String node) {
        try {
            String part1 = node.split("#")[0];
            part1 = part1.substring(0, part1.length() - 5);
            String part2 = node.split("#")[2];
            String caseId = String.format("test_%s_%s", part1.replaceAll("\\/", "_"), part2);
            return new Result(caseId);
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    @RequestMapping(value = "/update/response", method = RequestMethod.POST)
    @ResponseBody
    public Result updateResponse(
            @RequestParam(value = "node", required = true) String node,
            @RequestParam(value = "responseCopy", required = true) String responseCopy) {
        try {
            return new Result(testCaseBo.updateResponse(node, responseCopy));
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, String.valueOf(ex));
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestCaseController.class);
}
