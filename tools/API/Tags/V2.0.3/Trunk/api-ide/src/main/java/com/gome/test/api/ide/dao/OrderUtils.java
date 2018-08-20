package com.gome.test.api.ide.dao;

import com.gome.test.api.ide.model.OrderNode;
import com.gome.test.api.ide.model.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OrderUtils {

    private ArrayList<OrderNode> orderNodes;
    private Set<String> skipPaths;

    public OrderUtils(String testCaseDir) throws IOException, Exception {
        this(new File(testCaseDir));
    }

    public OrderUtils(File testCaseDir) throws IOException, Exception {
        orderNodes = new ArrayList<OrderNode>();
        skipPaths = new HashSet<String>();
        getAllTestCase(testCaseDir);
    }

    private void getAllTestCase(File testCaseDir) throws Exception {
        File globalSettingsExcel = new File(testCaseDir, "Globle.xlsx");
        if (globalSettingsExcel.isFile()) {
            this.skipPaths.add(globalSettingsExcel.getAbsolutePath());
        }
        File orderCaseExcel = new File(testCaseDir, "OrderCase.xlsx");
        if (orderCaseExcel.isFile()) {
            this.skipPaths.add(orderCaseExcel.getAbsolutePath());
        }
        getTest(testCaseDir, "test");
    }

    private void getTest(File testCaseDir, String methodPrefix) throws Exception {
        for (File file : testCaseDir.listFiles()) {
            if (file.isHidden() || skipPaths.contains(file.getAbsolutePath())) {
                continue;
            }

            String filename = file.getName();
            if (file.isDirectory()) {
                String newMethodPrefix = String.format("%s_%s", methodPrefix, filename);
                getTest(file, newMethodPrefix);
            } else if (filename.endsWith(".xlsx")) {
                filename = filename.substring(0, filename.lastIndexOf("."));
                if (!JAPIUtils.isClassFileName(filename)) {
                    continue;
                }
                String newMethodPrefix = String.format("%s_%s", methodPrefix, filename);
                ArrayList<TestCase> testCases = new ArrayList<TestCase>();
                getTestUnderFile(file, testCases);

                for (TestCase testCase : testCases) {
                    OrderNode treeNode = new OrderNode(testCase.getName(),
                            String.format("%s_%s", newMethodPrefix, testCase.getId())
                            , "false");
                    orderNodes.add(treeNode);
                }
            }
        }
    }

    private void getTestUnderFile(File testCaseExcelPath, ArrayList<TestCase> testCases) throws Exception {
        InputStream is = null;
        try {
            is = new FileInputStream(testCaseExcelPath);
            Workbook workbook = new XSSFWorkbook(is);
            int sheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNum; ++i) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                loadCasesInSheet(sheetName, workbook, testCases);
            }
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    private boolean loadCasesInSheet(String sheetName, Workbook workbook, ArrayList<TestCase> testCases) throws IOException {
        // skip non-testsuites sheet
        if (!sheetName.startsWith("testsuites")) {
            return false;
        }

        Sheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> rowIter = sheet.iterator();
        if (!rowIter.hasNext()) {
            return false;
        }
        List<String> sheetHeaders = ExcelUtils.toHeaders(rowIter.next());
        int id = sheetHeaders.indexOf("用例编号");
        int caseNameId = sheetHeaders.indexOf("用例名称");
        if (-1 == id || -1 == caseNameId) {
            return false;
        }
        int ownerId = sheetHeaders.indexOf("Owner");

        if (!rowIter.hasNext()) {
            return false;
        }
        rowIter.next();

        if (!rowIter.hasNext()) {
            return false;
        }
        rowIter.next();

        // Traversal each row
        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            // get case id
            Object caseIdObj = ExcelUtils.getCellValue(row.getCell(id));
            if (null == caseIdObj) {
                continue;
            }
            String caseId = caseIdObj.toString().trim();
            if ("".equals(caseId)) {
                continue;
            }

            // get case name
            Object caseNameObj = ExcelUtils.getCellValue(row.getCell(caseNameId));
            if (null == caseNameObj) {
                continue;
            }
            String caseName = caseNameObj.toString().trim();
            TestCase testCase = new TestCase();
            testCase.setId(caseId);
            testCase.setName(caseName);
            testCases.add(testCase);
        }
        return true;
    }

    public ArrayList<OrderNode> getOrderNodes() {
        return orderNodes;
    }

}
