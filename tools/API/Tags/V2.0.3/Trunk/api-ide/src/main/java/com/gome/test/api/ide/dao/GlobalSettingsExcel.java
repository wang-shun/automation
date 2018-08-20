package com.gome.test.api.ide.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GlobalSettingsExcel {

    public GlobalSettingsExcel() {
        m = new HashMap<String, Object>();
    }

    public void loadFrom(InputStream is) throws IOException {
        Workbook workbook = new XSSFWorkbook(is);
        is.close();
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; ++i) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName().trim();
            Iterator<Row> rowIter = sheet.iterator();
            // Get headers
            if (!rowIter.hasNext()) {
                continue;
            }
            List<String> headers = getHeaders(rowIter.next());
            if (0 == headers.size()) {
                continue;
            }

            // Traversal each row
            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                if (null == row.getCell(0)) {
                    continue;
                }
                String key = getCellValue(row.getCell(0)).toString();
                int cellNum = row.getLastCellNum();
                for (int k = 1; k < cellNum; ++k) {
                    Cell cell = row.getCell(k);
                    Object cellValue = getCellValue(cell);
                    if (null != cellValue && !"".equals(cellValue)) {
                        if (k < headers.size()) {
                            doMap(sheetName, key, headers.get(k), cellValue);
                        } else {
                            doMap(sheetName, key, "", cellValue);
                        }
                    }
                }
            }
        }
    }

    public void loadFrom(File excelFile) throws IOException {
        InputStream is = new FileInputStream(excelFile);
        loadFrom(is);
    }

    public void loadFrom(String excelPath) throws IOException {
        loadFrom(new File(excelPath));
    }

    public Object get(String key) {
        return m.get(key);
    }

    @Override
    public String toString() {
        return m.toString();
    }

    private boolean isMergedRegion(Sheet sheet, int row, int col) {
        int sheetMergedCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergedCount; ++i) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstCol = range.getFirstColumn();
            int lastCol = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow && col >= firstCol
                    && col <= lastCol) {
                return true;
            }
        }
        return false;
    }

    private Object getMergedRegionValue(Sheet sheet, int row, int col) {
        int sheetMergedCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergedCount; ++i) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstCol = range.getFirstColumn();
            int lastCol = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow && col >= firstCol
                    && col <= lastCol) {
                return getValueOfCell(sheet.getRow(firstRow).getCell(firstCol));
            }
        }
        return null;
    }

    private Object getValueOfCell(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                return "";
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:
                Double dval = cell.getNumericCellValue();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return HSSFDateUtil.getJavaDate(dval);
                }
                int ival = dval.intValue();
                if (dval - ival < Double.MIN_VALUE
                        && dval - ival > -Double.MIN_VALUE) {
                    return ival;
                }
                return dval;
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
        }
        return null;
    }

    private Object getCellValue(Cell cell) {
        if (null == cell) {
            return null;
        }
        Sheet sheet = cell.getSheet();
        int row = cell.getRowIndex();
        int col = cell.getColumnIndex();
        boolean isMerged = isMergedRegion(sheet, row, col);
        if (isMerged) {
            return getMergedRegionValue(sheet, row, col);
        }
        return getValueOfCell(cell);
    }

    private void doMap(String sheetName, String rowHeader, String cellHeader,
                       Object cellValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("{$");
        sb.append(sheetName);
        sb.append(".");
        sb.append(rowHeader);
        if (!"".equals(cellHeader)) {
            sb.append(".");
            sb.append(cellHeader);
        }
        sb.append("}");
        m.put(sb.toString(), cellValue);
    }

    private List<String> getHeaders(Row row) {
        List<String> headers = new ArrayList<String>();
        int cellNum = row.getLastCellNum();
        for (int i = 0; i < cellNum; ++i) {
            Cell cell = row.getCell(i);
            Object cellValue = getCellValue(cell);
            if (null != cellValue) {
                headers.add(cellValue.toString());
            } else {
                headers.add("");
            }
        }
        return headers;
    }

    private final Map<String, Object> m;
}
