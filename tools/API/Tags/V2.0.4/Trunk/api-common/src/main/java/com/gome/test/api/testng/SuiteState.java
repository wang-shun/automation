package com.gome.test.api.testng;

import java.util.Map;

import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;

public class SuiteState {

    private boolean hasFail;
    private boolean hasSkip;

    public SuiteState(ISuite suite) {
        hasFail = false;
        hasSkip = false;
        Map<String, ISuiteResult> suiteResults = suite.getResults();
        for (ISuiteResult sr : suiteResults.values()) {
            ITestContext tc = sr.getTestContext();
            int failedTests = tc.getFailedTests().getAllResults().size();
            int skippedTests = tc.getSkippedTests().getAllResults().size();
            if (failedTests > 0) {
                hasFail = true;
            }
            if (skippedTests > 0) {
                hasSkip = true;
            }
        }
    }

    public boolean hasFail() {
        return hasFail;
    }

    public boolean hasSkip() {
        return hasSkip;
    }
}
