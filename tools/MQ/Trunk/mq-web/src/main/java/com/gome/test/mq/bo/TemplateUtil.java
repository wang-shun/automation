package com.gome.test.mq.bo;


import com.gome.test.mq.dao.LogForDBDao;
import com.gome.test.mq.model.DataType;
import com.gome.test.mq.model.MQLogInfo;
import com.gome.test.mq.model.Template;
import com.gome.test.utils.StringUtils;
import com.gome.test.utils.XmlUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.io.*;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;





/**
 * Created by zhangjiadi on 15/12/24.
 */
@Component
public class TemplateUtil {

    public static HashMap<String,List<String>> templateMap;

    static {
        templateMap =new HashMap<>();


    }


    public  String convertToXmlString(ArrayList<String> paths,ArrayList<String> dtypes,ArrayList<String> values) throws Exception
    {

        Document document=getDocument();
        Element element=null;
        for(int i=0;i< paths.size();i++)
        {

            String dtype=dtypes.get(i);
            String value=values.get(i);
            String parth=paths.get(i);

            element = getValueElement(document,parth);

            DataType dataType=DataType.getValueOf(dtype);
            value =conventValue(dataType,value);

            element.setText(value);

        }
        System.out.println(document.asXML());

        return XmlFormatUtil.format(document.asXML());
    }


    private String conventValue(DataType dataType,String value) throws Exception{
        String result = value;


        switch (dataType) {
            case DataType_Date:
                result =conventDate (value,DataType.DataType_Date.getValue());
                break;
            case DataType_Date2:
                result =conventDate (value,DataType.DataType_Date2.getValue());
                break;

            case DataType_Int:
                result = Integer.valueOf(value).toString();
                break;

            case DataType_String:
                result = value.trim();
                break;

            case DataType_Double:
                result = Double.valueOf(value).toString();
                break;

            default:
                break;
        }

        return result;
    }

    private String conventDate(String value,String fmtStr) throws Exception
    {
        try{
            DateFormat fmt = new SimpleDateFormat(fmtStr);
            Date date = fmt.parse(value);
            value =date.toString();
        }catch (Exception ex)
        {
//                throw new Exception("日期转换发生异常！value："+ value);

        }
        return value;
    }



    public Element getValueElement(Document document,String path)
    {

        String[] pathList=path.split("/");

        Element element = null;

        for(int i=0;i<pathList.length;i++)
        {
            if(i==0) {
                element = getRootElement(document, pathList[0]);
                continue;
            }
            element= getElement(element,pathList[i]);
        }

        return  element;
    }


    public Element getElement(Element element, String name)
    {
        Element result=null;
        if(element.elements().size()==0)
        {
            element.addElement(name);
            result = getElement(element,name);
        }else {

            for (int i = 0; i < element.elements().size(); i++) {
                result = ((Element) element.elements().get(i));
                if (result.getName().equals(name))
                    return result;//有匹配上的直接返回

            }

            //没有匹配上的 添加子节点
            element.addElement(name);
            result = getElement(element, name);

        }
        return result;
    }

    /*
    * 获得根节点 如果根节点与传入name不一致 则返回null
    * */
    public Element getRootElement(Document document,String name)
    {
        if(document.getRootElement()==null)
          document.addElement(name);

        if(document.getRootElement().getName().equals(name))
            return document.getRootElement();
        return null;
    }





    public Document getDocument()
    {
        Document document=null;
        try {
            File xmlTemplate=getXMLTemplateFile();
            InputStream stream=  (InputStream)(new FileInputStream(xmlTemplate));
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(stream);
            document.remove(document.getRootElement());
            stream.close();

        }catch (Exception ex)
        {

        }

        return document;
    }



