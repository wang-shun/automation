/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gome.test.api.ide.dao;

import com.gome.test.api.ide.model.CaseVariable;
import com.gome.test.api.ide.model.CaseVariables;
import com.gome.test.api.ide.model.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author shan.tan
 *         所有的p-suites-开头的sheet的处理方式
 */
public class ParamizedSheet implements ExcelSheetDataSource {

    public List<TestCase> parse(Sheet dataSheet, Workbook workbook, List<TestCase> testCases, GlobalSettingsExcel settings) {

        String dataSheetName = dataSheet.getSheetName();
        // generate template case and get template params
        String templateName = "p-suites-tpl-" + dataSheetName.substring("p-suites-".length());
        Sheet templateSheet = workbook.getSheet(templateName);
        TemplateSheet templateSheetParser = new TemplateSheet();
        HashMap<String, String> params_tpl = templateSheetParser.parse(templateSheet, settings);

        // iterator 
        Iterator<Row> rowIter = dataSheet.iterator();
        if (!rowIter.hasNext()) {
            return testCases;
        }
        Row row = rowIter.next();
        List<String> headers = ExcelUtils.toHeaders(row);
        int id = headers.indexOf("用例编号");
        int caseNameId = headers.indexOf("用例名称");
        if (-1 == id || -1 == caseNameId) {
            return testCases;
        }
        // get owner
        int ownerId = headers.indexOf("Owner");
        Object owner = "Unknown";
        if (-1 != ownerId) {
            owner = ExcelUtils.getCellValue(row.getCell(ownerId));
            if (null == owner || "".equals(owner.toString().trim())) {
                owner = "Unknown";
            }
        }
        // get all variables and its index
        List<Integer> pos = new ArrayList<Integer>();
        List<String> varaiableKey = getParameters(row, pos);
        int caseParameterIndex = pos.get(varaiableKey.indexOf("用例参数"));
        String[] removeHeadersArray = {"用例编号", "用例名称", "Owner", "用例参数"};
        List<String> removeHeadersList = Arrays.asList(removeHeadersArray);
        for (String s : removeHeadersList) {
            varaiableKey.remove(s);
        }
        pos = pos.subList(removeHeadersList.size(), pos.size());

        //title takes three rows
        rowIter.next();
        rowIter.next();
        // Traversal each row
        while (rowIter.hasNext()) {
            row = rowIter.next();
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

            // get variables key-value pair in
            CaseVariable casevariable;
            CaseVariables caseVariables = new CaseVariables();
            params_tpl.remove("caseVariables");

            for (int k = 0; k < varaiableKey.size(); k++) {
                Object value = ExcelUtils.getCellValue(row.getCell(pos.get(k)));
                casevariable = new CaseVariable();
                casevariable.setKey(varaiableKey.get(k));
                if (value == null) {
                    casevariable.setValue(null);
                } else {
                    casevariable.setValue(value.toString());
                }
                caseVariables.add(casevariable);
            }
            // set case params from template and p-case
            HashMap<String, String> params = new HashMap<String, String>();
            for (String s : params_tpl.keySet()) {
                params.put(s, params_tpl.get(s));
            }
            //get verifySteps and verifyClass
            Object valueVerifyClass = ExcelUtils.getCellValue(row.getCell(caseParameterIndex));
            if (valueVerifyClass == null) {
                params.put("verifyClass", null);
            } else {
                params.put("verifyClass", valueVerifyClass.toString());
            }
            Object valueVerifySteps = ExcelUtils.getCellValue(row.getCell(caseParameterIndex + 1));
            if (valueVerifySteps == null) {
                params.put("verifySteps", null);
            } else {
                params.put("verifySteps", valueVerifySteps.toString());
            }
            //replace all variables
            Iterator<String> interator = params.keySet().iterator();
            String key = null;
            while (interator.hasNext()) {
                key = interator.next();
                for (int k = 0; k < varaiableKey.size(); ++k) {
                    Object value = ExcelUtils.getCellValue(row.getCell(pos.get(k)));
                    if (params.get(key) != null) {
                        params.put(key, setVaraible(params.get(key), varaiableKey.get(k), value));
                    }
                }
            }
            params.put("caseVariables", caseVariables.toJsonString());
            testCases.add(TestCase.loadFrom(caseId, caseName, params, owner.toString()));
        }
        return testCases;
    }

    private List<String> getParameters(Row row, List<Integer> pos) {
        List<String> params = new LinkedList<String>();
        Iterator<Cell> cellIter = row.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String param = ExcelUtils.getValueOfCell(cell).toString().trim();
            if (!"".equals(param)) {
                params.add(param);
                pos.add(cell.getColumnIndex());
            }
        }
        return params;
    }

    private String setVaraible(Object parameter, String variablePattern, Object variableValue) {
        String s = (String) parameter;
        Pattern p = Pattern.compile("\\$\\{" + getActualVaraible(variablePattern) + "\\}");
        Matcher matcher = p.matcher(s);
        while (matcher.find()) {
            String target = matcher.group(0);
            if (variableValue != null) {
                s = s.replace(target, variableValue.toString());
            } else {
                s = s.replace(target, "");
            }
        }
        return s;
    }

    private String getActualVaraible(String variablePattern) {
        int index1 = variablePattern.indexOf("{");
        int index2 = variablePattern.indexOf("}");
        variablePattern = variablePattern.substring(index1 + 1, index2);
        return variablePattern;
    }
} 
