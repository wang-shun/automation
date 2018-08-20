package com.ofo.test.api.client;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * Created by liangwei-ds on 2017/2/8.
 */
public class TestHttpClient  {

    CloseableHttpClient httpClient = null;


    private final CloseableHttpClient httpclient;
    private final RequestConfig requestConfig;
    private HttpRequestBase requestBase;
    private CloseableHttpResponse response;

    public TestHttpClient() {
//        HttpHost proxy = new HttpHost("127.0.0.1", 8888);

        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

        cm.setMaxTotal(400);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(40);

        //httpclient = HttpClients.createDefault();
        httpclient = HttpClients.custom().setConnectionManager(cm).setRetryHandler(httpRequestRetryHandler).build();
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(60000)
//                .setProxy(proxy)
                .setConnectTimeout(60000)
                .setSocketTimeout(60000).build();
    }

    public String sendGet(String request){
        CloseableHttpResponse ss = null;
        String response = null;
        HttpGet httpGet = new HttpGet(request);
        try {
            ss = httpclient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            response = EntityUtils.toString(ss.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void main(String args[]){
        TestHttpClient testHttpClient = new TestHttpClient();
        String request = "http://search.ofo.com.cn/search?question=skuId%3Apop8009538174";
        String s = "http://blog.csdn.net/wangpeng047/article/details/19624529";
        testHttpClient.sendGet(request);
        System.out.println("End");

    }
}
