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

import com.gome.test.Constant;
import com.gome.test.plugin.GlobalSettings;
import com.gome.test.plugin.Utils;
import com.gome.test.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author shan.tan
 */
public class TemplateSheet {

    public AtomicCase parse(Sheet sheet, GlobalSettings settings) {
        Iterator<Row> rowIter = sheet.iterator();
        AtomicCase atomicCase = null;
        if (!rowIter.hasNext()) {
            return atomicCase;
        }
        List<String> headers = ExcelUtils.getHeadersFrom(rowIter.next());
        int id = headers.indexOf(Constant.CASE_NO);
        int caseNameId = headers.indexOf(Constant.CASE_NAME);
        if (-1 == id || -1 == caseNameId) {
            return atomicCase;
        }
        int ownerId = headers.indexOf(Constant.OWNER_FIRST_UPPER);
        int priorityId = headers.indexOf(Constant.CASE_LEVEL);
        int groupId = headers.indexOf(Constant.CASE_TAG);

        if (!rowIter.hasNext()) {
            return atomicCase;
        }
        List<Integer> pos = new ArrayList<Integer>();
        List<String> paramKey = getParameters(rowIter.next(), pos);
        int size = paramKey.size();
        if (!rowIter.hasNext()) {
            return atomicCase;
        }
        rowIter.next();
        // Traversal template row only one
        if (rowIter.hasNext()) {
            Row row = rowIter.next();
            // get case id
            Object caseIdObj = ExcelUtils.getCellValue(row.getCell(id));
            if (null == caseIdObj) {
                return atomicCase;
            }
            String caseId = caseIdObj.toString().trim();
            if ("".equals(caseId)) {
                return atomicCase;
            }
            // get case name
            Object caseNameObj = ExcelUtils.getCellValue(row.getCell(caseNameId));
            if (null == caseNameObj) {
                return atomicCase;
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
            Object owner = Constant.UNKNOW;
            if (-1 != ownerId) {
                owner = ExcelUtils.getCellValue(row.getCell(ownerId));
                if (null == owner || "".equals(owner.toString().trim())) {
                    owner = Constant.UNKNOW;
                }
            }
            // get priority
            Object priority = Constant._NONE;
            if (-1 != priorityId) {
                priority = ExcelUtils.getCellValue(row.getCell(priorityId));
                if (null == priority || "".equals(priority.toString().trim())) {
                    priority = Constant._NONE;
                }
            }
            // get groups
            Set<String> groups = new HashSet<String>();
            if (-1 != groupId) {
                groups = Utils.stringToSet(ExcelUtils.getCellValue(row.getCell(groupId)).toString());
            }
            // do add
            atomicCase = new AtomicCase(caseId, caseName, paramKey.toArray(new String[0]),
                    paramValue.toArray(new Object[0]), owner.toString().trim(),
                    priority.toString().trim(), groups);
        }
        return atomicCase;
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
