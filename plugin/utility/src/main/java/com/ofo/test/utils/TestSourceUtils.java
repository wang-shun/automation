package com.ofo.test.utils;


import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjiadi on 15/10/8.
 */
public class TestSourceUtils {

    public static List<XmlSuite> suiteList=new ArrayList<XmlSuite>();
    public static  int testNGCount;
    private static final String SUITE_ALL = "SuiteAll";
    private static final String SUITE_HEAD1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String SUITE_HEAD2 = "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">";
    private static final String SUITE_HEAD3 = "<suite name=\"" + SUITE_ALL + "\">";
    private static final String SUITE_END = "</suite> <!-- " + SUITE_ALL + " -->";
    private static final String SUITE_HEAD = "<suite name=\"%s\">";

    private static void cmdSuite(XmlSuite suite,List<XmlSuite> suites)
    {
        XmlSuite suiteFirst = suiteList.get(0);
        testNGCount= Integer.MAX_VALUE;

        for(int i=0;i< suite.getTests().size();i++)
        {
            XmlSuite lastsuite=suites.size()==0?null: suites.get(suites.size()-1);
            if(lastsuite==null || lastsuite.getTests().size() == testNGCount)
            {
                XmlSuite su=(XmlSuite)suiteFirst.clone();
                su.getTests().clear();
                suites.add(su);
            }
            suites.get(suites.size()-1).addTest(suite.getTests().get(i));
        }
    }

    private static String removeSuiteAndXmlNode(String xml) {
        return xml.replace(SUITE_HEAD1, "").replace(SUITE_HEAD2, "").replace(SUITE_HEAD3, "").replace(SUITE_END, "");
    }


    /**
     * 生成testng配置文件
     * @param testngPath
     * @throws Exception
     */
    public static void saveXmlFile(String testngPath) throws Exception {


        List<XmlSuite> suites=new ArrayList<XmlSuite>();
        for(int i=0;i< suiteList.size();i++)
        {
            cmdSuite(suiteList.get(i),suites);
        }

        for(int t=0;t< suites.size();t++) {
            StringBuffer str = new StringBuffer();
            str.setLength(0);
            str.append(SUITE_HEAD1);
            str.append(SUITE_HEAD2);
            str.append(String.format(SUITE_HEAD,suites.size()== 1 ? SUITE_ALL : (SUITE_ALL+"_"+(t+1))));
            str.append(removeSuiteAndXmlNode(suites.get(t).toXml())).append(SUITE_END);
            String fileName = String.format("%s%stestng%s.xml", testngPath, File.separator, suites.size() == 1 ? "" : ("_"+(t+1)));
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
                writer.write(str.toString());
            } finally {
                if (null != writer) {
                    writer.close();
                }
            }
        }

    }
}
