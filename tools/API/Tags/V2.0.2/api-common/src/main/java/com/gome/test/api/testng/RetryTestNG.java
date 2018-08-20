package com.gome.test.api.testng;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.TestNG;
import org.testng.TestNGException;
import org.testng.TestRunner;
import org.testng.log4testng.Logger;

public class RetryTestNG extends TestNG {

    private static final Logger LOGGER = Logger.getLogger(RetryTestNG.class);
    private int m_status;

    public RetryTestNG() {
        super(false);
        m_status = 0;
    }

    @Override
    public int getStatus() {
        return m_status;
    }

    public void setStatus(int status) {
        m_status |= status;
    }

    public static List<String> loadSuiteFilesFromFile(String fileOfTestsToRun) {
        List<String> suiteFiles = new ArrayList<String>();
        if (null != fileOfTestsToRun) {
            File f = new File(fileOfTestsToRun);
            if (f.exists()) {
                try {
                    String content = FileUtils.readFileToString(f, "UTF-8");
                    String[] strs = StringUtils.split(content);
                    suiteFiles.addAll(Arrays.asList(strs));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return suiteFiles;
    }

    public static void main(String[] args) {
        CmdLineArgs cla = new CmdLineArgs();
        new JCommander(cla, args);
        // get outputDirectory
        String outputDirectory = cla.outputDirectory;
        if (null == outputDirectory) {
            outputDirectory = "test-output";
        }
        FileUtils.deleteQuietly(new File(outputDirectory));

        // get suite files
        cla.suiteFiles.addAll(loadSuiteFilesFromFile(cla.fileOfTestsToRun));
        Set<String> s = new HashSet<String>();
        Set<String> noExists = new HashSet<String>();
        for (String suiteFile : cla.suiteFiles) {
            String f = String.format("testng-%s.xml", suiteFile);
            if (!new File(f).exists()) {
                noExists.add(suiteFile);
            } else {
                s.add(f);
            }
        }
        cla.suiteFiles.clear();
        cla.suiteFiles.addAll(s);
        if (cla.suiteFiles.isEmpty()) {
            LOGGER.error("no tests to run");
            System.exit(1);
        }

        // generate init report
        ETPReporter erp = new ETPReporter(noExists);
        erp.generateReport(null, new ArrayList<ISuite>(), outputDirectory);

        if (cla.suiteFiles.isEmpty()) {
            System.exit(0);
        }

        try {
            validateCommandLineParameters(cla);
        } catch (ParameterException ex) {
            ETPReporter rp = new ETPReporter(noExists, ex);
            rp.generateReport(null, new ArrayList<ISuite>(), outputDirectory);
            exitWithError(ex.getMessage());
        }

        Integer maxRetryFailSuitesCount = cla.maxRetryFailSuitesCount;
        Double retryFailSuitesThreshold = cla.retryFailSuitesThreshold;

        RetryTestNG tng = null;
        int total = 0;
        int failed = 0;
        List<ISuite> failedSuites = new ArrayList<ISuite>();
        List<ISuite> skippedSuites = new ArrayList<ISuite>();
        List<ISuite> suites = new ArrayList<ISuite>();
        Set<String> reRunSuites = new HashSet<String>();
        for (int i = 0; i <= maxRetryFailSuitesCount; ++i) {
            log(String.format("\n[Attempt %03d]\n", i));

            try {
                tng = privateMain(cla);
            } catch (Exception ex) {
                ETPReporter rp = new ETPReporter(noExists, reRunSuites, ex);
                suites.addAll(failedSuites);
                suites.addAll(skippedSuites);
                rp.generateReport(null, suites, outputDirectory);
                if (TestRunner.getVerbose() > 1) {
                    ex.printStackTrace(System.out);
                } else {
                    LOGGER.error(ex.getMessage());
                }
                System.exit(HAS_FAILURE);
            }

            if (0 != i) {
                reRunSuites.addAll(cla.suiteFiles);
            }

            Set<IReporter> reporters = tng.getReporters();
            for (IReporter reporter : reporters) {
                if (reporter instanceof SuiteStatReporter) {
                    SuiteStatReporter rp = (SuiteStatReporter) reporter;
                    total = rp.getTotalSuitesCount();
                    failed = rp.getFailedSuitesCount();
                    failedSuites = rp.getFailedSuites();
                    skippedSuites = rp.getSkippedSuites();
                    suites.addAll(rp.getPassedSuites());
                    break;
                }
            }
            if (0 == total || (double) failed / total <= retryFailSuitesThreshold) {
                break;
            }
            cla.suiteFiles.clear();
            for (ISuite suite : failedSuites) {
                cla.suiteFiles.add(suite.getXmlSuite().getFileName());
            }
            for (ISuite suite : skippedSuites) {
                cla.suiteFiles.add(suite.getXmlSuite().getFileName());
            }
            if (0 == cla.suiteFiles.size()) {
                break;
            }
        }

        if (null != tng) {
            suites.addAll(failedSuites);
            suites.addAll(skippedSuites);
            JAPIReporter reporter = new JAPIReporter(noExists, reRunSuites);
            reporter.generateReport(null, suites, outputDirectory);
            System.exit(tng.getStatus());
        }
    }

    private static void exitWithError(String msg) {
        System.err.println(msg);
        new JCommander(new CmdLineArgs()).usage();
        System.exit(1);
    }

    private static RetryTestNG privateMain(CmdLineArgs cla) throws TestNGException {
        RetryTestNG tng = new RetryTestNG();
        tng.configure(cla);
        tng.run();
        return tng;
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
