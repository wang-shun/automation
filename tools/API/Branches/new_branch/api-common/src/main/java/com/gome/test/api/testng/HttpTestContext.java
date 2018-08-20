package com.gome.test.api.testng;

import com.gome.test.Constant;
import com.gome.test.api.client.HttpMessageClient;
import com.gome.test.api.client.IMessageClient;
import com.gome.test.context.ContextUtils;
import com.gome.test.context.IContext;
import com.gome.test.testng.ContextListener;
import com.gome.test.utils.Logger;
import org.reflections.util.ClasspathHelper;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.net.URL;
import java.util.*;

@Listeners({ContextListener.class})
public class HttpTestContext implements IContext {

    static IMessageClient messageClient;
    String configFile;
    String propFile;
    Collection<URL> urls;

    public HttpTestContext() {
        this.propFile = Constant.API_PROP;
        this.configFile = Constant.API_XML;
        this.urls = ClasspathHelper.forJavaClassPath();
    }

    public HttpTestContext(String configFile, String propFile, List<URL> urls) {
        this.propFile = configFile;
        this.configFile = propFile;
        this.urls = urls;
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpBeforeSuite() throws Exception {
        Logger.info("execute setUpBeforeSuite");
        messageClient = new HttpMessageClient();
        messageClient.setUpBeforeSuite();
        ContextUtils.LoadConfig(configFile, propFile, urls);
    }

    @BeforeTest(alwaysRun = true)
    public void setUpBeforeTest() throws Exception {
        messageClient.setUpBeforeTest();
    }

    @BeforeClass(alwaysRun = true)
    public void setUpBeforeClass() throws Exception {
        messageClient.setUpBeforeClass();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpBeforeMethod(ITestContext context) throws Exception {
        messageClient.setUpBeforeMethod();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownAfterMethod() throws Exception {
        messageClient.tearDownAfterMethod();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownAfterClass() throws Exception {
        messageClient.tearDownAfterClass();
    }

    @AfterTest(alwaysRun = true)
    public void tearDownAfterTest() throws Exception {
        messageClient.tearDownAfterTest();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownAfterSuite() throws Exception {
        context.clear();
        messageClient.tearDownAfterSuite();
    }

    public static void addToContext(String key, String value) {
        ContextUtils.addToContext(key, value);
    }

    public static IMessageClient getMessageClient() {
        return messageClient;
    }
}
