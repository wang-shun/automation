package com.gome.test.mock.db.base;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Properties;

@Repository
public class AppConfiguration {

    Properties properties = null;

    @PostConstruct
    public void readConfig() throws IOException {
        properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/application.properties"));
    }

    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }
}
