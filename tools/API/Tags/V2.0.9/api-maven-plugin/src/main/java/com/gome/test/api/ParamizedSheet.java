/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gome.test.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gome.test.api.model.ClientEnum;
import com.gome.test.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author shan.tan
 *         所有的p-testsuites开头的sheet的处理方式
 */
public class ParamizedSheet implements ExcelSheetDataSource {

    public ClientEnum parse(Sheet dataSheet, Workbook workbook, List<AtomicCase> testCases, GlobalSettings settings) {
        ClientEnum clientEnum = ClientEnum.HttpClient;
        String dataSheetName = dataSheet.getSheetName();
        String[] sheetNameArray = dataSheetName.split("-");
        String templateName = "p-suites-tpl-" + sheetNameArray[2];
        Sheet templateSheet = workbook.getSheet(templateName);
        TemplateSheet templateSheetParser = new TemplateSheet();
        // generate template atomic case
        AtomicCase tempalteAtomicCase = templateSheetParser.parse(templateSheet, settings);
        if (tempalteAtomicCase == null) {
            return clientEnum;
        }
        // iterator 
        Iterator<Row> rowIter = dataSheet.iterator();
        if (!rowIter.hasNext()) {
            return clientEnum;
        }
        Row row = rowIter.next();
        List<String> headers = ExcelUtils.getHeadersFrom(row);
        int id = headers.indexOf("用例编号");
        int caseNameId = headers.indexOf("用例名称");
        int ownerId = headers.indexOf("Owner");
        if (-1 == id || -1 == caseNameId) {
            return clientEnum;
        }
        // get all variables and its index
        List<Integer> pos = new ArrayList<Integer>();
        List<String> varaiableKey = getParameters(row, pos);
        int caseParameterIndex = pos.get(varaiableKey.indexOf("用例参数"));    //p-case 
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
            AtomicCase atomicCase = null;
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
            // get template paramKey
            ArrayList<String> paramKeyArray = new ArrayList<String>();
            for (String s : tempalteAtomicCase.getParamKeys()) {
                paramKeyArray.add(s);
            }
            int verifyClassIndex = paramKeyArray.indexOf("verifyClass");      //template case
            int verifyStepsIndex = paramKeyArray.indexOf("verifySteps");      //template case
            int caseVariablesIndex = paramKeyArray.indexOf("caseVariables");  //template case
            paramKeyArray.remove(caseVariablesIndex);
            String[] paramKey = new String[paramKeyArray.size()];
            int i = 0;
            for (String s : paramKeyArray) {
                paramKey[i++] = s;
            }
            // get paramValue
            ArrayList<Object> paramValueList = new ArrayList<Object>();
            for (Object o : tempalteAtomicCase.getParamValues()) {
                paramValueList.add(o);
            }
            paramValueList.remove(caseVariablesIndex);
            //set case verifySteps and verifyClass, in-pcase,not in template
            Object valueVerifyClass = ExcelUtils.getCellValue(row.getCell(caseParameterIndex));
            if (valueVerifyClass == null) {
                paramValueList.set(verifyClassIndex, null);
            } else {
                paramValueList.set(verifyClassIndex, valueVerifyClass.toString());
            }
            Object valueVerifySteps = ExcelUtils.getCellValue(row.getCell(caseParameterIndex + 1));
            if (valueVerifySteps == null) {
                paramValueList.set(verifyStepsIndex, null);
            } else {
                paramValueList.set(verifyStepsIndex, valueVerifySteps.toString());
            }
            // set variables value
            Object[] paramValue = paramValueList.toArray();
            for (int j = 0; j < paramKeyArray.size(); ++j) {
                for (int k = 0; k < varaiableKey.size(); ++k) {
                    Object value = ExcelUtils.getCellValue(row.getCell(pos.get(k)));
                    paramValue[j] = setVaraible(paramValue[j], varaiableKey.get(k), value);
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
            testCases.add(new AtomicCase(caseId, caseName, paramKey,
                    paramValue, owner.toString(),
                    tempalteAtomicCase.getPriority(), tempalteAtomicCase.getGroups()));
        }
        return clientEnum;
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

    private Object setVaraible(Object parameter, String variablePattern, Object variableValue) {
        if (parameter == null) {
            return parameter;
        }
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
