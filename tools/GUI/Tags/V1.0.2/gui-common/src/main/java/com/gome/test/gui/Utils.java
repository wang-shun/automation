package com.gome.test.gui;

import com.gome.test.utils.Logger;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class Utils {

    static boolean checkClassName(String str) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9-]+$");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    static String getSourceTemplateString(Class<?> c, String template) throws IOException {
        BufferedReader reader = null;
        String line = null;

        StringBuilder sb = new StringBuilder();
        try {
//            Logger.info(template);
            reader = new BufferedReader(new InputStreamReader(c.getResourceAsStream(template)));
            while (null != (line = reader.readLine())) {
                sb.append(line);
                sb.append(Constant.LINE_SEPARATOR);
            }
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return sb.toString();
    }

    static void saveSourceFile(File sourceFile, String string, boolean override) throws IOException {
        if (sourceFile.getParentFile().exists() == false)
            sourceFile.getParentFile().mkdirs();

        if (sourceFile.exists()) {
            if (override == false)
                return;
            else
                sourceFile.delete();
        }

        FileUtils.writeStringToFile(sourceFile, string,Constant.UTF_8);
        Logger.info("%s 保存完毕", sourceFile.getAbsolutePath());
    }

    /**
     * 获取Main文件夹路径
     *
     * @param xmlFile
     * @return
     */
    static String getMainFolderPath(File xmlFile) {
        String folderPath = xmlFile.getParentFile().getAbsolutePath();
        return folderPath.substring(0, folderPath.lastIndexOf(Constant.MAIN) + Constant.MAIN.length() + 1);
    }

    /**
     * 获取resource后的文件夹路径 不包含文件名
     *
     * @param xmlFile
     * @return
     */
    public static String getRelativePath(File xmlFile) {
        String folderPath = xmlFile.getParentFile().getAbsolutePath();
        return folderPath.substring(folderPath.lastIndexOf(Constant.RESOURCES) + Constant.RESOURCES.length() + 1);
    }

    /**
     * 获取resource前的文件夹路径 包含resources
     *
     * @param folderPath
     * @return
     */
    public static String getResourcesPath(String folderPath) {
        return folderPath.substring(0, folderPath.lastIndexOf(Constant.RESOURCES) + Constant.RESOURCES.length());
    }

    public static String getPageBasePackageName(File xmlFile) {
        String packageFromFolder = getRelativePath(xmlFile).toLowerCase().replace(Constant.FILE_SEPARATOR, Constant.DOT);
        String fileName = xmlFile.getName();
        String packageFromFile = fileName.indexOf(Constant.DOT) == fileName.lastIndexOf(Constant.DOT) ? "" : fileName.substring(fileName.indexOf(Constant.DOT), fileName.lastIndexOf(Constant.DOT));
        return String.format("%s%s", packageFromFolder, packageFromFile);
    }

    public static String getDomainPackageName(File xlsxFile) {
        return String.format("%s%s%s",
                getPageBasePackageName(xlsxFile).replaceFirst(String.format("%s%s", Constant.PAGE, Constant.DOT), String.format("%s%s", Constant.DOMAIN, Constant.DOT)),
                Constant.DOT,
                xlsxFile.getName().substring(0, xlsxFile.getName().indexOf(Constant.DOT)).toLowerCase()
        );
    }

    public static String getClassName(File file) {
        String filePath = file.getAbsolutePath();
        String className = filePath.substring(filePath.lastIndexOf(Constant.FILE_SEPARATOR) + 1, filePath.lastIndexOf(Constant.DOT));

        if (className.contains(Constant.DOT))
            className = Utils.formatClassName(className.substring(className.lastIndexOf(Constant.DOT) + 1));

        return formatClassName(className);
    }

    public static String formatClassName(String className) {
        StringBuffer stringBuffer = new StringBuffer(className.substring(0, 1).toUpperCase());
        stringBuffer.append(className.substring(1).toLowerCase());
        return stringBuffer.toString();
    }

    public static String formatFieldName(String fieldName) {
        StringBuffer stringBuffer = new StringBuffer(fieldName.substring(0, 1).toLowerCase());
        stringBuffer.append(fieldName.substring(1).toLowerCase());
        return stringBuffer.toString();
    }
}
