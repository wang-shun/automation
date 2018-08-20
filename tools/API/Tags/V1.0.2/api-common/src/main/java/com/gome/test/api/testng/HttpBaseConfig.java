package com.gome.test.api.testng;

import com.gome.test.api.client.HttpMessageClient;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

@Listeners({ContextListener.class})
public class HttpBaseConfig extends BaseConfig {

    public HttpBaseConfig(){
    	super();
        initMessageClient();
    }

    public HttpBaseConfig(String configFile,InputStream propFile, List<URL> urls) throws UnsupportedEncodingException {
    	super(configFile,propFile,urls);
        initMessageClient();
    }

    private void initMessageClient() {
        messageClient = new HttpMessageClient();
    }
    
    @Override
    @BeforeSuite(alwaysRun = true)
    public void setUpBeforeSuite() throws Exception {
        super.setUpBeforeSuite();
        messageClient = new HttpMessageClient();
        messageClient.setUpBeforeSuite();
    }

}
