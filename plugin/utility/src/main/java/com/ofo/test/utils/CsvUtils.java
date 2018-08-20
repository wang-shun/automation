package com.ofo.test.utils;


import org.apache.commons.io.FileUtils;
import org.relique.jdbc.csv.CsvConnection;
import org.relique.jdbc.csv.CsvResultSet;

import java.io.File;
import java.sql.*;
import java.util.List;

public class CsvUtils {

    private static final String DB_URL = "jdbc:relique:csv:%s";
    private static final String CSV_DRIVER = "org.relique.jdbc.csv.CsvDriver";

    static {
        try {
            Class.forName(CSV_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filePath   csv文件全路径
     * @param sqlExpress
     */
    public static CsvResultSet executeQuery(String filePath, String sqlExpress) throws Exception {
        File csvFile = new File(filePath);

        if (csvFile.exists() == false || csvFile.isFile() == false)
            throw new Exception(String.format("%s 不是文件!", filePath));

        if (sqlExpress == null || sqlExpress.isEmpty()) {
            sqlExpress = "1=1";
        }

        String sql = String.format("select * from %s where %s", csvFile.getName().substring(0, csvFile.getName().indexOf(".")), sqlExpress);
        System.out.println(sql);

        CsvConnection conn = null;
        CsvResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = (CsvConnection) DriverManager.getConnection(String.format(DB_URL, csvFile.getParent()));
            preparedStatement = conn.prepareStatement(sql);

            resultSet = (CsvResultSet)preparedStatement.executeQuery();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (conn != null)
                conn.close();
        }

        return resultSet;
    }

    public static void clearAndSave(List<List<String>> content, String filePath) throws Exception {
        File csv = new File(filePath);
        if (csv.exists() == false) {
            throw new Exception(String.format("%s 不存在", filePath));
        }

        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < content.size(); i++) {
            if (stringBuffer.length() > 0)
                stringBuffer.append("\r\n");

            for (int j = 0; j < content.get(i).size(); j++) {
                stringBuffer.append(content.get(i).get(j));
                if (j < content.get(i).size() - 1)
                    stringBuffer.append(",");
            }
        }

        FileUtils.write(csv, stringBuffer.toString(), false);
    }

    public static void main(String[] args) throws Exception{
        executeQuery("/Users/zhangjiadi/Documents/GOME/SourceCode_svn/SourceCode/sample/Trunk/APITest/TestCase/CaseCategory/CaseCategroy.csv","");

    }

}
