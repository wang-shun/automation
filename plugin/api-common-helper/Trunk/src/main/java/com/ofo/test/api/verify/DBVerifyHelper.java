package com.ofo.test.api.verify;

import com.ofo.test.context.ContextUtils;
import com.ofo.test.utils.DBUtils;
import java.util.List;
import java.util.Map;


public class DBVerifyHelper {

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
        String driverClassName = ContextUtils.getXpathNode(driverClassNameXPath);
        String url = ContextUtils.getXpathNode(urlXPath);
        String username = ContextUtils.getXpathNode(userNameXPath);
        String password = ContextUtils.getXpathNode(passwordXPath);
        String sql = ContextUtils.getXpathNode(sqlXPath);
        List<String[]> results = DBUtils.querySQL(driverClassName, url,
                username, password, sql, meta);
        return results;
    }


    public static void updateDB(String sqlId) throws Exception {
        String sqlXPath = String.format("//sql[@id='%s']", sqlId);
        String driverClassNameXPath = String.format("%s/../../driver_class",
                sqlXPath);
        String urlXPath = String.format("%s/../../url", sqlXPath);
        String userNameXPath = String.format("%s/../../username", sqlXPath);
        String passwordXPath = String.format("%s/../../password", sqlXPath);
        String driverClassName = ContextUtils.getXpathNode(driverClassNameXPath);
        String url = ContextUtils.getXpathNode(urlXPath);
        String username = ContextUtils.getXpathNode(userNameXPath);
        String password = ContextUtils.getXpathNode(passwordXPath);
        String sql = ContextUtils.getXpathNode(sqlXPath);
        DBUtils.updateDB(driverClassName, url, username, password, sql);
    }

}
