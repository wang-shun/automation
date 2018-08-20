package com.ofo.test.utils;

import org.dom4j.DocumentException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;

/**
 * Created by lizonglin on 2015/7/22/0022.
 */
public class XmlUtils {
    public static NodeList getXMLNode(String xmlStr, String xpathExp) throws DocumentException, XPathExpressionException {
        ByteArrayInputStream stream = new ByteArrayInputStream(xmlStr.getBytes());
        InputSource inputSource = new InputSource(new InputStreamReader(stream));
        return getNodeFromSource(inputSource, xpathExp);
    }

    public static NodeList getXMLNode(File xmlFile, String xpathExp) throws FileNotFoundException, XPathExpressionException {
        InputSource inputSource = new InputSource(new FileInputStream(xmlFile));
        return getNodeFromSource(inputSource, xpathExp);
    }

    public static void appendNode(String filePath, String parentNode, String childNodeName, String childNodeContent) throws Exception {
        Document doc = DocumentUtils.getDocument(new File(filePath));
        Element element = doc.createElement(childNodeName);
        NodeList nodeList = doc.getElementsByTagName(parentNode);

    }

    /**
     * 修改XML中某节点的值，简单XML，不支持xpath
     * @param xmlFilePath
     * @param tagName
     * @param index
     * @param newContent
     */
    public static void modifySingleNodeContent(String xmlFilePath, String tagName, int index, String newContent){
        try{
            Document xmldoc = parseXmlFile2Doc(xmlFilePath);

            Element root = xmldoc.getDocumentElement();
            if (root.getElementsByTagName(tagName).getLength() > 0) {
                root.getElementsByTagName(tagName).item(index).setTextContent(newContent);
            } else {
                throw new Exception("在" + xmlFilePath + "中找不到标签为<" + tagName + ">的节点");
            }

            reSaveXmlFile(xmldoc, xmlFilePath);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param xmldoc
     * @param xmlFilePath
     * @param parentTagName
     * @param parentIndex
     * @param childElement 必须为xmldoc create出来的element
     */
    public static void appendChildNode(Document xmldoc, String xmlFilePath, String parentTagName, int parentIndex, Element childElement) {
        try {
            Element root = xmldoc.getDocumentElement();
            if (root.getElementsByTagName(parentTagName).getLength() > 0) {
                root.getElementsByTagName(parentTagName).item(parentIndex).appendChild(childElement);
            } else {
                throw new Exception("在" + xmlFilePath + "中找不到父标签为<" + parentTagName + ">的节点");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static NodeList getNodeFromSource(InputSource inputSource, String xpathExp) throws XPathExpressionException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = xpath.compile(xpathExp);
        return (NodeList) expr.evaluate(inputSource, XPathConstants.NODESET);
    }

    public static Document parseXmlFile2Doc(String xmlFilePath) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document xmldoc = db.parse(xmlFilePath);
        return xmldoc;
    }

    public static void reSaveXmlFile(Document doc, String xmlFilePath) {
        try{
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer former = factory.newTransformer();
            former.setOutputProperty("encoding", "UTF-8");
            former.setOutputProperty("indent", "yes");
            former.transform(new DOMSource(doc), new StreamResult(new File(xmlFilePath)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }



    public static void main(String[] a) throws Exception {

        String xmlFilePath = "D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\pom.xml";

        Document xmldoc = parseXmlFile2Doc(xmlFilePath);
        Element childNode = xmldoc.createElement("child");
        childNode.setTextContent("child");
//        Element root = xmldoc.getDocumentElement();
//        root.getElementsByTagName("properties").item(0).appendChild(childNode);
//        reSaveXmlFile(xmldoc, xmlFilePath);

//        appendChildNode(xmlFilePath,"properties",0,childNode);
        appendNode(xmlFilePath,"properties","child","child");


//        modifySingleNodeContent(xmlFilePath, "environment", 0, "UAT");
    }

}
