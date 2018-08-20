package com.gome.test.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /* @Autowired
     private static HostDao hostDao;

     @Autowired
     private static ApiDao apiDao;

     @Autowired
     private static WorkFlowDao workFlowDao;

     private static final Logger logger = new Logger(Application.class);

     private static Map<String, String> hostMap = new HashMap<String, String>();
     private static String tips = "无法确定服务！";*/

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Application.class, args);

        /* System.out.println(hostDao == null);
         List<HostVo> hostList = hostDao.queryHostVos();

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
             logger.info("主机列表为空，无法启动程序！");
             System.exit(-1);;
         }

         SimpleHttpPipe pipe = new SimpleHttpPipe();
         SimpleStub<SimpleHttpRequest, SimpleHttpResponse> stub = pipe.createSimpleStub(hostMap);

         stub.addService("HttpService", new Service<SimpleHttpRequest, SimpleHttpResponse>() {
             @Override
             public void doService(Request<SimpleHttpRequest> request, Response<SimpleHttpResponse> response) throws Exception {
                 //获取请求对象
                 SimpleHttpRequest httpreq = request.pojo();
                 SimpleHttpResponse httpres = new SimpleHttpResponse();

                 //请求头Map
                 Map<String, List<String>> headMap = ParseRequest.getHeaderMap(httpreq);
                 //请求主机
                 String host = headMap.get(ConstDefine.HEAD_HOST).get(0);
                 //请求端口
                 int port = Integer.valueOf(headMap.get(ConstDefine.HEAD_PORT).get(0));
                 //请求路径
                 String realUrl = ParseRequest.getRealUrl(httpreq);
                 //请求Body
                 String body = httpreq.getContent() != null ? new String(httpreq.getContent(), "UTF-8") : null;
                 //服务列表
                 String serviceName = getServiceName(realUrl, port);

                 if (serviceName == null) {
                     logger.error(tips);
                     httpres.setContent(tips.getBytes("GBK"));
                     response.pojo(httpres);
                     return;
                 }

                 //定义api主键和Api实体
                 int apiId = 0;
                 Api api = null;

                 //Api列表
                 List<Api> apis = getApis(host, realUrl, port);

                 //获取Api主键
                 if (RouteMockApi.isWebService(headMap, body)) {//WebService
                     apiId = RouteMockApi.getWebServiceApi(apis, host, realUrl, body, port);
                 } else {//Http&&Rest
                     apiId = RouteMockApi.getHttpApi(apis, host, realUrl, port);
                 }

                 //ApiId大于零定位成功
                 if (apiId > 0) {
                     api = apiDao.queryApiById(apiId);
                 } else {
                     httpres.setContent(tips.getBytes("GBK"));
                     response.pojo(httpres);
                     return;
                 }

                 //获取Api对应的工作流程
                 List<WorkFlow> wfList = null;
                 if (api != null && api.getId() > 0) {
                     wfList = workFlowDao.queryWorkFlows(apiId);
                 }

                 //按流程和关键字调用反射进行处理和计算
                 WorkStep ws = null;
                 StringBuffer responseString = new StringBuffer();
                 if (wfList != null && wfList.size() > 0) {
                     for (WorkFlow wf : wfList) {
                         if (wf != null) {
                             ws = JSON.parseObject(wf.getFlowContent(), WorkStep.class);
                             List<Step> steps = ws.getSteps();

                             //获取反射对象
                             Command command = new Command();
                             Class<? extends Command> cls = command.getClass();
                             List<String> methodNames = new ArrayList<String>();
                             List<Method> methodList = new ArrayList<Method>();
                             Object obj = cls.newInstance();;
                             Method[] methods = cls.getMethods();
                             methodList = Arrays.asList(methods);
                             for (Method mthd : methodList) {
                                 methodNames.add(mthd.getName());
                             }

                             if (steps != null && steps.size() > 0) {
                                 for (Step step : steps) {
                                     if (methodNames.contains(step.getKeyWork().replace("@", ""))) {
                                         Method med = cls.getMethod(step.getKeyWork().replace("@", ""), null);
                                         System.out.println("第一个测试：" + med.invoke(obj, null));
                                         responseString.append(med.invoke(obj, null));
                                     }
                                 }
                             }
                         }
                     }
                 }

                 httpres.setContent(responseString.toString().getBytes());
                 response.pojo(httpres);
                 return;
             }
         });

         // finally start and hold it.
         stub.start();
         stub.hold();*/
    }

    /* public static List<Api> getApis(String domain, String url, int port) throws Exception {
         List<Api> apis = apiDao.queryApiList(domain, url, port);
         return apis;
     }

     public static String getServiceName(String url, int port) throws Exception {
         String serviceName = null;
         List<HostVo> list = hostDao.queryHostVosByUrlPort(url, port);
         if (list != null && list.size() == 1) {
             serviceName = list.get(0).getHost().getServiceName();
         }
         return serviceName;
     }

     public static HostDao getHostDao() {
         return hostDao;
     }

     public static ApiDao getApiDao() {
         return apiDao;
     }

     public static WorkFlowDao getWorkFlowDao() {
         return workFlowDao;
     }*/

}
