package com.gome.test.gui.helper;

import com.gome.test.utils.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.io.IOException;

public class TestBase {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws Exception {
        Logger.info("execute beforeSuite");
        GuiHelper.killDriver();
        Thread.sleep(3000);
        BasePage.initDriver();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() throws IOException, InterruptedException {
        Logger.info("execute afterSuite");
        BasePage.driver.quit();
        Thread.sleep(3000);
        GuiHelper.killDriver();
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        Logger.info("execute afterTest");
        BasePage.closeAllCaseWindows();
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest() throws Exception {
        Logger.info("execute beforeTest");
        BasePage.openAndSwitchtoCaseWindow();
    }
}
