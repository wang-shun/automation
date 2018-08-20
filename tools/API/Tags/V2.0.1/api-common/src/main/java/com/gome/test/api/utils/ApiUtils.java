package com.gome.test.api.utils;

import com.gome.test.api.model.Constant;
import com.gome.test.api.parameter.IDataBinder;
import com.gome.test.api.testng.IApiTestContext;
import com.gome.test.utils.ConfigurationUtils;
import com.gome.test.utils.Logger;
import org.apache.commons.configuration.CompositeConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

public class ApiUtils {

    public static Map<String, String> toMap(String[] keys, String[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException(
                    "Input keys' length not equals to values' length");
        }

        Map<String, String> kvs = new HashMap<String, String>();
        for (int i = 0; i < keys.length; ++i) {
            kvs.put(keys[i], values[i]);
        }
        return kvs;
    }

    public static String getXpathNode(String xpath) {
        String value = IApiTestContext.compositeConfiguration.getString(xpath);
        if (value != null)
            return loadValueWithContext(value);

        return value;
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        String value = defaultValue;

        if (key != null && containsKey(key)) {
            try {
                value = IApiTestContext.compositeConfiguration.getString(key);
            } catch (Exception e) {
            }
        }
        return value;
    }

    public static CompositeConfiguration getConfiguration() {
        return IApiTestContext.compositeConfiguration;
    }

    public static String getString(Object key) throws Exception {
        if(null != key)
            return getString(key.toString());

        return null;
    }

    public static boolean containsKey(String key) {
        if (key == null)
            return false;

        Iterator<String> keys = IApiTestContext.compositeConfiguration.getKeys();
        while (keys.hasNext()) {
            if (key.equals(keys.next()))
                return true;
        }

        return false;
    }

    public static String getFromContext(String key) {
        return IApiTestContext.context.get(key);
    }

    public static void addToContext(String key, String value) {
        IApiTestContext.context.put(key, value);
    }

    public static Map<String, String> getContext() {
        return IApiTestContext.context;
    }

    public static String loadValueWithContext(String v) {
        if (null == v) {
            return null;
        }

        //用户上下文
        for (String key : IApiTestContext.context.keySet()) {
            String param = String.format("\\$\\{%s\\}", key);
            String value = IApiTestContext.context.get(key);
            v = v.replaceAll(param, Matcher.quoteReplacement(value));
        }

        //配置上下文
        for (Iterator<String> it = IApiTestContext.compositeConfiguration.getKeys(); it.hasNext(); ) {
            String key = it.next();
            String param = String.format("\\$\\{%s\\}", key);
            v = v.replaceAll(param, Matcher.quoteReplacement(IApiTestContext.compositeConfiguration.getString(key)));
        }

        for (IDataBinder dataBinder : IApiTestContext.dataBinders) {
            v = dataBinder.bind(v);
        }
        return v;
    }

    public static Map<String, Object> getTestContextAll() {
        return IApiTestContext.testContext;
    }

    public static ITestContext getTestContext() {
        return (ITestContext) IApiTestContext.testContext.get(Constant.TEST_CONTEXT);
    }

    public static ITestResult getTestResult() {
        return (ITestResult) IApiTestContext.testContext.get(Constant.TEST_RESULT_CONTEXT);
    }

    public static String getTestName() {
        return ((ITestResult) IApiTestContext.testContext.get(Constant.TEST_RESULT_CONTEXT)).getName();
    }

    public static String getClassName() {
        return ((ITestResult) IApiTestContext.testContext.get(Constant.TEST_RESULT_CONTEXT)).getInstanceName();
    }

    public static void LoadConfig() throws Exception {
        LoadConfig(Constant.API_PROP, Constant.API_XML, ClasspathHelper.forJavaClassPath());
    }

    public static void LoadConfig(String configFile, String propFile, Collection<URL> urls) throws Exception {
        loadDataBinders(urls);
        Logger.info("load %s", configFile);
        Logger.info("load %s", propFile);
        IApiTestContext.compositeConfiguration.clear();
        ConfigurationUtils.readConfig(IApiTestContext.compositeConfiguration, configFile, propFile);
    }

    private static void loadDataBinders(Collection<URL> urls) throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(urls));
        Set<Class<? extends IDataBinder>> subTypes = reflections.getSubTypesOf(IDataBinder.class);
        IApiTestContext.dataBinders.clear();
        for (Class<? extends IDataBinder> subType : subTypes) {
            IApiTestContext.dataBinders.add(subType.newInstance());
        }
    }

}