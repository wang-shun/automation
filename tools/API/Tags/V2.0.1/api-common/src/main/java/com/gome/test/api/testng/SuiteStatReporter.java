package com.gome.test.api.testng;

import java.util.ArrayList;
import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

public class SuiteStatReporter implements IReporter {

    private final List<ISuite> passedSuites;
    private final List<ISuite> failedSuites;
    private final List<ISuite> skippedSuites;

    public SuiteStatReporter() {
        passedSuites = new ArrayList<ISuite>();
        failedSuites = new ArrayList<ISuite>();
        skippedSuites = new ArrayList<ISuite>();
    }

    public int getPassedSuitesCount() {
        return passedSuites.size();
    }

    public int getFailedSuitesCount() {
        return failedSuites.size();
    }

    public int getSkippedSuitesCount() {
        return skippedSuites.size();
    }

    public int getTotalSuitesCount() {
        return (getPassedSuitesCount() + getFailedSuitesCount() + getSkippedSuitesCount());
    }

    public List<ISuite> getPassedSuites() {
        return passedSuites;
    }

    public List<ISuite> getFailedSuites() {
        return failedSuites;
    }

    public List<ISuite> getSkippedSuites() {
        return skippedSuites;
    }

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        for (ISuite suite : suites) {
            SuiteState state = new SuiteState(suite);
            if (state.hasFail()) {
                failedSuites.add(suite);
            } else if (state.hasSkip()) {
                skippedSuites.add(suite);
            } else {
                passedSuites.add(suite);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------------\n");
        sb.append("Suites Statistic\n");
        sb.append(String.format("Total run: %d, Failures: %d, Skips: %d\n",
                getTotalSuitesCount(), getFailedSuitesCount(), getSkippedSuitesCount()));
        if (0 < getFailedSuitesCount()) {
            sb.append("- Failures\n");
        }
        for (ISuite suite : failedSuites) {
            sb.append(String.format("\t%s\n", suite.getName()));
        }
        if (0 < getSkippedSuitesCount()) {
            sb.append("- Skips\n");
        }
        for (ISuite suite : skippedSuites) {
            sb.append(String.format("\t%s\n", suite.getName()));
        }
        sb.append("-----------------------------------------------\n");
        System.out.println(sb.toString());
    }
}
