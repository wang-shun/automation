package com.ofo.test.api;

/**
 * Created by liangwei-ds on 2016/12/7.
 */

import com.ofo.test.Constant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Doc {
    private final Document document;

    public Doc(String path) throws DocumentException, IOException {
        this(new File(path));
    }

    public Doc(File file) throws DocumentException, IOException {
        this((InputStream) (new FileInputStream(file)));
    }

    public Doc(InputStream is) throws DocumentException, IOException {
        SAXReader saxReader = new SAXReader();
        this.document = saxReader.read(is);
        is.close();
    }

    public void delete() {
        Element suiteXmlFilesElement = (Element) this.document.selectSingleNode("/project/build/plugins/plugin/configuration/suiteXmlFiles");
        List list = suiteXmlFilesElement.selectNodes("suiteXmlFile");
        Iterator iter = list.iterator();
        ArrayList ss = new ArrayList();

        while (iter.hasNext()) {
            Element iters = (Element) iter.next();
            String i$ = iters.getTextTrim();
            ss.add(i$);
        }

        if (ss.size() > 1) {
            Iterator iters1 = list.iterator();
            Iterator i$2 = ss.iterator();

            while (i$2.hasNext()) {
                String s = (String) i$2.next();
                Element i$1 = (Element) iters1.next();
                String text = i$1.getTextTrim();
                if (text.contains("testng.xml")) {
                    suiteXmlFilesElement.remove(i$1);
                    System.out.println(String.format("删除了原子case的xml：%s", new Object[]{s}));
                }
            }
        }

    }

    public void dumpTo(String xmlPath) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        format.setLineSeparator(Constant.LINE_SEPARATOR);
        XMLWriter writer = null;

        try {
            writer = new XMLWriter(new FileOutputStream(new File(xmlPath)), format);
            writer.setEscapeText(true);
            writer.write(this.document);
        } finally {
            if (null != writer) {
                writer.close();
            }

        }

    }
}