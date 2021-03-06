package com.gome.test.api.client;

import com.gome.test.api.model.*;
import com.gome.test.api.utils.ApiUtils;
import com.gome.test.api.utils.HttpClient;
import com.gome.test.utils.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class HttpMessageClient extends IMessageClient {

    protected HttpClient httpClient;
    protected Integer statusCode;

    public HttpMessageClient() {
        httpClient = new HttpClient();
    }

    @Override
    public String assemble() throws Exception {
        String httpUrl = ApiUtils.loadValueWithContext(kvs.get(Constant.HTTP_URL));
        String httpMethod = ApiUtils.loadValueWithContext(kvs.get(Constant.HTTP_METHOD));
        Headers headers = Headers.loadFrom(ApiUtils.loadValueWithContext(kvs.get(Constant.HEADERS)));
        UrlParams urlParams = UrlParams.loadFrom(ApiUtils.loadValueWithContext(kvs.get(Constant.URL_PARAMS)));
        Entities entities = Entities.loadFrom(ApiUtils.loadValueWithContext(kvs.get(Constant.ENTITIES)));
        String request = httpClient.assemble(httpUrl, httpMethod, headers,
                urlParams, entities);
        return request;
    }

    @Override
    public String send() throws Exception {
        response = httpClient.send();
        statusCode = httpClient.getStatusCode();
        return response;
    }

    @Override
    public void close() throws Exception {
        httpClient.close();
    }

    @Override
    public void verify() throws Exception {
        String verifyClassName = ApiUtils.loadValueWithContext(kvs.get(Constant.VERIFY_CLASS));
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
                String methodName = ApiUtils.loadValueWithContext(step.getKeyword());
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("execute %s(", methodName));
                List<String> arguments = step.getArgs();
                Object[] args = new String[arguments.size()];
                for (int j = 0; j < args.length; ++j) {
                    if (0 != j) {
                        sb.append(", ");
                    }
                    args[j] = ApiUtils.loadValueWithContext(arguments.get(j));
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
