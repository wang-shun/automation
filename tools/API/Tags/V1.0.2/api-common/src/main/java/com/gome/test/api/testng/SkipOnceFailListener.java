package com.gome.test.api.testng;

import java.util.HashSet;
import java.util.Set;

import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.TestListenerAdapter;

public class SkipOnceFailListener extends TestListenerAdapter {

    private Set<ISuite> failedSuites;

    public SkipOnceFailListener() {
        super();
        failedSuites = new HashSet<ISuite>();
    }

    @Override
    public void onTestStart(ITestResult tr) {
        ISuite suite = tr.getTestContext().getSuite();
        boolean skipOnceFail = Boolean.valueOf(suite.getParameter("skipOnceFail"));
        if (hasFail(suite) && skipOnceFail) {
            throw new SkipException("skipping as previous test step failed");
        }
        super.onTestStart(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        ISuite suite = tr.getTestContext().getSuite();
        failedSuites.add(suite);
        super.onTestFailure(tr);
    }

    private boolean hasFail(ISuite suite) {
        return failedSuites.contains(suite);
    }
}
