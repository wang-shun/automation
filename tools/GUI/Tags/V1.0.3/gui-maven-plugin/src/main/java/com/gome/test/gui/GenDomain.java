package com.gome.test.gui;


import com.gome.test.utils.ExcelUtils;
import com.gome.test.utils.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

public class GenDomain extends GenBase {

    @Override
    protected void genByFile(File resourceFile) throws Exception {
        if (resourceFile.exists() == false)
            throw new Exception(String.format("%s 不存在", resourceFile.getAbsolutePath()));

        if (resourceFile.getName().endsWith(Constant.DOT_XLSX) == false
                || resourceFile.getName().equals("Globle.xlsx")
                || resourceFile.getName().equals("OrderCase.xlsx")
                || resourceFile.getAbsolutePath().contains(String.format("%scase%s", Constant.FILE_SEPARATOR, Constant.FILE_SEPARATOR)))
            return;

        Logger.info(String.format("%s 开始处理", resourceFile.getAbsolutePath()));

        FileInputStream file = new FileInputStream(resourceFile);
        try {
            Workbook workbook = new XSSFWorkbook(file);

            String packageName = Utils.getDomainPackageName(resourceFile);
            String maiFolderPath = Utils.getMainFolderPath(resourceFile);

            int sheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNum; ++i) {
                genClassSource(workbook.getSheetAt(i), packageName, maiFolderPath);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (file != null)
                file.close();
        }
    }

    private void genClassSource(Sheet sheet, String packageName, String maiFolderPath) throws Exception {
        String className = sheet.getSheetName();
        if (false == Utils.checkClassName(className))
            throw new Exception(String.format("%s 不是合法的类名!", className));

        String pageBase = tpls.get(Constant.RESOURCE_DOMAIN)
                .replace("@package@", packageName)
                .replace("@class@", className)
                .replace("@field@", genField(sheet));

        File sourceFile = new File(maiFolderPath,
                String.format("%s%s%s%s%s%s%s",
                        Constant.JAVA, Constant.FILE_SEPARATOR,
                        packageName.replace(Constant.DOT, Constant.FILE_SEPARATOR), Constant.FILE_SEPARATOR, className, Constant.DOT, Constant.JAVA));

        Utils.saveSourceFile(sourceFile, pageBase, true);
    }

    private String genField(Sheet sheet) {
        StringBuffer stringBuffer = new StringBuffer();

        Iterator<Row> rowIter = sheet.iterator();
        if (rowIter.hasNext()) {
            List<String> headers = ExcelUtils.getHeadersFrom(rowIter.next());

            for (String header : headers) {
                if (header.equals("name") == false) {
                    stringBuffer.append(tpls.get(Constant.RESOURCE_FIELD)
                            .replace("@fieldName@", Utils.formatFieldName(header))
                            .replace("@fieldNameUpper@", Utils.formatClassName(header)));
                }
            }
        }

        return stringBuffer.toString();
    }

    public static void main(String[] strings) throws Exception {
        new GenDomain().genByFolder(new File("/Users/zhangliang/SourceCode/sample/Trunk/GUITest/Helper/src/main/resources/"));
    }
}
