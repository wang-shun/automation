package com.gome.test.api.testng;

import com.gome.test.api.client.IMessageClient;
import com.gome.test.api.parameter.IDataBinder;
import com.gome.test.api.utils.Props;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.tree.MergeCombiner;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.annotations.*;

public class BaseConfig {

	protected static Map<String, String> context;
	protected static Set<IDataBinder> dataBinders;
	protected static CombinedConfiguration config;
	protected static IMessageClient messageClient;
	public static final String API_XML = "/api.xml";
	public static final String API_PROP = "/api.properties";
	protected String configFile;
	protected InputStream propFile;
	protected Collection<URL> urls;

	public BaseConfig() {
		URL url = BaseConfig.class.getResource(API_XML);
		if (null != url) {
			configFile = url.getFile();
			Logger.info("BaseConfig configFile:" + configFile);
		} else {
			configFile = null;
		}

		this.propFile = Object.class.getResourceAsStream(API_PROP);
		this.urls = ClasspathHelper.forJavaClassPath();
	}

	public BaseConfig(String configFile,InputStream propFile, List<URL> urls) {
		this.configFile = configFile;
		this.urls = urls; 
		this.propFile = propFile;
	}
 
	private void readApiProp() {
		if(propFile != null)
		{
			Props props = new Props();
			try
			{
				Logger.info("load " + API_PROP);
			    props.loadFrom(this.propFile);
				//InputStream stream = new FileInputStream(BaseConfig.class.getResource(API_PROP).getFile());
				//props.loadFrom(stream);
				for (String key : props.toMap().keySet()) {
					if (context.containsKey(key))
						Logger.error(MessageFormat.format("上下文已经存在 {0}", key));
					else
						context.put(key, props.get(key));
				}
				Logger.info("load " + context);
			}
			catch(Exception ex)
			{
				Logger.info("load " + API_PROP + " fail");
			}
		}
		else
		{
			Logger.info("can not find " + API_PROP);
		}
	}
	
	@BeforeSuite(alwaysRun = true)
	public void setUpBeforeSuite() throws Exception {
		  // init dataBinders
		Logger.info("execute setUpBeforeSuite");
		context = new HashMap<String, String>();
        // init dataBinders
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().setUrls(urls));
        Set<Class<? extends IDataBinder>> subTypes
                = reflections.getSubTypesOf(IDataBinder.class);
        dataBinders = new HashSet<IDataBinder>();
        for (Class<? extends IDataBinder> subType : subTypes) {
            dataBinders.add(subType.newInstance());
        }
    
        config = new CombinedConfiguration();
        if (null != configFile) {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            builder.setFile(new File(configFile));
            config = builder.getConfiguration(true);
            NodeCombiner combiner = new MergeCombiner();
            config.setNodeCombiner(combiner);
            config.setExpressionEngine(new XPathExpressionEngine());
        }
        readApiProp();
	}

	@BeforeTest(alwaysRun = true)
	public void setUpBeforeTest() throws Exception {
		messageClient.setUpBeforeTest();
	}

	@BeforeClass(alwaysRun = true)
	public void setUpBeforeClass() throws Exception {
		messageClient.setUpBeforeClass();
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpBeforeMethod(ITestContext context) throws Exception {
		if (context != null) {
			ISuite suite = context.getSuite();
			if (suite != null) {
				Logger.info("BeforeMethod clean begin");
				// 判定为非Ordercase
				if (suite.getParameter("skipOnceFail") == null) {
					if (this.context != null)
					{
						this.context.clear();
						readApiProp();
					}
					if (this.dataBinders != null)
						this.dataBinders.clear();
 
					Logger.info("BeforeMethod clean end");
				}
			}
		}
		messageClient.setUpBeforeMethod();
	}

	@AfterMethod(alwaysRun = true)
	public void tearDownAfterMethod() throws Exception {
		messageClient.tearDownAfterMethod();
	}

	@AfterClass(alwaysRun = true)
	public void tearDownAfterClass() throws Exception {
		messageClient.tearDownAfterClass();
	}

	@AfterTest(alwaysRun = true)
	public void tearDownAfterTest() throws Exception {
		messageClient.tearDownAfterTest();
	}

	@AfterSuite(alwaysRun = true)
	public void tearDownAfterSuite() throws Exception {
		context.clear();
		messageClient.tearDownAfterSuite();
	}

	public static IMessageClient getMessageClient() {
		return messageClient;
	}

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

	public static String getString(String key) throws Exception {
		return loadValueWithContext(config.getString(key));
	}

	public static String getFromContext(String key) {
		return context.get(key);
	}

	public static void addToContext(String key, String value) {
		context.put(key, value);
	}

	public static Map<String, String> getContext() {
		return context;
	}

	public static String loadValueWithContext(String v) throws Exception {
		if (null == v) {
			return null;
		}
		for (String key : context.keySet()) {
			String param = String.format("\\$\\{%s\\}", key);
			String value = context.get(key);
			v = v.replaceAll(param, Matcher.quoteReplacement(value));
		}
		for (IDataBinder dataBinder : dataBinders) {
			v = dataBinder.bind(v);
		}
		return v;
	}
}
