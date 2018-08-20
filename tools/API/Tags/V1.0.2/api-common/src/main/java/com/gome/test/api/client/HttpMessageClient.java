package com.gome.test.api.client;

import com.gome.test.api.testng.BaseConfig;
import com.gome.test.api.utils.HttpClient;
import com.gome.test.api.model.Entities;
import com.gome.test.api.model.Headers;
import com.gome.test.api.model.Step;
import com.gome.test.api.model.Steps;
import com.gome.test.api.model.UrlParams;
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
        String httpUrl = BaseConfig.loadValueWithContext(kvs.get("httpUrl"));
        String httpMethod = BaseConfig.loadValueWithContext(kvs.get("httpMethod"));
        Headers headers = Headers.loadFrom(BaseConfig.loadValueWithContext(kvs.get("headers")));
        UrlParams urlParams = UrlParams.loadFrom(BaseConfig.loadValueWithContext(kvs.get("urlParams")));
        Entities entities = Entities.loadFrom(BaseConfig.loadValueWithContext(kvs.get("entities")));
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
        String verifyClassName = BaseConfig.loadValueWithContext(kvs.get("verifyClass"));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!StringUtils.isBlank(verifyClassName)) {
            Class<?> verifyClass = cl.loadClass(verifyClassName);
            Constructor<?> verifyConstructor = verifyClass.getConstructor();
            Object verifyObject = verifyConstructor.newInstance();
            Method method = verifyObject.getClass().getMethod("setHttpClient",
                    HttpClient.class);
            method.invoke(verifyObject, httpClient);
            method = verifyObject.getClass().getMethod("setInputParams",
                    Map.class);
            method.invoke(verifyObject, kvs);
            method = verifyObject.getClass().getMethod("setResponse",
                    String.class);
            method.invoke(verifyObject, response);
            method = verifyObject.getClass().getMethod("setStatusCode",
                    Integer.class);
            method.invoke(verifyObject, statusCode);
            Steps steps = Steps.loadFrom(kvs.get("verifySteps"));
            execute(verifyObject, steps);
        }
    }

    @Override
    public void setUp() throws Exception {
        String setUpClassName = BaseConfig.loadValueWithContext(kvs.get("setUpClass"));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!StringUtils.isBlank(setUpClassName)) {
            Class<?> setUpClass = cl.loadClass(setUpClassName);
            Constructor<?> setUpConstructor = setUpClass.getConstructor();
            Object setUpObject = setUpConstructor.newInstance();
            Method method = setUpObject.getClass().getMethod("setMessageClient",
                    IMessageClient.class);
            method.invoke(setUpObject, this);
            Steps steps = Steps.loadFrom(kvs.get("setUpSteps"));
            execute(setUpObject, steps);
        }
    }

    @Override
    public void tearDown() throws Exception {
        String tearDownClassName = BaseConfig.loadValueWithContext(kvs.get("tearDownClass"));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!StringUtils.isBlank(tearDownClassName)) {
            Class<?> tearDownClass = cl.loadClass(tearDownClassName);
            Constructor<?> tearDownConstructor = tearDownClass.getConstructor();
            Object tearDownObject = tearDownConstructor.newInstance();
            Method method = tearDownObject.getClass().getMethod("setMessageClient",
                    IMessageClient.class);
            method.invoke(tearDownObject, this);
            Steps steps = Steps.loadFrom(kvs.get("tearDownSteps"));
            execute(tearDownObject, steps);
        }
    }

    private void execute(Object object, Steps steps) throws Exception {
        if (null != steps) {
            for (int i = steps.size() - 1; i >= 0; --i) {
                Step step = steps.get(i);
                String methodName = BaseConfig.loadValueWithContext(step.getKeyword());
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("execute %s(", methodName));
                List<String> arguments = step.getArgs();
                Object[] args = new String[arguments.size()];
                for (int j = 0; j < args.length; ++j) {
                    if (0 != j) {
                        sb.append(", ");
                    }
                    args[j] = BaseConfig.loadValueWithContext(arguments.get(j));
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
