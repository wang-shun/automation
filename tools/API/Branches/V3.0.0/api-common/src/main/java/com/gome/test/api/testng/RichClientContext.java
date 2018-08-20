package com.gome.test.api.testng;


import com.gome.test.Constant;
import com.gome.test.context.ContextUtils;
import com.gome.test.context.IContext;
import com.gome.test.testng.ContextListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

@Listeners({ContextListener.class})
@ContextConfiguration(locations = {Constant.APPLICATION_ALL_XML})
public class RichClientContext extends AbstractTestNGSpringContextTests implements IContext {

    @BeforeSuite(alwaysRun = true)
    public void setUpBeforeSuite() throws Exception {
        ContextUtils.LoadConfig();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownAfterSuite() throws Exception {
        context.clear();
    }
}