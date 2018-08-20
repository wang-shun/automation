package com.gome.test.api.ide.dao;

import com.gome.test.api.ide.model.OrderCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class OrderCases {

    private Workbook workbook;
    private File excelPath;

    public OrderCases(String orderCaseExcelPath)
            throws IOException {
        this(new File(orderCaseExcelPath));
    }

    public OrderCases(File orderCaseExcelFile)
            throws IOException {
        excelPath = orderCaseExcelFile;
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

    public OrderCases(InputStream is)
            throws IOException {
        try {
            workbook = new XSSFWorkbook(is);
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public ArrayList<OrderCase> query(String sheetName) throws IOException {
        ArrayList<OrderCase> orderCases = new ArrayList<OrderCase>();
        Set<String> s1 = new HashSet<String>();
        Set<String> s2 = new HashSet<String>();
        // skip non-OrderList sheet
        if (!sheetName.startsWith("OrderList")) {
            return null;
        }
        Sheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> rowIter = sheet.iterator();
        // skip headers
        if (!rowIter.hasNext()) {
            return null;
        }
        Row header = rowIter.next();
        List<String> headers = ExcelUtils.getHeadersFrom(header);
        int caseNameId = headers.indexOf("用例名称");

        // Traversal each row
        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            if (row.getLastCellNum() < 4) {
                continue;
            }

            int t = 0;
            // get case id
            Cell cell = row.getCell(t++);
            if (null == cell) {
                continue;
            }
            String id = ExcelUtils.getCellValue(cell).toString();
            if ("".equals(id)) {
                continue;
            }

            // get case name
            String caseName = "Unknown";
            if (caseNameId == t) {
                cell = row.getCell(t++);
                if (null == cell) {
                    continue;
                }
                caseName = ExcelUtils.getCellValue(cell).toString();
                if ("".equals(caseName)) {
                    caseName = "UnKnown";
                }
            }

            // continue or skip after failure
            cell = row.getCell(t++);
            boolean continueAfterFailure = true;
            if (null != cell) {
                continueAfterFailure = Boolean.valueOf(ExcelUtils.getCellValue(cell).toString());
            }

            // get owner
            cell = row.getCell(t++);
            String owner = "Unknown";
            if (null != cell) {
                owner = ExcelUtils.getCellValue(cell).toString();
            }

            if (s1.contains(id)) {
                throw new IllegalArgumentException(String.format(
                        "Duplicated ordercase '%s' found in order cases", id));
            }
            s1.add(id);

            // get test steps
            List<String> steps = new ArrayList<String>();
            s2.clear();
            for (int j = t; j < row.getLastCellNum(); ++j) {
                cell = row.getCell(j);
                if (null == cell) {
                    continue;
                }
                String step = ExcelUtils.getCellValue(cell).toString();
                if (null == step) {
                    continue;
                }
                step = step.trim();
                if (step.isEmpty()) {
                    continue;
                }
                if (s2.contains(step)) {
                    throw new IllegalArgumentException(String.format(
                            "Duplicated case '%s' found in order cases", step));
                }
                s2.add(step);
                steps.add(step);
            }

            OrderCase orderCase = new OrderCase(id, caseName,
                    continueAfterFailure, owner, steps, null);
            orderCases.add(orderCase);
        }
        return orderCases;

    }

    public OrderCase query(String sheetName, String orderCaseId) throws IOException {
        Sheet sheet = workbook.getSheet(sheetName);
        Row row = getRowByOrderCaseId(sheet, orderCaseId);
        if (row.getLastCellNum() < 4) {
            return null;
        }

        int t = 0;
        // get case id
        Cell cell = row.getCell(t++);
        if (null == cell) {
            return null;
        }
        String id = ExcelUtils.getCellValue(cell).toString();
        if ("".equals(id)) {
            return null;
        }

        String caseName;
        cell = row.getCell(t++);
        if (null == cell) {
            return null;
        }
        caseName = ExcelUtils.getCellValue(cell).toString();
        if ("".equals(caseName)) {
            caseName = "UnKnown";
        }

        // continue or skip after failure
        cell = row.getCell(t++);
        boolean continueAfterFailure = true;
        if (null != cell) {
            continueAfterFailure = Boolean.valueOf(ExcelUtils.getCellValue(cell).toString());
        }
        // get owner
        cell = row.getCell(t++);
        String owner = "Unknown";
        if (null != cell) {
            owner = ExcelUtils.getCellValue(cell).toString();
        }

        // get test steps
        List<String> steps = new ArrayList<String>();
        for (int j = t; j < row.getLastCellNum(); ++j) {
            cell = row.getCell(j);
            if (null == cell) {
                continue;
            }
            String step = ExcelUtils.getCellValue(cell).toString();
            if (null == step) {
                continue;
            }
            step = step.trim();
            if (step.isEmpty()) {
                continue;
            }
            steps.add(step);
        }

        OrderCase orderCase = new OrderCase(id, caseName,
                continueAfterFailure, owner, steps, null);
        return orderCase;

    }

    public void update(String sheetName, OrderCase orderCase, String newCaseId) throws IOException, Exception {
        if (!sheetName.startsWith("OrderList")) {
            return;
        }
        Sheet sheet = workbook.getSheet(sheetName);
        Row existRow = getRowByOrderCaseId(sheet, newCaseId);
        if (existRow != null) {
            throw new Exception("用例编号重复");
        }
        Iterator<Row> rowIter = sheet.iterator();
        // skip headers
        if (!rowIter.hasNext()) {
            return;
        }
        Row header = rowIter.next();
        List<String> headers = ExcelUtils.getHeadersFrom(header);
        String orderCaseId = orderCase.getId();
        Row row = getRowByOrderCaseId(sheet, orderCaseId);

        int lastRowNum = row.getLastCellNum();
        for (int i = 4; i < lastRowNum; i++) {
            row.removeCell(row.getCell(i));
        }
        // test steps
        List<String> steps = orderCase.getSteps();
        for (int j = 0; j < steps.size(); ++j) {
            row.createCell(j + 4).setCellValue(steps.get(j));
        }

        for (int i = 0; i < 4; ++i) {
            if ("用例编号".equals(headers.get(i))) {
                row.createCell(i).setCellValue(newCaseId);
            } else if ("用例名称".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.getCaseDesc());
            } else if ("ContinueAfterFailure".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.isContinueAfterFailure());
            } else if ("Owner".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.getOwner());
            }
        }

    }

    public void update(String sheetName, OrderCase orderCase) throws IOException {
        if (!sheetName.startsWith("OrderList")) {
            return;
        }
        Sheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> rowIter = sheet.iterator();
        // skip headers
        if (!rowIter.hasNext()) {
            return;
        }
        Row header = rowIter.next();
        List<String> headers = ExcelUtils.getHeadersFrom(header);
        String orderCaseId = orderCase.getId();
        Row row = getRowByOrderCaseId(sheet, orderCaseId);

        int lastRowNum = row.getLastCellNum();
        for (int i = 4; i < lastRowNum; i++) {
            row.removeCell(row.getCell(i));
        }
        // test steps
        List<String> steps = orderCase.getSteps();
        for (int j = 0; j < steps.size(); ++j) {
            row.createCell(j + 4).setCellValue(steps.get(j));
        }

        for (int i = 0; i < 4; ++i) {
            if ("用例编号".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCaseId);
            } else if ("用例名称".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.getCaseDesc());
            } else if ("ContinueAfterFailure".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.isContinueAfterFailure());
            } else if ("Owner".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.getOwner());
            }
        }

    }


    public void updateSheet(String oldSheetName, String newSheetName) {
        workbook.setSheetName(workbook.getSheetIndex(oldSheetName), newSheetName);
    }

    public void updateCaseName(String sheetName, OrderCase orderCase) {
        Sheet sheet = workbook.getSheet(sheetName);

        Row row = getRowByOrderCaseId(sheet, orderCase.getId());
        List<String> sheetHeaders = ExcelUtils.toHeaders(sheet.getRow(0));
        for (int i = 0; i < sheetHeaders.size(); ++i) {
            if ("用例名称".equals(sheetHeaders.get(i))) {
                Cell cell = row.getCell(i);
                if (null == cell) {
                    cell = row.createCell(i);
                }
                cell.setCellValue(orderCase.getCaseDesc());
            }
        }

    }

    public void delete(String sheetName, String orderCaseId) throws IOException {
        Sheet sheet = workbook.getSheet(sheetName);
        int lastRowNum = sheet.getLastRowNum();
        Row row = getRowByOrderCaseId(sheet, orderCaseId);
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

    public void add(String sheetName, OrderCase orderCase) throws IOException {
        String caseId = String.format("%s_%03d",
                "OrderCase",
                System.currentTimeMillis());
        orderCase.setId(caseId);
        if ("".equals(orderCase.getCaseDesc())) {
            orderCase.setCaseDesc(caseId);
        }
        if (!sheetName.startsWith("OrderList")) {
            return;
        }
        Sheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> rowIter = sheet.iterator();
        // skip headers
        if (!rowIter.hasNext()) {
            return;
        }
        Row header = rowIter.next();
        List<String> headers = ExcelUtils.getHeadersFrom(header);
        int rowId = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(rowId);

        // test steps
        List<String> steps = orderCase.getSteps();
        for (int j = 0; j < steps.size(); ++j) {
            row.createCell(j + 4).setCellValue(steps.get(j));
        }

        for (int i = 0; i < 4; ++i) {
            if ("用例编号".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.getId());
            } else if ("用例名称".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.getCaseDesc());
            } else if ("ContinueAfterFailure".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.isContinueAfterFailure());
            } else if ("Owner".equals(headers.get(i))) {
                row.createCell(i).setCellValue(orderCase.getOwner());
            }
        }

    }

    private Row getRowByOrderCaseId(Sheet sheet, String ordercaseId) {
        int lastRowNum = sheet.getLastRowNum();
        Row row = null;
        for (int i = 1; i <= lastRowNum; ++i) {
            Row r = sheet.getRow(i);
            Object cellValueObj = ExcelUtils.getCellValue(r.getCell(0));
            if (null == cellValueObj) {
                continue;
            }
            if (cellValueObj.toString().equals(ordercaseId)) {
                row = r;
                break;
            }
        }
        return row;
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
//    public static void main(String[] args) throws IOException {
//        OrderCases orderCases = new OrderCases("D:\\Develop\\SVN\\Test\\JAPITestDriver\\Trunk\\Samples\\tutorial\\xiaofeng.liu\\JAPITest\\TestCase\\OrderCase.xlsx", "OrderList");
//    }
}
