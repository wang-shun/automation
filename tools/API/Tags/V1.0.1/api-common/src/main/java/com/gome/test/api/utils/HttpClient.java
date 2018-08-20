package com.gome.test.api.utils;

import com.gome.test.api.model.Headers;
import com.gome.test.api.model.NameValuePair;
import com.gome.test.api.model.UrlParam;
import com.gome.test.api.model.UrlParams;
import com.gome.test.api.model.Entities;
import com.gome.test.api.model.Header;
import com.gome.test.api.model.NameValuePairs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

public class HttpClient {

    public HttpClient() {
        httpclient = HttpClients.createDefault();
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(600000)
                .setConnectTimeout(600000)
                .setSocketTimeout(600000).build();
    }

    public String assemble(String httpUrl, String httpMethod,
                           Headers headers, UrlParams urlParams, Entities entities)
            throws Exception {
        // add urlParams to httpUrl
        URI uri = getURI(httpUrl, urlParams);
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
            for (int i = 0; i < headers.size(); ++i) {
                Header header = headers.get(i);
                String key = header.getKey();
                String value = header.getEncryptionValue();
                requestBase.setHeader(key, value);
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
            ObjectMapper objMapper = new ObjectMapper();
            try {
                NameValuePairs nameValuePairs = objMapper.readValue(
                        entities.getData(), NameValuePairs.class);
                List<BasicNameValuePair> basicNameValuePairs
                        = new ArrayList<BasicNameValuePair>();
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
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        }
    }

    private final CloseableHttpClient httpclient;
    private final RequestConfig requestConfig;
    private HttpRequestBase requestBase;
    private CloseableHttpResponse response;
}
