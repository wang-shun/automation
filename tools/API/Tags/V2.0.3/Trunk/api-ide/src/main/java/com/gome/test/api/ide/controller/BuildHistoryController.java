package com.gome.test.api.ide.controller;

import com.gome.test.api.ide.bo.BuildBo;

import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/workbench")
public class BuildHistoryController {

    @Autowired
    private BuildBo buildBo;

    @RequestMapping(value = "/history/{tid}/console", method = RequestMethod.GET)
    public void getConsoleLog(@PathVariable Long tid,
                              HttpServletResponse response) {
        try {
            File consoleLog = new File(buildBo.getConsoleLog(tid));
            response.setContentType("text/plain; charset=UTF-8");
            FileUtils.copyFile(consoleLog, response.getOutputStream());
        } catch (Exception ex) {
            response.setContentType("text/plain; charset=UTF-8");
            try {
                response.getWriter().write("Error: ");
                response.getWriter().write(String.valueOf(ex));
            } catch (IOException ioe) {
            }
        }
    }
}
