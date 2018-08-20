package com.gome.test.api.utils;

import com.gome.test.api.testng.BaseConfig;
import com.gome.test.api.testng.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

public class DBUtils {

    private static final String properties = "useUnicode=true;characterEncoding=utf-8;"
            + "zeroDateTimeBehaior=convertToNull;transformedBitIsBoolean=true;autoReconnect=true;"
            + "failOverReadOnly=false;maxReconnects=10;initialTimeout=2;"
            + "connectTimeout=0;socketTimeout=0";

    public static List<String[]> querySQL(String sqlId) throws Exception {
        return querySQL(sqlId, null);
    }

    public static List<String[]> querySQL(String sqlId,
                                          Map<Integer, String> meta) throws Exception {
        String sqlXPath = String.format("//sql[@id='%s']", sqlId);
        String driverClassNameXPath = String.format("%s/../../driver_class",
                sqlXPath);
        String urlXPath = String.format("%s/../../url", sqlXPath);
        String userNameXPath = String.format("%s/../../username", sqlXPath);
        String passwordXPath = String.format("%s/../../password", sqlXPath);
        String driverClassName = BaseConfig.getString(driverClassNameXPath);
        String url = BaseConfig.getString(urlXPath);
        String username = BaseConfig.getString(userNameXPath);
        String password = BaseConfig.getString(passwordXPath);
        String sql = BaseConfig.getString(sqlXPath);
        List<String[]> results = DBUtils.querySQL(driverClassName, url,
                username, password, sql, meta);
        return results;
    }

    public static List<String[]> querySQL(String driverClassName, String url,
                                          String username, String password, String sql) throws Exception {
        return querySQL(driverClassName, url, username, password, sql, null);
    }

    public static List<String[]> querySQL(String driverClassName, String url,
                                          String username, String password, String sql,
                                          Map<Integer, String> meta) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setConnectionProperties(properties);
        return querySQL(dataSource, sql, meta);
    }

    public static List<String[]> querySQL(BasicDataSource dataSource, String sql,
                                          Map<Integer, String> meta) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        List<String[]> results = new ArrayList<String[]>();

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            Formatter f = new BasicFormatterImpl();
            try {
                Logger.info(String.format("执行SQL语句: %s", f.format(sql)));
            } catch (Exception ex) {
                Logger.info(String.format("执行SQL语句：%s", sql));
            }
            rset = stmt.executeQuery(sql);
            int numcols = rset.getMetaData().getColumnCount();
            if (null != meta) {
                ResultSetMetaData rsmd = rset.getMetaData();
                for (int i = 1; i <= numcols; ++i) {
                    meta.put(i - 1, rsmd.getColumnLabel(i));
                }
            }
            while (rset.next()) {
                String[] result = new String[numcols];
                for (int i = 1; i <= numcols; ++i) {
                    result[i - 1] = rset.getString(i);
                }
                results.add(result);
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (null != rset) {
                    rset.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != conn) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataSource.close();
        }
        return results;
    }

    public static void updateDB(String sqlId) throws Exception {
        String sqlXPath = String.format("//sql[@id='%s']", sqlId);
        String driverClassNameXPath = String.format("%s/../../driver_class",
                sqlXPath);
        String urlXPath = String.format("%s/../../url", sqlXPath);
        String userNameXPath = String.format("%s/../../username", sqlXPath);
        String passwordXPath = String.format("%s/../../password", sqlXPath);
        String driverClassName = BaseConfig.getString(driverClassNameXPath);
        String url = BaseConfig.getString(urlXPath);
        String username = BaseConfig.getString(userNameXPath);
        String password = BaseConfig.getString(passwordXPath);
        String sql = BaseConfig.getString(sqlXPath);
        updateDB(driverClassName, url, username, password, sql);
    }

    public static void updateDB(String driverClassName, String url,
                                String username, String password, String sql) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        updateDB(dataSource, sql);
    }

    public static void updateDB(BasicDataSource dataSource, String sql) throws Exception {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            Formatter f = new BasicFormatterImpl();
            Logger.info(String.format("execute sql: %s", f.format(sql)));
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != conn) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataSource.close();
        }
    }
}
