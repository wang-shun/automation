package com.gome.test.api.verify;

import com.gome.test.api.utils.ApiUtils;
import com.gome.test.utils.DBUtils;
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
        String driverClassName = ApiUtils.getXpathNode(driverClassNameXPath);
        String url = ApiUtils.getXpathNode(urlXPath);
        String username = ApiUtils.getXpathNode(userNameXPath);
        String password = ApiUtils.getXpathNode(passwordXPath);
        String sql = ApiUtils.getXpathNode(sqlXPath);
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
        String driverClassName = ApiUtils.getXpathNode(driverClassNameXPath);
        String url = ApiUtils.getXpathNode(urlXPath);
        String username = ApiUtils.getXpathNode(userNameXPath);
        String password = ApiUtils.getXpathNode(passwordXPath);
        String sql = ApiUtils.getXpathNode(sqlXPath);
        DBUtils.updateDB(driverClassName, url, username, password, sql);
    }

}
