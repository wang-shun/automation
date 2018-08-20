package com.gome.util;


import java.util.Properties;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Repository;


import javax.annotation.PostConstruct;


/**
 * Created by liangwei on 2016/10/29.
 */

@Repository
public class AppConfiguration {

    Properties properties =null;

    public AppConfiguration(){
        try {
            readconfig();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @PostConstruct
    public void readconfig() throws IOException{
        properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(Content.PROPERTIES_PATH));
    }

    public String getConfig(String key){
        return this.properties.getProperty(key);
    }

}
