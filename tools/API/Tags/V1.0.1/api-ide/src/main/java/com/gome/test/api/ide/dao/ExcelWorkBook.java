package com.gome.test.api.ide.dao;

import com.gome.test.api.ide.model.CaseVariable;
import com.gome.test.api.ide.model.CaseVariables;
import com.gome.test.api.ide.model.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWorkBook {

    private File excelPath;
    private Workbook workbook;

    public ExcelWorkBook(String excelPath) throws IOException {
        this(new File(excelPath));
    }

    public ExcelWorkBook(File excelPath) throws IOException {
        this.excelPath = excelPath;
        InputStream is = null;
        try {
            is = new FileInputStream(excelPath);
            workbook = new XSSFWorkbook(is);
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public ExcelWorkBook(InputStream inputStream) throws IOException {
        InputStream is = inputStream;
        try {
            workbook = new XSSFWorkbook(is);
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public void deleteSheet(String sheetName) {
        workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
    }

    public void addSheet(String sheetName, Sheet templateSheet) {
        Sheet sheet = workbook.createSheet(sheetName);
        CopySheets.copySheets(sheet, templateSheet);
    }

    public void updateSheet(String oldSheetName, String newSheetName) {
        workbook.setSheetName(workbook.getSheetIndex(oldSheetName), newSheetName);
    }

    public Sheet getSheet(String sheetName) {
        return workbook.getSheet(sheetName);
    }

    public void add(String sheetName, TestCase testCase) {
        String path = excelPath.getName();
        String caseId = String.format("%s_%03d",
                path.substring(0, path.lastIndexOf('.')),
                System.currentTimeMillis());
        testCase.setId(caseId);
        if ("".equals(testCase.getName())) {
            testCase.setName(caseId);
        }
        Sheet sheet = workbook.getSheet(sheetName);
        int rowId = sheet.getLastRowNum() + 1;
        Map<String, Integer> cellMap = new HashMap<String, Integer>();
        Row header = sheet.getRow(1);
        Iterator<Cell> cellIter = header.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String param = ExcelUtils.getValueOfCell(cell).toString().trim();
            cellMap.put(param, cell.getColumnIndex());
        }
        Row row = sheet.createRow(rowId);
        Map<String, String> params = testCase.getParams();
        if (!(sheetName.startsWith("p-suites-tpl"))) {
            params.remove("caseVariables");
        }
        for (String key : params.keySet()) {
            Integer index = cellMap.get(key);
            row.createCell(index).setCellValue(params.get(key));
        }

        List<String> sheetHeaders = ExcelUtils.toHeaders(sheet.getRow(0));
        for (int i = 0; i < sheetHeaders.size(); ++i) {
            if ("用例编号".equals(sheetHeaders.get(i))) {
                row.createCell(i).setCellValue(caseId);
            } else if ("用例名称".equals(sheetHeaders.get(i))) {
                row.createCell(i).setCellValue(testCase.getName());
            } else if ("Owner".equals(sheetHeaders.get(i))) {
                row.createCell(i).setCellValue(testCase.getOwner());
            }
        }
    }

    public void padd(String sheetName, TestCase testCase) {
        //创建case的新的一行
        String path = excelPath.getName();
        String caseId = String.format("%s_%03d",
                path.substring(0, path.lastIndexOf('.')),
                System.currentTimeMillis());
        testCase.setId(caseId);
        if ("".equals(testCase.getName())) {
            testCase.setName(caseId);
        }
        Sheet sheet = workbook.getSheet(sheetName);
        int rowId = sheet.getLastRowNum() + 1;
        Map<String, Integer> cellMap = new HashMap<String, Integer>();
        Row header = sheet.getRow(0);
        Iterator<Cell> cellIter = header.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String param = ExcelUtils.getValueOfCell(cell).toString().trim();
            cellMap.put(param, cell.getColumnIndex());
        }
        Row row = sheet.createRow(rowId);
        //从模板中取得变量的key
        Map<String, String> params = testCase.getParams();
        CaseVariables caseVariables = CaseVariables.loadFrom(params.get("caseVariables"));
        HashMap<String, String> variables = new HashMap<String, String>();
        for (CaseVariable c : caseVariables) {
            variables.put(c.getKey(), c.getValue());
        }
        for (String key : variables.keySet()) {
            Integer index = cellMap.get(key);
            row.createCell(index).setCellValue(variables.get(key));
        }
        Integer index = cellMap.get("用例参数");
        row.createCell(index).setCellValue(params.get("verifyClass"));
        row.createCell(index + 1).setCellValue(params.get("verifySteps"));

        List<String> sheetHeaders = ExcelUtils.toHeaders(sheet.getRow(0));
        for (int i = 0; i < sheetHeaders.size(); ++i) {
            if ("用例编号".equals(sheetHeaders.get(i))) {
                row.createCell(i).setCellValue(caseId);
            } else if ("用例名称".equals(sheetHeaders.get(i))) {
                row.createCell(i).setCellValue(testCase.getName());
            } else if ("Owner".equals(sheetHeaders.get(i))) {
                row.createCell(i).setCellValue(testCase.getOwner());
            }
        }
    }

    public void delete(String sheetName, String caseId) {
        Sheet sheet = workbook.getSheet(sheetName);
        int lastRowNum = sheet.getLastRowNum();
        Row row = getRowByCaseId(sheet, caseId);
        if (null != row) {
            int rowNum = row.getRowNum();
            if (rowNum < lastRowNum) {
                sheet.removeRow(row);
                sheet.shiftRows(rowNum + 1, lastRowNum, -1);
            } else {
                sheet.removeRow(row);
            }
        }
    }

    public void insert(String sheetName, int sourceRowNum, int targetRowNum, String position) throws IOException {
        Sheet sheet = workbook.getSheet(sheetName);
        CopyRow.copyRow(workbook, sheet, sourceRowNum, targetRowNum);
    }

    public void move(String sheetName, String sourceCaseId, String targetCaseId, String position, int[] loc) {
        Sheet sheet = workbook.getSheet(sheetName);
        int targetRowNum = getRowByCaseId(sheet, targetCaseId).getRowNum();
        int sourceRowNum = getRowByCaseId(sheet, sourceCaseId).getRowNum();
        loc[0] = sourceRowNum;
        int rowNum = sheet.getLastRowNum();
        int destRowNum = targetRowNum;
        if ((targetRowNum == rowNum) && (position.equals("after"))) {
            loc[1] = rowNum + 1;
            return;
        }
        if ("after".equals(position)) {
            destRowNum = targetRowNum + 1;
        }
        if (sourceRowNum >= destRowNum) {
            loc[0] = sourceRowNum + 1;
        }
        loc[1] = destRowNum;
        sheet.shiftRows(destRowNum, rowNum, 1);
    }

    public void delete(String sheetName, int sourceRowNum, int targetRowNum, String position) {
        Sheet sheet = workbook.getSheet(sheetName);
        int lastRowNum = sheet.getLastRowNum();
        Row row = sheet.getRow(sourceRowNum);
        if (null != row) {
            int rowNum = row.getRowNum();
            if (sourceRowNum < lastRowNum) {
                sheet.removeRow(row);
                sheet.shiftRows(rowNum + 1, lastRowNum, -1);
            } else {
                sheet.removeRow(row);
            }
        }
    }

    public void update(String sheetName, TestCase testCase) {
        Sheet sheet = workbook.getSheet(sheetName);
        Map<String, Integer> cellMap = new HashMap<String, Integer>();
        Row header = sheet.getRow(1);
        Iterator<Cell> cellIter = header.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String param = ExcelUtils.getValueOfCell(cell).toString().trim();
            cellMap.put(param, cell.getColumnIndex());
        }
        Row row = getRowByCaseId(sheet, testCase.getId());
        Map<String, String> params = testCase.getParams();
        for (String key : params.keySet()) {
            Integer index = cellMap.get(key);
            if (null == index) {
                continue;
            }
            Cell cell = row.getCell(index);
            if (null == cell) {
                cell = row.createCell(index);
            }
            cell.setCellValue(params.get(key));
        }
    }

    public void pupdate(String sheetName, TestCase testCase) {
        Sheet sheet = workbook.getSheet(sheetName);
        Map<String, Integer> cellMap = new HashMap<String, Integer>();
        Row header = sheet.getRow(0);
        Iterator<Cell> cellIter = header.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String param = ExcelUtils.getValueOfCell(cell).toString().trim();
            cellMap.put(param, cell.getColumnIndex());
        }
        Row row = getRowByCaseId(sheet, testCase.getId());
        Map<String, String> params = testCase.getParams();
        Map<String, String> variables = new HashMap<String, String>();
        if (params.containsKey("caseVariables")) {
            CaseVariables caseVariables = CaseVariables.loadFrom(params.get("caseVariables"));
            for (CaseVariable c : caseVariables) {
                variables.put(c.getKey(), c.getValue());
            }
        }
        for (String key : variables.keySet()) {
            Integer index = cellMap.get(key);
            if (null == index) {
                continue;
            }
            Cell cell = row.getCell(index);
            if (null == cell) {
                cell = row.createCell(index);
            }
            cell.setCellValue(variables.get(key));
        }

        Integer index = cellMap.get("用例参数");
        Cell cell = row.getCell(index);
        if (null == cell) {
            cell = row.createCell(index);
        }
        cell.setCellValue(params.get("verifyClass"));
        cell = row.getCell(index + 1);
        if (null == cell) {
            cell = row.createCell(index);
        }
        cell.setCellValue(params.get("verifySteps"));
    }

    public void update(String sheetName, TestCase testCase, String newCaseId) throws Exception {

        Sheet sheet = workbook.getSheet(sheetName);

        Row existRow = getRowByCaseId(sheet, newCaseId);
        if (existRow != null) {
            throw new Exception("用例编号重复");
        }

        Map<String, Integer> cellMap = new HashMap<String, Integer>();
        Row header = sheet.getRow(1);
        Iterator<Cell> cellIter = header.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String param = ExcelUtils.getValueOfCell(cell).toString().trim();
            cellMap.put(param, cell.getColumnIndex());
        }
        Row row = getRowByCaseId(sheet, testCase.getId());
        Map<String, String> params = testCase.getParams();
        for (String key : params.keySet()) {
            Integer index = cellMap.get(key);
            if (null == index) {
                continue;
            }
            Cell cell = row.getCell(index);
            if (null == cell) {
                cell = row.createCell(index);
            }
            cell.setCellValue(params.get(key));
        }

        List<String> sheetHeaders = ExcelUtils.toHeaders(sheet.getRow(0));
        for (int i = 0; i < sheetHeaders.size(); ++i) {
            if ("用例编号".equals(sheetHeaders.get(i))) {
                Cell cell = row.getCell(i);
                if (null == cell) {
                    cell = row.createCell(i);
                }
                cell.setCellValue(newCaseId);
            }
        }
    }

    public void updateCaseName(String sheetName, TestCase testCase) {
        Sheet sheet = workbook.getSheet(sheetName);

        Row row = getRowByCaseId(sheet, testCase.getId());
        List<String> sheetHeaders = ExcelUtils.toHeaders(sheet.getRow(0));
        for (int i = 0; i < sheetHeaders.size(); ++i) {
            if ("用例名称".equals(sheetHeaders.get(i))) {
                Cell cell = row.getCell(i);
                if (null == cell) {
                    cell = row.createCell(i);
                }
                cell.setCellValue(testCase.getName());
            }
        }

    }

    public void updateResponse(String sheetName, String caseId, String responseCopy) {
        Sheet sheet = workbook.getSheet(sheetName);
        Row row = getRowByCaseId(sheet, caseId);
        List<String> sheetHeaders = ExcelUtils.toHeaders(sheet.getRow(0));
        for (int i = 0; i < sheetHeaders.size(); ++i) {
            if ("Response".equals(sheetHeaders.get(i))) {
                Cell cell = row.getCell(i);
                if (null == cell) {
                    cell = row.createCell(i);
                }
                cell.setCellValue(responseCopy);
            }
        }

    }

    public String queryResponse(String sheetName, String caseId) {
        String response = null;
        Sheet sheet = workbook.getSheet(sheetName);
        Row row = getRowByCaseId(sheet, caseId);
        List<String> sheetHeaders = ExcelUtils.toHeaders(sheet.getRow(0));
        for (int i = 0; i < sheetHeaders.size(); ++i) {
            if ("Response".equals(sheetHeaders.get(i))) {
                Cell cell = row.getCell(i);
                if (null == cell) {
                    return response;
                } else {
                    response = cell.getStringCellValue();
                }
                break;
            }
        }
        return response;
    }

    public List<String> getSuiteNames() {
        List<String> suiteNames = new ArrayList<String>();
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; ++i) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            if (sheetName.startsWith("testsuites")) {
                suiteNames.add(sheetName);
            } else if (sheetName.startsWith("p-suites-")) {
                suiteNames.add(sheetName);
            }
        }
        return suiteNames;
    }

    public List<String> getOrderSuiteNames() {
        List<String> suiteNames = new ArrayList<String>();
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; ++i) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            if (sheetName.startsWith("OrderList")) {
                suiteNames.add(sheetName);
            }
        }
        return suiteNames;
    }

    public void dump() throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(excelPath);
            workbook.write(os);
        } finally {
            if (null != os) {
                os.close();
            }
        }
    }

    private Row getRowByCaseId(Sheet sheet, String caseId) {
        int lastRowNum = sheet.getLastRowNum();
        Row row = null;
        for (int i = 3; i <= lastRowNum; ++i) {
            Row r = sheet.getRow(i);
            Object cellValueObj = ExcelUtils.getCellValue(r.getCell(0));
            if (null == cellValueObj) {
                continue;
            }
            if (cellValueObj.toString().equals(caseId)) {
                row = r;
                break;
            }
        }
        return row;
    }
}
