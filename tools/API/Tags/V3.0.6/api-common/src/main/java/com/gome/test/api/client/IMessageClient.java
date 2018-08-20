package com.gome.test.api.client;

import com.gome.test.Constant;

import com.gome.test.api.model.Steps;
import com.gome.test.context.ContextUtils;
import com.gome.test.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public abstract class IMessageClient {

    protected String response;
    protected Map<String, String> kvs;
    protected List<Header> responseHeaders;

    public void setUp() throws Exception {
        executeSteps(Constant.SETUP_CLASS, Constant.SETUP_STEPS);
    }

    public abstract String assemble() throws Exception;

    public abstract String send() throws Exception;

    public abstract List<Header> getAllHeaders() throws Exception;

    public abstract void verify() throws Exception;

    public abstract void close() throws Exception;

    public void tearDown() throws Exception {
        executeSteps(Constant.TEARDOWN_CLASS, Constant.TEARDOWN_STEPS);
    }

    public void run(Map<String, String> kvs) throws Exception {
        run(kvs, true);
    }

    public void run(Map<String, String> kvs, boolean doVerify) throws Exception {
        this.kvs = kvs;
        try {
            Logger.info("执行前置条件，进行环境数据准备工作...");
            setUp();

            Logger.info("开始组装要发送的请求...");
            String request = assemble();
            Logger.info(String.format("组装后的请求是: %s", request));
            Logger.info("开始发送请求...");
            response = send();
            Logger.info(String.format("得到的响应是: %s", response));
            responseHeaders = getAllHeaders();
            Logger.info(String.format("得到的响应头是：%s",responseHeaders.toString()));
            if (doVerify) {
                Logger.info("开始检验...");
                verify();
            }
        } catch (Exception ex) {
            Logger.info(ex.toString());
            throw ex;
        }
        finally {
            Logger.info("执行后续处理工作...");
            tearDown();
        }
    }

    public String getResponse() {
        return response;
    }

//    public String getHeader(String key) {}

    public void setUpBeforeSuite() throws Exception {
    }

    public void setUpBeforeTest() throws Exception {
    }

    public void setUpBeforeClass() throws Exception {
    }

    public void setUpBeforeMethod() throws Exception {
    }

    public void tearDownAfterMethod() throws Exception {
    }

    public void tearDownAfterClass() throws Exception {
    }

    public void tearDownAfterTest() throws Exception {
    }

    public void tearDownAfterSuite() throws Exception {
        close();
    }

    private void executeSteps(String className, String stepsName) throws Exception {
        String executeClassName = ContextUtils.loadValueWithContext(kvs.get(className));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!StringUtils.isBlank(executeClassName)) {
            Class<?> loadClass = cl.loadClass(executeClassName);
            Constructor<?> setUpConstructor = loadClass.getConstructor();
            Object setUpObject = setUpConstructor.newInstance();
            Method method = setUpObject.getClass().getMethod(Constant.SET_MESSAGE_CLIENT, IMessageClient.class);
            method.invoke(setUpObject, this);
            Steps steps = Steps.loadFrom(kvs.get(stepsName));
            execute(setUpObject, steps);
        }



    }

    protected abstract void execute(Object object, Steps steps) throws Exception;
}
