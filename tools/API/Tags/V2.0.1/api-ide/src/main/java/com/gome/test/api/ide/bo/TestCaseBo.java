package com.gome.test.api.ide.bo;

import com.gome.test.api.ide.model.TestCase;
import com.gome.test.api.ide.dao.ExcelWorkBook;
import com.gome.test.api.ide.dao.GlobalSettingsExcel;
import com.gome.test.api.ide.dao.TestCases;
import com.gome.test.api.ide.model.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public class TestCaseBo {

    @Value(value = "${tests.path}")
    private String testsPath;

    public String add(String node, TestCase testCase) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        if (sheetName.startsWith("p-suites")) {
            if (!(sheetName.startsWith("p-suites-tpl"))) {
                workbook.padd(sheetName, testCase);
                workbook.dump();
                return String.format("%s#%s#%s", excel, sheetName, testCase.getId());
            }
        }
        workbook.add(sheetName, testCase);
        workbook.dump();
        return String.format("%s#%s#%s", excel, sheetName, testCase.getId());
    }

    public void delete(String node) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        workbook.delete(sheetName, caseId);
        workbook.dump();
    }

    public TestCase query(String node) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        File globalSettingsExcel = new File(String.format("%s/Globle.xlsx",
                testsPath));
        GlobalSettingsExcel settings = new GlobalSettingsExcel();
        if (globalSettingsExcel.exists()) {
            settings.loadFrom(globalSettingsExcel);
        }
        TestCases testcases = new TestCases(excelPath, settings, sheetName);
        List<TestCase> testCaseList = testcases.getTestCases();
        for (int i = 0; i < testCaseList.size(); ++i) {
            TestCase testCase = testCaseList.get(i);
            if (testCase.getId().equals(caseId)) {
                return testCase;
            }
        }
        return null;
    }


    public List<TestCase> pquery(String node) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        File globalSettingsExcel = new File(String.format("%s/Globle.xlsx",
                testsPath));
        GlobalSettingsExcel settings = new GlobalSettingsExcel();
        if (globalSettingsExcel.exists()) {
            settings.loadFrom(globalSettingsExcel);
        }
        TestCases testcases = new TestCases(excelPath, settings, sheetName);
        List<TestCase> testCaseList = testcases.getTestCases();
        return testCaseList;
    }


    public Response queryResponse(String node) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        Response response = new Response(workbook.queryResponse(sheetName, caseId));

        return response;
    }


    public String update(String node, TestCase testCase) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        String newCaseId = testCase.getId();
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        if (newCaseId.equals("")) {
            throw new Exception("用例编号不能为空");
        }
        testCase.setId(caseId);
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        String returnStr = "";
        if (caseId.equals(newCaseId)) {
            returnStr = String.format("%s#%s#%s", excel, sheetName, caseId);
            if (sheetName.startsWith("p-suites-")) {
                if (!(sheetName.startsWith("p-suites-tpl-"))) {
                    workbook.pupdate(sheetName, testCase);
                    workbook.dump();
                    return returnStr;
                }
            }
            workbook.update(sheetName, testCase);

        } else {
            workbook.update(sheetName, testCase, newCaseId);
            returnStr = String.format("%s#%s#%s", excel, sheetName, newCaseId);
        }
        workbook.dump();
        return returnStr;

    }

    public String updateResponse(String node, String responseCopy) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        String returnStr = "";
        workbook.updateResponse(sheetName, caseId, responseCopy);
        returnStr = String.format("%s#%s#%s", excel, sheetName, caseId);
        workbook.dump();
        return returnStr;

    }

    public String updateCaseName(String node, TestCase testCase) throws Exception {
        String excel = node.split("#")[0];
        String sheetName = node.split("#")[1];
        String caseId = node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        testCase.setId(caseId);
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        workbook.updateCaseName(sheetName, testCase);
        workbook.dump();
        return String.format("%s#%s#%s", excel, sheetName, testCase.getId());
    }

    public String move(String moved_node, String target_node, String parent_node, String position) throws IOException {
        String excel = parent_node.split("#")[0];
        String sheetName = parent_node.split("#")[1];
        String sourceCaseId = moved_node.split("#")[2];
        String targetCaseId = target_node.split("#")[2];
        File excelPath = new File(String.format("%s/%s",
                testsPath, excel));
        int[] loc = new int[2];
        System.out.println(sheetName + "  " + sourceCaseId + "   " + targetCaseId);
        ExcelWorkBook workbook = new ExcelWorkBook(excelPath);
        workbook.move(sheetName, sourceCaseId, targetCaseId, position, loc);
        workbook.dump();

        ExcelWorkBook workbook2 = new ExcelWorkBook(excelPath);
        workbook2.insert(sheetName, loc[0], loc[1], position);
        workbook2.dump();


        ExcelWorkBook workbook3 = new ExcelWorkBook(excelPath);
        workbook3.delete(sheetName, loc[0], loc[1], position);
        workbook3.dump();

        return null;

    }

}
