package com.gome.test.mock.utils;

import com.gome.test.mock.cnst.Ak47Constants;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;


/**
 * org.w3c.dom.Document-related helper class
 * 
 * XML相关辅助，基于org.w3c.dom
 * 
 */
public class XmlUtil {

    /**
     * document(w3c.dom.Document) to string(XML Text)
     * 
     * 
     * @param document
     * @return
     * @throws TransformerException
     * @throws UnsupportedEncodingException
     */
    public static String document2String(Document document) throws TransformerException, UnsupportedEncodingException{
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        transformer.transform(source, result);
        return bos.toString(Ak47Constants.DEFAULT_ENCODING);
    }
    
    /**
     * string(XML Text) to document(w3c.dom.Document)
     * 
     * @param string
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Document string2Document(String string) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        InputStream is = new ByteArrayInputStream(string.getBytes(Ak47Constants.DEFAULT_ENCODING));
        DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(is);
        return document;
    }
}
