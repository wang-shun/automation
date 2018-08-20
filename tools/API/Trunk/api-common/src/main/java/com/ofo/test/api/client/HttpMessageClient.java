package com.ofo.test.api.client;

import com.ofo.test.Constant;
import com.ofo.test.api.model.*;
import com.ofo.test.api.utils.HttpClient;
import com.ofo.test.context.ContextUtils;
import com.ofo.test.utils.Logger;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class HttpMessageClient extends IMessageClient {

    protected HttpClient httpClient;
    protected Integer statusCode;

    public HttpMessageClient() {
        httpClient = new HttpClient();
    }

    @Override
    public String assemble() throws Exception {
//        if (ContextUtils.getContext().containsKey(Constant.IS_ORDER_CASE)) {
//            if (ContextUtils.getFromContext(Constant.IS_ORDER_CASE).equals("false")) {
//                //原子Case重新new 一个 httpclient，初始化HttpClient内容
//                httpClient = new HttpClient();
//            }
//        }
        //无论是否为OrderCase都重新new一个HttpClient
        httpClient = new HttpClient();

        if ((Boolean.parseBoolean(String.valueOf(ContextUtils.getContext().get(Constant.SHOW_DETAIL_LOG))))) {
            Logger.info("==================================详细日志==================================");
            Logger.info("Http 地址：" + ContextUtils.loadValueWithContext(kvs.get(Constant.HTTP_URL)));
            Logger.info("Http 方法：" + ContextUtils.loadValueWithContext(kvs.get(Constant.HTTP_METHOD)));
            Logger.info("Http Headers：" + ContextUtils.loadValueWithContext(kvs.get(Constant.HEADERS)));
            Logger.info("Http 参数：" + ContextUtils.loadValueWithContext(kvs.get(Constant.URL_PARAMS)));
            Logger.info("Http Entities：" + ContextUtils.loadValueWithContext(kvs.get(Constant.ENTITIES)));
            Logger.info("============================================================================");
        }
        Logger.info("Context: " + ContextUtils.getContext().toString());
        String httpUrl = ContextUtils.loadValueWithContext(kvs.get(Constant.HTTP_URL));


        String httpMethod = ContextUtils.loadValueWithContext(kvs.get(Constant.HTTP_METHOD));
        Headers headers = Headers.loadFrom(ContextUtils.loadValueWithContext(kvs.get(Constant.HEADERS)));
        UrlParams urlParams = UrlParams.loadFrom(ContextUtils.loadValueWithContext(kvs.get(Constant.URL_PARAMS)));
        Entities entities = Entities.loadFrom(ContextUtils.loadValueWithContext(kvs.get(Constant.ENTITIES)));
        String request = httpClient.assemble(httpUrl, httpMethod, headers,
                urlParams, entities);
        return request;
    }

    @Override
    public String send() throws Exception {
        response = httpClient.send();
        statusCode = httpClient.getStatusCode();
        responseHeaders = httpClient.getAllHeaders();
        return response;
    }

    @Override
    public List<org.apache.http.Header> getAllHeaders() {
        return responseHeaders;
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }

    @Override
    public void verify() throws Exception {
        String verifyClassName = ContextUtils.loadValueWithContext(kvs.get(Constant.VERIFY_CLASS));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!StringUtils.isBlank(verifyClassName)) {
            Class<?> verifyClass = cl.loadClass(verifyClassName);
            Constructor<?> verifyConstructor = verifyClass.getConstructor();
            Object verifyObject = verifyConstructor.newInstance();
            Method method = verifyObject.getClass().getMethod(Constant.SET_HTTP_CLIENT,
                    HttpClient.class);
            method.invoke(verifyObject, httpClient);
            method = verifyObject.getClass().getMethod(Constant.SET_INPUT_PARAMS,
                    Map.class);
            method.invoke(verifyObject, kvs);
            method = verifyObject.getClass().getMethod(Constant.SET_RESPONSE,
                    String.class);
            method.invoke(verifyObject, response);
            method = verifyObject.getClass().getMethod(Constant.SET_STATUS_CODE,
                    Integer.class);
            method.invoke(verifyObject, statusCode);
            Steps steps = Steps.loadFrom(kvs.get(Constant.VERIFY_STEPS));
            execute(verifyObject, steps);
        }
    }

    protected void execute(Object object, Steps steps) throws Exception {
        if (null != steps) {
            for (int i = steps.size() - 1; i >= 0; --i) {
                Step step = steps.get(i);
                String methodName = ContextUtils.loadValueWithContext(step.getKeyword());
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("execute %s(", methodName));
                List<String> arguments = step.getArgs();
                Object[] args = new String[arguments.size()];
                for (int j = 0; j < args.length; ++j) {
                    if (0 != j) {
                        sb.append(", ");
                    }
                    args[j] = ContextUtils.loadValueWithContext(arguments.get(j));
                    sb.append(args[j]);
                }
                sb.append(")");
                Logger.info(sb.toString());
                Class<?>[] parameterTypes = new Class<?>[args.length];
                for (int j = 0; j < args.length; ++j) {
                    parameterTypes[j] = args[j].getClass();
                }
                Method method = object.getClass().getMethod(methodName, parameterTypes);
                try {
                    Object obj = method.invoke(object, args);
                    if (null != obj && obj instanceof Boolean
                            && Boolean.valueOf(obj.toString()).equals(Boolean.TRUE)) {
                        break;
                    }
                } catch (Exception ex) {
                    Logger.error(String.format("校验失败：%s", String.valueOf(ex.getCause())));
                    throw new Exception(ex.getCause());
                }
            }
        }
    }
}
