package com.gome.test.mq.bo;


import java.io.StringReader;
import java.io.StringWriter;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Created by zhangjiadi on 15/12/23.
 */
public class XmlFormatUtil {

    public static String format(String str) throws Exception {
        SAXReader reader = new SAXReader();
        // System.out.println(reader);
        // 注释：创建一个串的字符输入流
        StringReader in = new StringReader(str);
        Document doc = reader.read(in);
        // System.out.println(doc.getRootElement());
        // 注释：创建输出格式
        OutputFormat formater = OutputFormat.createPrettyPrint();
        //formater=OutputFormat.createCompactFormat();
        // 注释：设置xml的输出编码
        formater.setEncoding("utf-8");
        // 注释：创建输出(目标)
        StringWriter out = new StringWriter();
        // 注释：创建输出流
        XMLWriter writer = new XMLWriter(out, formater);
        // 注释：输出格式化的串到目标中，执行后。格式化后的串保存在out中。
        writer.write(doc);

        writer.close();
        System.out.println(out.toString());
        // 注释：返回我们格式化后的结果
        return out.toString();
    }

    public static <T> T convertToObject(String xml, Class<T> type) {
        StringReader sr = new StringReader(xml);
        try {
            JAXBContext jAXBContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(sr);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }

    public static String convertToXmlString(Object source) {
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jAXBContext = JAXBContext
                    .newInstance(source.getClass());
            Marshaller marshaller = jAXBContext.createMarshaller();
            marshaller.marshal(source, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


//    public static String toFormatedXML(Document object) throws Exception {
//        Document doc = (Document) object;
//        TransformerFactory transFactory = TransformerFactory.newInstance();
//        Transformer transFormer = transFactory.newTransformer();
//        transFormer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
//        DOMSource domSource = new DOMSource(doc);
//
//        StringWriter sw = new StringWriter();
//        StreamResult xmlResult = new StreamResult(sw);
//
//        transFormer.transform(domSource, xmlResult);
//
//        return sw.toString();
//
//    }




}
