package com.gome.test.mq.dao;

import com.gome.test.mq.Constant.Constant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhangjiadi on 15/12/22.
 */
@Repository
public class AppConfiguration {

    Properties properties = null;

    @PostConstruct
    public void readConfig() throws IOException {
        properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(Constant.APP_PROP_FILE));
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }
}
