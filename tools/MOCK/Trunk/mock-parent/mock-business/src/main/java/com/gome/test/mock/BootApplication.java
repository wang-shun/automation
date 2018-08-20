package com.gome.test.mock;

import com.alibaba.fastjson.JSON;
import com.gome.test.mock.cnst.ConstDefine;
import com.gome.test.mock.cnst.ProtocolConst;
import com.gome.test.mock.common.Command;
import com.gome.test.mock.common.ParseRequest;
import com.gome.test.mock.common.RouteMockApi;
import com.gome.test.mock.common.WorkContext;
import com.gome.test.mock.core.Request;
import com.gome.test.mock.core.Response;
import com.gome.test.mock.core.Service;
import com.gome.test.mock.core.stub.SimpleStub;
import com.gome.test.mock.dao.common.DaoManager;
import com.gome.test.mock.exception.MyException;
import com.gome.test.mock.model.bean.Api;
import com.gome.test.mock.model.bean.RequestResponseLog;
import com.gome.test.mock.model.bean.Template;
import com.gome.test.mock.model.vo.HostVo;
import com.gome.test.mock.model.vo.Step;
import com.gome.test.mock.model.vo.WorkStep;
import com.gome.test.mock.pipes.http.SimpleHttpRequest;
import com.gome.test.mock.pipes.http.SimpleHttpResponse;
import com.gome.test.mock.pipes.http.pipe.impl.SimpleHttpPipe;
import com.gome.test.mock.reflex.Reflection;
import com.gome.test.mock.reflex.ReflexObject;
import com.gome.test.mock.util.SoapUtil;
import com.gome.test.mock.utils.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.*;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
public class BootApplication extends SpringBootServletInitializer {

    private static final Logger logger = new Logger(BootApplication.class);

