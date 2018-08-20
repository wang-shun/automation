package com.gome.test.gtp.Load;

import com.gome.test.gtp.model.LoadScene;
import com.gome.test.utils.FileUtils;
import com.gome.test.utils.Logger;
import com.gome.test.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by lizonglin on 2016/1/22/0022.
 */
public class LoadConfBo {
    public Properties getPropsByResource(String resourceName) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/" + resourceName);
            properties.load(new InputStreamReader(inputStream, "gbk"));
        } catch (Exception ex) {
            Logger.error("加载" + resourceName + "中的属性失败！");
            Logger.error(ex.getMessage());
        }
        return properties;
    }

    public void copyTplIs2File(InputStream is, String targetFilePath) {
        FileUtils.copyInputStream2File(is, new File(targetFilePath), true);
    }

    public void copyTpl2File(String tplFilePath, String filePath) {
        File tplFile = new File(tplFilePath);
        File file = new File(filePath);

//        FileUtils.copyFile(tplFile, file, true);
    }

    public void insertLoadScene2UserXml(Properties props, LoadScene loadScene, Document userXmlDoc, String userXmlPath) {
        try {
            NodeList stringPropNodeList = userXmlDoc.getElementsByTagName(props.getProperty("gtp.load.user.proptag"));
//            if (userJmxNodeList.getLength() > 0 && ) {
//                throw new Exception("user.xml中未找到父节点<jmx>");
//            }
            Map<String, String> nameMap = new HashMap<String, String>();
            nameMap.put(props.getProperty("gtp.load.user.onerror"), loadScene.getOnError());
            nameMap.put(props.getProperty("gtp.load.user.threadnum"), String.valueOf(loadScene.getThreadNum()));
            nameMap.put(props.getProperty("gtp.load.user.initdelay"), String.valueOf(loadScene.getInitDelay()));
            nameMap.put(props.getProperty("gtp.load.user.startcount"), String.valueOf(loadScene.getStartCount()));
            nameMap.put(props.getProperty("gtp.load.user.startcountburst"), String.valueOf(loadScene.getStartCountBurst()));
            nameMap.put(props.getProperty("gtp.load.user.startperiod"), String.valueOf(loadScene.getStartPeriod()));
            nameMap.put(props.getProperty("gtp.load.user.stopcount"), String.valueOf(loadScene.getStopCount()));
            nameMap.put(props.getProperty("gtp.load.user.stopperiod"), String.valueOf(loadScene.getStopPeriod()));
            nameMap.put(props.getProperty("gtp.load.user.flighttime"), String.valueOf(loadScene.getFlightTime()));
            nameMap.put(props.getProperty("gtp.load.user.rampup"), String.valueOf(loadScene.getRampUp()));

            Set<String> nameSet = nameMap.keySet();
            Set<String> fileNameSet = new HashSet<String>();

            if (stringPropNodeList.getLength() != nameMap.size()) {
                throw new Exception(userXmlDoc.getDocumentURI() + "中的<stringProp>节点数不正确");
            }

            if (userXmlDoc.getElementsByTagName(props.getProperty("gtp.load.user.proptag")).getLength() == 0) {
                throw new Exception(userXmlDoc.getDocumentURI() + "中的<jmx>节点不存在");
            }

            if (stringPropNodeList.getLength() > 0
                    && !stringPropNodeList.item(0).getParentNode().getNodeName().equals(props.getProperty("gtp.load.user.parenttag"))) {
                throw new Exception(userXmlDoc.getDocumentURI() + "中的<jmx>节点不是<stringProp>节点的父节点");
            }

            for (int i = 0; i < stringPropNodeList.getLength(); i++) {
                Element node = (Element) stringPropNodeList.item(i);
                String name = node.getAttribute("name");
                if (nameSet.contains(node.getAttribute("name"))) {
                    node.setTextContent(nameMap.get(name));
                } else {
                    throw new Exception(userXmlDoc.getDocumentURI() + "中的<stringProp>包含非法的name属性");
                }
                fileNameSet.add(name);
            }
            if (!fileNameSet.containsAll(nameSet)) {
                throw new Exception(userXmlDoc.getDocumentURI() + "中的<stringProp>包含的name属性不全或有重复");
            }

            XmlUtils.reSaveXmlFile(userXmlDoc, userXmlPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getResourcePath(String resourceName) {
        String fileName  = "";
        try {
            URL url = this.getClass().getResource("/" + resourceName);
            fileName = URLDecoder.decode(url.getFile(), "UTF-8");
//        return this.getClass().getResource("/" + resourceName).getFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileName;
    }

    private void checkNodeName(Element node, String name) {
        try {
            String nameAttr = node.getAttribute("name");
            if (!nameAttr.equals(name)) {
                throw new Exception(String.format("<%s name = '%s'>和将要插入的值属性不一致", node.getTagName(), nameAttr));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] a) throws Exception {
//        File pom = new File("D:\\svn\\SVNCode\\Doraemon\\GTP\\Trunk\\gtp-domain\\src\\main\\resources\\config-api.xml");
//        System.out.println(pom.exists());
//        System.out.println(pom.length());
//        System.out.println(XmlUtils.getXMLNode(pom,"/project/canRoam").getLength());
//        XmlUtils.getXMLNode(pom,"/project/canRoam").item(0).setTextContent("true");
//        XmlUtils.modifySingleNodeContent("D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\pom.xml","environment",0,"UAT");

//        File fromFile = new File("D:\\svn\\SVNCode\\Doraemon\\GTP\\Trunk\\gtp-domain\\src\\main\\resources\\config-api.xml");
//        File toFile = new File("D:\\svn\\SVNCode\\Doraemon\\GTP\\Trunk\\gtp-domain\\src\\main\\resources\\config-api-copy.xml");
//        FileUtils.copyFile(fromFile, toFile, true);

//        ConfBo confBo = new ConfBo();
//        System.out.println(confBo.getClass().getResource("/args-tpl.xml").getPath());


//        String argsXmlPath = "D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\args.xml";
//
//        List<String> argsScriptSvnList = new ArrayList<String>();
//        argsScriptSvnList.add("svn url 1");
//        argsScriptSvnList.add("svn url 2");
//        argsScriptSvnList.add("svn url 3");
//
//        Document xmldoc = XmlUtils.parseXmlFile2Doc(argsXmlPath);
//        System.out.println(xmldoc.getElementsByTagName("Params").getLength());
//
//        Node parentNode = xmldoc.getElementsByTagName("Params").item(0);
//
//        for (String scriptSvn : argsScriptSvnList) {
//            Node svnNode = xmldoc.createElement("svnPath");
//            svnNode.setTextContent(scriptSvn);
//            Element newScriptNode = xmldoc.createElement("script");
//            newScriptNode.appendChild(svnNode);
////            XmlUtils.appendChildNode(xmldoc, argsXmlPath, "Params",0,newScriptNode);
//            parentNode.appendChild(newScriptNode);
//        }
//        XmlUtils.reSaveXmlFile(xmldoc, argsXmlPath);


//        String newContent = "svnNode.setTextContent(scriptSvn);\n" +
//                "//           ;";
//        File toFile = new File("D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\args.xml");
//        FileUtils.appendContent2File(toFile, newContent);


//        LoadSceneDao loadSceneDao = Application.getBean(LoadSceneDao.class);
//        ConfBo confBo = new ConfBo();
//        Properties properties = new Properties();
//        properties.load(confBo.getClass().getResourceAsStream("/load.properties"));
//
//        Document userXmlDoc = XmlUtils.parseXmlFile2Doc("D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\user.xml");
//
//        LoadScene loadScene = loadSceneDao.get(3);
//
//        NodeList jmxNodeList = userXmlDoc.getElementsByTagName("stringProp");
//        System.out.println("1: " + jmxNodeList.getLength());
//        System.out.println("2: " + jmxNodeList.item(0).getTextContent());
//        System.out.println("3: " + jmxNodeList.item(0).getLocalName());
//        System.out.println("4: " + jmxNodeList.item(0).getNodeName());
//        System.out.println("5: " + jmxNodeList.item(0).getChildNodes().item(0).getNodeName());
//        System.out.println("5: " + jmxNodeList.item(0).getParentNode().getNodeName());
//
//        jmxNodeList.item(0).setTextContent("999");
//
//        XmlUtils.reSaveXmlFile(userXmlDoc, "D:\\svn\\SVNCode\\NewLoadTest\\orderGroup\\ExecuteTest\\user.xml");


//        confBo.insertLoadScene2UserXml(properties,loadScene,userXmlDoc);

//        ConfBo confBo = new ConfBo();
//        System.out.println(confBo.getClass().getResource("/user-tpl.xml").getPath());


        File file = new File("D:/maven/localRepository/com/gome/test/gtp/gtp-maven-plugin/3.0.0/gtp-maven-plugin-3.0.0.jar/args-tpl.xml");
        System.out.println(file.exists());
        System.out.println(file.length());
    }
}
