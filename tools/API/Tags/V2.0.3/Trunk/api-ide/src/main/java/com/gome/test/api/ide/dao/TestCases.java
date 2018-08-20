package com.gome.test.api.ide.dao;

import com.gome.test.api.ide.model.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TestCases {

    private List<TestCase> testCases;

    public TestCases(String testCaseExcelPath, GlobalSettingsExcel settings)
            throws IOException {
        this(new File(testCaseExcelPath), settings);
    }

    public TestCases(File testCaseExcelFile, GlobalSettingsExcel settings)
            throws IOException {
        this(new FileInputStream(testCaseExcelFile), settings);
    }

    public TestCases(String testCaseExcelPath, GlobalSettingsExcel settings,
                     String sheetName) throws IOException {
        this(new File(testCaseExcelPath), settings, sheetName);
    }

    public TestCases(File testCaseExcelFile, GlobalSettingsExcel settings,
                     String sheetName) throws IOException {
        this(new FileInputStream(testCaseExcelFile), settings, sheetName);
    }

    public TestCases(InputStream is, GlobalSettingsExcel settings,
                     String sheetName) throws IOException {
        testCases = new ArrayList<TestCase>();
        try {
            Workbook workbook = new XSSFWorkbook(is);
            loadCasesInSheet(sheetName, workbook, settings);
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public TestCases(InputStream is, GlobalSettingsExcel settings)
            throws IOException {
        testCases = new ArrayList<TestCase>();
        try {
            Workbook workbook = new XSSFWorkbook(is);
            int sheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNum; ++i) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                loadCasesInSheet(sheetName, workbook, settings);
            }
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    private boolean loadCasesInSheet(String sheetName, Workbook workbook,
                                     GlobalSettingsExcel settings) throws IOException {
        // skip non-testsuites sheet
        if (!sheetName.startsWith("testsuites")) {
            if (!sheetName.startsWith("p-suites"))
                return false;
        }

        Sheet sheet = workbook.getSheet(sheetName);

        if (sheetName.startsWith("p-suites")) {
            if (!sheetName.startsWith("p-suites-tpl-")) {
                ExcelSheetDataSource dataSource = new ParamizedSheet();
                testCases = dataSource.parse(sheet, workbook, testCases, settings);
                return true;
            }
        }

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
        List<Integer> pos = new ArrayList<Integer>();
        List<String> paramKey = getParameters(rowIter.next(), pos);
        int size = paramKey.size();

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
            // get param value
            List<Object> paramValue = new LinkedList<Object>();
            for (int k = 0; k < size; ++k) {
                Object value = ExcelUtils.getCellValue(row.getCell(pos.get(k)));
                if (null == value) {
                    paramValue.add(null);
                    continue;
                }
                Object val = settings.get(value.toString());
                if (null != val) {
                    value = val;
                }
                paramValue.add(value);
            }

            Map<String, String> params = new HashMap<String, String>();
            for (int i = 0; i < paramKey.size(); ++i) {
                if (null == paramValue.get(i)) {
                    params.put(paramKey.get(i), null);
                } else {
                    params.put(paramKey.get(i), paramValue.get(i).toString());
                }
            }
            // get owner
            Object owner = "Unknown";
            if (-1 != ownerId) {
                owner = ExcelUtils.getCellValue(row.getCell(ownerId));
                if (null == owner || "".equals(owner.toString().trim())) {
                    owner = "Unknown";
                }
            }

            testCases.add(TestCase.loadFrom(caseId, caseName, params, owner.toString()));
        }
        return true;
    }

    private List<String> getParameters(Row row, List<Integer> pos) {
        List<String> params = new LinkedList<String>();
        Iterator<Cell> cellIter = row.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String param = ExcelUtils.getValueOfCell(cell).toString().trim();
            if (param.startsWith("$")) {
                param = param.substring(1);
            }
            if (!"".equals(param)) {
                params.add(param);
                pos.add(cell.getColumnIndex());
            }
        }
        return params;
    }
}
