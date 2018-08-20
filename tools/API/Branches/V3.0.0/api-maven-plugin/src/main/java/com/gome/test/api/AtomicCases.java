
package com.gome.test.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import com.gome.test.Constant;
import com.gome.test.api.model.ClientEnum;
import com.gome.test.plugin.GlobalSettings;
import com.gome.test.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AtomicCases {

    private List<AtomicCase> testCases;

    private ClientEnum clientType = ClientEnum.HttpClient;

    public ClientEnum getClientType() {
        return clientType;
    }

    public void setClientType(ClientEnum clientType) {
        this.clientType = clientType;
    }

    public AtomicCases(String testCaseExcelPath, GlobalSettings settings) throws IOException {
        this(new File(testCaseExcelPath), settings);
    }

    public AtomicCases(File testCaseExcelFile, GlobalSettings settings) throws IOException {
        this(new FileInputStream(testCaseExcelFile), settings);
    }

    public AtomicCases(InputStream is, GlobalSettings settings) throws IOException {
        testCases = new ArrayList<AtomicCase>();
        try {
            parse(is, settings);
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    public List<AtomicCase> getTestCases() {
        return testCases;
    }

    private void parse(InputStream is, GlobalSettings settings) throws IOException {
        Workbook workbook = new XSSFWorkbook(is);
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; ++i) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            // skip non-testsuites sheet
            if (!sheetName.startsWith(Constant.P_SUITES_)) {
                if ((!sheetName.startsWith(Constant.TEST_SUITES))) {
                    continue;
                }
            }
            // process sheet depending on different sheet name
            if (sheetName.startsWith(Constant.TEST_SUITES) && !(sheetName.startsWith(Constant.TEST_SUITES_FOR_DEV))) {
                ExcelSheetDataSource sheetSource = new OriginalSheet();
                this.clientType = sheetSource.parse(sheet, workbook, testCases, settings);
            }
            if (sheetName.startsWith(Constant.P_SUITES_)) {
                if (!(sheetName.startsWith(Constant.P_SUITES_TPL_))) {
                    String[] nameArray = sheetName.split("-");
                    if (workbook.getSheet(Constant.P_SUITES_TPL_ + nameArray[2]) == null) {
                        continue;
                    }
                    ExcelSheetDataSource sheetSource = new ParamizedSheet();
                    this.clientType = sheetSource.parse(sheet, workbook, testCases, settings);
                }
            }
        }
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
