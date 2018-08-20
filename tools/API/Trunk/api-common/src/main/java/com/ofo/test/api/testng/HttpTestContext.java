package com.ofo.test.api.testng;

import com.ofo.test.Constant;
import com.ofo.test.api.client.HttpMessageClient;
import com.ofo.test.api.client.IMessageClient;
import com.ofo.test.context.ContextUtils;
import com.ofo.test.context.IContext;
import com.ofo.test.testng.ContextListener;
import com.ofo.test.utils.Logger;
import org.reflections.util.ClasspathHelper;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.net.URL;
import java.util.Collection;
import java.util.List;

@Listeners({ContextListener.class})
public class HttpTestContext  implements IContext   {

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
        ContextUtils.LoadConfig(configFile, propFile, urls);
        messageClient = new HttpMessageClient();
        messageClient.setUpBeforeSuite();
//        ContextUtils.LoadConfig(configFile, propFile, urls);
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
