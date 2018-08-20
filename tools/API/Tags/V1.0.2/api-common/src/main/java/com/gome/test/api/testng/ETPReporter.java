package com.gome.test.api.testng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class ETPReporter implements IReporter {

    private Set<String> reRunSuites;
    private Set<String> noExists;
    private Exception ex;
    private int id;

    public ETPReporter() {
        this(null, null, null);
    }

    public ETPReporter(Set<String> noExists) {
        this(noExists, null, null);
    }

    public ETPReporter(Set<String> noExists, Exception ex) {
        this(noExists, null, ex);
    }

    public ETPReporter(Set<String> noExists, Set<String> reRunSuites) {
        this(noExists, reRunSuites, null);
    }

    public ETPReporter(Set<String> noExists, Set<String> reRunSuites, Exception ex) {
        if (null == noExists) {
            this.noExists = new HashSet<String>();
        } else {
            this.noExists = noExists;
        }
        if (null == reRunSuites) {
            this.reRunSuites = new HashSet<String>();
        } else {
            this.reRunSuites = reRunSuites;
        }
        this.ex = ex;
    }

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        JSONObject obj = new JSONObject();
        int pass = 0;
        int failed = 0;
        int aborted = 0;
        long minStartDate = new Date().getTime();
        long maxEndDate = -1;
        JSONArray arr = new JSONArray();
        for (ISuite suite : suites) {
            JSONObject o = parseAsJson(suite);
            String testResult = o.getString("TestResult");
            if (testResult.equals("Passed")) {
                ++pass;
            } else if (testResult.equals("Failed")) {
                ++failed;
            } else {
                ++aborted;
            }
            String startTime = o.getString("StartTime");
            long startDate = Long.parseLong(startTime.substring(7, startTime.length() - 3));
            String endTime = o.getString("EndTime");
            long endDate = Long.parseLong(endTime.substring(7, endTime.length() - 3));
            if (minStartDate > startDate) {
                minStartDate = startDate;
            }
            if (maxEndDate < endDate) {
                maxEndDate = endDate;
            }
            arr.put(o);
        }
        if (-1 == maxEndDate) {
            maxEndDate = minStartDate;
        }
        for (String caseName : noExists) {
            arr.put(getNoExistCase(caseName, minStartDate));
        }
        obj.put("summary", getSummary(pass, failed, aborted + noExists.size(),
                minStartDate, maxEndDate));
        obj.put("details", arr);
        Writer writer = null;
        try {
            try {
                File reportFile = new File(String.format("%s/japi4etp.json", outputDirectory));
                reportFile.getParentFile().mkdirs();
                writer = new OutputStreamWriter(new FileOutputStream(reportFile), "UTF-8");
                writer.write(obj.toString(2));
            } finally {
                if (null != writer) {
                    writer.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JSONObject getSummary(int pass, int failed, int aborted,
                                  long minStartDate, long maxEndDate) {
        JSONObject obj = new JSONObject();
        obj.put("TotalCases", pass + failed + aborted);
        obj.put("Pass", pass);
        obj.put("Fail", failed);
        obj.put("Aborted", aborted);
        obj.put("StartTime", "\\/Date(" + minStartDate + ")\\/");
        obj.put("EndTime", "\\/Date(" + maxEndDate + ")\\/");
        obj.put("Duration", getDuration(minStartDate, maxEndDate));
        if (null != ex) {
            obj.put("ErrorMessage", ex.getMessage());
        }
        return obj;
    }

    private double getDuration(Date startDate, Date endDate) {
        return getDuration(startDate.getTime(), endDate.getTime());
    }

    private double getDuration(long startDate, long endDate) {
        return (double) (endDate - startDate) / 1000 / 60;
    }

    private JSONObject parseAsJson(ISuite suite) {
        String level = suite.getParameter("level");
        if (null == level) {
            return parseAsJson(suite.getName(), suite, 1, suite.getParameter("description"));
        } else {
            id = 0;
            JSONArray arr = new JSONArray(level);
            JSONArray arr1 = new JSONArray(suite.getParameter("descLevel"));
            return parseAsJson(suite.getName(), suite, arr, arr1, suite.getParameter("description"));
        }
    }

    private JSONObject getNoExistCase(String caseName, long date) {
        JSONObject obj = new JSONObject();
        obj.put("TestCaseName", caseName);
        obj.put("ErrorMessage", String.format("testng-%s.xml not exists", caseName));
        obj.put("StartTime", "\\/Date(" + date + ")\\/");
        obj.put("EndTime", "\\/Date(" + date + ")\\/");
        obj.put("TestResult", "NotExecuted");
        return obj;
    }

    private JSONObject parseAsJson(String caseName, ISuite suite, JSONArray arr,
                                   JSONArray arr1, String description) {
        int startId = id + 1;
        JSONArray children = new JSONArray();
        for (int i = 0; i < arr.length(); ++i) {
            Object o = arr.get(i);
            if (o instanceof JSONObject) {
                JSONObject obj = (JSONObject) o;
                String cn = obj.keySet().iterator().next().toString();
                JSONObject obj1 = arr1.getJSONObject(i);
                String desc = obj1.keySet().iterator().next().toString();
                children.put(parseAsJson(cn, suite, obj.getJSONArray(cn),
                        obj1.getJSONArray(desc), desc));
            } else {
                children.put(parseAsJson(o.toString(), suite, ++id, arr1.getString(i)));
            }
        }
        int endId = id;
        // get Rerun
        boolean reRun = reRunSuites.contains(suite.getXmlSuite().getName());
        // get ComputerName
        String host = suite.getHost();
        // get startTime/EndTime/Duration
        Date startDate = getStartDate(suite, startId);
        Date endDate = getEndDate(suite, endId);
        double duration = getDuration(startDate, endDate);
        // get testResult
        String testResult = getRunState(suite, startId, endId);
        // get errorMessage
        String errorMessage = null;
        // get stackTrace
        String stackTrace = null;
        // get Owner
        String owner = suite.getParameter("owner");
        // assemble to json
        JSONObject obj = new JSONObject();
        obj.put("TestCaseName", caseName);
        obj.put("CaseDesc", description);
        obj.put("Rerun", reRun);
        obj.put("ComputerName", host);
        obj.put("StartTime", "\\/Date(" + startDate.getTime() + ")\\/");
        obj.put("EndTime", "\\/Date(" + endDate.getTime() + ")\\/");
        obj.put("Duration", duration);
        obj.put("TestResult", testResult);
        obj.put("ErrorMessage", errorMessage);
        obj.put("StackTrace", stackTrace);
        obj.put("Owner", owner);
        obj.put("Children", children);
        return obj;
    }

    private JSONObject parseAsJson(String caseName, ISuite suite, int stepId,
                                   String description) {
        // get Rerun
        boolean reRun = reRunSuites.contains(suite.getXmlSuite().getName());
        // get ComputerName
        String host = suite.getHost();
        // get startTime/EndTime/Duration
        Date startDate = getStartDate(suite, stepId);
        Date endDate = getEndDate(suite, stepId);
        double duration = getDuration(startDate, endDate);
        // get testResult
        String testResult = getRunState(suite, stepId);
        // get errorMessage
        String errorMessage = null;
        if (testResult.equals("Failed")) {
            errorMessage = getErrorMessage(suite, stepId);
        }
        // get stackTrace
        String stackTrace = null;
        if (testResult.equals("Failed")) {
            stackTrace = getStackTrace(suite, stepId);
        }
        // get Owner
        String owner = suite.getParameter("owner");
        // assemble to json
        JSONObject obj = new JSONObject();
        obj.put("TestCaseName", caseName);
        obj.put("CaseDesc", description);
        obj.put("Rerun", reRun);
        obj.put("ComputerName", host);
        obj.put("StartTime", "\\/Date(" + startDate.getTime() + ")\\/");
        obj.put("EndTime", "\\/Date(" + endDate.getTime() + ")\\/");
        obj.put("Duration", duration);
        obj.put("TestResult", testResult);
        obj.put("ErrorMessage", errorMessage);
        obj.put("StackTrace", stackTrace);
        obj.put("Owner", owner);
        obj.put("Children", new JSONArray());
        return obj;
    }

    private String getErrorMessage(ISuite suite, int stepId) {
        Map<String, ISuiteResult> results = suite.getResults();
        String step = String.format("step_%03d", stepId);
        Set<ITestResult> testResults = results.get(step).getTestContext().getFailedTests().getAllResults();
        StringBuilder sb = new StringBuilder();
        for (ITestResult testResult : testResults) {
            sb.append(testResult.getThrowable().getMessage());
        }
        return sb.toString();
    }

    private String getStackTrace(ISuite suite, int stepId) {
        Map<String, ISuiteResult> results = suite.getResults();
        String step = String.format("step_%03d", stepId);
        Set<ITestResult> testResults = results.get(step).getTestContext().getFailedTests().getAllResults();
        StringBuilder sb = new StringBuilder();
        for (ITestResult testResult : testResults) {
            Throwable tw = testResult.getThrowable();
            sb.append(tw);
            StackTraceElement[] elements = tw.getStackTrace();
            for (StackTraceElement element : elements) {
                sb.append(String.format("%s\tat ", System.getProperty("line.separator")));
                sb.append(element.toString());
            }
        }
        return sb.toString();

    }

    private Date getStartDate(ISuite suite, int stepId) {
        Map<String, ISuiteResult> results = suite.getResults();
        String step = String.format("step_%03d", stepId);
        return results.get(step).getTestContext().getStartDate();
    }

    private Date getEndDate(ISuite suite, int stepId) {
        Map<String, ISuiteResult> results = suite.getResults();
        String step = String.format("step_%03d", stepId);
        return results.get(step).getTestContext().getEndDate();
    }

    private String getRunState(ISuite suite, int stepId) {
        return getRunState(suite, stepId, stepId);
    }

    private String getRunState(ISuite suite, int start, int end) {
        Map<String, ISuiteResult> results = suite.getResults();
        boolean hasFail = false;
        boolean hasSkip = false;
        for (int i = start; i <= end; ++i) {
            String step = String.format("step_%03d", i);
            ITestContext tc = results.get(step).getTestContext();
            int failedTests = tc.getFailedTests().getAllResults().size();
            int skippedTests = tc.getSkippedTests().getAllResults().size();
            if (failedTests > 0) {
                hasFail = true;
            }
            if (skippedTests > 0) {
                hasSkip = true;
            }
        }
        String testResult = "Passed";
        if (hasFail) {
            testResult = "Failed";
        } else if (hasSkip) {
            testResult = "NotExecuted";
        }
        return testResult;
    }
}
