package com.gome.test.api.ide.controller;

import com.gome.test.api.client.IMessageClient;
import com.gome.test.api.ide.model.Result;
import com.gome.test.api.ide.model.TestCase;
import com.gome.test.api.ide.model.editor.TestCaseEditor;
import com.gome.test.api.model.Constant;
import com.gome.test.api.testng.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RequestController {

    @Value(value = "${app.classpath}")
    private String classpaths;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(TestCase.class, new TestCaseEditor());
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    @ResponseBody
    public Result send(
            @RequestParam(value = "case", required = true) TestCase testCase) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            URLClassLoader ucl = new URLClassLoader(getAppClassPath(), cl);
            Thread.currentThread().setContextClassLoader(ucl);
            Map<String, String> kvs = testCase.getParams();
            BaseConfig config = new HttpBaseConfig(getConfigFile(),getPropFile(),
                    Arrays.asList(ucl.getURLs()));
            try {
            	config.setUpBeforeSuite();
                IMessageClient mc = BaseConfig.getMessageClient();
                mc.run(kvs, false);
                String response = mc.getResponse();
                return new Result(response);
            } finally {
                try {
                    config.tearDownAfterSuite();
                } catch (Exception ex) {
                    LOG.error("Exception", ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception", ex);
            return new Result(true, String.valueOf(ex));
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    @ResponseBody
    public Result run(
            @RequestParam(value = "case", required = true) TestCase testCase) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            URLClassLoader ucl = new URLClassLoader(getAppClassPath(), cl);
            Thread.currentThread().setContextClassLoader(ucl);
            Map<String, String> kvs = testCase.getParams();
            BaseConfig config = new HttpBaseConfig(getConfigFile(),getPropFile(),
                    Arrays.asList(ucl.getURLs()));
            
            try {
            	config.setUpBeforeSuite();
                IMessageClient mc = BaseConfig.getMessageClient();
                mc.run(kvs);
                String response = mc.getResponse();
            } finally {
                config.tearDownAfterSuite();
            }
            return new Result(bos.toString());
        } catch (Exception ex) {
            LOG.error("Exception", ex);
            try {
                return new Result(true, String.valueOf(ex), bos.toString());
            } catch (Exception e) {
                LOG.error("Exception", e);
                return new Result(true, String.valueOf(e));
            }
        } finally {
            System.setOut(System.out);
            System.setErr(System.err);
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    private URL[] getAppClassPath() throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        for (String classPath : classpaths.split(";")) {
            if (!classPath.isEmpty()) {
                urls.add(new File(classPath).toURI().toURL());
            }
        }
        return urls.toArray(new URL[0]);
    }

    private String getConfigFile() {
        for (String classPath : classpaths.split(";")) {
        	File f = new File(classPath, Constant.API_XML);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
        }
        return null;
    }

    private InputStream getPropFile() throws FileNotFoundException {
        for (String classPath : classpaths.split(";")) {
        	File f = new File(classPath, Constant.API_PROP);
            if (f.exists()) {
            	return new FileInputStream(f);
            }
        }
        return null;
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(
            RequestController.class);
}
