package com.ofo.test.utils;


import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {

    private static boolean isMergedRegion(Sheet sheet, int row, int col) {
        int sheetMergedCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergedCount; ++i) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstCol = range.getFirstColumn();
            int lastCol = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow
                    && col >= firstCol && col <= lastCol) {
                return true;
            }
        }
        return false;
    }

    private static Object getMergedRegionValue(Sheet sheet, int row, int col) {
        int sheetMergedCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergedCount; ++i) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstCol = range.getFirstColumn();
            int lastCol = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow
                    && col >= firstCol && col <= lastCol) {
                return getValueOfCell(sheet.getRow(firstRow).getCell(firstCol));
            }
        }
        return null;
    }

    public static Object getValueOfCell(Cell cell) {
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

    public static Object getCellValue(Cell cell) {
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

    public static List<String> toHeaders(Row row) {
        List<String> headers = new ArrayList<String>();
        int cellNum = row.getLastCellNum();
        for (int i = 0; i < cellNum; ++i) {
            Cell cell = row.getCell(i);
            Object cellValue = ExcelUtils.getCellValue(cell);
            if (null != cellValue) {
                headers.add(cellValue.toString());
            } else {
                headers.add("");
            }
        }
        return headers;
    }

    public static List<String> getHeadersFrom(Row row) {
        List<String> headers = new ArrayList<String>();
        int cellNum = row.getLastCellNum();
        for (int i = 0; i < cellNum; ++i) {
            Cell cell = row.getCell(i);
            Object cellValue = ExcelUtils.getCellValue(cell);
            if (null != cellValue) {
                headers.add(cellValue.toString());
            } else {
                headers.add("");
            }
        }
        return headers;
    }

    public static void clearRow(Row row)
    {
        Iterator<Cell> cellIter = row.cellIterator();
        while (cellIter.hasNext()) {
            cellIter.next().setCellValue("");
        }
    }
}
