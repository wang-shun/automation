package com.gome.test.mock.common;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.alibaba.fastjson.JSON;
import com.gome.test.api.annotation.Step;
import com.gome.test.mock.Computer;
import com.gome.test.mock.reflex.Reflection;
import com.gome.test.mock.util.JaxbUtil;
import com.gome.test.mock.util.MapToXml;
import com.gome.test.mock.util.TimeoutException;
import com.gome.test.mock.util.TimeoutThread;
import com.gome.test.mock.utils.Logger;
import com.gome.test.utils.DBUtils;
import com.jayway.jsonpath.JsonPath;

public class Command extends Computer {
    private static final Logger logger = new Logger(Command.class);

    /*private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://10.126.59.1:3306/userdata";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";*/

    public Command() {

    }

    //返回传入字符串
    @Step(description = "返回传入字符串")
    public static String success(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    //随机UUID字符串
    @Step(description = "随机UUID字符串")
    public static String GUID() {
        return UUID.randomUUID().toString();
    }

    //随机给定长度字符串，入参错误返回长度为9位的字符串
    @Step(description = "随机给定长度字符串，入参错误返回长度为9位的字符串")
    public static String getRandomString(String length) {
        int defLength = 9;
        try {
            defLength = Integer.valueOf(length);
        } catch (Exception e) {
            defLength = 9;
            logger.error("getRandomString入参错误：", e.getMessage());
        }
        if (defLength == 0) {
            defLength = 9;
        }
        byte[] bs = new byte[defLength];
        Random random = new Random();
        for (int i = 0; i < defLength; i++) {
            int num = random.nextInt(75) + 48;
            if (num > 57 && num < 65) {
                num = num + 7;
            } else if (num > 90 && num < 97) {
                num = num + 6;
            }
            bs[i] = (byte) num;
        }
        return new String(bs);
    }

    //随机给定长度数字，入参错误返回长度为4位的数字
    @Step(description = "随机给定长度数字，入参错误返回长度为4位的数字")
    public static String getRandomInt(String length) {
        int defLength = 4;
        try {
            defLength = Integer.valueOf(length);
        } catch (Exception e) {
            defLength = 4;
            logger.error("getRandomInt入参错误：", e.getMessage());
        }
        if (defLength == 0) {
            defLength = 4;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append((int) (Math.random() * 9) + 1);
        for (int i = 0; i < Math.abs(defLength) - 1; i++) {
            buffer.append((int) (Math.random() * 10));
        }
        return buffer.toString();
    }

    //乱码xml字符串转正常xml字符串
    @Step(description = "乱码xml字符串转正常xml字符串")
    public static String messageToXml(String message) throws Exception {
        SAXReader reader = new SAXReader();
        reader.setEncoding("UTF-8");
        StringWriter stringWriter = new StringWriter();
        Document document = reader.read(new StringReader(StringEscapeUtils.unescapeXml(message)));
        OutputFormat format = new OutputFormat(" ", true);
        XMLWriter writer = new XMLWriter(stringWriter, format);;
        writer.write(document);
        writer.flush();
        writer.close();
        return stringWriter.getBuffer().toString();
    }

    //乱码xml字符串转正常xml字符串
    @Step(description = "乱码xml字符串转正常xml字符串")
    public static String msgToXml(String message) throws Exception {
        Pattern p = Pattern.compile("<\\!\\[CDATA\\[(.*?)\\]\\]>");
        Matcher m = p.matcher(message);
        while (m.find()) {
            message = m.group(1);
        }
        SAXReader reader = new SAXReader();
        reader.setEncoding("UTF-8");
        StringWriter stringWriter = new StringWriter();
        Document document = reader.read(new StringReader(StringEscapeUtils.unescapeXml(message)));
        OutputFormat format = new OutputFormat(" ", true);
        XMLWriter writer = new XMLWriter(stringWriter, format);;
        writer.write(document);
        writer.flush();
        writer.close();
        return stringWriter.getBuffer().toString();
    }

    //xpath挖取值
    @SuppressWarnings("unused")
    @Step(description = "xpath挖取值")
    public static String xPathe(String xpath) throws Exception {
        String xml = new String(workContext.getRequest().getContent(), "UTF-8");
        if (xml != null) {
            SAXReader reader = new SAXReader();
            reader.setEncoding("UTF-8");
            StringWriter stringWriter = new StringWriter();
            Document document = reader.read(new StringReader(StringEscapeUtils.unescapeXml(xml)));
            return Xpath(xpath, document);
        } else {
            return null;
        }

    }

    //xpath挖取值
    @SuppressWarnings("unused")
    @Step(description = "xpath挖取值")
    public static String xPathe(String xml, String xpath) throws Exception {
        if (xml != null) {
            SAXReader reader = new SAXReader();
            reader.setEncoding("UTF-8");
            StringWriter stringWriter = new StringWriter();
            Document document = reader.read(new StringReader(StringEscapeUtils.unescapeXml(xml)));
            return Xpath(xpath, document);
        } else {
            return null;
        }

    }

    //jsonpath挖取值
    @Step(description = "jsonpath挖取值")
    public static String jsonPath(String json, String jsonPath) {
        return JsonPath.parse(json).read(jsonPath).toString();
    }

    //jsonpath挖取值
    @SuppressWarnings("unused")
    @Step(description = "jsonpath挖取值")
    public static String jsonPathe(String jsonPath) throws Exception {
        String json = new String(workContext.getRequest().getContent(), "UTF-8");
        if (json == null) {
            return "";
        }
        return jsonPath(json, jsonPath);

    }

    //挖取请求参数
    @Step(description = "挖取请求参数")
    public static String getHttpParam(String key) throws Exception {
        if (key == null || "".equals(key) || "null".equals(key)) {
            return workContext.getParamMap().get(ParseRequest.DEFAULT_KEY);
        }
        return workContext.getParamMap().get(key);
    }

    //请求延时
    @Step(description = "请求延时")
    public static void delay(String milliSecond) {
        //实例化Timer类
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                this.cancel();
            }
        }, Long.valueOf(milliSecond));//毫秒
    }

    //请求间隔n毫秒延时
    @Step(description = "请求间隔n毫秒延时")
    public static void spaceDelay(String milliSecond, String spaceMilliSecond) {
        //实例化Timer类
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                this.cancel();
            }
        }, Long.valueOf(milliSecond), Long.valueOf(spaceMilliSecond));//间隔spaceMilliSecond毫秒延时
    }

    //请求超时
    @Step(description = "请求超时")
    public static void timeOut(String milliSecond) {
        TimeoutThread t = new TimeoutThread(Long.valueOf(milliSecond), new TimeoutException("请求超时"));
        t.start();
    }

    //替换字符串中的关键字
    @Step(description = "替换字符串中的关键字")
    @SuppressWarnings("static-access")
    public static String replaceKey() {
        String originalText = workContext.getTemplate().getTemplateContent().trim();
        for (String key : workContext.getResMap().keySet()) {
            if (originalText.contains(key.trim())) {
                originalText = originalText.replace(key, workContext.getResMap().get(key).toString());
            }
        }
        return originalText;
    }

    //反射调用实体类生成xml或json
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Step(description = "反射调用实体类生成xml或json")
    public static String queryObj(String keyword, String classUrl, String type) throws Exception {
        Class cls = Class.forName(classUrl);
        //指明所要调用的构造方法的形参
        Class<?>[] argTypes = { String.class };
        //获取指定参数的构造方法
        Constructor<?> constructor = cls.getConstructor(argTypes);
        Object obj = constructor.newInstance(keyword);
        if (Integer.valueOf(type) == 0) {
            return JSON.toJSONString(obj);
        } else {
            JaxbUtil<Object> jaxbUtil = new JaxbUtil<Object>();
            return jaxbUtil.beanToXml(obj);
        }
    }

    //反射调用实体类生成xml或json
    @Step(description = "反射调用查询数据库")
    public static String SQLQuery(String driverClassName, String url, String username, String password, String sql, String table, String type) throws Exception {
        Map<Integer, String> meta = new HashMap<Integer, String>();
        List<String[]> list = DBUtils.querySQL(driverClassName, url, username, password, sql, meta);
        ArrayList<HashMap<String, String>> objList = new ArrayList<HashMap<String, String>>();
        for (String[] arr : list) {
            HashMap<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < arr.length; i++) {
                map.put(meta.get(i), arr[i]);
            }
            objList.add(map);
        }
        if (Integer.valueOf(type.trim()) == 0) {
            return JSON.toJSONString(objList);

        } else {
            return messageToXml(MapToXml.listToXML(objList, table));
        }
    }

    @Step(description = "反射调用更新数据库")
    public static void SQLUpdate(String driverClassName, String url, String username, String password, String sql) throws Exception {
        DBUtils.updateDB(driverClassName, url, username, password, sql);
    }

    @Override
    public Object compute(Object... args) {
        StringBuffer buffer = new StringBuffer();
        for (Object s : args) {
            buffer.append(s);
        }
        return buffer.toString();
    }

    @SuppressWarnings({ "unused", "rawtypes" })
    private static Map<String, String> getRootNameSpace(Document doc) {
        Map<String, String> map = new HashMap<String, String>();
        Element root = doc.getRootElement();
        List list = root.content();
        if (list != null && list.size() > 0) {
            for (Object obj : list) {
                if (obj instanceof Namespace) {
                    //System.out.println(((Namespace) obj).getPrefix() + ":" + ((Namespace) obj).getURI());
                }

            }
        }
        return null;
    }

    //递归遍历节点获取命名空间
    @SuppressWarnings("unchecked")
    private static void getDocumentNameSpace(Element element, Map<String, String> map) {
        if (element.content() != null) {
            //遍历属性
            for (Object obj : element.content()) {
                if (obj instanceof Namespace) {
                    if (((Namespace) obj).getPrefix() == null || "".equals(((Namespace) obj).getPrefix())) {
                        map.put("default", ((Namespace) obj).getURI());
                    } else {
                        map.put(((Namespace) obj).getPrefix(), ((Namespace) obj).getURI());
                    }
                }
            }
        }

        for (Iterator<Element> iter = element.elementIterator(); iter.hasNext();) {
            //递归遍历每一个子节点
            getDocumentNameSpace(iter.next(), map);
        }

    }

    @SuppressWarnings("unchecked")
    private static String Xpath(String xpath, Document doc) {
        Map<String, String> map = new HashMap<String, String>();
        getDocumentNameSpace(doc.getRootElement(), map);
        SAXReader reader = new SAXReader();
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        List<Node> nodes = doc.selectNodes(xpath);
        StringBuffer buffer = new StringBuffer();
        if (nodes != null) {
            if (nodes.size() == 1) {
                buffer.append(nodes.get(0).getText());
            } else {
                for (Node node : nodes) {
                    buffer.append(node.getName() + "=" + node.getText() + "\r\n");
                }
            }
        }
        return buffer.toString();
    }

    public static WorkContext getWorkContext() {
        return workContext;
    }

    public static void setWorkContext(WorkContext workContext) {
        Command.workContext = workContext;
    }

    @SuppressWarnings({ "unused", "unchecked", "rawtypes" })
    public static void main(String[] args) throws Exception {
        String message = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:axis=\"http://ws.apache.org/axis2\">";
        message += "<soapenv:Header/>";
        message += "<soapenv:Body>";
        message += "<axis:sayHelloToPerson>";
        message += "<first>chai</first><name>zhongbao</name>";
        message += "</axis:sayHelloToPerson>";
        message += "</soapenv:Body>";
        message += "</soapenv:Envelope>";
        //String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><person><age>22</age><cardNo>12345</cardNo><age>20</age><sex>男</sex><job>工程师</job></person>";
        //SAXReader reader = new SAXReader();
        //reader.setEncoding("UTF-8");
        //StringWriter stringWriter = new StringWriter();
        //Document document = reader.read(new StringReader(StringEscapeUtils.unescapeXml(message)));
        //getRootNameSpace(document);
        //String a = xPathe("//axis:sayHelloToPerson/*");
        //System.out.println(a);
        //System.out.println(messateToXml(message));
        //try {
        //timeOut("5000");
        //} catch (Exception e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
        //}

        //Class cls = Class.forName("com.gome.test.mock.bean.Person");
        //Class<?>[] argTypes = { String.class }; //指明所要调用的构造方法的形参
        //Constructor<?> constructor = cls.getConstructor(argTypes);//获取指定参数的构造方法
        //Object obj = constructor.newInstance("小明");

        //JaxbUtil<Person> jaxbUtil = new JaxbUtil<Person>();
        //System.out.println(jaxbUtil.beanToXml(new Person("feifei")));
        //System.out.println(JSON.toJSON(obj));
        //System.err.println(queryObj("小明", "com.gome.test.mock.bean.Person", "0"));
        //String sql = "select * from person";
        //System.out.println(SQLQuery(DB_DRIVER, DB_URL, DB_USER, DB_PASSWORD, sql, "1"));

        Computer command = new Command();
        Reflection ref = new Reflection(command);
        Object obj = ref.cls.newInstance();
        String method = "delay";
        Method med = ref.cls.getDeclaredMethod(method, String.class);
        System.out.println();

    }
}
