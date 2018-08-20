package com.gome.test.api.ide.bo;

import com.gome.test.api.ide.dao.ExcelWorkBook;
import com.gome.test.api.ide.dao.GlobalSettingsExcel;
import com.gome.test.api.ide.dao.OrderCases;
import com.gome.test.api.ide.dao.TestCases;
import com.gome.test.api.ide.model.CaseVariable;
import com.gome.test.api.ide.model.CaseVariables;
import com.gome.test.api.ide.model.OrderCase;
import com.gome.test.api.ide.model.TestCase;
import com.gome.test.api.ide.model.TreeNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gome.test.api.model.ClientEnum;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TreeNodeBo {

    @Value(value = "${tests.path}")
    private String testsPath;

    @Autowired
    private TestCaseBo testCaseBo;

    public List<TreeNode> getChildren(String node, String type) throws Exception {
        if ("folder".equals(type)) {
            return getChildrenUnderFolder(node);
        } else if ("xlsx".equals(type)) {
            return getChildrenUnderExcel(node);
        } else if ("suite".equals(type)) {
            return getChildrenUnderSuite(node);
        } else if ("order".equals(type)) {
            return getChildrenUnderOrderExcel(node);
        } else if ("ordersuite".equals(type)) {
            return getChildrenUnderOrderSuite(node);
        } else {
            return new ArrayList<TreeNode>();
        }
    }

    public void deleteNode(String node) throws Exception {
        if (node.contains("#")) {
            String sheetName = node.split("#")[1];
            File excelPath = new File(String.format("%s/%s",
                    testsPath, node.split("#")[0]));
            ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
            workbook.deleteSheet(sheetName);
            workbook.dump();
        }
    }

    public String addFolder(String node, String name) throws Exception {
        File nodePath = new File(String.format("%s/%s/%s", testsPath, node, name));
        if (nodePath.exists()) {
            throw new Exception(String.format("文件夹%s/%s已存在", node, name));
        }
        nodePath.mkdir();
        if ("".equals(node)) {
            return name;
        }
        return String.format("%s/%s", node, name);
    }

    public String addFile(String node, String name) throws Exception {
        File nodePath = new File(String.format("%s/%s/%s.xlsx", testsPath, node, name));
        if (nodePath.exists()) {
            throw new Exception(String.format("Excel文件%s/%s.xlsx已存在", node, name));
        }
        Resource res = new ClassPathResource("template.xlsx");
        FileUtils.copyInputStreamToFile(res.getInputStream(), nodePath);
        ExcelWorkBook workbook = new ExcelWorkBook(nodePath);
        Sheet OrderListSheet = workbook.getSheet("OrderList");
        if (OrderListSheet != null) {
            workbook.deleteSheet("OrderList");
            workbook.dump();
        }
        workbook = new ExcelWorkBook(nodePath);
        Sheet devSheet = workbook.getSheet("testsuites-for-dev");
        if (devSheet != null) {
            workbook.deleteSheet("testsuites-for-dev");
            workbook.dump();
        }
        workbook = new ExcelWorkBook(nodePath);
        Sheet p_suites_tpl = workbook.getSheet("p-suites-tpl");
        if (p_suites_tpl != null) {
            workbook.deleteSheet("p-suites-tpl");
            workbook.dump();
        }
        workbook = new ExcelWorkBook(nodePath);
        Sheet p_suites = workbook.getSheet("p-suites");
        if (p_suites != null) {
            workbook.deleteSheet("p-suites");
            workbook.dump();
        }
        return String.format("%s/%s.xlsx", node, name);
    }

    public String addSuite(String node, String name) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        Resource res = new ClassPathResource("template.xlsx");
        ExcelWorkBook workbook = new ExcelWorkBook(res.getInputStream());
        Sheet templateSheet = workbook.getSheet("testsuites");

        workbook = new ExcelWorkBook(excelPath);
        workbook.addSheet("testsuites" + name, templateSheet);
        workbook.dump();
        return String.format("%s#testsuites%s", node, name);
    }

    public String addPSuite(String node, String name) throws Exception {
        // 读取template excel
        File excelPath = new File(String.format("%s/%s",
                testsPath, node));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        Resource res = new ClassPathResource("template.xlsx");
        ExcelWorkBook workbook = new ExcelWorkBook(res.getInputStream());
        Sheet templateSheet = workbook.getSheet("p-suites");

        workbook = new ExcelWorkBook(excelPath);
        String tpl_name = "p-suites-" + "tpl-" + name;
        Sheet tpl = workbook.getSheet(tpl_name);
        if (null == tpl) {
            throw new Exception("新建p-suites失败，请先建立模板p-suites-tpl-" + name + "！");
        }
        // load template case and get all variables
        File globalSettingsExcel = new File(String.format("%s/Globle.xlsx", testsPath));
        GlobalSettingsExcel settings = new GlobalSettingsExcel();
        if (globalSettingsExcel.exists()) {
            settings.loadFrom(globalSettingsExcel);
        }
        TestCases testcases = new TestCases(excelPath, settings, tpl_name);
        List<TestCase> testCaseList = testcases.getTestCases();
        TestCase testCase = testCaseList.get(0);
        CaseVariables caseVariables = testCase.getCaseVariables();
        ArrayList<String> variables = new ArrayList<String>();
        for (CaseVariable c : caseVariables) {
            variables.add(c.getKey());
        }
        //update vriables to p-suites
        workbook.addSheet("p-suites-" + name, templateSheet);
        Sheet suite = workbook.getSheet("p-suites-" + name);
        Iterator<Row> rowIter = suite.iterator();
        Row row = rowIter.next();
        int variableStart = 5;
        for (int i = 0; i < variables.size(); i++) {
            suite.addMergedRegion(new CellRangeAddress(0, (short) 2, i + variableStart, (short) (i + variableStart)));
            row.createCell(i + variableStart).setCellValue(variables.get(i));
        }
        workbook.dump();
        return String.format("%s#p-suites-%s", node, name);
    }


    public String addPSuiteTpl(String node, String name) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        Resource res = new ClassPathResource("template.xlsx");
        ExcelWorkBook workbook = new ExcelWorkBook(res.getInputStream());
        Sheet templateSheet = workbook.getSheet("p-suites-tpl");

        workbook = new ExcelWorkBook(excelPath);
        workbook.addSheet("p-suites-tpl-" + name, templateSheet);
        workbook.dump();
        return String.format("%s#p-suites-tpl-%s", node, name);
    }


    //added by zonglin.li
    public String addDevSuite(String node, String name) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        Resource res = new ClassPathResource("template.xlsx");
        ExcelWorkBook workbook = new ExcelWorkBook(res.getInputStream());
        Sheet templateSheet = workbook.getSheet("testsuites-for-dev");

        workbook = new ExcelWorkBook(excelPath);
        workbook.addSheet("testsuitesForDev" + name, templateSheet);
        workbook.dump();
        return String.format("%s#testsuitesForDev%s", node, name);
    }


    public String addOrderSuite(String node, String name) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        Resource res = new ClassPathResource("template.xlsx");
        ExcelWorkBook workbook = new ExcelWorkBook(res.getInputStream());
        Sheet templateSheet = workbook.getSheet("OrderList");

        workbook = new ExcelWorkBook(excelPath);
        workbook.addSheet("OrderList" + name, templateSheet);
        workbook.dump();
        return String.format("%s#OrderList%s", node, name);
    }

    public String updateSuite(String node, String name, String newSheetName) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node.split("#")[0]));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        workbook.updateSheet("testsuites" + name, "testsuites" + newSheetName);
        workbook.dump();
        return String.format("%s#testsuites%s", node.split("#")[0], newSheetName);
    }

    public String updatePSuiteTpl(String node, String name, String newSheetName) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node.split("#")[0]));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        workbook.updateSheet("p-suites-tpl-" + name, "p-suites-tpl-" + newSheetName);
        workbook.dump();
        return String.format("%s#p-suites-tpl-%s", node.split("#")[0], newSheetName);
    }

    public String updatePSuite(String node, String name, String newSheetName) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node.split("#")[0]));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        workbook.updateSheet("p-suites-" + name, "p-suites-" + newSheetName);
        workbook.dump();
        return String.format("%s#p-suites-%s", node.split("#")[0], newSheetName);
    }

    public String updateOrderSuite(String node, String name, String newSheetName) throws Exception {
        File excelPath = new File(String.format("%s/%s",
                testsPath, node.split("#")[0]));
        if (!excelPath.exists()) {
            throw new Exception(String.format("Excel文件%s不存在", node));
        }
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        workbook.updateSheet("OrderList" + name, "OrderList" + newSheetName);
        workbook.dump();
        return String.format("%s#OrderList%s", node.split("#")[0], newSheetName);
    }

    private List<TreeNode> getChildrenUnderFolder(String node) throws Exception {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        File nodePath = new File(String.format("%s/%s",
                testsPath, node));

        for (File f : nodePath.listFiles()) {
            if (f.isHidden() || f.getName().equals("Globle.xlsx")
                    || (f.isFile() && !f.getName().endsWith(".xlsx"))) {
                continue;
            }

            TreeNode treeNode = new TreeNode();
            treeNode.setLabel(f.getName());
            treeNode.setLoadOnDemand(true);
            treeNode.setChildren(new ArrayList<TreeNode>());
            if (f.isDirectory()) {
                treeNode.setType("folder");
            } else {
                if (f.getName().equals("OrderCase.xlsx")) {
                    treeNode.setType("order");
                } else {
                    treeNode.setType("xlsx");
                }
            }
            if ("".equals(node)) {
                treeNode.setId(f.getName());
            } else {
                treeNode.setId(String.format("%s/%s", node, f.getName()));
            }
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    private List<TreeNode> getChildrenUnderOrderExcel(String node) throws Exception {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        File nodePath = new File(String.format("%s/%s",
                testsPath, node));
        ExcelWorkBook workbook = new ExcelWorkBook(nodePath);
        List<String> suiteNames = workbook.getOrderSuiteNames();
        for (String suiteName : suiteNames) {
            TreeNode treeNode = new TreeNode(
                    String.format("%s#%s", node, suiteName), suiteName,
                    "ordersuite", true, new ArrayList<TreeNode>());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    private List<TreeNode> getChildrenUnderExcel(String node) throws Exception {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        File nodePath = new File(String.format("%s/%s",testsPath, node));

        ExcelWorkBook workbook = new ExcelWorkBook(nodePath);
        List<String> suiteNames = workbook.getSuiteNames();

        if (workbook.getClientType() == ClientEnum.HttpClient) {
            for (String suiteName : suiteNames) {
                TreeNode treeNode = new TreeNode(
                        String.format("%s#%s", node, suiteName), suiteName,
                        "suite", true, new ArrayList<TreeNode>());
                treeNodes.add(treeNode);
            }
        }
        return treeNodes;
    }

    private List<TreeNode> getChildrenUnderSuite(String node) throws Exception {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        String sheetName = node.split("#")[1];
        File excelPath = new File(String.format("%s/%s",
                testsPath, node.split("#")[0]));
        GlobalSettingsExcel settings = new GlobalSettingsExcel();
        TestCases testcases = new TestCases(excelPath, settings, sheetName);
        List<TestCase> testCaseList = testcases.getTestCases();
        for (TestCase testCase : testCaseList) {
            TreeNode treeNode = new TreeNode(
                    String.format("%s#%s", node, testCase.getId()),
                    testCase.getName(), "case", false, null);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    private List<TreeNode> getChildrenUnderOrderSuite(String node) throws Exception {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        String sheetName = node.split("#")[1];
        File excelPath = new File(String.format("%s/%s",
                testsPath, node.split("#")[0]));

        OrderCases orderCases = new OrderCases(excelPath);
        List<OrderCase> orderCaseList = orderCases.query(sheetName);
        for (OrderCase orderCase : orderCaseList) {
            TreeNode treeNode = new TreeNode(
                    String.format("%s#%s", node, orderCase.getId()),
                    orderCase.getCaseDesc(), "ordercase", false, null);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }
}
