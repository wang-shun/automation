package com.gome.test.api.testng;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

public class VerboseReporter implements IReporter {

    private final List<ISuite> passedSuites;
    private final List<ISuite> failedSuites;
    private final List<ISuite> skippedSuites;
    private Set<String> noExists;

    public VerboseReporter() {
        this(null);
    }

    public VerboseReporter(Set<String> noExists) {
        this.passedSuites = new ArrayList<ISuite>();
        this.failedSuites = new ArrayList<ISuite>();
        this.skippedSuites = new ArrayList<ISuite>();
        this.noExists = noExists;
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
        verboseOutput();
    }

    private void verboseOutput() {
        int pass = passedSuites.size();
        int fail = failedSuites.size();
        int skip = skippedSuites.size();
        if (null != noExists) {
            skip += noExists.size();
        }
        int total = pass + fail + skip;
        StringBuilder sb = new StringBuilder();
        sb.append("***********************************************\n");
        sb.append("Final Statistic\n");
        sb.append(String.format("Total run: %d, Failures: %d, Skips: %d\n",
                total, fail, skip));
        if (0 < fail) {
            sb.append("- Failures\n");
        }
        for (ISuite suite : failedSuites) {
            sb.append(String.format("\t%s: %s\n", suite.getName(), suite.getParameter("description")));
        }
        if (0 < skip || !noExists.isEmpty()) {
            sb.append("- Skips\n");
        }
        for (ISuite suite : skippedSuites) {
            sb.append(String.format("\t%s: %s\n", suite.getName(), suite.getParameter("description")));
        }
        for (String noExist : noExists) {
            sb.append(String.format("\t%s\n", noExist));
        }
        sb.append("***********************************************\n");
        System.out.println(sb.toString());
    }
}
