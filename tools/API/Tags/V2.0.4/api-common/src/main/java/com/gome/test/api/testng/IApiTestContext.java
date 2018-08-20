package com.gome.test.api.testng;

import com.gome.test.api.parameter.IDataBinder;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;

import java.util.*;

public interface IApiTestContext {
    /**
     * 用户上下文
     */
    public static Map<String, Object> context = new HashMap<String, Object>();
    /**
     * 数据绑定
     */
    public static Set<IDataBinder> dataBinders = new HashSet<IDataBinder>();
    /**
     * 系统配置，api.xml + api.properties
     */
    public static CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
    /**
     *  testNg上下文
     */
    public static Map<String, Object> testContext = new HashMap<String, Object>();



}
