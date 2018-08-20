package com.gome.test.gui.helper;

import com.gome.test.utils.Logger;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;

public class HttpClientUtils {

    static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(600000)
            .setConnectTimeout(600000)
            .setSocketTimeout(600000).build();

    public static boolean checkSuccess(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpRequestBase requestBase = new HttpGet(new URI(url));
            requestBase.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(requestBase);
            String status = BasePage.compositeConfiguration.getString("link.status");
            if (status == null || status.isEmpty())
                status = "200";

            for (String s : status.split(","))
                if (s.equals(String.valueOf(response.getStatusLine().getStatusCode())))
                    return true;
        } catch (Exception ex) {
            Logger.error("checkSuccess : %s", ex.getMessage());
        } finally {
            try {
                if (httpclient != null)
                    httpclient.close();
            } catch (Exception e) {
            }
        }

        return false;
    }
}
