package com.gome.test.api.utils;

import com.gome.test.api.model.Constant;

import com.gome.test.api.model.EncodeParam;
import com.gome.test.api.model.EncodeTypeEnum;
import com.gome.test.api.parameter.IDataBinder;
import com.gome.test.api.testng.IApiTestContext;
import com.gome.test.utils.*;
import com.gome.test.utils.Logger;
import javassist.bytecode.stackmap.BasicBlock;
import org.apache.commons.configuration.CompositeConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.ITestContext;
import org.testng.ITestResult;

import java.net.URL;
import java.util.*;
import java.util.logging.*;
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
        if (null != key)
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
        return IApiTestContext.context.get(key).toString();
    }

    public static void addToContext(String key, String value) {
        IApiTestContext.context.put(key, value);
        Logger.info(String.format("addToContext %s=%s", key, value));
    }

    public static void addAESContext(String key, String value, String encodeKey) {

        if (!IApiTestContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.AES);
            IApiTestContext.context.put(key, param);
        }
    }

    public static void addAESAndBASE64Context(String key, String value, String encodeKey) {

        if (!IApiTestContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.AESAndBASE64);
            IApiTestContext.context.put(key, param);
        }
    }
    public static void addMD5Context(String key, String value, String encodeKey) {
        if (!IApiTestContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.MD5);
            IApiTestContext.context.put(key, param);
        }
    }

    public static void addBASE64Context(String key, String value, String encodeKey) {
        if (!IApiTestContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.BASE64);
            IApiTestContext.context.put(key, param);
        }
    }

    public static Map<String, Object> getContext() {
        return IApiTestContext.context;
    }

    public static String loadValueWithContext(String v) {
        if (null == v) {
            return null;
        }

        //用户上下文
        for (String key : IApiTestContext.context.keySet()) {
            String param = String.format("\\$\\{%s\\}", key);
            Object o = IApiTestContext.context.get(key);
            String value = null;
            if (o instanceof String) {
                value = o.toString();
            } else {
                try {
                    //加密相关内容并加入到上下文中
                    EncodeParam encodeParam = (EncodeParam) o;
                    if (encodeParam.getEncodeType() == EncodeTypeEnum.AESAndBASE64) {
                        value =Base64Utils.encodeMessage(AESUtils.encryptMessageForBASE64(encodeParam.getValue(), encodeParam.getKey()))  ;
                    } else if (encodeParam.getEncodeType() == EncodeTypeEnum.MD5) {
                        value = MD5Utils.encodeMessage(encodeParam.getValue(), encodeParam.getKey());
                    } else if (encodeParam.getEncodeType() == EncodeTypeEnum.BASE64) {
                        value = Base64Utils.encodeMessage(encodeParam.getValue().getBytes("UTF-8"));
                    }else if(encodeParam.getEncodeType() == EncodeTypeEnum.AES) {
                        value =new String(AESUtils.encryptAESOnly(encodeParam.getValue(), encodeParam.getKey()));
                    }
                } catch (Exception ex) {
                    value = null;
                }
            }
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