package com.gome.test.api.client;

import com.gome.test.api.testng.Logger;

import java.util.Map;

public abstract class IMessageClient {

    protected String response;
    protected Map<String, String> kvs;

    public abstract void setUp() throws Exception;

    public abstract String assemble() throws Exception;

    public abstract String send() throws Exception;

    public abstract void verify() throws Exception;

    public abstract void close() throws Exception;

    public abstract void tearDown() throws Exception;

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
            if (doVerify) {
                Logger.info("开始检验...");
                verify();
            }
        } finally {
            Logger.info("执行后续处理工作...");
            tearDown();
        }
    }

    public String getResponse() {
        return response;
    }

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
}
