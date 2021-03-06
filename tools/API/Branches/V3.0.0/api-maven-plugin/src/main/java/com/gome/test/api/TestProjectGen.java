package com.gome.test.api;

import java.io.*;
import java.sql.ResultSetMetaData;
import java.util.*;

import com.gome.test.Constant;
import com.gome.test.plugin.*;
import com.gome.test.testng.RetryListener;
import com.gome.test.utils.CsvUtils;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.DocumentException;
import org.relique.jdbc.csv.CsvResultSet;
import org.testng.xml.XmlSuite;


public class TestProjectGen {

    private final static String CASECATEGROY = String.format("CaseCategory%sCaseCategroy.csv", System.getProperty("file.separator"));
    private Log logger;

    private String javaPath;
    private String resourcePath;
    private String testngPath;
    private String timeout;
    private GlobalSettings settings;
    private Set<String> skipPaths;
    private Map<String, String> classByMethod;
    private Map<String, String> descByMethod;
    private Map<String, List<String>> testSuitesByGroup;
    private Set<String> caseNameSet;
    private Set<String> caseIdSet;
    public Map<String, CaseFileAndList> testCaseList;
    int suiteIndex = 0;

    class CaseFileAndList {
        CaseFileAndList(String prefix, List<String> caseList) {
            this.prefix = prefix;
            this.caseList = caseList;
        }

        String prefix;
        List<String> caseList;

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public List<String> getCaseList() {
            return caseList;
        }

        public void setCaseList(List<String> caseList) {
            this.caseList = caseList;
        }
    }

    /*
   * 验证case是否有重名的，如果有重名的 则返回重名的CaseName；
   * */
    private String getExistsCaseIdorName(String prefix, List<AtomicCase> atomicCases) {

        for (AtomicCase cases : atomicCases) {
            if (this.caseIdSet.contains(cases.getId()))
                return "CaseId: " + cases.getId();
            else {
                this.caseIdSet.add(cases.getId());
                //加入到caseList列表
                List<String> caseList = new ArrayList<String>();
                caseList.add(cases.getId());
                caseList.add(cases.getName());
                caseList.add(cases.getOwner());
                this.testCaseList.put(cases.getId(), new CaseFileAndList(prefix, caseList));
            }

            if (this.caseNameSet.contains(cases.getName()))
                return "CaseName: " + cases.getName();
            else {
                this.caseNameSet.add(cases.getName());
            }


        }
        return null;
    }


    private void genTestSource(File testCaseDir, String packageName, String methodPrefix) throws Exception {
        if (testCaseDir.isDirectory() && !testCaseDir.isHidden()) {
            if (caseNameSet == null)
                caseNameSet = new HashSet<String>();
            if (caseIdSet == null)
                caseIdSet = new HashSet<String>();

            for (File file : testCaseDir.listFiles()) {
                if (file.isHidden() || skipPaths.contains(file.getAbsolutePath())) {
                    continue;
                }

                String filename = file.getName();
                if (file.isDirectory()) {
                    String newPackageName = String.format("%s.%s", packageName, filename);
                    String newMethodPrefix = String.format("%s", filename);
                    genTestSource(file, newPackageName, newMethodPrefix);
                } else if (filename.endsWith(".xlsx")) {
                    filename = filename.substring(0, filename.lastIndexOf("."));
                    if (!Utils.isClassFileName(filename)) {
                        continue;
                    }
                    if (null != logger) {
                        logger.info(String.format("Generate test cases from %s", file.getAbsolutePath()));
                    }

                    // Generate test source
                    String status = Constant.STATUS_SUCC;
                    String className = Utils.getClassName(filename);
                    String newMethodPrefix = String.format("%s_%s", methodPrefix, filename);
                    try {
                        AtomicCases cases = new AtomicCases(file, settings);
                        //验证Case名称或者Id是否重复
                        String caseName = getExistsCaseIdorName(String.format("%s_%s_", methodPrefix, filename), cases.getTestCases());
                        if (caseName != null) {
                            //记录异常日志
                            if (null != logger) {
                                logger.info(String.format("%s\\%s, The %s  already exists ", testCaseDir.getName(), file.getName(), caseName));
                            }
                            throw new Exception(String.format("文件%s\\%s的用例:%s，重复了，无法继续执行，请修改！ ", testCaseDir.getName(), file.getName(), caseName));
                        }
                        TestSource source = new TestSource(javaPath,
                                resourcePath, packageName, className,
                                cases, newMethodPrefix,
                                testngPath, classByMethod, descByMethod,
                                timeout, testSuitesByGroup);


                        if (suiteIndex == 0)
                            source.generate(RetryListener.class);
                        else
                            source.generate(null);

                        suiteIndex++;
                    } catch (Exception ex) {
                        status = Constant.STATUS_FAIL;
                        throw ex;
                    } finally {
                        if (null != logger) {
                            logger.info(String.format("Status: %s", status));
                        }
                    }
                }
            }
        }
    }

