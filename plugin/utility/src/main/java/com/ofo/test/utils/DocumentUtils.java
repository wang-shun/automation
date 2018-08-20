package com.ofo.test.utils;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by Administrator on 2015/7/20.
 */
public class DocumentUtils {



    public static Document getDocument(File file) throws Exception
    {
        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document document=null;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            document = db.parse(file);
        }catch (Exception ex)
        {
            throw ex;
        }
        return document;
    }
}
