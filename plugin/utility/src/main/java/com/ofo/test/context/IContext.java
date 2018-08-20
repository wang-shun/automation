package com.ofo.test.context;

import com.ofo.test.api.parameter.IDataBinder;
import org.apache.commons.configuration.CompositeConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface IContext {
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