    private List<List<String>> getCaseCategroy(String filePath) throws Exception {
        List<List<String>> content = new ArrayList<List<String>>();
        CsvResultSet rs = CsvUtils.executeQuery(filePath, "");
        ResultSetMetaData rsdata = rs.getMetaData();
        int columnCount = rsdata.getColumnCount();
        List<String> caseDetails = new ArrayList<String>();
        //写入标题行
        int index = 0;
        for (int i = 0; i < columnCount; i++) {
            index = i == 0 ? 1 : ++index;
            caseDetails.add(rsdata.getColumnName(index));
        }
        content.add(caseDetails);
        index = 0;
        while (rs.next()) {
            caseDetails = new ArrayList<String>();
            for (int i = 0; i < columnCount; i++) {
                index = i == 0 ? 1 : ++index;
                caseDetails.add(rs.getString(index));
            }
            content.add(caseDetails);
        }
        return content;
    }

    /**
     * 空的写入CaseCategory的List
     *
     * @return
     */
    private List<String> getCaseCategoryList(int length) {
        List<String> reslut = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            reslut.add(" ");
        }
        return reslut;
    }

    private void updateCaseCategory(File caseCategory) throws Exception {
        if (!caseCategory.exists()) {
            throw new Exception("No CaseCategroy.xlsx found  ");
        }

        logger.info(String.format("%s 开始更新", caseCategory.getAbsolutePath()));

        List<List<String>> content = getCaseCategroy(caseCategory.getPath());
        for (String key : testCaseList.keySet()) {
            String caseId = String.format("%s%s", testCaseList.get(key).prefix, testCaseList.get(key).caseList.get(0));
            String caseName = testCaseList.get(key).caseList.get(1);
            String caseOwner = testCaseList.get(key).caseList.get(2);
            boolean boo = false;
            for (int t = 0; t < content.size(); t++) {
                List<String> con = content.get(t);
                if (con.get(0).equals(caseId)) {
                    con.set(1, caseName);
                    con.set(2, caseOwner);
                    boo = true;
                }
                content.set(t, con);
            }
            if (!boo) {
                List<String> emt = getCaseCategoryList(content.get(0).size());
                emt.set(0, caseId);
                emt.set(1, caseName);
                emt.set(2, caseOwner);
                content.add(emt);
            }

        }

        CsvUtils.clearAndSave(content, caseCategory.getPath());

    }

    public void genTestProject(String groupId, String artifactId,
                               String version, File testProjectDir, File testCaseDir, String timeout, String caseCategoryPath,
                               Log logger) throws Exception {
        this.logger = logger;
        this.testSuitesByGroup = new HashMap<String, List<String>>();
        this.timeout = timeout;
        this.classByMethod = new HashMap<String, String>();
        this.descByMethod = new HashMap<String, String>();
        TestProjectArch arch = new TestProjectArch(
                testProjectDir.getAbsolutePath());

        this.javaPath = arch.getJavaPath();
        this.resourcePath = arch.getResourcePath();
        this.testngPath = arch.getTestNGPath();
        this.skipPaths = new HashSet<String>();
        this.testCaseList = new HashMap<String, CaseFileAndList>();

        File globalSettingsExcel = new File(testCaseDir,Constant.GLOBLE_XLSX);
        if (globalSettingsExcel.isFile()) {
            this.skipPaths.add(globalSettingsExcel.getAbsolutePath());
        }
        File orderCaseExcel = new File(testCaseDir, Constant.ORDERCASE_XLSX);
        if (orderCaseExcel.isFile()) {
            this.skipPaths.add(orderCaseExcel.getAbsolutePath());
        }
        //增加caseCategroy文件的排除
        File caseCategroy = new File(testCaseDir, CASECATEGROY);
        if (caseCategroy.isFile()) {
            this.skipPaths.add(caseCategroy.getAbsolutePath());
        }
        /**
         * STEP1: Generate test project architecture
         */
        TestProject project = new TestProject("com.gome.test.api","application-programming-interface-helper-parent","RELEASE",groupId, artifactId, version);
        project.generateTo(testProjectDir, logger);
        // STEP2: Load global settings' excel
        settings = new GlobalSettings();
        settings.loadFrom(globalSettingsExcel);
        // STEP3: dfs test case directory to generate test sources
        StringBuilder sb = new StringBuilder();
        sb.append(groupId);
        sb.append(".");
        sb.append(artifactId);
        String packageName = sb.toString();

        // Generate test sources
        TestSource.clearXml();
        genTestSource(testCaseDir, packageName, "");//读取操作case的相关.xlsx生成代码
        TestSource.saveXmlFile(testngPath);
        logger.info("Generate order case");
        GenerateOrderCase(logger, orderCaseExcel);//读取操作OrderCase.xlsx
        logger.info("Update suiteXmlFiles in pom.xml");
        updateSuiteXmlFilesInPom(arch);//updateSuite
        logger.info(String.format("Update %s", caseCategroy.getAbsolutePath()));
        if (caseCategroy.isFile())
            updateCaseCategory(caseCategroy);

    }
    private void GenerateOrderCase(Log logger,
                                   File orderCaseExcel) throws Exception {
        if (null != logger) {
            logger.info(String.format("Generate test cases from %s",
                    orderCaseExcel.getAbsolutePath()));
        }

        String status = Constant.STATUS_SUCC;
        try {
            if (null != orderCaseExcel && orderCaseExcel.exists()) {
                OrderCases orderCases = new OrderCases(orderCaseExcel,
                        testngPath, classByMethod, descByMethod,
                        testSuitesByGroup);
                List<XmlSuite> xmlSuites = orderCases.getXmlSuites();
                List<OrderCase> orderCaseList = orderCases.getOrderCaseList();
                for (int i = 0; i < xmlSuites.size(); ++i) {
                    XmlSuite suite = xmlSuites.get(i);
                    Writer writer = null;
                    try {
                        writer = new OutputStreamWriter(new FileOutputStream(
                                suite.getFileName()), Constant.UTF_8);
                        writer.write(suite.toXml());
                    } finally {
                        if (null != writer) {
                            writer.close();
                        }
                    }
                }

                for (OrderCase oCase : orderCaseList) {
                    if (this.testCaseList.containsKey(oCase.getId())) {
                        //记录异常日志
                        if (null != logger) {
                            logger.info(String.format("%s, The 用例编号:%s  already exists ", orderCaseExcel.getAbsolutePath(), oCase.getId()));
                        }
                        throw new Exception(String.format("文件%s\\%s的用例编号:%s，重复了，无法继续执行，请修改！ ", orderCaseExcel.getAbsolutePath(), oCase.getId()));

                    }
                    List<String> caseList = new ArrayList<String>();
                    caseList.add(oCase.getId());
                    caseList.add(oCase.getCaseDesc());
                    caseList.add(oCase.getOwner());
                    this.testCaseList.put(oCase.getId(), new CaseFileAndList("", caseList));
                }
            }
        } catch (Exception ex) {
            status = Constant.STATUS_FAIL;
            throw ex;
        } finally {
            if (null != logger) {
                logger.info(String.format("Status: %s", status));
            }
        }
    }

    private void updateSuiteXmlFilesInPom(TestProjectArch arch)
            throws DocumentException, IOException {
        List<String> fileNames = new ArrayList<String>();
        for (File f : new File(testngPath).listFiles()) {
            if (f.isFile() && f.getName().endsWith(".xml")) {
                fileNames.add(String.format("testng/%s", f.getName()));
            }
        }
        String pomPath = arch.getPomPath();
        XmlDocument doc = new XmlDocument(pomPath);
        doc.updateSuiteXmlFiles(fileNames);
        doc.dumpTo(pomPath);
    }
}
