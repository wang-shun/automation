package com.ofo.test.api;

import com.ofo.test.Constant;
import com.ofo.test.plugin.*;
import com.ofo.test.plugin.graph.XmlSuite;
import com.ofo.test.testng.RetryListener;
import com.ofo.test.utils.CsvUtils;
import com.ofo.test.utils.JsonUtils;
import com.ofo.test.utils.Logger;
import com.ofo.test.utils.TestSourceUtils;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.DocumentException;
import org.relique.jdbc.csv.CsvResultSet;

import java.io.*;
import java.sql.ResultSetMetaData;
import java.util.*;


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
    private int testNGCount;
    public Map<String, CaseFileAndList> testCaseList;
    int suiteIndex = 0;
    private static final int categoryBeginColIndex = 8;
    private Map<String,Map<String, String>> caseIdCategoryMap;

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
            if (caseIdCategoryMap == null)
                caseIdCategoryMap = new HashMap<String, Map<String, String>>();
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
                        cases.testNGCount=this.testNGCount;
                        //验证Case名称或者Id是否重复
                        String caseName = getExistsCaseIdorName(String.format("%s_%s_", methodPrefix, filename), cases.getTestCases());

                        //将遍历的case添加到map，updateCategory
                        getCaseIdCategoryMap(cases, caseIdCategoryMap);

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


                        if (suiteIndex == 0) {
                            source.generate(RetryListener.class);
                        } else {
                            source.generate(null);
                        }

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
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < length; i++) {
            result.add(" ");
        }
        return result;
    }

    private void updateCaseCategory(File caseCategory) throws Exception {
        if (!caseCategory.exists()) {
            throw new Exception("No CaseCategroy.xlsx found  ");
        }

        logger.info(String.format("%s 开始更新", caseCategory.getAbsolutePath()));

        List<List<String>> content = getCaseCategroy(caseCategory.getPath());

        Map<String, Integer> categoryColIndexMap = new HashMap<String, Integer>();

        /**
         * 获取表头
         */
        List<String> headCon = content.get(0);

        /**
         * 获取所有的category key
         */
        Set<String> categorySet = new TreeSet<String>();
        for (String caseId : caseIdCategoryMap.keySet()) {
            for (String key : caseIdCategoryMap.get(caseId).keySet()) {
                categorySet.add(key);
            }
        }

        /**
         * 更新表头（最后的category列表头）
         */
        while (headCon.size() > categoryBeginColIndex && headCon.get(categoryBeginColIndex) != null) {
            headCon.remove(categoryBeginColIndex);
        }

        for (String categoryKey : categorySet) {
            headCon.add(categoryKey);
        }

        /**
         * 将更新后的表头存入content，并按新表头更新categoryColIndexMap
         */
            content.set(0, headCon);
            for (int i = categoryBeginColIndex; i < headCon.size(); i++) {
                categoryColIndexMap.put(headCon.get(i), i);
            }

        /**
         * 遍历caseList，逐行更新CaseCategory
         */
        for (String key : testCaseList.keySet()) {
            String caseId = String.format("%s%s", testCaseList.get(key).prefix, testCaseList.get(key).caseList.get(0));
            String caseName = testCaseList.get(key).caseList.get(1);
            String caseOwner = testCaseList.get(key).caseList.get(2);
            boolean boo = false;
            /**
             * 逐行更新
             */
            for (int t = 0; t < content.size(); t++) {
                List<String> con = content.get(t);
                if (con.get(0).equals(caseId)) {
                    con.set(1, caseName);
                    con.set(2, caseOwner);

                    /**
                     * 初始化每一行的category列为“”
                     */
                    while (con.size() > categoryBeginColIndex && con.get(categoryBeginColIndex) != null) {
                        con.remove(categoryBeginColIndex);
                    }

                    for (int i = categoryBeginColIndex; i < headCon.size(); i++) {
                        con.add("");
                    }

                    /**
                     * Update caseCategory
                     */
                    //提前获取的<caseId,<key,value>>不为空，<cateKey, colIndex>不为空，<caseId,<key,value>>包含当前caseId
                    if (caseIdCategoryMap != null && categoryColIndexMap != null && caseIdCategoryMap.get(key) != null) {
                        Set<String> singleCategorySet = caseIdCategoryMap.get(key).keySet();
                        for (String cate : singleCategorySet) {
                            if (con.size() > categoryColIndexMap.get(cate)) {
                                con.set(categoryColIndexMap.get(cate), caseIdCategoryMap.get(key).get(cate));
                            } else {
                                throw new Exception(String.format("CaseCategory caseId 为%s的列索引小于%d",key,categoryColIndexMap.get(cate)));
                            }
                        }

                    }
                    boo = true;
                }
                content.set(t, con);
            }
            /**
             * 处理新增case
             */
            if (!boo) {
                List<String> emt = getCaseCategoryList(content.get(0).size());
                emt.set(0, caseId);
                emt.set(1, caseName);
                emt.set(2, caseOwner);
                if (caseIdCategoryMap != null && categoryColIndexMap != null && caseIdCategoryMap.get(key) != null) {
                    Set<String> singleCategorySet = caseIdCategoryMap.get(key).keySet();
                    for (String cate : singleCategorySet) {
                        if (emt.size() > categoryColIndexMap.get(cate)) {
                            emt.set(categoryColIndexMap.get(cate), caseIdCategoryMap.get(key).get(cate));
                        } else {
                            throw new Exception(String.format("新增CaseCategory caseId 为%s的列索引小于%d",key,categoryColIndexMap.get(cate)));
                        }
                    }

                }
                content.add(emt);
            }

        }

        /**
         * 设置第二行表头
         */
        content.set(1, headCon);

        /**
         * 处理CaseCategory中的历史case（CaseCategory中已有，但testCaseList中没有的，删除）
         */
        Set<String> caseIdSet = new HashSet<String>();
        for (String key : testCaseList.keySet()) {
            String caseId = String.format("%s%s", testCaseList.get(key).prefix, testCaseList.get(key).caseList.get(0));
            caseIdSet.add(caseId);
        }
        content = removeHistoryCase(content, caseIdSet);

        /**
         * 覆盖CaseCategory文件
         */
        CsvUtils.clearAndSave(content, caseCategory.getPath());

    }

    public void genTestProject(String groupId, String artifactId,
                               String version, File testProjectDir, File testCaseDir, String timeout, String caseCategoryPath,
                               Log logger,int testNGCount) throws Exception {
        this.logger = logger;
        this.testSuitesByGroup = new HashMap<String, List<String>>();
        this.timeout = timeout;
        this.classByMethod = new HashMap<String, String>();
        this.descByMethod = new HashMap<String, String>();
        TestProjectArch arch = new TestProjectArch(
                testProjectDir.getAbsolutePath());
        this.testNGCount=testNGCount;
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
        TestProject project = new TestProject("com.ofo.test.api","api-helper-parent","RELEASE",groupId, artifactId, version);
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

                orderCases.setTestCount(TestSourceUtils.testNGCount);

                List<XmlSuite> xmlSuites = orderCases.getXmlSuites();
                List<OrderCase> orderCaseList = orderCases.getOrderCaseList();
                for (int i = 0; i < xmlSuites.size(); ++i) {
                    XmlSuite suite = xmlSuites.get(i);
                    int index=1;

                   for(int t=0;t< suite.getM_testListArray().size();t++)
                   {
                       suite.setTests(suite.getM_testListArray().get(t));
                       String suiteName=suite.getName();
                       suite.setName(suiteName+(suite.getM_testListArray().size()==1?"":"_"+index));
                       Writer writer = null;
                       try {
                           String filename=suite.getFileName();
                           if(filename.lastIndexOf(".xml") == (filename.length()-4) && suite.getM_testListArray().size()>1)
                           {
                               filename= String.format("%s.xml",filename.substring(0,filename.lastIndexOf(".xml"))+"_"+index) ;
                           }

                           writer = new OutputStreamWriter(new FileOutputStream(
                                   filename), Constant.UTF_8);
                           writer.write(suite.toXml());
                       } finally {
                           if (null != writer) {
                               writer.close();
                           }
                       }
                       suite.setName(suiteName);
                       index++;

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

    private void getCaseIdCategoryMap(AtomicCases cases, Map<String, Map<String, String>> caseIdCategoryMap) {
        int indexCategory;
        try {
            for (AtomicCase atomicCase : cases.getTestCases()) {
                if ((indexCategory = Arrays.asList(atomicCase.getParamKeys()).indexOf("caseCategory")) != -1) {
                    if (atomicCase.getParamValues()[indexCategory] != null) {//paramKeys 和 paramValues 长度有可能不同
                        String categoryStr = atomicCase.getParamValues()[indexCategory].toString();
                        if (!categoryStr.equals("") && !categoryStr.equals("{}"))
                            caseIdCategoryMap.put(atomicCase.getId(), JsonUtils.readValue(categoryStr, TreeMap.class));
                    }
                }
            }
        } catch (Exception e) {
            Logger.info("更新CaseCategory.xlsx时，获取Categories失败：" + e.toString());
        }
    }

    public List<List<String>> removeHistoryCase (List<List<String>> content, Set<String> keySet) {
        for (int i = 2; i < content.size(); i++) {
            if (!keySet.contains(content.get(i).get(0))) {
                content.remove(i);
                removeHistoryCase(content, keySet);
            }
        }
        return content;
    }


    public static void main(String[] args) throws Exception {

        TestProjectGen gen = new TestProjectGen();
        CsvResultSet rs = CsvUtils.executeQuery("D:\\svn\\SVNCode\\NewAPITest\\14\\TestCase\\CaseCategory\\CaseCategroy.csv", "fount='test1' or activeRedCoupon='notRun'");
        List<List<String>> content = new ArrayList<List<String>>();
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
        for (List<String> con : content) {
            System.out.println(con);
        }
    }
 }
