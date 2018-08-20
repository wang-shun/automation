package com.gome.test.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import com.gome.test.api.model.ClientEnum;
import com.gome.test.api.model.Constant;
import com.gome.test.api.testng.RetryListener;
import org.apache.maven.plugin.MojoFailureException;
import org.dom4j.DocumentException;
import org.testng.ITestNGListener;
import org.testng.TestListenerAdapter;
import org.testng.collections.Lists;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class TestSource {

    private final String javaSourcePath;
    private final String resourcePath;

    private final String packageName;
    private final String className;
    private final String packageNameUpper;
    private final AtomicCases atomicCases;
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
    private  Logger logger;

    public TestSource(String javaSourcePath, String resourcePath,
                      String packageName, String className, AtomicCases testCases,
                      String methodPrefix, String testngPath,
                      Map<String, String> classByMethod,
                      Map<String, String> descByMethod,
                      String timeout, Map<String, List<String>> testSuitesByGroup)
            throws IOException {
        this.javaSourcePath = javaSourcePath;
        this.resourcePath = resourcePath;
        this.packageName = packageName.toLowerCase();
        this.packageNameUpper=packageName;
        this.className = className;
        this.atomicCases = testCases;
        this.methodPrefix = methodPrefix;
        this.classByMethod = classByMethod;
        this.descByMethod = descByMethod;
        this.timeout = timeout;
        this.testSuitesByGroup = testSuitesByGroup;
    }

    public void generate(Class<? extends ITestNGListener>... listeners) throws Exception {

        new File(getTestPackagePath()).mkdirs();
//		new File(getTestResourcePath()).mkdirs();


            generateTestNGXmlSingleFile(listeners);

            generateTestSourceParam();
            if (this.atomicCases.getClientType() == ClientEnum.HttpClient)
                generateHttpTestSource();
            else
                generateRichClientTestSource();



    }




    private void generateTestNGXmlSingleFile(Class<? extends ITestNGListener>... listeners) throws IOException {
        XmlSuite suite = new XmlSuite();
        suite.setName(SUITE_ALL);

        if (listeners != null) {
            for (Class<? extends ITestNGListener> listener : listeners) {
                suite.addListener(listener.toString().substring(6));
            }
        }
        for (int i = 0; i < atomicCases.getTestCases().size(); ++i) {
            AtomicCase testCase = atomicCases.getTestCases().get(i);

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

//			APIUtils.addGroup(testSuitesByGroup, testCase.getGroups(),
//					suiteName); 
//			Map<String, String> parameters = new HashMap<String, String>();
//			parameters.put("messageClient", messageClient);
//			parameters.put("description", testCase.getName());
//			parameters.put("owner", testCase.getOwner());
//			suite.setParameters(parameters);
            XmlTest test = new XmlTest(suite);
            test.setName(testName);
            List<XmlClass> classes = new ArrayList<XmlClass>();
//			classes.add(new XmlClass(BASE_CONFIG_CLASS, false));
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

    private void check(Set<String> paramSet) {
        Map<String, String> m = new HashMap<String, String>();
        for (String paramKey : paramSet) {
            String p = PluginUtils.toParam(paramKey);
            if (m.containsKey(p)) {
                throw new IllegalArgumentException(String.format(
                        "Duplicated param key %s and %s found", m.get(p),
                        paramKey));
            }
            m.put(p, paramKey);
        }
    }

    private void generateTestSourceParam() throws IOException {
        Writer writer = null;
        String testSourceParamPath = getTestSourceParamPath();
        try {
            writer = new OutputStreamWriter(new FileOutputStream(
                    testSourceParamPath), "UTF-8");
            writer.write(getSourceTemplateString("/source.tpl6").replace(
                    "@packageName@", packageName).replace("@className@",
                    className));
            writer.write(Constant.LINE_SEPARATOR);
            Set<String> paramSet = new HashSet<String>();
            for (int i = 0; i < atomicCases.getTestCases().size(); ++i) {
                paramSet.addAll(Arrays.asList(atomicCases.getTestCases().get(i).getParamKeys()));
            }
            check(paramSet);
            String tpl = getSourceTemplateString("/source.tpl7");
            for (String paramKey : paramSet) {
                writer.write(tpl.replace("@param@",
                        PluginUtils.toLiteralString(paramKey)).replace(
                        "@param_upper_case@", PluginUtils.toParam(paramKey)));
                writer.write(Constant.LINE_SEPARATOR);
            }
            writer.write(getSourceTemplateString("/source.tpl8"));
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    private void generateHttpTestSource() throws IOException {
        Writer writer = null;
        String testSourceImplPath = getTestSourcePath();
        try {
            writer = new OutputStreamWriter(new FileOutputStream(testSourceImplPath), "UTF-8");
            writer.write(getSourceTemplateString("/source.tpl1")
                    .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                    .replaceAll("@className@", Matcher.quoteReplacement(className)));
            for (int i = 0; i < atomicCases.getTestCases().size(); ++i) {
                writer.write(getMethod(atomicCases.getTestCases().get(i)));
            }
            writer.write(getSourceTemplateString("/source.tpl5"));
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    private void generateRichClientTestSource() throws IOException {
        Writer writer = null;
        String testSourceImplPath = getTestSourcePath();
        try {
            String[] serviceName = packageName.split("\\.");
            String[] serviceNameUpper=this.packageNameUpper.split("\\.");//包名是小写的，类名是大写的，所以这里需要用到大写的
            writer = new OutputStreamWriter(new FileOutputStream(testSourceImplPath), "UTF-8");
            writer.write(getSourceTemplateString("/richclietsource1.tpl")
                    .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                    .replaceAll("@className@", Matcher.quoteReplacement(className))
                    .replaceAll("@servicename@", Matcher.quoteReplacement(serviceName[serviceName.length - 1]))
                    .replaceAll("@verifyClass@", Matcher.quoteReplacement(PluginUtils.changeTheFirstCharToUpper(serviceNameUpper[serviceNameUpper.length - 1]))+ className.replace("Test", "")));
            //PluginUtils.changeTheFirstCharToUpper(serviceName)
            for (int i = 0; i < atomicCases.getTestCases().size(); ++i) {
                writer.write(getMethod(atomicCases.getTestCases().get(i)));
            }
            writer.write(getSourceTemplateString("/source.tpl5"));
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    private String getMethod(AtomicCase testCase) throws IOException {
        StringBuilder sb = new StringBuilder();
        String method = String.format("%s_%s", methodPrefix, testCase.getId());
        sb.append(getSourceTemplateString(this.atomicCases.getClientType() == ClientEnum.HttpClient ? "/source.tpl2" : "/richclietsource2.tpl")
                .replaceAll("@timeout@", Matcher.quoteReplacement(timeout))
                .replaceAll("@packageName@", Matcher.quoteReplacement(packageName))
                .replaceAll("@className@", Matcher.quoteReplacement(className))
                .replaceAll("@method@", Matcher.quoteReplacement(method))
                .replaceAll("@caseName@", Matcher.quoteReplacement(PluginUtils.toLiteral(testCase.getName())))
                .replaceAll("@owner@", Matcher.quoteReplacement(testCase.getOwner()))
                .replace("@priority@", testCase.getPriority()));
        sb.append(getSourceTemplateString(this.atomicCases.getClientType() == ClientEnum.HttpClient ? "/source.tpl3" : "/richclietsource3.tpl")
                .replace("@paramKeys@", join(className, testCase.getParamKeys()))
                .replace("@paramValues@", join(testCase.getParamValues())));
        sb.append(getSourceTemplateString("/source.tpl4"));
        return sb.toString();
    }

    private String join(String className, String[] paramKeys) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramKeys.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }

            sb.append(className);
            sb.append("Param.");
            sb.append(PluginUtils.toParam(paramKeys[i]));
        }
        return sb.toString();
    }

    private String join(Object[] objs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objs.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }

            Object obj = objs[i];
            if (null == obj) {
                sb.append("null");
            } else {
                sb.append(PluginUtils.toLiteralString(obj.toString()));
            }
        }
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

//	private String getTestResourcePath() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(resourcePath);
//		sb.append(File.separator);
//		sb.append(APIUtils.packageNameToPath(packageName));
//		return sb.toString();
//	}


    private String getTestSourceParamPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTestPackagePath());
        sb.append(File.separator);
        sb.append(className);
        sb.append("Param.java");
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
