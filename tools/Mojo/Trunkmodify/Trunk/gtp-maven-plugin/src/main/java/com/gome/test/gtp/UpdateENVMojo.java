package com.gome.test.gtp;

import com.gome.test.gtp.utils.RuntimeENV;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weijianxing on 2016/7/20.
 */
/**
* @Mojo(name = "gtp")
* @goal updateENV
* @requiresDependencyResolution compile+runtime
* @requiresProject false
* jenkins slave 执行的第一个步骤。更新Task状态
* 示例 mvn gtp:updateENV   -DtaskListId=1
*/

@SpringApplicationConfiguration(classes = Application.class)
public class UpdateENVMojo extends AbstractMojo {
    private static final String SERVICE_EXT = "../HelperExtension/pom.xml";

    /**
     * @parameter
     * expression="${taskListId}"
     */
    private int taskListId;
    //-DENVuri=/#EnvURI#/ -DENVversion=/#EnvVersion#/
    /**
     * @parameter
     * expression="${ENVuri}"
     */
    private String ENVuri;


    private String baseCodeDir;


    /**
     * @parameter
     * expression="${ENVversion}"
     */
    private String ENVversion;

    /**
     * get env properties by taskid.
     * @return
     */
    private Map<String , String> getENVProperties(){
        Map<String , String > properties = new HashMap<String, String>();
        //todo get env map by taskListID.

//        <zk.ip>10.58.50.203:2181,10.58.50.204:2181,10.58.50.205:2181</zk.ip>
//        properties.put("newVersion" , "2.0.0-SNAPSHOT");
//        properties.put("newVersion" , "1.0.0-SNAPSHOT");
//        properties.put("zk.ip" , "10.58.50.203:2181,10.58.50.204:2181,10.58.50.205:2181-test");
//        properties.put("service.aid" , "bluecoupon-client-test");
        //<wsdl>http://popproxy.bbcuat.com/ws/ATG?wsdl</wsdl>
//        properties.put("wsdl" , "http://popproxy.bbcuat.com/ws/ATG?wsdl/test");
            //parse ENV from maven plugin parameter.
        getLog().info("Get env uri string is : " + ENVuri);
        getLog().info("Get env version string is : " + ENVversion);
        properties = this.parseStringn2Map(ENVuri , ENVversion);
        return properties;
    }
    private RuntimeENV getENV(int jobID){
        //TODO
        RuntimeENV env = RuntimeENV.API_DUBBO;
        //Get env from DB. or ... ...
        return env;
    }
// the parameter can define as json string.
//    {
//        "versions": [
//        {
//            "serviceA": "1.0"
//        },
//        {
//            "serviceB": "1.2"
//        }
//        ],
//        "ZKString": "10.58.50.203:2181,10.58.50.204:2181,10.58.50.205:2181"
//    }

//    {"versions": “gui-maven:1.0.6,gui-maven:1.0.6,gui-maven:1.0.6”,"URIString": "10.58.50.203:2181,10.58.50.204:2181,10.58.50.205:2181"}


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("---------------execute update env.------------------------------");
        //todo
        baseCodeDir = "pom.xml";
        getLog().debug("========== base code dir is : > " + baseCodeDir);
        getLog().info("========== task list id is : > " + taskListId);
        RuntimeENV env = this.getENV(taskListId);
        if(env ==RuntimeENV.API_DUBBO || env == RuntimeENV.API_WEBSERVICE ) {
            updateFile(baseCodeDir);
        }else{
            getLog().info(env + " Type API envless");
        }
    }

    private File getPomXML(String dir){
        //TODO get pomFile.
        return null;
    }

    /**
     * using dom parse pom.xml
     * @param filePath
     * @return
     */
    private Document getPomDom(String filePath){
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            //DOM parser instance
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            //parse an XML file into a DOM tree
            File pomFile = new File(filePath);
            if(!pomFile.exists() && !pomFile.isFile()){
                getLog().error("File -> " +filePath + " is not exist.");
                return null;
            }
            document = builder.parse(pomFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * update pom properties from map by key.
     * @param map
     * @param parent
     * @return
     */
    private boolean updateElements(Map<String , String> map ,NodeList parent ){

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            NodeList children = parent.item(0).getChildNodes();
            for(int i = 0 ;i<children.getLength();i++) {
                Node nodeAttr = children.item(i);
                if (nodeAttr != null && nodeAttr.getNodeName().equals(key)) {
                    nodeAttr.setTextContent(value);
                    getLog().info(key +" has modified to " + value);
                }
            }
        }
        return true;
    }

    /**
     * write file
     * @param document
     * @param filePath
     */
    private void writeBackPom(Document document , String filePath){
        // write the DOM object to the file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(filePath));
        try {
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    public boolean updateFile(String filePath){
        //TODO update file the properties value where define at map.
        Map<String , String> data = getENVProperties();
        Document document = getPomDom(filePath);
        Document wsdlDom = getWebServiceDocument();
        if(wsdlDom!=null){
            getLog().info("---------------update webservice pom.----------------------");
            //update key .
            data = updateMapByENV(data , "webservice");
            updateWebServiceDocument(wsdlDom, data);
            writeBackPom(wsdlDom, SERVICE_EXT);
        }
        NodeList props = document.getElementsByTagName("properties");
        //update dubbo match pom key.
        data = updateMapByENV(data , "dubbo");
        updateElements(data, props);
        writeBackPom(document, filePath);
        getLog().info("update pom configuration.");
        return true;
    }

    /**
     * If API sourcecode exist SERVICE_EXT dir then update webservice configuration.
     * @return
     */
    private Document getWebServiceDocument() {
        File pomFile = new File(SERVICE_EXT);
        if (!pomFile.exists() && !pomFile.isFile()) {
            getLog().info("***********API is not webservice.****************");
            return null;
        } else {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            Document document = null;
            DocumentBuilder builder = null;
            try {
                builder = builderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            try {
                document = builder.parse(pomFile);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return document;
        }

    }

    public Map<String , String> parseJson2Map(String json){
//        {"versions": “gui-maven:1.0.6,gui-maven:1.0.6,gui-maven:1.0.6”,
//            "URIString": "10.58.50.203:2181,10.58.50.204:2181,10.58.50.205:2181"}
        getLog().info("parseStringn2Map  string is : " + json);
        Map<String,String> map = new HashMap<String , String>();
        JSONObject jsonObject = JSONObject.fromObject(json);
        Map<String, Object> jsonMap = (Map<String, Object>) JSONObject.toBean(jsonObject, Map.class);
        Object objVersion = jsonMap.get("version");
        Object objURI = jsonMap.get("URIString");
        if(objVersion !=null){
            String srcV = (String)objVersion;

            String[] vset = srcV.split(",");
            for(String version : vset){
                String [] vstr = version.split(":");
                if(vset.length != 2){
                    getLog().error("env string error.");
                }else{
                    getLog().info(" get evn parameter version info : key is " + vstr[0] + " ; value is : " + vstr[1] );
                    map.put(vstr[0] , vstr[1]);
                }
            }
        }
        if(objURI != null){
            map.put("URIString" , (String)(objURI));
        }

        return  map;
    }

    public Map<String , String> parseStringn2Map(String strURI , String srcV){
//{URIString:127.21.25.22,version:userBase-facade:1.0.0UAT-SNAPSHOT,userBase-common:1.0.0UAT-SNAPSHOT}
//        getLog().info("parseString2Map  string is : " + str);
        Map<String,String> map = new HashMap<String , String>();
//        String versionF = "version";
//        String URLStringF = "URIString";
//        int iURIString = str.indexOf(URLStringF)+URLStringF.length()+2;
//        int iVersion= str.indexOf(versionF)+ versionF.length()+1;
//
//        String strURI = str.substring( iURIString , (str.indexOf(versionF) -2));
//        String  srcV= str.substring(iVersion, str.length()-1);
//        getLog().info("src url " + strURI);
//        getLog().info("src version " + srcV);
        if(srcV !=null){
            String[] vset = srcV.split(",");
            for(String version : vset){
                String [] vstr = version.split(":");
                if(vset.length != 2){
                    getLog().error("env string error.");
                }else{
                    getLog().info(" get evn parameter version info : key is " + vstr[0] + " ; value is : " + vstr[1] );
                    map.put(vstr[0] , vstr[1]);
                }
            }
        }
        if(strURI != null){
            map.put("URIString" , strURI);
        }

        return  map;
    }

    /**
     * base on the env to modify map key.
     * @param map
     * @param env webservice , dubbo
     * @return
     */
    public Map<String ,String> updateMapByENV(Map<String , String> map , String env){
        String value = map.get("URIString");
        if(value!=null && !value.isEmpty()){
            if(env.equals("webservice")){
                map.put("wsdl" , value);
            }else if(env.equals("dubbo")){
                map.put("zk.ip",value);
            }else{}
            map.remove("URIString");
        }

        return  map;
    }

    /**
     * update wsdl configuration pom.
     * @param document
     * @param map
     */
    private  void updateWebServiceDocument(Document document , Map<String , String> map){
       NodeList build = document.getElementsByTagName("build").item(0).getChildNodes();
        for(int i = 0;i<build.getLength();i++){
            Node plugins = build.item(i);
            if(plugins.getNodeName().equals("plugins")){
                getLog().info("find node plugins");
                NodeList plugin = plugins.getChildNodes();
                for(int j= 0; j<plugin.getLength();j++){
                    if(plugin.item(j).getNodeName().equals("plugin")){
                        getLog().debug("find node plugin");
                        NodeList executions = plugin.item(j).getChildNodes();
                        for(int iexecutions = 0;iexecutions<executions.getLength();iexecutions++){
                            if(executions.item(iexecutions).getNodeName().equals("executions")){
                                getLog().debug("find node executions");
                                NodeList execution = executions.item(iexecutions).getChildNodes();
                                for(int iexecution = 0;iexecution<execution.getLength();iexecution++){
                                    if(execution.item(iexecution).getNodeName().equals("execution")){
                                        getLog().debug("find node execution");
                                        NodeList configuration = execution.item(iexecution).getChildNodes();
                                        for(int iconfiguration = 0 ; iconfiguration<configuration.getLength();iconfiguration++){
                                            if(configuration.item(iconfiguration).getNodeName().equals("configuration")){
                                                getLog().debug("find node configuration");
                                                NodeList wsdlOptions = configuration.item(iconfiguration).getChildNodes();
                                                for(int iwsdlOptions = 0;iwsdlOptions<wsdlOptions.getLength();iwsdlOptions++){
                                                    if(wsdlOptions.item(iwsdlOptions).getNodeName().equals("wsdlOptions")){
                                                        getLog().debug("find node wsdlOptions");
                                                        NodeList wsdlOption = wsdlOptions.item(iwsdlOptions).getChildNodes();
                                                        for(int iwsdlOption = 0 ; iwsdlOption<wsdlOption.getLength();iwsdlOption++){
                                                            if(wsdlOption.item(iwsdlOption).getNodeName().equals("wsdlOption")){
                                                                getLog().debug("find node wsdlOption");
                                                                NodeList wsdl = wsdlOption.item(iwsdlOption).getChildNodes();
                                                                for(Map.Entry<String,String> entry : map.entrySet()){
                                                                    String key = entry.getKey();
                                                                    String value = entry.getValue();
                                                                    getLog().info("map key is : " + key);
                                                                    getLog().info("map value is : " + value);
                                                                    for(int iwsdl = 0 ;iwsdl<wsdl.getLength();iwsdl++) {
                                                                        Node nodeAttr = wsdl.item(iwsdl);
                                                                        getLog().debug("node name is : " + nodeAttr.getNodeName());
                                                                        if (nodeAttr != null && nodeAttr.getNodeName().equals("wsdl")) {
                                                                            nodeAttr.setTextContent(value);
                                                                            getLog().info(key +" has modified to " + value);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
