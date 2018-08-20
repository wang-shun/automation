package com.ofo.test.api.client;

import com.ofo.test.Constant;
import com.ofo.test.api.model.Steps;
import com.ofo.test.context.ContextUtils;
import com.ofo.test.utils.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

public abstract class IMessageClient {

    protected String response;
    protected Map<String, String> kvs;
    protected List<Header> responseHeaders;

    public void setUp() throws Exception {
        executeSteps(Constant.SETUP_CLASS, Constant.SETUP_STEPS);
    }

    public abstract String assemble() throws Exception;

    public abstract String send() throws Exception;

    public abstract List<Header> getAllHeaders() throws Exception;

    public abstract void verify() throws Exception;

    public abstract void close() throws Exception;

    public void tearDown() throws Exception {
        executeSteps(Constant.TEARDOWN_CLASS, Constant.TEARDOWN_STEPS);
    }

    public String run(Map<String, String> kvs) throws Exception {
        return  run(kvs, true, null);
    }
/*

    public void savePost(String context) throws IOException{
        File file;
        file = new File(System.getProperty("user.dir")+ Constant.FILE_SEPARATOR+"//target//"+"Context.txt");
*/
/*        if (file.exists()){
            file.delete();
        }*//*


*/
/*        if (System.getenv().get("OS").contains("Windows")){
            file = new File("C:"+Constant.FILE_SEPARATOR+"Context.txt");
        }else {
            file = new File(Constant.FILE_SEPARATOR+"app"+Constant.FILE_SEPARATOR+"Context.txt");
        }*//*

        FileOutputStream fos = new FileOutputStream(file,true);
        OutputStreamWriter osw = new OutputStreamWriter(fos,"utf-8");
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(context+"\n");
        bw.close();
        osw.close();
        fos.close();
    }
*/

    public String run(Map<String, String> kvs, boolean doVerify, StringBuilder rsp) throws Exception {
        this.kvs = kvs;
        try {
            Logger.info("执行前置条件，进行环境数据准备工作...");
            setUp();

            Logger.info("开始组装要发送的请求...");
            String request = assemble();
            Logger.info(String.format("组装后的请求是: %s", URLDecoder.decode(request,"utf-8")));
            Logger.info(String.format("组装后的请求是: %s", request));
//            this.savePost(request);
//            HttpTestContext txc = new HttpTestContext(request);
            ContextUtils.getContext().put("post",URLDecoder.decode(request,"utf-8"));
            Logger.info("开始发送请求...");
            response = send();
            if (rsp != null) {
                rsp.append(response);
            }
            Logger.info(String.format("得到的响应是: %s", response));
            responseHeaders = getAllHeaders();
            if (responseHeaders != null) {
                Logger.info(String.format("得到的响应头是：%s",responseHeaders.toString()));
            }
            if (doVerify) {
                Logger.info("开始检验...");
                verify();
            }
        } catch (Exception ex) {
            Logger.info(ex.toString());
            throw ex;
        }
        finally {
            Logger.info("执行后续处理工作...");
            tearDown();
        }
        return this.response;
    }

    public String getResponse() {
        return response;
    }

//    public String getHeader(String key) {}

    public void setUpBeforeSuite() throws Exception {
    }

    public void setUpBeforeTest() throws Exception {
    }

    public void setUpBeforeClass() throws Exception {
    }

    public void setUpBeforeMethod() throws Exception {
    }

    public void tearDownAfterMethod() throws Exception {
    }

    public void tearDownAfterClass() throws Exception {
    }

    public void tearDownAfterTest() throws Exception {
    }

    public void tearDownAfterSuite() throws Exception {
        close();
    }

    //执行setup/teardown中的每个方法步骤
    private void executeSteps(String className, String stepsName) throws Exception {
        String executeClassName = ContextUtils.loadValueWithContext(kvs.get(className));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (!StringUtils.isBlank(executeClassName)) {
            Class<?> loadClass = cl.loadClass(executeClassName);
            Constructor<?> setUpConstructor = loadClass.getConstructor();
            Object setUpObject = setUpConstructor.newInstance();
            Method method = setUpObject.getClass().getMethod(Constant.SET_MESSAGE_CLIENT, IMessageClient.class);
            method.invoke(setUpObject, this);
            Steps steps = Steps.loadFrom(kvs.get(stepsName));
            execute(setUpObject, steps);
        }



    }

    protected abstract void execute(Object object, Steps steps) throws Exception;
}
