package com.ofo.test.api.client;

import com.ofo.test.Constant;
import com.ofo.test.api.model.Step;
import com.ofo.test.api.model.Steps;
import com.ofo.test.api.model.UrlParam;
import com.ofo.test.api.model.UrlParams;
import com.ofo.test.api.utils.DubboClient;
import com.ofo.test.context.ContextUtils;
import com.ofo.test.utils.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by lizonglin on 2016/3/8/0008.
 */
public class TelnetMessageClient extends IMessageClient {
    protected DubboClient dubboClient;
    protected String ip;
    protected int port;

    public TelnetMessageClient() {
        dubboClient = new DubboClient();
    }

//    public TelnetMessageClient(Map<String, String> kvs) throws Exception {
//        this.kvs = kvs;
//        setUp();
//        socket2Address(ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_ADDRESS)));
//        dubboClient = new DubboClient(ip, port);
//    }
//
//    public TelnetMessageClient(String ip, int port) throws Exception {
//        try {
//            dubboClient = new DubboClient(ip, port);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
    @Override
    public String assemble() throws Exception {
        dubboClient = new DubboClient();
        if ((Boolean.parseBoolean(String.valueOf(ContextUtils.getContext().get(Constant.SHOW_DETAIL_LOG))))) {
            Logger.info("==================================详细日志==================================");
            Logger.info("Dubbo 地址：" + ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_ADDRESS)));
            Logger.info("Dubbo 服务：" + ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_SERVICE)));
            Logger.info("Dubbo 方法：" + ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_METHOD)));
            Logger.info("Dubbo 参数：" + ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_PARAM)));
            Logger.info("============================================================================");

        }
        String socket = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_ADDRESS));
        socket2Address(socket);
        String service = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_SERVICE));
        String method = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_METHOD));
        String dubboParams = loadDubboParam(UrlParams.loadFrom(ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_PARAM))));
        String command = String.format("invoke %s.%s%s",service, method, dubboParams);
        dubboClient.assemble(ip, port, command);
        return command;
    }

    @Override
    public String send() throws Exception {
        StringBuilder invokeSuccess = new StringBuilder("false");
        String rawResponse = dubboClient.send();
        response = pureResponse(rawResponse, invokeSuccess).trim();
        if (invokeSuccess.toString().equals("false")) {
            throw new Exception("Dubbo调用失败：" + pureResponse(rawResponse, invokeSuccess));
        }
        return response;
    }

    @Override
    public List<Header> getAllHeaders() throws Exception {
        return null;
    }

    @Override
    public void verify() throws Exception {
        String verifyClassName = ContextUtils.loadValueWithContext(kvs.get(Constant.VERIFY_CLASS));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!StringUtils.isBlank(verifyClassName)) {
            Class<?> verifyClass = cl.loadClass(verifyClassName);
            Constructor<?> verifyConstructor = verifyClass.getConstructor();
            Object verifyObject = verifyConstructor.newInstance();
            Method method = verifyObject.getClass().getMethod(Constant.SET_INPUT_PARAMS,
                    Map.class);
            method.invoke(verifyObject, kvs);
            method = verifyObject.getClass().getMethod(Constant.SET_RESPONSE,
                    String.class);
            method.invoke(verifyObject, response);
            Steps steps = Steps.loadFrom(kvs.get(Constant.VERIFY_STEPS));
            execute(verifyObject, steps);
        }
    }

    @Override
    public void close() throws Exception {
        dubboClient.disconnect();
    }

    @Override
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

    public List<String> getServiceList(Map<String, String> kvs) throws Exception {
        this.kvs = kvs;
        setUp();
        String socket = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_ADDRESS));
        Logger.info("Dubbo Address:" + socket);
        String cmd = "ls";
        return getResponseList(socket, cmd);
    }

    public List<String> getMethodList(Map<String, String> kvs) throws Exception {
        this.kvs = kvs;
        setUp();
        String socket = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_ADDRESS));
        String service = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_SERVICE));
        String cmd = String.format("%s %s","ls", service);
        return getResponseList(socket, cmd);
    }

    public String loadDubboParam(UrlParams dubboParams) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (UrlParam dubboParam : dubboParams) {
            String value = dubboParam.getEncryptionValue().trim();
            if (!value.equals("")) {
                sb.append(value);
                sb.append(",");
            }
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() -1);
        }
        sb.append(")");
        return sb.toString();
    }

//    public String invokeDubbo(Map<String, String> kvs) throws Exception {
//        this.kvs = kvs;
//        setUp();
//        String socket = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_ADDRESS));
//        String service = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_SERVICE));
//        String method = ContextUtils.loadValueWithContext(kvs.get(Constant.DUBBO_METHOD));
//
//    }


    private List<String> getResponseList(String socket, String cmd) throws Exception {
        return response2List(getResponse(socket, cmd));
    }

    private List<String> response2List(String response) {
        String[] r2Array = response.split("\n");
        List<String> responseList = new ArrayList<String>(Arrays.asList(r2Array));
        for (int i = responseList.size() - 1; i >= 0; i--) {
            if (responseList.get(i).trim().equals("dubbo>")) {
                responseList.remove(i);
            }
        }
        return responseList;
    }

    private String getResponse(String socket, String cmd) throws Exception {
        socket2Address(socket);
        dubboClient = new DubboClient();
        dubboClient.assemble(ip, port, cmd);
        String response = dubboClient.send();
        dubboClient.disconnect();

        return response;
    }

    private void socket2Address(String socket) throws Exception {
        String[] adds = socket.split(":");
        if (adds.length == 2) {
            ip = adds[0];
            port = Integer.valueOf(adds[1]);
        } else {
            throw new Exception("提供的Dubbo 地址有误：" + socket);
        }
    }

    private String pureResponse(String rawResponse, StringBuilder invokeSuccess) {
        String[] r2Array = rawResponse.split("\n");
        List<String> responseList = new ArrayList<String>(Arrays.asList(r2Array));
        for (int i = responseList.size() - 1; i >= 0; i--) {
            if (responseList.get(i).trim().startsWith("elapsed:") && responseList.get(i).trim().endsWith("s.")) {
                invokeSuccess.setLength(0);
                invokeSuccess.append("true");
            }
            if (responseList.get(i).trim().equals("dubbo>") ||
                    (responseList.get(i).trim().startsWith("elapsed:") && responseList.get(i).trim().endsWith("s.")) ||
                    responseList.get(i).trim().equals("")) {
                responseList.remove(i);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String responseLine : responseList) {
            sb.append(responseLine);
        }
        return sb.toString();
    }


    @Test
    public void testTelnet() throws Exception {
        socket2Address("10.126.56.89:20881");
        System.out.println(ip + " " + port);
    }

    @Test
    public void testListRemove() {
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).trim().equals("5")) {
                list.remove(i);
            }
        }

        System.out.println(list);
    }

    @Test
    public void testLoadDubboParam() throws Exception {
        System.out.println(getResponse("10.126.53.233:20885", "ls"));
    }
}