    private static Map<String, String> hostMap = new HashMap<String, String>();
    private static String tips = "提示：无法找到对应服务，请先注册服务。";

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootApplication.class);
    }

    public static void main(String[] args) {

        /**
         * ==================================预先准备daoBean，监听主机列表，创建管道==================================
         */
        //获取Spring容器
        ApplicationContext ctx = SpringApplication.run(BootApplication.class, args);

        //获取Dao管理者
        final DaoManager daoManager = (DaoManager) ctx.getBean("daoManager");

        //查询主机列表
        List<HostVo> hostList = new ArrayList<HostVo>();
        try {
            hostList = daoManager.getHostDao().queryHostVos();
        } catch (MyException e) {
            logger.error("数据库中获取主机列表失败,无法启动程序！", e.getMessage());
            System.exit(-1);
        }

        //判断主机类型
        if (hostList != null && hostList.size() > 0) {
            for (HostVo hostVo : hostList) {
                if (hostVo != null) {
                    if (hostVo.getHost().getProtocolType() == ProtocolConst.HTTP_PROTOCOL_CODE) {//http
                        hostMap.put(hostVo.getHost().getDomain(), String.valueOf(hostVo.getPort().getPortNumber()));
                    } else if (hostVo.getHost().getProtocolType() == ProtocolConst.WEB_SERVICE_PROTOCOL_CODE) {
                        hostMap.put(hostVo.getHost().getDomain(), String.valueOf(hostVo.getPort().getPortNumber()));
                    } else if (hostVo.getHost().getProtocolType() == ProtocolConst.DUBBO_PROTOCOL_CODE) {
                        logger.info("Dubbo协议暂未实现！");
                    } else if (hostVo.getHost().getProtocolType() == ProtocolConst.SOCKET_PROTOCOL_CODE) {
                        logger.info("Socket协议暂未实现！");
                    } else {
                        logger.error("未知协议错误！");
                    }
                }
            }
        } else {
            logger.error("获取主机列表为空，无法启动程序！");
            System.exit(-1);
        }

        //创建管道服务
        SimpleHttpPipe pipe = new SimpleHttpPipe();
        SimpleStub<SimpleHttpRequest, SimpleHttpResponse> stub = pipe.createSimpleStub(hostMap);

        //业务服务处理
        stub.addService("HttpService", new Service<SimpleHttpRequest, SimpleHttpResponse>() {

            @Override
            @SuppressWarnings({ "unchecked", "static-access" })
            public void doService(Request<SimpleHttpRequest> request, Response<SimpleHttpResponse> response) {

                /**
                 * ==================================分析请求==================================
                 */
                //获取请求对象
                SimpleHttpRequest httpreq = request.pojo();
                SimpleHttpResponse httpres = new SimpleHttpResponse();

                //响应日志
                RequestResponseLog reqResLog = new RequestResponseLog();
                String uuid = Command.GUID();
                reqResLog.setSessionId(uuid);
                reqResLog.setRequestTime(new Date().getTime());
                reqResLog.setClientAddress(ParseRequest.getClientAddress(httpreq));

                //创建工作流程上下文
                WorkContext workContext = WorkContext.getContextInstance();
                workContext.setSessionId(uuid);
                workContext.setRequest(httpreq);
                workContext.setResponse(httpres);

                //请求头、主机名、端口、路径、请求body
                Map<String, List<String>> headMap = ParseRequest.getHeaderMap(httpreq);

                String host = headMap.get(ConstDefine.HEAD_HOST).get(0);
                int port = Integer.valueOf(headMap.get(ConstDefine.HEAD_PORT).get(0));
                String realUrl = ParseRequest.getRealUrl(httpreq);
                String body = "";
                try {
                    body += httpreq.getContent() != null ? new String(httpreq.getContent(), "UTF-8") : null;
                } catch (UnsupportedEncodingException e) {
                    logger.error("请求Body字节数组转字符串错误！", e.getMessage());
                }

                /**
                 * ==================================路由匹配host和api==================================
                 */

                //通过请求内容获取对应主机ID列表
                List<HostVo> list = null;
                try {
                    list = daoManager.getHostDao().queryHostVosByHostUrlPort(host, realUrl, port);
                } catch (MyException e) {
                    logger.error("通过请求内容获取对应主机ID列表失败！", e.getMessage());
                    return;
                }
                List<Integer> hostIds = new ArrayList<Integer>();
                if (list == null) {
                    responseTips(httpres, response, tips);
                    return;
                } else {
                    for (HostVo hostVo : list) {
                        if (hostVo != null) {
                            hostIds.add(hostVo.getHost().getId());
                        }
                    }
                    //去重
                    HashSet<Integer> set = new HashSet<Integer>(hostIds);
                    hostIds.clear();
                    hostIds.addAll(set);
                }

                //定义api主键和Api实体
                Api api = null;

                //通过主机ID查找匹配的Api列表
                List<Api> apis = null;
                try {
                    apis = daoManager.getApiDao().queryApiList(hostIds);
                } catch (MyException e) {
                    logger.error("通过主机ID查找匹配的Api列表失败！", e.getMessage());
                    return;
                }
                if (apis == null) {
                    responseTips(httpres, response, tips);
                    return;
                }

                /**
                 * ==================================区分Http和webservice==================================
                 */

                //判断是WebService还是普通Http，然后获取Api主键
                String protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
                try {
                    if (RouteMockApi.isWebService(headMap, body, protocol)) {//Soap-WebService
                        api = RouteMockApi.getServiceApi(apis, body);
                        workContext.setWebServcie(true);
                        workContext.setSoapProtocol(protocol);
                        reqResLog.setRequestData(body);
                    } else {//Http&&Rest-WebService
                        api = RouteMockApi.getServiceApi(apis, realUrl);
                        if (api != null) {
                            Map<String, String> paramMap = ParseRequest.getParamsMap(httpreq, api.getInterceptParam());
                            workContext.setParamMap(paramMap);
                            reqResLog.setRequestData(JSON.toJSONString(paramMap));
                        }
                    }
                } catch (Exception e) {
                    logger.error("判断是WebService还是普通Http错误！", e.getMessage());
                    return;
                }

                if (api == null) {
                    responseTips(httpres, response, tips);
                    return;
                }

                /**
                 * ==================================按对应stepFlow处理请求，生成响应内容==================================
                 */
                //按流程和关键字调用反射进行处理和计算
                WorkStep ws = null;
                StringBuffer responseContent = new StringBuffer();

                //预加载groovy脚本文件（递归加载，可改进线程加载可能会快些）
                Map<String, Object> objMap = ReflexObject.reflexObjMap;

                //定义对象
                Reflection ref = null;

                //定义
                List<String> methods = new ArrayList<String>();
                Map<String, Object> resMap = new HashMap<String, Object>();
                List<String> params = new ArrayList<String>();

                //处理工作流程
                ws = JSON.parseObject(api.getFlowContent(), WorkStep.class);
                reqResLog.setApiId(api.getId());

                //处理流程中的步骤
                List<Step> steps = ws.getSteps();
                if (steps != null && steps.size() > 0) {
                    //一个流程有多少步骤
                    try {
                        Object result = null;
                        Computer obj = null;
                        for (int i = 0; i < steps.size(); i++) {

                            //调用默认Command计算
                            String keyWork = steps.get(i).getKeyWork();

                            if (keyWork.contains("@")) {
                                obj = (Computer) objMap.get(keyWork.split("@")[0]);
                                if (obj.getWorkContext() == null) {
                                    obj.setWorkContext(workContext);
                                }
                                params = steps.get(i).getParams();

                                //判断关键字是哪个方法
                                String method = keyWork.split("@")[1];
                                ref = new Reflection(obj);
                                methods = ref.getMethodNames();

                                if (methods.contains(method)) {
                                    //获取方法
                                    //Method med = ref.cls.getDeclaredMethod(method, ref.getClasses(params));
                                    Method med = ref.cls.getDeclaredMethod(method, Object[].class);
                                    if (!"void".equals(med.getReturnType().getName())) {
                                        //反射调用方法，并将返回值丢到resMap中,最后把resMap放到流程上下文中
                                        //result = (String) med.invoke(obj, ref.paramsToArray(params, resMap, "@Steps-"));
                                        result = med.invoke(obj, new Object[] { ref.paramsToObjArray(params, resMap, "@Steps-") });

                                        resMap.put("@Steps-" + i, result);

                                        obj.getWorkContext().setResMap(resMap);
                                    } else {
                                        //反射调用方法，并将返回值丢到resMap中,最后把resMap放到流程上下文中
                                        //result = (String) med.invoke(obj, ref.paramsToArray(params, resMap, "@Steps-"));
                                        med.invoke(obj, new Object[] { ref.paramsToObjArray(params, resMap, "@Steps-") });
                                    }
                                }
                            } else {
                                obj = (Computer) objMap.get(ReflexObject.DEFAULT_OBJ);
                                if (obj.getWorkContext() == null) {
                                    obj.setWorkContext(workContext);
                                }
                                params = steps.get(i).getParams();
                                //判断关键字是哪个方法
                                String method = keyWork.replace("$", "");
                                ref = new Reflection(obj);
                                methods = ref.getMethodNames();

                                if (methods.contains(method)) {
                                    //获取方法
                                    Method med = ref.cls.getDeclaredMethod(method, ref.getClasses(params));
                                    if (!"void".equals(med.getReturnType().getName())) {
                                        //反射调用方法，并将返回值丢到resMap中,最后把resMap放到流程上下文中
                                        result = med.invoke(obj, ref.paramsToArray(params, resMap, "@Steps-"));
                                        resMap.put("@Steps-" + i, result);

                                        obj.getWorkContext().setResMap(resMap);
                                    } else {
                                        //反射调用方法，并将返回值丢到resMap中,最后把resMap放到流程上下文中
                                        med.invoke(obj, ref.paramsToArray(params, resMap, "@Steps-"));
                                    }
                                }
                            }
                            //处理流程到最后一步，处理返回流程
                            if (i == (steps.size() - 1)) {
                                List<String> backList = new ArrayList<String>();
                                Map<String, List<String>> backMap = new HashMap<String, List<String>>();

                                backList.add("text/html;charset=utf-8");
                                backMap.put("Content-Type", backList);
                                httpres.setHeaders(backMap);
                                SOAPMessage soapMessage = null;
                                if (api.getTemplateId() > 0) {
                                    Template template = daoManager.getTemplateDao().get(api.getTemplateId());
                                    if (template != null) {
                                        obj.getWorkContext().setTemplate(template);
                                        responseContent.append(Command.replaceKey());
                                    } else {
                                        tips = "请检查Template是否存在！";
                                    }
                                } else {
                                    if (obj.getWorkContext().isWebServcie()) {
                                        if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(obj.getWorkContext().getSoapProtocol())) {
                                            soapMessage = SoapUtil.createSoapMessage(SOAPConstants.SOAP_1_1_PROTOCOL, result);
                                        } else {
                                            soapMessage = SoapUtil.createSoapMessage(SOAPConstants.SOAP_1_2_PROTOCOL, result);
                                        }
                                        responseContent.append(SoapUtil.formartSoapToString(soapMessage));
                                    } else {
                                        responseContent.append(result);
                                    }
                                }
                                reqResLog.setResponseData(responseContent.toString());

                                daoManager.getRequestResponseLogDao().save(reqResLog);
                                httpres.setContent(responseContent.toString().getBytes("UTF-8"));
                                response.pojo(httpres);
                                response.disconnectOnComplete();
                                return;
                            }
                        }
                    } catch (Exception e) {
                        logger.error("处理工作流程时报错！", e.getMessage());
                        return;
                    }
                }
                tips = "无法处理你的请求，请检查是否创建处理流程！";
                responseTips(httpres, response, tips);
                return;
            }
        });

        //启动管道服务
        try {
            stub.start();
            stub.hold();
        } catch (Exception e) {
            logger.error("启动管道服务时出现异常，启动失败！", e.getMessage());
        }
    }

    public static void responseTips(SimpleHttpResponse httpres, Response<SimpleHttpResponse> response, String tips) {
        logger.error(tips);
        try {
            httpres.setContent(tips.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("字节数组转字符串错误！", e.getMessage());
        }
        response.pojo(httpres);
    }
}

/**
 * String[] beanNames = ctx.getBeanDefinitionNames();
 * Arrays.sort(beanNames);
 * for (String beanName : beanNames) {
 *     System.out.println(beanName);
 * }
 **/
