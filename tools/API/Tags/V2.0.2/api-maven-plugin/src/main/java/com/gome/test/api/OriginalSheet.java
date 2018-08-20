/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gome.test.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.gome.test.api.model.ClientEnum;
import com.gome.test.api.model.Constant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author shan.tan 所有以testsuites开头的sheet的处理方式
 */
public class OriginalSheet implements ExcelSheetDataSource {
    public ClientEnum parse(Sheet sheet, Workbook workbook, List<AtomicCase> testCases, GlobalSettings settings) {
        ClientEnum clientEnum = ClientEnum.HttpClient;

        Iterator<Row> rowIter = sheet.iterator();
        if (!rowIter.hasNext()) {
            return clientEnum;
        }
        List<String> headers = ExcelUtils.getHeadersFrom(rowIter.next());
        int id = headers.indexOf("用例编号");
        int caseNameId = headers.indexOf("用例名称");
        if (-1 == id || -1 == caseNameId) {
            return clientEnum;
        }
        int ownerId = headers.indexOf("Owner");
        int priorityId = headers.indexOf("用例级别");
        int groupId = headers.indexOf("用例标签");

        if (!rowIter.hasNext()) {
            return clientEnum;
        }
        List<Integer> pos = new ArrayList<Integer>();
        List<String> paramKey = getParameters(rowIter.next(), pos);
        int size = paramKey.size();

        if (size > 0 && paramKey.get(0).equals(Constant.SETUP_CLASS))
            clientEnum = ClientEnum.HttpClient;
        else
            clientEnum = ClientEnum.RichClient;

        if (!rowIter.hasNext()) {
            return clientEnum;
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
            // get owner
            Object owner = "Unknown";
            if (-1 != ownerId) {
                owner = ExcelUtils.getCellValue(row.getCell(ownerId));
                if (null == owner || "".equals(owner.toString().trim())) {
                    owner = "Unknown";
                }
            }
            // get priority
            Object priority = "_None";
            if (-1 != priorityId) {
                priority = ExcelUtils.getCellValue(row.getCell(priorityId));
                if (null == priority || "".equals(priority.toString().trim())) {
                    priority = "_None";
                }
            }
            // get groups
            Set<String> groups = new HashSet<String>();
            if (-1 != groupId) {
                groups = PluginUtils.stringToSet(ExcelUtils.getCellValue(row.getCell(groupId)).toString());
            }
            // do add
            testCases.add(new AtomicCase(caseId, caseName, paramKey.toArray(new String[0]),
                    paramValue.toArray(new Object[0]), owner.toString().trim(),
                    priority.toString().trim(), groups));
        }
        return clientEnum;
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
