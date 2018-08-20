package com.ofo.test.utils;

import com.ofo.test.Constant;
import com.ofo.test.gui.model.Step;
import com.ofo.test.gui.model.Case;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class TestCaseUtils {
    public static void loadCaseByFile (File file, List<Case> cases, Set<String> caseIds, Set<String> caseNames) throws Exception {
        if (file.exists() == false)
            throw new Exception(String.format("%s 不存在", file.getAbsolutePath()));

        if (file.getName().endsWith(Constant.DOT_XLSX)) {
            Logger.info("%s 开始处理", file.getAbsolutePath());

            FileInputStream in = new FileInputStream(file);
            try {
                Workbook workbook = new XSSFWorkbook(in);

                int sheetNum = workbook.getNumberOfSheets();
                for (int i = 0; i < sheetNum; ++i) {
                    getGuiCase(file, workbook.getSheetAt(i), cases, caseIds, caseNames);
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                if (in != null)
                    in.close();
            }

            Logger.info("%s 处理完毕", file.getAbsolutePath());
        }
    }

    private static void getGuiCase(File file, Sheet sheet, List<Case> cases, Set<String> caseIds, Set<String> caseNames) throws Exception {
        Iterator<Row> rowIter = sheet.iterator();
        if (rowIter.hasNext()) {
            if (rowIter.hasNext())
                rowIter.next();

            List<String> headers = ExcelUtils.getHeadersFrom(rowIter.next());
            if (headers.size() == 3)
                throw new Exception(String.format("%s[%s] 没有步骤", file.getAbsolutePath(), sheet.getSheetName()));

//            if ((headers.size() - 3) % 2 != 0)
//                throw new Exception(String.format("%s[%s] 步骤不完整", file.getAbsolutePath(), sheet.getSheetName()));

            if (rowIter.hasNext())
                rowIter.next();

            while (rowIter.hasNext()) {
                Row row = rowIter.next();
                if (ExcelUtils.getValueOfCell(row.getCell(0)).toString().isEmpty())
                    continue;

                Case c = new Case();
                //caseid
                c.setId(ExcelUtils.getValueOfCell(row.getCell(0)).toString());
                if (caseIds.contains(c.getId()))
                    throw new Exception(String.format("%s 中的caseId [%s] 已存在", file.getAbsolutePath(), c.getId()));

                //casename
                c.setName(ExcelUtils.getValueOfCell(row.getCell(1)).toString());
                if (caseNames.contains(c.getId()))
                    throw new Exception(String.format("%s 中的caseNames [%s] 已存在", file.getAbsolutePath(), c.getName()));


                if(headers.contains("caseCategory") && ExcelUtils.getCellValue(row.getCell(3)) != null) {
                    String caseCategory =ExcelUtils.getCellValue(row.getCell(3)).toString();
                    TreeMap<String,String> _category=StringUtils.isEmpty(caseCategory)?new TreeMap<String, String>(): JsonUtils.readValue(caseCategory,TreeMap.class);
                    c.setCaseCategory(_category);
                }


                //owner
                c.setOwner(ExcelUtils.getValueOfCell(row.getCell(2)).toString());

                //step
                List<Step> steps = new ArrayList<Step>();

                for (int i = (headers.contains("caseCategory")?4:3); i < headers.size(); i += 2) {
                    if (row.getCell(i) == null || row.getCell(i + 1) == null)
                        continue;

                    Step step = new Step();
                    step.setKey(ExcelUtils.getValueOfCell(row.getCell(i)).toString());
                    step.setValue(ExcelUtils.getValueOfCell(row.getCell(i + 1)).toString());

                    if (step.getKey().isEmpty() == false)
                        steps.add(step);
                }

                c.setSteps(steps);
                cases.add(c);
                if (caseIds != null)
                    caseIds.add(c.getId());
                if (caseNames != null)
                    caseNames.add(c.getName());
            }
        }
    }
}
