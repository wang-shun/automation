package com.gome.test.mock.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.exolab.castor.xml.Marshaller;

public class SoapUtil {

    /**
    * 创建SOAP消息
    */
    public static SOAPMessage createSoapMessage(String protocol, Object obj) {
        SOAPMessage message = null;
        try {
            // 创建消息工厂
            MessageFactory factory = MessageFactory.newInstance(protocol);
            // 根据消息工厂创建SoapMessage
            message = factory.createMessage();

            // 创建SOAPPart
            SOAPPart part = message.getSOAPPart();
            // 获取SOAPEnvelope
            SOAPEnvelope envelope = part.getEnvelope();
            // 通过SoapEnvelope可以获取到相应的Body信息
            SOAPBody body = envelope.getBody();
            //body.addDocument(document);
            // 根据Qname创建相应的节点,Qname是一个带有命名空间的节点
            //QName qname = new QName(namespaceUri, localPart, prefix);

            /* List<Person> list = new ArrayList<Person>();
             for (int i = 0; i < 3; i++) {
                 list.add(new Person());
             }
            */
            StringWriter writer = new StringWriter();
            Marshaller marshaller = new Marshaller(writer);
            marshaller.setEncoding("UTF-8");
            marshaller.marshal(obj);
            //System.out.println(writer.toString());
            //body.addBodyElement(qname);
            body.addDocument(DOMUtils.parseXMLDocument(writer.toString()));
            //ele.addChildElement("a").setValue("11");
            //ele.addChildElement("b").setValue("22");
            // 打印消息信息
            //System.out.println(message.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * <把soap字符串格式化为SOAPMessage>
     * <功能详细描述>
     * @param soapString
     * @return 
    */
    public static SOAPMessage formartStringToSoap(String soapString) {
        MessageFactory msgFactory;
        try {
            msgFactory = MessageFactory.newInstance();
            SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(soapString.getBytes(Charset.forName("UTF-8"))));
            reqMsg.saveChanges();
            return reqMsg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
      * 把soap对象格式化为字符串
      * @param soapPart
      * @return 
     */
    public static String formartSoapToString(SOAPMessage soapMessage) {
        String str = "";
        try {
            SOAPPart soapPart = soapMessage.getSOAPPart();
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            trans.transform(new DOMSource(soapPart.getEnvelope()), new StreamResult(sw));
            sw.flush();
            sw.close();
            str = String.format(sw.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String getSoapRequest(String xmlStr, Map<String, String> params) {
        try {
            //InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(xmlPath)), "UTF-8");
            StringReader isr = new StringReader(xmlStr);
            BufferedReader reader = new BufferedReader(isr);
            String soap = "";
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                soap += tmp;
            }
            reader.close();
            isr.close();

            for (String paramName : params.keySet()) {
                String paramValue = params.get(paramName);
                soap = soap.replace(":" + paramName, paramValue);
            }

            return soap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** 
     *  
     * @param urlString 
     *            ：webservice的url 
     * @param SOAPAction 
     *            ：调用的接口中的方法名 
     * @param xmlPath 
     *            ：传入参数的xml文件路径 
     * @param params 
     *            ：需要传入的动态配置的参数 
     * @return：返回一个可指定编码的IO 
     * @throws Exception 
     */
    public static InputStreamReader getSoapInputStream(String urlString, String SOAPAction, String xmlStr, Map<String, String> params) throws Exception {
        try {
            String soap = getSoapRequest(xmlStr, params);
            if (soap == null) {
                return null;
            }
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", Integer.toString(soap.length()));
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("SOAPAction", urlString + "/" + SOAPAction);

            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
            osw.write(soap);
            osw.flush();
            osw.close();

            InputStream is = conn.getInputStream();
            // 指定返回编码  
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");

            return isr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** 
     * 将返回的IO转化为一个String返回 
     *  
     * @param urlString 
     * @param SOAPAction 
     * @param xmlPath 
     * @param params 
     * @return 
     * @throws Exception 
     */
    public static String getSoapResponse(String urlString, String SOAPAction, String xmlStr, Map<String, String> params) throws Exception {
        InputStreamReader isr = getSoapInputStream(urlString, SOAPAction, xmlStr, params);
        StringBuffer result = new StringBuffer();
        int b = -1;
        while ((b = isr.read()) != -1) {
            result.append((char) b);
        }
        isr.close();
        return result.toString();
    }

    /** 
     * xml文件中是一个soap协议形式的内容 
     *  
     * @param xmlPath 
     * @return 
     */
    public static String getDemoResponse(String xmlPath) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(xmlPath)), "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            String soap = "";
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                soap += tmp;
            }
            reader.close();
            isr.close();

            return soap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) {

        /*try {

            // 创建连接
            // ==================================================
            SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnFactory.createConnection();

            //  创建消息对象
            // ===========================================
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();
            //              message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "gb2312");

            // 创建soap消息主体==========================================
            SOAPPart soapPart = message.getSOAPPart();// 创建soap部分
            SOAPEnvelope envelope = soapPart.getEnvelope();
            SOAPBody body = envelope.getBody();
            //  根据要传给mule的参数，创建消息body内容。具体参数的配置可以参照应用集成接口技术规范1.1版本
            // =====================================
            SOAPElement bodyElement = body.addChildElement(envelope.createName("process", "Request", "http://esb.service.com/"));
            bodyElement.addChildElement("ServiceCode").addTextNode("10000061");
            bodyElement.addChildElement("OrigAppId").addTextNode("999");
            bodyElement.addChildElement("HomeAppId").addTextNode("998");
            Calendar c = Calendar.getInstance();
            String reqTime = String.valueOf(c.getTimeInMillis());
            bodyElement.addChildElement("ReqTime").addTextNode(reqTime);
            bodyElement.addChildElement("IpAddress").addTextNode("10.212.40.112");
            bodyElement.addChildElement("OrigSerialNo").addTextNode("201205242011");
            //（ServiceCode+ OrigAppId+ ReqTime+ IpAddress）签名
            String AppSignature = "10000061" + "999" + reqTime + "10.212.40.112" + "123456";
            bodyElement.addChildElement("AppSignature").addTextNode(MD5Utils.encodeMessage(AppSignature));
            bodyElement.addChildElement("Version").addTextNode("014");
            //              bodyElement.addChildElement("RelSessionId").addTextNode("RelSessionId");
            //              bodyElement.addChildElement("ReplyCode").addTextNode("ReplyCode");
            bodyElement.addChildElement("ReplyVersion").addTextNode("05");
            bodyElement.addChildElement("TimeOut").addTextNode("30");
            //              bodyElement.addChildElement("FtpDir").addTextNode("FtpDir");
            //              bodyElement.addChildElement("FileList").addTextNode("FileList");
            bodyElement.addChildElement("serviceParas").addTextNode("<param><name>apptest</name><password>apptest</password></param>");
            // Save the message
            message.saveChanges();
            // 打印客户端发出的soap报文，做验证测试
            System.out.println(" REQUEST: ");
            message.writeTo(System.out);
            System.out.println(" ");
            
             * 实际的消息是使用 call()方法发送的，该方法接收消息本身和目的地作为参数，并返回第二个 SOAPMessage 作为响应。
             * call方法的message对象为发送的soap报文，url为mule配置的inbound端口地址。
             
            URL url = new URL("http://localhost:9003/WebServiceSyn/process");
            System.out.println(url);
            // 响应消息
            // ===========================================================================
            SOAPMessage reply = connection.call(message, url);
            //reply.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "gb2312");
            // 打印服务端返回的soap报文供测试
            System.out.println("RESPONSE:");
            // ==================创建soap消息转换对象
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // Extract the content of the reply======================提取消息内容
            Source sourceContent = reply.getSOAPPart().getContent();
            // Set the output for the transformation
            StreamResult result = new StreamResult(System.out);
            transformer.transform(sourceContent, result);
            // Close the connection 关闭连接 ==============
            System.out.println("");
            connection.close();
            
             * 模拟客户端A，异常处理测试
             
            SOAPBody ycBody = reply.getSOAPBody();
            Node ycResp = ycBody.getFirstChild();
            System.out.print("returnValue:" + ycResp.getTextContent());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }*/
        //List<Person> list = new ArrayList<Person>();
        //for (int i = 0; i < 3; i++) {
        //    list.add(new Person());
        //}
        //System.out.println(formartSoapToString(SoapUtil.createSoapMessage(SOAPConstants.SOAP_1_2_PROTOCOL, list)));

        /* Map<String, String> map = new HashMap<String, String>();
         map.put("a", "111");
         map.put("b", "222");
         String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><note ><to>George</to><from>John</from><heading>Reminder</heading><body>Don't forget the meeting!</body></note>";
         System.out.println(getSoapRequest(xmlStr, map));*/

        String str = "<root id='tt'><![CDATA[111      3333]]><root><basisInfo><![CDATA[222      4444]]>";
        Pattern p = Pattern.compile("<\\!\\[CDATA\\[(.*?)\\]\\]>");

        Matcher m = p.matcher(str);
        String rtn = "";
        while (m.find()) {
            rtn += m.group(1);
        }
        System.out.println(rtn);
    }

}
