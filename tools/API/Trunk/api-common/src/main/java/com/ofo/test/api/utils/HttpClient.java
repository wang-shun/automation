package com.ofo.test.api.utils;

import com.ofo.test.api.model.*;
import com.ofo.test.api.model.Header;
import com.ofo.test.api.model.NameValuePair;
import com.ofo.test.utils.JsonUtils;

import org.apache.commons.httpclient.URIUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpClient {

    private final CloseableHttpClient httpclient;
    private final RequestConfig requestConfig;
    private HttpRequestBase requestBase;
    private CloseableHttpResponse response;

    public HttpClient() {
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

/*    public void  configConnectManage(String url){
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

        cm.setMaxTotal(400);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(40);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), 100);
    }*/


    public String assemble(String httpUrl, String httpMethod,
                           Headers headers, UrlParams urlParams, Entities entities)
            throws Exception {

        // add urlParams to httpUrl
        URI uri = getURI(httpUrl, urlParams);

//        uri = new URI(URIUtil.decode(uri.toString()));
        if (httpMethod.equalsIgnoreCase("GET")) {
            requestBase = new HttpGet(uri);
        } else if (httpMethod.equalsIgnoreCase("POST")) {
            HttpPost httpPost = new HttpPost(uri);
            // set entity
            httpPost.setEntity(getEntity(entities));
            requestBase = httpPost;
        } else if (httpMethod.equals("DELETE")) {
            requestBase = new HttpDelete(uri);
        } else if (httpMethod.equals("PUT")) {
            HttpPut httpPut = new HttpPut(uri);
            // set entity
            httpPut.setEntity(getEntity(entities));
            requestBase = httpPut;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported http method %s!", httpMethod));
        }

        // add header to requestBase
        if (null != headers) {
            StringBuilder cookieContent = new StringBuilder();
            for (int i = 0; i < headers.size(); ++i) {
                Header header = headers.get(i);
                String key = header.getKey();
                String value = header.getEncryptionValue();
                //Cookie单独处理
                if (key.equals("Cookie")) {
                    cookieContent.append(value).append(";");
                } else {
                    requestBase.setHeader(key, value);
                }
            }
            //组装并添加Cookie
            if (cookieContent.length() > 0) {
                cookieContent.setLength(cookieContent.length() - 1);
                requestBase.setHeader("Cookie", cookieContent.toString());
            }
        }
        requestBase.setConfig(requestConfig);
        return requestBase.getRequestLine().toString();
    }

    public String send() throws Exception {
        try {
        	  response = httpclient.execute(requestBase);
        	  return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
           } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    public int getStatusCode() throws Exception {
        return response.getStatusLine().getStatusCode();
    }

    public void close() throws Exception {
        httpclient.close();
    }

    public HttpRequestBase getRequest() {
        return requestBase;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public CloseableHttpClient getHttpClient() {
        return httpclient;
    }

//    public String getCookies() {
//        return getHeaderContentByKey("Set-Cookie");
//    }

    private URI getURI(String httpUrl, UrlParams urlParams) throws Exception {
        String hostPort = httpUrl;
        String schema = "http";
        if (httpUrl.startsWith("http://")) {
            hostPort = httpUrl.substring("http://".length());
        } else if (httpUrl.startsWith("https://")) {
            hostPort = httpUrl.substring("https://".length());
            schema = "https";
        }
        String path = "/";
        int index = hostPort.indexOf("/");
        if (-1 != index) {
            path = hostPort.substring(index);
            hostPort = hostPort.substring(0, index);
        }
        String[] hostPortSplit = hostPort.split(":");
        String host = hostPort;
        int port = 80;
        if (2 == hostPortSplit.length) {
            host = hostPortSplit[0];
            port = Integer.parseInt(hostPortSplit[1]);
        }

        URIBuilder builder = new URIBuilder();
        builder.setCharset(Charset.forName("utf-8"));
        builder.setScheme(schema).setHost(host).setPort(port).setPath(path);
        if (null != urlParams) {
            for (int i = 0; i < urlParams.size(); ++i) {
                UrlParam urlParam = urlParams.get(i);
                String key = urlParam.getKey();
                String value = urlParam.getEncryptionValue();
                if (null != value) {
                    builder.addParameter(key, value);
                }
            }
        }

        URI uri = builder.build();

        return uri;
    }

    private HttpEntity getEntity(Entities entities) {
        if (null == entities) {
            return null;
        }
        if ("x-www-form-urlencoded".equals(entities.getType())) {
            try {
                NameValuePairs nameValuePairs = JsonUtils.readValue(entities.getData(), NameValuePairs.class);
                List<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
                for (int i = 0; i < nameValuePairs.size(); ++i) {
                    NameValuePair nameValuePair = nameValuePairs.get(i);
                    String key = nameValuePair.getKey();
                    String value = nameValuePair.getEncryptionValue();
                    basicNameValuePairs.add(new BasicNameValuePair(key, value));
                }
                return new UrlEncodedFormEntity(basicNameValuePairs, "UTF-8");
            } catch (IOException ex) {
                return null;
            }
        } else {
            try {
                StringEntity entity = new StringEntity(entities.getData(), "UTF-8");
                entity.setContentType("application/json");
                return entity;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * 取到所有的Header
     * @return
     */
    public List<org.apache.http.Header> getAllHeaders() {
        return Arrays.asList(response.getAllHeaders());
    }

    public static void main(String[] args) throws Exception {
        List<org.apache.http.NameValuePair> list =  new ArrayList<org.apache.http.NameValuePair>();
        list.add(new BasicNameValuePair("test", "+55+TO+100测试"));
        String requests = URLEncodedUtils.format(list,"UTF-8");
        System.out.println("编码后的值："+requests);
        System.out.println("使用URL解码："+URLDecoder.decode(requests,"UTF-8"));
        System.out.println("使用URI工具解码："+URIUtil.decode(requests));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpHost target = new HttpHost("ofo.com.cn", 80, "http");
            HttpHost proxy = new HttpHost("127.0.0.1", 8888);

            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
            HttpGet request = new HttpGet(URLEncoder.encode("/${test","utf-8"));
            System.out.println(request.toString());
            System.out.println(URLDecoder.decode(request.toString(),"utf-8"));
            request.setConfig(config);

            System.out.println("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);
//            System.out.println("Executing request " + request.getRequestLine() + " to " + target);

            CloseableHttpResponse response = httpclient.execute(target, request);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
