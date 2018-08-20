package com.gome.test.api.testng;

import com.gome.test.api.model.Constant;
import com.gome.test.utils.Props;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.text.MessageFormat;

public class ContextListener extends TestListenerAdapter {

    public static final String API_PROP = "/api.properties";

    @Override
    public void onTestStart(ITestResult tr) {
        ISuite suite = tr.getTestContext().getSuite();
        if (BaseConfig.getContext() != null) {
            if (suite != null) {
                Logger.info("BeforeMethod clean begin");
                // 判定为非Ordercase
                if (suite.getParameter("skipOnceFail") == null) {
                    BaseConfig.getContext().clear();
                    BaseConfig.readApiProp(Object.class.getResourceAsStream(Constant.API_PROP));
                    Logger.info("BeforeMethod clean end");
                }
            }
        }

        super.onTestStart(tr);
    }

}