    private String repConfigFile(File file) throws Exception {

        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        InputStreamReader isr = new InputStreamReader(bufferedInputStream, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder resultCfgStr = new StringBuilder("");
        while ((line = br.readLine()) != null) {
            resultCfgStr.append(line);
        }
        br.close();
        isr.close();
        System.out.println(resultCfgStr.toString());
        return resultCfgStr.toString();
    }

    /*
    * 获取某个tree下面的模板名称（仅仅取出模板名称不带有编号和xml标志）
    * */
    public List<String> getTemplateNameByTreeid(String treeid)
    {
        List<String> result=new ArrayList<>();
        File templates=getTemplatesFile();

        if(templates!=null && templates.isDirectory())
        {

            for(File file: templates.listFiles())
            {
                String filName=file.getName();
                String p_str = String.format("_%s.xml$", treeid);

                Pattern p= Pattern.compile(p_str);
                Matcher matcher = p.matcher(filName);


                if(matcher.find())
                    result.add(file.getName().trim().replace(matcher.group(0),""));
            }
        }
        return  result;
    }

    public List<String> getAllTemplate()
    {
        List<String> result=new ArrayList<>();
        File templates=getTemplatesFile();

        if(templates!=null && templates.isDirectory())
        {
            for(File file: templates.listFiles())
            {
                if(file.getName().indexOf(".svn")==-1)
                    result.add(file.getName());
            }
        }
        return  result;
    }

    public List<String[]> getMQQtemplate(String pid)
    {
        List<String[]> list=new ArrayList<>();
        String p_str = String.format("^.*_%s.xml$", pid);
        File templates=getTemplatesFile();
        if(templates!=null && templates.isDirectory())
        {
            for(File file: templates.listFiles())
            {
                String filName=file.getName();
                Pattern p= Pattern.compile(p_str);
                Matcher matcher = p.matcher(filName);
                if(matcher.matches())
                {
                   String filename=  file.getName().trim().replace(String.format("_%s.xml",pid),"");
                    list.add(getMQQTemplate(file,filename));
                }
            }
        }
        return list;
    }

    public String[] getMQQTemplate(File tmpFile,String filename){
        String[] result = new String[4];
        try {
            if (tmpFile != null && tmpFile.isFile()) {
                String xmlText = repConfigFile(tmpFile);
                if (!StringUtils.isEmpty(xmlText)) {
                    NodeList nodeList = XmlUtils.getXMLNode(xmlText, "//qnode");

                    if(nodeList!=null && nodeList.getLength() >0 )
                    {
                        Node node=nodeList.item(0);

                        Node qname=  node.getAttributes().getNamedItem("qname");
                        Node qtype= node.getAttributes().getNamedItem("qtype");
                        result[0]=filename;
                        result[1]=getNodevalue(qname);
                        result[2]= getNodevalue(qtype);
                    }
                }

            }
        } catch (Exception ex) {

            LogUtil.mqErrorLoger.info("获取模板失败！模板名称:"+tmpFile.getName()+" 异常原因："+ex.getMessage());
        }

        return result;

    }



//
//
//    public List<Template> getMQTemplate(String templateName,String pid) {
//        List<Template> result = new ArrayList<>();
//        try {
//            File tmpFile = getTemplateFile(templateName, pid);
//            if (tmpFile != null && tmpFile.isFile()) {
//                String xmlText = repConfigFile(tmpFile);
//                if (!StringUtils.isEmpty(xmlText)) {
//                    NodeList nodeList = XmlUtils.getXMLNode(xmlText, "//node");
//
//
//                    for (int i = 0; i < nodeList.getLength(); i++) {
//                        result.add(getTemplate(nodeList.item(i)));
//                    }
//                }
//
//            }
//        } catch (Exception ex) {
//
//            LogUtil.mqErrorLoger.info("获取模板失败！模板名称:"+templateName+" pid:"+pid +" 异常原因："+ex.getMessage());
//        }
//
//        return result;
//
//    }

    @Autowired
    LogForDBDao logForDBDao;

    private void setTemplateValue(String logId,Template template) throws  Exception
    {
        MQLogInfo logInfo=null;
        try {
            logInfo = logForDBDao.getLogById(Integer.valueOf(logId));
            NodeList nodeList = XmlUtils.getXMLNode(logInfo.getMq_message(),String.format("/%s",template.getPath()));
            if(nodeList != null && nodeList.getLength() >0)
            {
                if(nodeList.item(0).getFirstChild()!=null)
                    template.setValue(nodeList.item(0).getFirstChild().getNodeValue());
            }

        }catch (Exception ex)
        {
            if(logInfo==null)
                throw new Exception(String.format("获取日志失败！日志id：%s",logId));
            else
                throw new Exception(String.format("转换日志失败！path：%s",template.getPath()));
        }
    }

    public List<Template> getMQTemplate(String templateName,String pid,String logId) {
        List<Template> result = new ArrayList<>();
        try {
            File tmpFile = getTemplateFile(templateName, pid);
            if (tmpFile != null && tmpFile.isFile()) {
                String xmlText = repConfigFile(tmpFile);
                if (!StringUtils.isEmpty(xmlText)) {
                    NodeList nodeList = XmlUtils.getXMLNode(xmlText, "//node");
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Template template=getTemplate(nodeList.item(i));
                        if(!StringUtils.isEmpty(logId))
                            setTemplateValue(logId,template);
                        if(StringUtils.isEmpty(template.getValue()) && (DataType.getValueOf(template.getDtype()) == DataType.DataType_Date || DataType.getValueOf(template.getDtype()) == DataType.DataType_Date2)) {
                            template.setValue(new SimpleDateFormat(DataType.DataType_Date.getValue()).format(new Date()).replace("0800","08:00"));
                        }

                        result.add(template);
                    }
                }

            }
        } catch (Exception ex) {

            LogUtil.mqErrorLoger.info("获取模板失败！模板名称:"+templateName+" pid:"+pid +" 异常原因："+ex.getMessage());
        }

        return result;

    }

