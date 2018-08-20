package com.ofo.test.gui.helper;

import bsh.StringUtil;
import com.gome.test.context.ContextUtils;
import com.gome.test.utils.Logger;
import com.gome.test.utils.StringUtils;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;

public class TestBase {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws Exception {
        Runtime.getRuntime().gc();
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

        if(ContextUtils.getTestCaseIsTimeOut())
        {
            try{

                String outPath =ContextUtils.getContext().containsKey("gui_PicturePath")?ContextUtils.getContext().get("gui_PicturePath").toString():"";
                if(!StringUtils.isEmpty(outPath)) {
                    File screenCaptureDir = new File(outPath);
                    if (screenCaptureDir != null ) {
                        String name = "case_is_timeout";
                        BasePage.helper.captureScreenShot(name, 1100, screenCaptureDir);
                    }
                }else
                {
                    Logger.info("case is TimeOut , screenCaptureDir is null ");
                }

            }catch (Exception ex)
            {
                Logger.info("case is Error");
            }

        }

        BasePage.closeAllCaseWindows();
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest() throws Exception {
        Logger.info("execute beforeTest");
        BasePage.openAndSwitchtoCaseWindow();
    }



}
