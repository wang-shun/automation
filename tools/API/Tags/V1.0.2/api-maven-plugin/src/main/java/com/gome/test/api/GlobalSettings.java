package com.gome.test.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GlobalSettings {

    public GlobalSettings() {
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
            List<String> headers = ExcelUtils.getHeadersFrom(rowIter.next());
            if (0 == headers.size()) {
                continue;
            }

            // Traversal each row
            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                if (null == row.getCell(0)) {
                    continue;
                }
                String key = ExcelUtils.getCellValue(row.getCell(0)).toString();
                int cellNum = row.getLastCellNum();
                for (int k = 1; k < cellNum; ++k) {
                    Cell cell = row.getCell(k);
                    Object cellValue = ExcelUtils.getCellValue(cell);
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
        if (null != excelFile && excelFile.exists()) {
            InputStream is = new FileInputStream(excelFile);
            loadFrom(is);
        }
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

    private void doMap(String sheetName, String rowHeader, String cellHeader, Object cellValue) {
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

    private final Map<String, Object> m;
}
