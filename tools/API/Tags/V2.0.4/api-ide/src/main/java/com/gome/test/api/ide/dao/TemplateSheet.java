/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gome.test.api.ide.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author shan.tan
 */
public class TemplateSheet {

    public HashMap<String, String> parse(Sheet sheet, GlobalSettingsExcel settings) {
        HashMap<String, String> params = new HashMap<String, String>();
        Iterator<Row> rowIter = sheet.iterator();
        if (!rowIter.hasNext()) {
            return params;
        }
        List<String> sheetHeaders = ExcelUtils.toHeaders(rowIter.next());
        int id = sheetHeaders.indexOf("用例编号");
        int caseNameId = sheetHeaders.indexOf("用例名称");
        if (-1 == id || -1 == caseNameId) {
            return params;
        }
        if (!rowIter.hasNext()) {
            return params;
        }
        List<Integer> pos = new ArrayList<Integer>();
        List<String> paramKey = getParameters(rowIter.next(), pos);
        int size = paramKey.size();

        if (!rowIter.hasNext()) {
            return params;
        }
        rowIter.next();

        // Traversal only one row
        if (rowIter.hasNext()) {
            Row row = rowIter.next();
            // get case id
            Object caseIdObj = ExcelUtils.getCellValue(row.getCell(id));
            if (null == caseIdObj) {
                return params;
            }
            String caseId = caseIdObj.toString().trim();
            if ("".equals(caseId)) {
                return params;
            }
            // get case name
            Object caseNameObj = ExcelUtils.getCellValue(row.getCell(caseNameId));
            if (null == caseNameObj) {
                return params;
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

            for (int i = 0; i < paramKey.size(); ++i) {
                if (null == paramValue.get(i)) {
                    params.put(paramKey.get(i), null);
                } else {
                    params.put(paramKey.get(i), paramValue.get(i).toString());
                }
            }
        }
        return params;
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
