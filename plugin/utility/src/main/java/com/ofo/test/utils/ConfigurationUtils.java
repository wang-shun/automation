package com.ofo.test.utils;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.tree.MergeCombiner;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

import java.io.File;
import java.net.URL;
import java.util.List;

public class ConfigurationUtils {

    /**
     * 支持properties xml
     * @param configFiles 需要读取的各文件，拓展名必须是properties和xml,其他名拓展名的文件会被忽略
    // * @see 1http://liuzidong.iteye.com/blog/776730
     */
    public static CompositeConfiguration readConfig(String... configFiles) throws ConfigurationException {
        CompositeConfiguration config = new CompositeConfiguration();
        readConfig(config,configFiles);
        return config;
    }

    public static void readConfig(CompositeConfiguration config,String... configFiles) throws ConfigurationException {
        for(String filePath : configFiles)
        {
            if(filePath.toLowerCase().endsWith(".properties"))
            {
                URL url = Object.class.getResource(filePath);
                if(null != url)
                    config.addConfiguration(new PropertiesConfiguration(Object.class.getResource(filePath)));
                else
                    config.addConfiguration(new PropertiesConfiguration(filePath));
            }
            else if(filePath.toLowerCase().endsWith(".xml"))
            {
                config.addConfiguration(readCombinedConfiguration(filePath));
            }
        }
    }

    public static CombinedConfiguration readCombinedConfiguration(String configFile) throws ConfigurationException {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        URL url = Object.class.getResource(configFile);
        if(null != url)
            builder.setFile(new File(url.getFile()));
        else
            builder.setFile(new File(configFile));

        CombinedConfiguration configuration = builder.getConfiguration(true);
        NodeCombiner combiner = new MergeCombiner();
        configuration.setNodeCombiner(combiner);
        configuration.setExpressionEngine(new XPathExpressionEngine());
        return  configuration;
    }
}