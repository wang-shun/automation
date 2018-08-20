package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.bo.BuildBo;
import com.gome.test.api.ide.bo.ProcBo;
import com.gome.test.api.ide.bo.SvnBo;
import com.gome.test.api.ide.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/workbench")
public class WorkBenchController {

    @Autowired
    @Qualifier("testsSvnBo")
    private SvnBo svnBo;

    @Autowired
    private BuildBo buildBo;

    @Autowired
    private ProcBo procBo;

    @Value(value = "${base.dir}")
    private String baseDir;

    @Value(value = "mvn clean compile -DskipTests=true")
    private String cmd;

    @Value(value = "${os.name}")
    private String osName;

    private static final Logger LOG = LoggerFactory.getLogger(
            WorkBenchController.class);

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update() {
        try {
            svnBo.update();
            return new Result();
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, ex.getMessage());
        }
    }

    @RequestMapping(value = "/commit", method = RequestMethod.POST)
    @ResponseBody
    public Result commit() {
        try {
            svnBo.commit();
            return new Result();
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, ex.getMessage());
        }
    }

    @RequestMapping(value = "/compile", method = RequestMethod.POST)
    @ResponseBody
    public Result compile() {
        try {
            int rc;
            if (!osName.toLowerCase().contains("win")) {
                rc = procBo.run(baseDir, buildBo.getNextBuildLog(), cmd);
            } else {
                rc = procBo.run(baseDir, buildBo.getNextBuildLog(),
                        String.format("cmd.exe /c %s", cmd));
            }
            if (0 == rc) {
                return new Result();
            } else {
                return new Result(true, String.format("编译返回值为%d", rc));
            }
        } catch (Exception ex) {
            LOG.error("Exception:", ex);
            return new Result(true, ex.getMessage());
        }
    }
}
