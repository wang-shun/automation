package com.gome.test.api.testng;


import com.gome.test.api.model.Constant;
import com.gome.test.api.utils.ApiUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

@Listeners({ContextListener.class})
@ContextConfiguration(locations = {Constant.APPLICATION_ALL_XML})
public class RichClientContext extends AbstractTestNGSpringContextTests implements IApiTestContext {

    @BeforeSuite(alwaysRun = true)
    public void setUpBeforeSuite() throws Exception {
        ApiUtils.LoadConfig();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownAfterSuite() throws Exception {
        context.clear();
    }
}