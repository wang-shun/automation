package com.gome.test.api.testng;

import com.gome.test.api.model.Constant;
import com.gome.test.api.utils.ApiUtils;
import org.testng.IRetryAnalyzer;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.Reporter;
import com.gome.test.utils.*;

public class TestngRetry implements IRetryAnalyzer {
    private int retryCount = 1;
    private static int maxRetryCount = 2;

    @Override
    public boolean retry(ITestResult result) {
        ISuite suite = result.getTestContext().getSuite();
        if (suite != null) {
            // 判定为非Ordercase
            if (suite.getParameter(Constant.SKIP_ONCE_FAIL) == null) {
                try {
                    maxRetryCount = Integer.valueOf(ApiUtils.getString("maxRetryCount", String.valueOf(maxRetryCount)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (retryCount <= maxRetryCount) {
                    String message = "Retry for [" + result.getName() + "] on class [" + result.getTestClass().getName() + "] Retry " + retryCount + " times";
                    Logger.info(message);
                    Reporter.setCurrentTestResult(result);
                    Logger.info("RunCount=" + (retryCount + 1));
                    retryCount++;
                    return true;
                }
                return false;
            }
        }

        return false;
    }

    public static int getMaxRetryCount() {
        return maxRetryCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

}
