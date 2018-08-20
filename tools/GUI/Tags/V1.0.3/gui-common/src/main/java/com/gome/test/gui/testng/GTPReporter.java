package com.gome.test.gui.testng;


import com.gome.test.gui.Constant;
import com.gome.test.gui.annotation.CaseOwner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.*;

public class GTPReporter implements IReporter {

    private Set<String> noExists;
    private Exception ex;

    public GTPReporter() {
        this(null, null, null);
    }

    public GTPReporter(Set<String> noExists, Set<String> reRunSuites) {
        this(noExists, reRunSuites, null);
    }

    public GTPReporter(Set<String> noExists, Set<String> reRunSuites, Exception ex) {
        if (null == noExists) {
            this.noExists = new HashSet<String>();
        } else {
            this.noExists = noExists;
        }

        this.ex = ex;
    }

    private String getAuthors(ITestNGMethod method) {
        Annotation[] annotations = method.getConstructorOrMethod().getMethod().getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == CaseOwner.class) {
                return ((CaseOwner) annotation).description();
            }
        }

        return Constant.UNKNOW;
    }

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        JSONArrayCase arr = new JSONArrayCase();
        for (ISuite suite : suites) {
            String description = suite.getParameter(Constant.DESCRIPTION);
            String owner = suite.getParameter(Constant.OWNER.toLowerCase());
            if (suite.getParameter(Constant.SKIP_ONCE_FAIL) == null) {
                List<JSONObject> atomCases = parseAsJsonForAtomCaseSuite(suite);
                for (JSONObject atomCase : atomCases) {
                    arr.put(atomCase);
                }
            } else {
                JSONObject o = parseAsJsonForOrderCaseSuite(suite.getName(), suite, description, owner);
                arr.put(o);
            }
        }

        for (String caseName : noExists) {
            arr.put(getNoExistCase(caseName, arr.getMinStartDate()));
        }

        JSONObject obj = new JSONObject();
        String filePath = String.format("%s/japi4gtp.json", outputDirectory);

        appendSummary(obj, arr.getPassed(), arr.getFailed(), arr.getAborted() + noExists.size(), arr.getMinStartDate(), arr.getMaxEndDate(), filePath, new Date().getTime());
        obj.put(Constant.DETAILS, arr);
        Writer writer = null;
        try {
            try {
                File reportFile = new File(filePath);
                reportFile.getParentFile().mkdirs();
                writer = new OutputStreamWriter(new FileOutputStream(reportFile), Constant.UTF_8);
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

    private int getDate(long begin) {
        Date date = new Date(begin);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.HOUR_OF_DAY) < 8)
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return Integer.valueOf(format.format(calendar.getTime()));
    }


    private void appendSummary(JSONObject obj, int pass, int failed, int aborted, long minStartDate, long maxEndDate, String filePath, long generateTime) {
        obj.put(Constant.TOTAL_CASES, pass + failed + aborted);
        obj.put(Constant.PASSED, pass);
        obj.put(Constant.FAILED, failed);
        obj.put(Constant.ABORD, aborted);
        obj.put(Constant.START_TIME, minStartDate);
        obj.put(Constant.END_TIME, minStartDate);
        obj.put(Constant.DURATION, getDuration(minStartDate, maxEndDate));
        obj.put(Constant.RESULT_FILE_PATH, filePath);
        obj.put(Constant.GENERATE_TIME, generateTime);
        obj.put(Constant.DATE, getDate(minStartDate));
        obj.put(Constant.TASK_ID, 0);
        obj.put(Constant.TASK_TYPE, 0);

        if (null != ex) {
            obj.put("ErrorMessage", ex.getMessage());
        }
    }

    private long getDuration(long startDate, long endDate) {
        return (endDate - startDate);
    }

    private JSONObject getNoExistCase(String caseName, long date) {
        JSONObject obj = new JSONObject();
        obj.put(Constant.TEST_CASE_NAME, caseName);
        obj.put(Constant.ERROR_MESSAGE, String.format("testng-%s.xml not exists", caseName));
        obj.put(Constant.START_TIME, date);
        obj.put(Constant.END_TIME, date);
        obj.put(Constant.TEST_RESULT, Constant.NOT_EXECUTED);
        return obj;
    }

    private List<JSONObject> parseAsJsonForAtomCaseSuite(ISuite suite) {
        List<JSONObject> cases = new ArrayList<JSONObject>();
        for (Map.Entry<String, ISuiteResult> result : suite.getResults().entrySet()) {
            cases.add(GenerateJsonObjectForMethod(suite, result.getValue()));
        }
        return cases;
    }

    private JSONObject GenerateJsonObjectForMethod(ISuite suite, ISuiteResult result) {
        ITestNGMethod method = result.getTestContext().getAllTestMethods()[0];
        return GenerateJsonObject(method.getMethodName(), suite, method.getDescription(), result);
    }

    private JSONObject GenerateJsonObject(String caseName, ISuite suite, String description, ISuiteResult result) {
        String host = suite.getHost();
        long startDate = result.getTestContext().getStartDate().getTime();
        long endDate = result.getTestContext().getEndDate().getTime();
        long duration = getDuration(startDate, endDate);
        String testResult = getRunState(result.getTestContext());

        int reRunCount = result.getTestContext().getFailedTests().size() + result.getTestContext().getPassedTests().size() - 1;

        String errorMessage = null;
        String stackTrace = null;
        if (testResult.equals(Constant.FAILED)) {
            errorMessage = getErrorMessage(result.getTestContext());
            stackTrace = getStackTrace(result.getTestContext());
        }

        String owner = getAuthors(result.getTestContext().getAllTestMethods()[0]);

        return generateJSONObject(caseName, description, reRunCount, host, startDate,
                endDate, duration, testResult, errorMessage, stackTrace, owner, null);
    }

    private JSONObject parseAsJsonForOrderCaseSuite(String caseName, ISuite suite, String description, String owner) {
        JSONArray children = new JSONArray();
        long startDateAll = 0;
        long endDateAll = 0;
        long durationAll = 0;
        String testResultAll = Constant.NOT_EXECUTED;
        for (Map.Entry<String, ISuiteResult> result : suite.getResults().entrySet()) {
            JSONObject json = GenerateJsonObjectForMethod(suite, result.getValue());
            children.put(json);

            if (startDateAll == 0)
                startDateAll = json.getLong(Constant.START_TIME);

            endDateAll = json.getLong(Constant.END_TIME);
            durationAll += json.getDouble(Constant.DURATION);

            if (json.getString(Constant.TEST_RESULT).equals(Constant.FAILED))
                testResultAll = Constant.FAILED;
            else if (json.getString(Constant.TEST_RESULT).equals(Constant.PASSED) && testResultAll.equals(Constant.FAILED) == false)
                testResultAll = Constant.PASSED;
        }

        return generateJSONObject(caseName, description, 0, suite.getHost(), startDateAll,
                endDateAll, durationAll, testResultAll, "", "", owner, children);
    }

    private JSONObject generateJSONObject(String caseName, String description, int reRunCount, String host, long startDate,
                                          long endDate, long duration, String testResult,
                                          String errorMessage, String stackTrace, String owner, JSONArray children) {
        JSONObject child = new JSONObject();
        child.put(Constant.TEST_CASE_NAME, caseName);
        child.put(Constant.CASE_DESC, description);
        child.put(Constant.RE_RUN, reRunCount > 1);
        child.put(Constant.RE_RUN_COUNT, reRunCount);
        child.put(Constant.COMPUTER_NAME, host);
        child.put(Constant.START_TIME, startDate);
        child.put(Constant.END_TIME, endDate);
        child.put(Constant.DURATION, duration);
        child.put(Constant.TEST_RESULT, testResult);
        child.put(Constant.ERROR_MESSAGE, errorMessage);
        child.put(Constant.STACK_TRACE, stackTrace);
        child.put(Constant.OWNER.toLowerCase(), owner);
        child.put(Constant.CHILDREN, children == null ? new JSONArray() : children);
        return child;
    }


    private String getErrorMessage(ITestContext context) {
        StringBuilder sb = new StringBuilder();
        for (ITestResult testResult : context.getFailedTests().getAllResults()) {
            sb.append(testResult.getThrowable().getMessage());
        }
        return sb.toString();
    }

    private String getStackTrace(ITestContext context) {
        Set<ITestResult> testResults = context.getFailedTests().getAllResults();
        StringBuilder sb = new StringBuilder();
        for (ITestResult testResult : testResults) {
            Throwable tw = testResult.getThrowable();
            sb.append(tw);
            StackTraceElement[] elements = tw.getStackTrace();
            for (StackTraceElement element : elements) {
                sb.append(String.format("%s\tat ", Constant.LINE_SEPARATOR));
                sb.append(element.toString());
            }
        }
        return sb.toString();

    }

    private String getRunState(ITestContext context) {
        int failedTests = context.getFailedTests().getAllResults().size();
        int skippedTests = context.getSkippedTests().getAllResults().size();

        String testResult = Constant.PASSED;
        if (failedTests > 0) {
            testResult = Constant.FAILED;
        } else if (skippedTests > 0) {
            testResult = Constant.NOT_EXECUTED;
        }
        return testResult;
    }


    class JSONArrayCase extends JSONArray {
        private int passed = 0;
        private int failed = 0;
        private int aborted = 0;
        private long minStartDate = new Date().getTime();
        private long maxEndDate = -1;

        public int getPassed() {
            return passed;
        }

        public int getFailed() {
            return failed;
        }

        public int getAborted() {
            return aborted;
        }

        public long getMinStartDate() {
            return minStartDate;
        }

        public long getMaxEndDate() {
            if (-1 == maxEndDate)
                return minStartDate;

            return maxEndDate;
        }

        @Override
        public JSONArray put(Object value) {
            if (value instanceof JSONObject && ((JSONObject) value).getString(Constant.TEST_CASE_NAME).isEmpty() == false) {
                String testResult = ((JSONObject) value).getString(Constant.TEST_RESULT);
                if (testResult.equals(Constant.PASSED)) {
                    ++passed;
                } else if (testResult.equals(Constant.FAILED)) {
                    ++failed;
                } else {
                    ++aborted;
                }

                long startDate = ((JSONObject) value).getLong(Constant.START_TIME);
                long endDate = ((JSONObject) value).getLong(Constant.END_TIME);
                if (minStartDate > startDate) {
                    minStartDate = startDate;
                }
                if (maxEndDate < endDate) {
                    maxEndDate = endDate;
                }
            }
            return super.put(value);
        }
    }
}