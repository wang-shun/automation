package com.gome.test.api.testng;

import com.gome.test.api.model.Constant;
import com.gome.test.api.utils.ApiUtils;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import com.gome.test.utils.Logger;

public class ContextListener extends TestListenerAdapter {

    @Override
    public void onTestStart(ITestResult tr) {
        ISuite suite = tr.getTestContext().getSuite();
        if (ApiUtils.getContext() != null) {
            if (suite != null) {
                Logger.info(" Case %s 开始", tr.getName());
                // 判定为非Ordercase
                if (suite.getParameter(Constant.SKIP_ONCE_FAIL) == null) {
                    ApiUtils.getContext().clear();
                }
                IApiTestContext.testContext.put(Constant.TEST_RESULT_CONTEXT, tr);
            }
        }

        super.onTestStart(tr);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        Logger.info("Case %s 成功结束", tr.getName());
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        Logger.info("Case %s 执行失败", tr.getName());
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        Logger.info("Case %s 跳过", tr.getName());
    }

    @Override
    public void onStart(ITestContext testContext) {
        IApiTestContext.testContext.put(Constant.TEST_CONTEXT,testContext);
        super.onStart(testContext);
    }
}