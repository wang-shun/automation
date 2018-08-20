package com.ofo.test.gui.helper;

import com.gome.test.utils.DBUtils;

import java.util.List;
import java.util.Map;

public class DBHelper {

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
        String driverClassName = GuiUtils.getXpathNode(driverClassNameXPath);
        String url = GuiUtils.getXpathNode(urlXPath);
        String username = GuiUtils.getXpathNode(userNameXPath);
        String password = GuiUtils.getXpathNode(passwordXPath);
        String sql = GuiUtils.getXpathNode(sqlXPath);
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
        String driverClassName = GuiUtils.getXpathNode(driverClassNameXPath);
        String url = GuiUtils.getXpathNode(urlXPath);
        String username = GuiUtils.getXpathNode(userNameXPath);
        String password = GuiUtils.getXpathNode(passwordXPath);
        String sql = GuiUtils.getXpathNode(sqlXPath);
        DBUtils.updateDB(driverClassName, url, username, password, sql);
    }
}
