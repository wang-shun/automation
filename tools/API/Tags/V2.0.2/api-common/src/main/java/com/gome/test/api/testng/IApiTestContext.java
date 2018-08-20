package com.gome.test.api.testng;


import com.gome.test.api.parameter.IDataBinder;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;

import java.util.*;

public interface IApiTestContext {
    public static Map<String, String> context = new HashMap<String, String>();
    public static Set<IDataBinder> dataBinders = new HashSet<IDataBinder>();
    public static CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
    public static Map<String, Object> testContext = new HashMap<String, Object>();

}
