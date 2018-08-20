package com.ofo.test.api.environment;

import com.ofo.test.Constant;
import com.ofo.test.api.annotation.SetUp;
import com.ofo.test.api.annotation.TearDown;
import com.ofo.test.api.client.IMessageClient;
import com.ofo.test.api.verify.DBVerifyHelper;
import com.ofo.test.context.ContextUtils;
import com.ofo.test.utils.Logger;
import com.jayway.jsonpath.JsonPath;
import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvManager {

    protected IMessageClient messageClient;

    public void setMessageClient(IMessageClient messageClient) {
        this.messageClient = messageClient;
    }

    //    public void setMessageClient() {
//        this.messageClient = new HttpMessageClient();
//    }
//
    @SetUp(description = "添加/更新名为[name]的上下文环境，使得值为[value]")
    public void set(String name, String value) {
        ContextUtils.getContext().put(name, value);
    }

    @SetUp(description = "添加/更新名为[name]的上下文环境，使得值为[value]")
    public void addContext(String name, String value) {
        ContextUtils.getContext().put(name, value);
    }

    @SetUp(description = "添加/更新名为[name]的上下文环境，使得值为执行[sqlId]对应的SQL语句返回值")
    public void addContextFromSQL(String name, String sqlId) throws Exception {
        List<String[]> results = DBVerifyHelper.querySQL(sqlId);
        addContext(name, results.get(0)[0]);
    }

    @SetUp(description = "将[sqlId]对应的SQL语句返回值放到上下文环境中")
    public void addContextsFromSQL(String sqlId) throws Exception {
        Map<Integer, String> meta = new HashMap<Integer, String>();
        List<String[]> results = DBVerifyHelper.querySQL(sqlId, meta);
        for (Integer k : meta.keySet()) {
            String name = meta.get(k);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < results.size(); ++i) {
                if (0 != i) {
                    sb.append(",");
                }
                sb.append(results.get(i)[k]);
            }
            addContext(name, sb.toString());
        }
    }

    @SetUp(description = "执行[sqlId]对应的SQL语句")
    public void executeSQL(String sqlId) throws Exception {
        DBVerifyHelper.updateDB(sqlId);
    }

    @SetUp(description = "移除名为[name]的上下文环境")
    public void removeContext(String name) {
        ContextUtils.getContext().remove(name);
    }

    @SetUp(description = "清空上下文环境")
    public void clearContext() {
        ContextUtils.getContext().clear();
    }

    @TearDown(description = "将json返回值中[jsonPath]对应的字段，在后续测试步骤中可以以[name]引用")
    public void dataTransfer(String jsonPath, String name) {
        String response = messageClient.getResponse();
        if (response.startsWith("{") == false && response.startsWith("[") == false) {
            response = response.replaceAll("^[\t]", "").trim().replaceAll("^[a-z_]*[(]", "").trim().replaceAll("[)]$", "").trim();
            Logger.info("格式化为:" + response);
        }

        String value = JsonPath.read(response, jsonPath).toString();
        ContextUtils.addToContext(name, value);
    }

    @TearDown(description = "将Response Headers中的所有Set-Cookie存入上下文，后续测试步骤中可以以[setCookies]引用")
    public void cookiesTransfer() throws Exception{
        StringBuilder headerContent = new StringBuilder();
        List<org.apache.http.Header> resultHeaders = getResponseCookie();
        if (resultHeaders.size() > 0) {
            for (org.apache.http.Header header : resultHeaders) {
                headerContent.append(header.getValue().split(";")[0]).append(";");
            }
            if (headerContent.length() > 0) {
                headerContent.setLength(headerContent.length() - 1);
            }
            String cookieContent = headerContent.toString();
            if (ContextUtils.getContext().containsKey(Constant.SET_COOKIE)) {
                String oldCookieContent = ContextUtils.getFromContext(Constant.SET_COOKIE);
                cookieContent = oldCookieContent + ";" + cookieContent;
            }
            ContextUtils.addToContext(Constant.SET_COOKIE, cookieContent);
            Logger.info("TearDownCookieContent:" + cookieContent);
        }
    }

    @TearDown(description = "将Response Header中的某个Set-Cookie存入上下文，后续测试步骤中可以以[setCookies_key]")
    public void cookieTransfer(String key) throws Exception {
        StringBuilder headerContent = new StringBuilder();
        List<org.apache.http.Header> resultHeaders = getResponseCookie();
        if (resultHeaders.size() > 0) {
            //
            for (org.apache.http.Header header : resultHeaders) {
                String headerValue = header.getValue().split(";")[0].trim();
                if (headerValue.split("=")[0].trim().equals(key)) {
                    if (headerContent.length() == 0) {
                        headerContent.append(header.getValue().split(";")[0]);
                    } else {
                        headerContent.setLength(0);
                        headerContent.append(header.getValue().split(";")[0]);
                    }
                }
            }
            String cookieContent = headerContent.toString();
            ContextUtils.addToContext(Constant.SET_COOKIE + "_" + key, cookieContent);
            Logger.info("TearDownCookieContent:" + cookieContent);
        }
    }

    @SetUp(description = "MD5加密，[name]的上下文环境，使得值为[value]，加密秘钥为[key]")
    public void addMD5Context(String name,String value,String key)
    {
        ContextUtils.addMD5Context(name,value,key);
    }

    @SetUp(description = "AES加密，[name]的上下文环境，使得值为[value]，加密秘钥为[key]")
    public void addAESContext(String name,String value,String key)
    {
        ContextUtils.addAESContext(name,value,key);
    }


    @SetUp(description = "AES加密后进行BASE64转码，[name]的上下文环境，使得值为[value]，加密秘钥为[key]")
    public void addAESAndBASE64Context(String name,String value,String key)
    {
        ContextUtils.addAESAndBASE64Context(name,value,key);
    }

    @SetUp(description = "MD5加密，[name]的上下文环境，使得值为[key]+[sortedMapKeyValue]+[key],加密密钥为[key]")
    public void addMD5SortedMapContext(String name, String value, String key) {
        ContextUtils.addMD5SortedMapContext(name,value,key);
    }

    private List<org.apache.http.Header> getResponseCookie () {
        List<org.apache.http.Header> resultHeaders = new ArrayList<Header>();
        try {
            List<Header> headers = messageClient.getAllHeaders();
            if (headers != null) {
                for (org.apache.http.Header header : headers) {
                    if (header.getName().equals("Set-Cookie")) {
                        resultHeaders.add(header);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.error("获取响应头中的Cookie失败：" + ex.getMessage());
        }
        return resultHeaders;
    }
}