    private String getNodevalue(Node node)
    {
        if(node==null)
            return "";

        return node.getNodeValue();
    }


    private Template getTemplate(Node node)
    {
        Template template=new Template();
        if(node.hasAttributes())
        {
            Node pathNode=  node.getAttributes().getNamedItem("path");
            Node nameNode =node.getAttributes().getNamedItem("name");
            Node isemptyNode= node.getAttributes().getNamedItem("isempty");
            Node dtypeNode= node.getAttributes().getNamedItem("dtype");
            Node ismustNode= node.getAttributes().getNamedItem("ismust");
            Node decNode= node.getAttributes().getNamedItem("dec");
            Node remarkNode= node.getAttributes().getNamedItem("remark");
            Node valueNode= node.getAttributes().getNamedItem("value");

            template.setDec(getNodevalue(decNode));
            template.setDtype(getNodevalue(dtypeNode));
            template.setIsempty(getNodevalue(isemptyNode).equals("1"));
            template.setIsmust(getNodevalue(ismustNode).equals("1"));
            template.setName(getNodevalue(nameNode));
            template.setPath(getNodevalue(pathNode));
            template.setRemark(getNodevalue(remarkNode));
            template.setValue(getNodevalue(valueNode));
        }
        return  template;

    }

    private File getTemplateFile(String pid){

        String p_str = String.format("^.*__%s.xml$", pid);
        File templates=getTemplatesFile();
        if(templates!=null && templates.isDirectory())
        {
            for(File file: templates.listFiles())
            {
                String filName=file.getName();
                if(matcher(p_str,filName))
                {
                    return  file;
                }
            }
        }
        return null;
    }

    private File getTemplateFile(String templateName,String pid){

        String p_str = String.format("^%s_%s.xml$", templateName.replace("(","\\(").replace(")","\\)"), pid);
        File templates=getTemplatesFile();
        if(templates!=null && templates.isDirectory())
        {
            for(File file: templates.listFiles())
            {
                String filName=file.getName();
                if(matcher(p_str,filName))
                {
                    return  file;
                }
            }
        }
        return null;
    }

    /*
    * 匹配文件夹
    * */
    private Boolean matcher(String Pattern_str,String value)
    {
        Pattern p= Pattern.compile(Pattern_str);
        Matcher matcher = p.matcher(value);
        return matcher.matches();
    }




    //得到resource/templates路径文件夹
    private File getTemplatesFile()
    {
        File fileResources=new File(this.getClass().getClassLoader().getResource("").getPath()) ;
        if(fileResources.isDirectory())
        {
            File [] files=fileResources.listFiles();
            for(int i=0;i< files.length;i++)
            {
                if(files[i].getName().equals("templates"))
                    return files[i] ;
            }
        }
        return null;
    }


    private File getXMLTemplateFile()
    {
        File fileResources=new File(this.getClass().getClassLoader().getResource("").getPath()) ;
        if(fileResources.isDirectory())
        {
            File [] files=fileResources.listFiles();
            for(int i=0;i< files.length;i++)
            {
                if(files[i].getName().equals("xml-templates.xml"))
                    return files[i] ;
            }
        }
        return null;
    }




    public static void main(String args[]) throws Exception {
//

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZ");

        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String snow = sdf.format(new Date());  // 2009-11-19 14:12:23


        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZ");
        Date date = Calendar.getInstance().getTime();
        TimeZone srcTimeZone = TimeZone.getTimeZone("EST");
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");

        Long targetTime = date.getTime() - srcTimeZone.getRawOffset() + destTimeZone.getRawOffset();
        String reslut= formatter.format(new Date());








        TemplateUtil util=new TemplateUtil();
        File xmlTemplate=new File("/Users/zhangjiadi/Documents/GOME/Doraemon/MQ/Trunk/mq-web/src/main/resources/xml-templates.xml");
        InputStream stream=  (InputStream)(new FileInputStream(xmlTemplate));
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(stream);
        document.remove(document.getRootElement());
        stream.close();



        Element element=util.getValueElement(document, "track_n_trace/status/status_code");

        System.out.println(document.asXML());


    }
}
