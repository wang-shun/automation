package com.gome.test.gui;


import com.gome.test.gui.model.Case;
import org.testng.ITestNGListener;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;

public class TestSource {

    private final String javaSourcePath;
    private final String resourcePath;

    private final String packageName;
    private final String className;
    private final String packageNameUpper;
    private final List<Case> atomicCases;
    private final String methodPrefix;
    private final Map<String, String> classByMethod;
    private final Map<String, String> descByMethod;
    private final String timeout;
    private final Map<String, List<String>> testSuitesByGroup;
    private static StringBuffer testngContext = new StringBuffer();

    private static final String SUITE_ALL = "SuiteAll";

    private static final String SUITE_HEAD1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String SUITE_HEAD2 = "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">";
    private static final String SUITE_HEAD3 = "<suite name=\"" + SUITE_ALL + "\">";
    private static final String SUITE_END = "</suite> <!-- " + SUITE_ALL + " -->";
    private Logger logger;
    private File excelFile;

    public TestSource(String javaSourcePath, String resourcePath,
                      String packageName, String className, List<Case> testCases,
                      String methodPrefix, String testngPath,
                      Map<String, String> classByMethod,
                      Map<String, String> descByMethod,
                      String timeout, Map<String, List<String>> testSuitesByGroup, File excelFile)
            throws IOException {
        this.javaSourcePath = javaSourcePath;
        this.resourcePath = resourcePath;
        this.packageName = packageName.toLowerCase();
        this.packageNameUpper = packageName;
        this.className = className;
        this.atomicCases = testCases;
        this.methodPrefix = methodPrefix;
        this.classByMethod = classByMethod;
        this.descByMethod = descByMethod;
        this.timeout = timeout;
        this.testSuitesByGroup = testSuitesByGroup;
        this.excelFile = excelFile;
    }

    public void generate(Class<? extends ITestNGListener>... listeners) throws Exception {
        new File(getTestPackagePath()).mkdirs();
        generateTestNGXmlSingleFile(listeners);
        this.generateGuiTestSource();
    }

    private void generateTestNGXmlSingleFile(Class<? extends ITestNGListener>... listeners) throws IOException {
        XmlSuite suite = new XmlSuite();
        suite.setName(SUITE_ALL);

        if (listeners != null) {
            for (Class<? extends ITestNGListener> listener : listeners) {
                suite.addListener(listener.toString().substring(6));
            }
        }
        for (int i = 0; i < atomicCases.size(); ++i) {
            Case testCase = atomicCases.get(i);

            String caseId = testCase.getId();

            String testName = testCase.getName();
            String xmlClassName = String
                    .format("%s.%s", packageName, className);
            String methodName = String.format("%s_%s", methodPrefix, caseId);

            if (classByMethod.containsKey(methodName)) {
                throw new IllegalArgumentException(String.format(
                        "%s is duplicated in class %s and %s", methodName,
                        xmlClassName, classByMethod.get(methodName)));
            }
            classByMethod.put(methodName, xmlClassName);
            descByMethod.put(methodName, testCase.getName());

            XmlTest test = new XmlTest(suite);
            test.setName(testName);
            List<XmlClass> classes = new ArrayList<XmlClass>();
            XmlClass xmlClass = new XmlClass(xmlClassName, false);
            List<XmlInclude> includedMethods = new ArrayList<XmlInclude>();
            includedMethods.add(new XmlInclude(methodName));
            xmlClass.setIncludedMethods(includedMethods);
            classes.add(xmlClass);
            test.setXmlClasses(classes);
        }

        testngContext.append(removeSuiteAndXmlNode(suite.toXml()));
    }

    public static void clearXml() {
        testngContext.setLength(0);
        testngContext.append(SUITE_HEAD1);
        testngContext.append(SUITE_HEAD2);
        testngContext.append(SUITE_HEAD3);
    }

    public static void saveXmlFile(String testngPath) throws Exception {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(String.format("%s%stestng.xml", testngPath,
                    File.separator)),
                    "UTF-8"
            );
            testngContext.append(SUITE_END);
            writer.write(testngContext.toString());
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    private String removeSuiteAndXmlNode(String xml) {
        return xml.replace(SUITE_HEAD1, "").replace(SUITE_HEAD2, "").replace(SUITE_HEAD3, "").replace(SUITE_END, "");
    }

    private void generateGuiTestSource() throws IOException {
        Writer writer = null;
        String testSourceImplPath = getTestSourcePath();
        try {
            writer = new OutputStreamWriter(new FileOutputStream(testSourceImplPath), "UTF-8");
            writer.write(getSourceTemplateString("/classbegin.tpl")
                    .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@casefile@", this.excelFile.getAbsolutePath().replace("\\","\\\\\\\\")));

            for (int i = 0; i < atomicCases.size(); ++i) {
                writer.write(getMethod(atomicCases.get(i)));
            }
            writer.write(getSourceTemplateString("/classend.tpl"));
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    private String getMethod(Case testCase) throws IOException {
        StringBuilder sb = new StringBuilder();
        String method = String.format("%s_%s", methodPrefix, testCase.getId());
        sb.append(getSourceTemplateString("/classfunc.tpl")
                        .replaceAll("@timeout@", Matcher.quoteReplacement(timeout))
                        .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                        .replaceAll("@className@", Matcher.quoteReplacement(className))
                        .replaceAll("@method@", Matcher.quoteReplacement(method))
                        .replaceAll("@caseName@", Matcher.quoteReplacement(PluginUtils.toLiteral(testCase.getName())))
                        .replaceAll("@owner@", Matcher.quoteReplacement(testCase.getOwner()))
                        .replaceAll("@caseId@", Matcher.quoteReplacement(testCase.getId()))
        );
        return sb.toString();
    }

    private String getSourceTemplateString(String template) throws IOException {
        BufferedReader reader = null;
        String line = null;

        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getClass()
                    .getResourceAsStream(template)));
            while (null != (line = reader.readLine())) {
                sb.append(line);
                sb.append(Constant.LINE_SEPARATOR);
            }
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return sb.toString();
    }

    private String getTestPackagePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(javaSourcePath);
        sb.append(File.separator);
        sb.append(PluginUtils.packageNameToPath(packageName));
        return sb.toString();
    }

    private String getTestSourcePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTestPackagePath());
        sb.append(File.separator);
        sb.append(className);
        sb.append(".java");
        return sb.toString();
    }
}