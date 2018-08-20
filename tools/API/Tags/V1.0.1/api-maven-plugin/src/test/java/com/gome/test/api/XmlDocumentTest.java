package com.gome.test.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

import com.gome.test.api.XmlDocument;
import org.dom4j.DocumentException;
import org.testng.annotations.*;

public class XmlDocumentTest {
//
//    @Test(expectedExceptions = {DocumentException.class})
//    public void testNonXmlDocument() throws DocumentException, IOException {
//        new XmlDocument(getClass().getResourceAsStream("/global.xlsx"));
//    }
//
//    @Test
//    public void testUpdateTextAndSave() throws IOException, DocumentException {
//        XmlDocument xmlDoc = new XmlDocument(getClass().getResourceAsStream("/pom1.xml"));
//        Map<String, String> m = new HashMap<String, String>();
//        m.put("/project/groupId", "com.elong.etf");
//        m.put("/project/artifactId", "ETF");
//        m.put("/project/version", "1.0");
//
//        xmlDoc.updateText(m);
//        String tmpfilePath = System.getProperty("java.io.tmpdir") + File.separator
//                + "pom-update-text.xml";
//        xmlDoc.dumpTo(tmpfilePath);
//        assertTrue(compareEq(getClass().getResourceAsStream("/pom1-update-text.xml"),
//                new FileInputStream(new File(tmpfilePath))));
//    }
//
//    @Test
//    public void testUpdateAttributeAndSave() throws DocumentException, IOException {
//        XmlDocument xmlDoc = new XmlDocument(getClass().getResourceAsStream(
//                "/testng.xml"));
//        Map<String, String> m = new HashMap<String, String>();
//        m.put("/suite/@name", "suite_name");
//        m.put("/suite/test/@name", "test_name");
//        xmlDoc.updateAttribute(m);
//        String tmpfilePath = System.getProperty("java.io.tmpdir") + File.separator
//                + "testng-update-attr.xml";
//        xmlDoc.dumpTo(tmpfilePath);
//        assertTrue(compareEq(getClass().getResourceAsStream("/testng-update-attr.xml"),
//                new FileInputStream(new File(tmpfilePath))));
//    }
//
//    @Test
//    public void testAddElementAttrsAndSave() throws DocumentException, IOException {
//        XmlDocument xmlDoc = new XmlDocument(getClass().getResourceAsStream("/testng.xml"));
//        String[] attrs = {"com.elong.Test1", "com.elong.Test2", "com.elong.Test3"};
//        for (String attr : attrs) {
//            xmlDoc.addElementAttr("/suite/test/classes", "class", "name", attr);
//        }
//        String tmpfilePath = System.getProperty("java.io.tmpdir") + File.separator
//                + "testng-add-elements.xml";
//        xmlDoc.dumpTo(tmpfilePath);
//        assertTrue(compareEq(getClass().getResourceAsStream("/testng-add-elements.xml"),
//                new FileInputStream(new File(tmpfilePath))));
//    }
//
//    private boolean compareEq(InputStream is1, InputStream is2) throws IOException {
//        BufferedReader bis1 = new BufferedReader(new InputStreamReader(is1));
//        BufferedReader bis2 = new BufferedReader(new InputStreamReader(is2));
//        String line1 = null, line2;
//
//        while (null != (line1 = bis1.readLine())) {
//            line2 = bis2.readLine();
//            if (null == line2) {
//                return false;
//            }
//
//            if (!line1.trim().equals(line2.trim())) {
//                return false;
//            }
//        }
//
//        line2 = bis2.readLine();
//        is1.close();
//        is2.close();
//        return (null == line2);
//    }
}
