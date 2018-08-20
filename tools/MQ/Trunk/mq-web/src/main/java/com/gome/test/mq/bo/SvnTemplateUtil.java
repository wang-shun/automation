package com.gome.test.mq.bo;

import com.gome.test.utils.SvnUtils;
import com.gome.test.utils.XmlUtils;
import org.dom4j.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.reflections.util.Utils;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangjiadi on 16/1/7.
 */
@Component
public class SvnTemplateUtil {

    private static String svnUrl="https://repo.ds.gome.com.cn:8443/svn/test/SourceCode/gtp-mock-mq/template";
    private static String svnTemplate="svnTemplate";
    private static String templates="templates.xml";
    private static String treeid="treeid";
    private static String sort="sort";
    private static  String  CONFIGSystemFILE="config-system.xml";

    static {
        SvnTemplateUtil.createTemplate();

    }

    public static HashMap<String,List<String>> readTemplate(String templatesPath)
    {
        HashMap<String,List<String>> result=new HashMap();
        File file=new File(templatesPath+File.separator+templates);
        if(file.isFile())//如果templates.xml存在
        {
            try {
                String xmlText =  XMLUitl.repConfigFile(file);
                if(!Utils.isEmpty(xmlText))
                {
                    NodeList nodeList = XmlUtils.getXMLNode(xmlText, "//treenode");
                    if(nodeList!=null )
                    {
                       for(int i=0;i<nodeList.getLength();i++)
                       {
                             result.putAll(getTemplates(nodeList.item(i)));
                       }
                    }
                }

            }catch (Exception ex)
            {
                LogUtil.mqErrorLoger.error("读取svn模板失败");
            }
        }
        return result;
    }

    public static HashMap<String,List<String>> getTemplates(Node treeNode)
    {
        HashMap<String,List<String>> result=new HashMap<>();
        List<String> kes=new ArrayList<>();
        for(int i=0;i< treeNode.getChildNodes().getLength();i++)
        {
            Node node=treeNode.getChildNodes().item(i);
            if(node.getNodeName().equals(treeid))
            {
               kes=getChildValue(node);
            }else
            {
                List<String> values=getChildValue(node);
                for(String key:kes)
                {
                    result.put(key,values);
                }
            }

        }
        return result;

    }

    public static List<String> getChildValue(Node pnode)
    {
        List<String> result=new ArrayList<>();
        for(int i=0;i<pnode.getChildNodes().getLength();i++)
        {
            Node node=pnode.getChildNodes().item(i);
            if(node.hasChildNodes()) {
                String value =node.getFirstChild().getNodeValue();
                if (!result.contains(value)) {
                    result.add(value);
                }
            }
        }
        return result;
    }


    public static void createTemplate() {
        File templates = getTemplatesFile();

        //删除已经下载的svn文件
        if (templates != null && templates.isDirectory()) {
            for (File file : templates.listFiles()) {
                if (file.getName().equals(svnTemplate)) {
                    deleteFile(file);
                }
				if(file.getName().indexOf(".xml")==file.getName().length()-4)
                    deleteFile(file);
            }
        }

        String filePath = templates.getPath() + File.separator + svnTemplate;
        //创建svn目录文件
        File svnTemplateFile = new File(filePath);
        if (svnTemplateFile.isFile())
            svnTemplateFile.mkdir();
        //svn更新
        svnUp(filePath);
        //得到配置的模板hash
        HashMap<String, List<String>> templateHash = readTemplate(filePath);
        //根据配置的模板，生成配置文件
        //生成排序文件
        updateConfigFile(templateHash,templates);
        //生成具体模板.xml
        updateTemplate(templateHash,templates);
        //删除SVNTemplate

        if (svnTemplateFile.isFile()&& svnTemplateFile.exists())
            deleteFile(svnTemplateFile);


    }

    private static void updateTemplate(HashMap<String, List<String>> templateHash,File templates) {
        for(String key : templateHash.keySet())
        {
            List<String> tems=templateHash.get(key);
            for(String tem :tems) {
                try {
                    String fileStr = XMLUitl.repConfigFile(getFile(templates.getPath() + File.separator + svnTemplate+ File.separator + tem +".xml"));
                    File file =new File(templates.getPath()+File.separator + tem +"_"+ key +".xml");
                    writeFile(fileStr,file);
                }catch (Exception ex)
                {

                }
            }

        }



    }

    private static File getFile(String filePath)
    {
        File file=new File(filePath);
        if(file.isFile() && file.exists())
            return file;

        return null;
    }



    private static void updateConfigFile(HashMap<String, List<String>> templateHash,File templates) {

        File configFile = new File(templates.getParent() + File.separator + CONFIGSystemFILE);
        try {
            if (configFile.isFile() && configFile.exists()) {
                InputStream stream = (InputStream) (new FileInputStream(configFile));
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(stream);
                for (int i = 0; i < document.getRootElement().elements().size(); i++) {

                    if (((Element) document.getRootElement().elements().get(i)).attribute("value").getValue().equals("sortTemplate")) {
                        String treeid = ((Element) document.getRootElement().elements().get(i)).attribute("treeid").getValue();
                        List<String> temps = templateHash.get(treeid);
                        if (temps != null) {
                            Element element = ((Element) document.getRootElement().elements().get(i));
                            while (element.elements().size() > 0) {
                                element.remove((Element) element.elements().get(0));
                            }
                            for (String tem : temps) {
                                element.addElement("value").addText(tem);
                            }
                        }
                    }
                }

                String xmlText=document.asXML().replaceAll("\r|\n", "").replaceAll("(?<=>)\\s+(?=<)", "");
                writeFile(xmlText, configFile);
            }

        } catch (Exception ex) {

        }


    }

    private static void writeFile(String xmlText,File resultFile)   {

        try {
            String path=resultFile.getPath();
            if (resultFile.isFile() && resultFile.exists())
                resultFile.delete();

            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
                bw.write(xmlText);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(bw !=null) {
                    bw.flush();
                    bw.close();
                }
            }

        } catch (Exception ex) {


        }

    }

    public static void main(String args[]) throws Exception {
        SvnTemplateUtil svnTemplateUtil=new SvnTemplateUtil();
        svnTemplateUtil.createTemplate();
    }


    private static void svnUp(String svnCheckoutDIR)
    {
        try {
            SvnUtils svnUtil = com.gome.test.utils.SvnUtils.newInstance(svnUrl, false);
            String svnUser = svnUtil.getStoreUserName();
            String svnPassword = svnUtil.getStorePassword();

            //判断该SVN地址是否存在，如果不存在， 则跳过
            if (svnUtil.isURLExist(svnUtil.getSvnUrl(), svnUser, svnPassword)) {

                SvnUtils.checkout(svnUtil.getSvnUrl(), svnCheckoutDIR, svnUser, svnPassword);
            }

        }catch (Exception ex)
        {
            LogUtil.mqMessageLoger.error("svn checkOut error:"+ex.getMessage());
        }
    }

    private static void deleteFile(File file)
    {
        if(file.isDirectory())
        {
            for(File childFile: file.listFiles())
            {
                if(childFile.isFile() && !childFile.isDirectory())
                    childFile.delete();
                else deleteFile(childFile);
            }
        }else
            file.delete();
    }



    //得到resource/templates路径文件夹
    private static File getTemplatesFile()
    {
        File fileResources=new File(SvnTemplateUtil.class.getClassLoader().getResource("").getPath()) ;
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


}
