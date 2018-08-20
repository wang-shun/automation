package com.ofo.test.utils;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.util.List;
import java.util.Map;

public class DBCommonUtils {

    public static final String XML_CONFIG = "/api.xml";
    private volatile static CombinedConfiguration combinedConfiguration = null;

    static {
        try {

            combinedConfiguration = ConfigurationUtils.readCombinedConfiguration(XML_CONFIG);
        } catch (ConfigurationException e) {
            Logger.error("初始化配置失败：" + e.getMessage());
        }
    }

    public static void initConfiguration(String configFile) throws ConfigurationException {
        combinedConfiguration = ConfigurationUtils.readCombinedConfiguration(configFile);
    }

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
        String driverClassName = combinedConfiguration.getString(driverClassNameXPath);
        String url = combinedConfiguration.getString(urlXPath);
        String username = combinedConfiguration.getString(userNameXPath);
        String password = combinedConfiguration.getString(passwordXPath);
        String sql = combinedConfiguration.getString(sqlXPath);
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
        String driverClassName = combinedConfiguration.getString(driverClassNameXPath);
        String url = combinedConfiguration.getString(urlXPath);
        String username = combinedConfiguration.getString(userNameXPath);
        String password = combinedConfiguration.getString(passwordXPath);
        String sql = combinedConfiguration.getString(sqlXPath);
        DBUtils.updateDB(driverClassName, url, username, password, sql);
    }
}
