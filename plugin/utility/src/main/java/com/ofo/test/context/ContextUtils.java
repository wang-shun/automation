package com.ofo.test.context;


import com.ofo.test.Constant;
import com.ofo.test.api.model.EncodeParam;
import com.ofo.test.api.model.EncodeTypeEnum;
import com.ofo.test.api.parameter.IDataBinder;
import com.ofo.test.utils.*;
import org.apache.commons.configuration.CompositeConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.ITestResult;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

public class ContextUtils {

    private static String testCase_isTimeOut="TestCase_IsTimeout";

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
        String value = IContext.compositeConfiguration.getString(xpath);
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
                value = IContext.compositeConfiguration.getString(key);
            } catch (Exception e) {
            }
        }
        return value;
    }

    public static CompositeConfiguration getConfiguration() {
        return IContext.compositeConfiguration;
    }

    public static String getString(Object key) throws Exception {
        if (null != key)
            return getString(key.toString());

        return null;
    }

    public static boolean containsKey(String key) {
        if (key == null)
            return false;

        Iterator<String> keys = IContext.compositeConfiguration.getKeys();
        while (keys.hasNext()) {
            if (key.equals(keys.next()))
                return true;
        }

        return false;
    }

    public static String getFromContext(String key) {
        return IContext.context.get(key).toString();
    }

    public static void addToContext(String key, String value) {
        IContext.context.put(key, value);
        Logger.info(String.format("addToContext %s=%s", key, value));
    }

    public static void addAESContext(String key, String value, String encodeKey) {

        if (!IContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.AES);
            IContext.context.put(key, param);
        }
    }

    public static void addAESAndBASE64Context(String key, String value, String encodeKey) {

        if (!IContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.AESAndBASE64);
            IContext.context.put(key, param);
        }
    }

    public static void addMD5Context(String key, String value, String encodeKey) {
        if (!IContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.MD5);
            IContext.context.put(key, param);
        }
    }

    public static void addMD5PopContext(String key, String value) {
        if (!IContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(null, value, EncodeTypeEnum.POPMD5);
            IContext.context.put(key, param);
        }
    }

    public static void addBASE64Context(String key, String value, String encodeKey) {
        if (!IContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.BASE64);
            IContext.context.put(key, param);
        }
    }

    public static void addMD5SortedMapContext(String key, String value, String encodeKey) {
        if (!IContext.context.containsKey(key)) {
            EncodeParam param = new EncodeParam(encodeKey, value, EncodeTypeEnum.SORTEDMAPMD5);
            IContext.context.put(key, param);
        }
    }

    public static Map<String, Object> getContext() {
        return IContext.context;
    }

    public static String loadValueWithContext(String v) {
        if (null == v) {
            return null;
        }

        //用户上下文
        for (String key : IContext.context.keySet()) {
            String param = String.format("\\$\\{%s\\}", key);
            Object o = IContext.context.get(key);
            String value = null;
            if (o instanceof String || o instanceof Boolean) {
                value = o.toString();
            } else {
                try {

                    //加密相关内容并加入到上下文中
                    EncodeParam encodeParam = (EncodeParam) o;
                    if (encodeParam.getEncodeType() == EncodeTypeEnum.AESAndBASE64) {
                        value = Base64Utils.encodeMessage(AESUtils.encryptMessageForBASE64(encodeParam.getValue(), encodeParam.getKey()));
                    } else if (encodeParam.getEncodeType() == EncodeTypeEnum.MD5) {
                        value = MD5Utils.encodeMessage(encodeParam.getValue(), encodeParam.getKey());
                    } else if (encodeParam.getEncodeType() == EncodeTypeEnum.BASE64) {
                        value = Base64Utils.encodeMessage(encodeParam.getValue().getBytes("UTF-8"));
                    } else if (encodeParam.getEncodeType() == EncodeTypeEnum.AES) {
                        value = new String(AESUtils.encryptAESOnly(encodeParam.getValue(), encodeParam.getKey()));
                    } else if (encodeParam.getEncodeType() == EncodeTypeEnum.SORTEDMAPMD5) {
                        value = MD5Utils.encodeJsonMapMessage(encodeParam.getValue(), encodeParam.getKey());
                    }

                } catch (Exception ex) {
                    value = null;
                }
            }
            v = v.replaceAll(param, Matcher.quoteReplacement(value));

        }

        //配置上下文
        for (Iterator<String> it = IContext.compositeConfiguration.getKeys(); it.hasNext(); ) {
            String key = it.next();
            String param = String.format("\\$\\{%s\\}", key);
            v = v.replaceAll(param, Matcher.quoteReplacement(IContext.compositeConfiguration.getString(key)));
        }

        for (IDataBinder dataBinder : IContext.dataBinders) {
            v = dataBinder.bind(v);
        }

        return v;
    }

    public static Map<String, Object> getTestContextAll() {
        return IContext.testContext;
    }

    public static IContext getTestContext() {
        return (IContext) IContext.testContext.get(Constant.TEST_CONTEXT);
    }

    public static ITestResult getTestResult() {
        return (ITestResult) IContext.testContext.get(Constant.TEST_RESULT_CONTEXT);
    }

    public static String getTestName() {
        return ((ITestResult) IContext.testContext.get(Constant.TEST_RESULT_CONTEXT)).getName();
    }

    public static String getClassName() {
        return ((ITestResult) IContext.testContext.get(Constant.TEST_RESULT_CONTEXT)).getInstanceName();
    }

    public static void LoadConfig() throws Exception {
        LoadConfig(Constant.API_PROP, Constant.API_XML, ClasspathHelper.forJavaClassPath());
    }

    public static void LoadConfig(String configFile, String propFile, Collection<URL> urls) throws Exception {
        loadDataBinders(urls);
        Logger.info("load %s", configFile);
        Logger.info("load %s", propFile);
        IContext.compositeConfiguration.clear();
        ConfigurationUtils.readConfig(IContext.compositeConfiguration, configFile, propFile);
    }

    private static void loadDataBinders(Collection<URL> urls) throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(urls));
        Set<Class<? extends IDataBinder>> subTypes = reflections.getSubTypesOf(IDataBinder.class);
        IContext.dataBinders.clear();
        for (Class<? extends IDataBinder> subType : subTypes) {
            IContext.dataBinders.add(subType.newInstance());
        }
    }

    public static void BeginTestCase()
    {
        IContext.testContext.put(testCase_isTimeOut,"1");
    }

    public static void EndTestCase()
    {
        IContext.testContext.put(testCase_isTimeOut,"0");
    }

    public static Boolean getTestCaseIsTimeOut()
    {
        try{

            if(IContext.testContext.containsKey(testCase_isTimeOut))
                return IContext.testContext.get(testCase_isTimeOut).toString().equals("1");

        }catch (Exception ex)
        {
            return false;
        }

        return false;
    }

}