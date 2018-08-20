package com.gome.test.mq.bo;


import com.fasterxml.jackson.databind.node.TextNode;
import com.gome.test.mq.model.MQUser;
import com.gome.test.mq.model.TreeNode;
import com.gome.test.utils.XmlUtils;
import com.ibm.mq.pcf.CMQC;
import org.dom4j.Element;
import org.reflections.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangjiadi on 15/12/16.
 */
@Component
public class LeftTreeService {

    private final  String GETQNAME="getQName";
    private final  String   LOCALQNAME="getLocalQName";
    private final  String   REMOTEQNAME="getRemoteQName";
    private final  String  TREEFILE="TreeNode.xml";
    private final  String  CONFIGSystemFILE="config-system.xml";
    private final  String GETTEMPLATE="getTemplate";
    private final  String GETPAGE ="getPage";

    public static String xmlTreeNode;
    public static String xmlConfig;
    private static HashMap<String,List<String>> cmdQNameHasMap;
    private static HashMap<String,List<String[]>> templateHasMap;
    private static HashMap<String,List<String>> sortTemplateHasMap;
    private static HashMap<String,List<String[]>> mqNameHasMap;

    static {
        cmdQNameHasMap=new HashMap<>();
        templateHasMap=new HashMap<>();
        sortTemplateHasMap=new HashMap<>();
        mqNameHasMap=new HashMap<>();
    }

    public static void clean()
    {
        cmdQNameHasMap=new HashMap<>();
        templateHasMap=new HashMap<>();
        sortTemplateHasMap=new HashMap<>();
        mqNameHasMap=new HashMap<>();
        xmlTreeNode=null;
        xmlConfig = null;
    }




    private String repConfigFile(String fileName) throws Exception {
        InputStream fis = this.getClass().getResourceAsStream(String.format("/%s", fileName));
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        String resultCfgStr = "";
        while ((line = br.readLine()) != null) {
            resultCfgStr = resultCfgStr + line.trim() ;
        }
        br.close();
        isr.close();
        System.out.println(resultCfgStr);

        return resultCfgStr;
    }



    private List<TreeNode> getTreeNodebyFile(String pid) throws Exception
    {

        String xmlText = getXmlTreeNode();

        NodeList nodeList = XmlUtils.getXMLNode(xmlText, "//node");
        List<TreeNode> list= new ArrayList<TreeNode>();
        for(int i=0;i< nodeList.getLength();i++)
        {
            Node node= nodeList.item(i);
            String Pid=node.getAttributes().getNamedItem("pid").getNodeValue();
            if(Pid.equals(pid)) {
                String id= node.getAttributes().getNamedItem("id").getNodeValue();
                String label=node.getAttributes().getNamedItem("label").getNodeValue();
                String value=node.getAttributes().getNamedItem("value").getNodeValue();
                String type=node.getAttributes().getNamedItem("type").getNodeValue();
                String url=node.getAttributes().getNamedItem("url").getNodeValue();
                String user=node.getAttributes().getNamedItem("user")==null?"":  node.getAttributes().getNamedItem("user").getNodeValue();
                String pwd=node.getAttributes().getNamedItem("pwd")==null?"": node.getAttributes().getNamedItem("pwd").getNodeValue();
                Boolean loadOnDemand=Boolean.valueOf(node.getAttributes().getNamedItem("loadOnDemand").getNodeValue()) ;
                TreeNode info = new TreeNode(id, label, Pid,value,type,url,loadOnDemand);

                info.setUser(user);
                info.setPwd(pwd);
                List<TreeNode> childTree=getChildNode(id, value);
                info.setChildren(childTree);


                list.add(info);
            }
        }

        return  list;
    }




    public List<TreeNode> getChildNode(String _id,String value) throws  Exception{


        List<TreeNode> nodeList = new ArrayList<TreeNode>();

        if (value.equals(GETQNAME))//找到qName
        {
            nodeList = getTreeNode(_id);
        } else if (value.equals(LOCALQNAME)) {
            nodeList = getChildLocalTreeNode(_id);
        } else if (value.equals(REMOTEQNAME)) {
            nodeList = getChildRemoteTreeNode(_id);
        } else if (value.equals(GETTEMPLATE)) {
            nodeList = getChildTemplateTreeNode2(_id);
        } else if (value.equals(GETPAGE)) {
            nodeList = getTreeNodebyFile(_id);
        }

        return nodeList;
    }




    public List<TreeNode> getChildLocalTreeNode(String pid) throws  Exception {
        List<TreeNode> tree = new ArrayList<TreeNode>();
        List<TreeNode> list = getTreeNodebyFile(pid);
        for (TreeNode node : list) {
            String value = node.getValue();
            String[] valueList = value.split("[,]");
            if (valueList.length == 5)//10.126.53.124,1416,C_SVR_GOME02,1208
            {
                // MQUtil util=new MQUtil("10.126.53.124", "QM_TEST3_GOME02","C_SVR_GOME02",1208,1416);
                MQUtil util = new MQUtil(valueList[0], valueList[1], valueList[2], Integer.valueOf(valueList[3]), Integer.valueOf(valueList[4]));

                try {
                    String[] qNames = getNamesBymqNameHash(node.getId(),CMQC.MQQT_LOCAL,util);// util.getQName(CMQC.MQQT_LOCAL);

                    List<String> qNameList = getQName(qNames, node.getId());

                    for (int i = 0; i < qNameList.size(); i++) {
                        tree.add(getTreeList(qNameList.get(i).trim(), node, i, "Local"));
                    }
                } catch (Exception ex) {
                    LogUtil.mqMessageLoger.error(ex.getMessage());
                }

            }

        }
        return tree;
    }

    private String[] getNamesBymqNameHash(String id,int mqType,MQUtil util)
    {
        List<String[]> lists=new ArrayList<>();
        if(mqNameHasMap.containsKey(id))
            lists= mqNameHasMap.get(id);

        if(lists.size()==0)
        {
            String[] local_qNames = util.getQName(CMQC.MQQT_LOCAL);
            String[] remove_qName= util.getQName(CMQC.MQQT_REMOTE);
            lists.add(local_qNames);
            lists.add(remove_qName);

            mqNameHasMap.put(id,lists);
        }


        if(mqType == CMQC.MQQT_LOCAL)
            return lists.get(0);
        else
            return lists.get(1);
    }

    private HashMap<String,List<String>> getCmdQNameHasMap()
    {
        if(cmdQNameHasMap.size()>0)
            return cmdQNameHasMap;

        String xmlString= getXmlConfig();
        try {
            NodeList nodeList = XmlUtils.getXMLNode(xmlString, "//node[@value=\"cmdQName\"]");
            for(int i=0;i< nodeList.getLength();i++)
            {
                Node node=nodeList.item(i);
                if(node.hasAttributes())
                {
                    String key= node.getAttributes().getNamedItem("treeid").getNodeValue();
                    if(!Utils.isEmpty(key)) {
                        List<String> value = new ArrayList<>();
                        for(int index=0;index< node.getChildNodes().getLength();index++)
                        {
                            Node vnode=node.getChildNodes().item(index).getFirstChild();
                            if(vnode!=null)
                                value.add(vnode.getNodeValue());
                        }
                        cmdQNameHasMap.put(key,value);
                    }

                }

            }
        }catch (Exception ex)
        {
            LogUtil.mqErrorLoger.error(ex.getMessage());
        }

        return cmdQNameHasMap;

    }

    private HashMap<String,List<String>> getSortTemplateHasMap()
    {
        if(sortTemplateHasMap.size()>0)
            return sortTemplateHasMap;
        String xmlString= getXmlConfig();
        try {
            NodeList nodeList = XmlUtils.getXMLNode(xmlString, "//node[@value=\"sortTemplate\"]");
            for(int i=0;i< nodeList.getLength();i++)
            {
                Node node=nodeList.item(i);
                if(node.hasAttributes())
                {
                    String key= node.getAttributes().getNamedItem("treeid").getNodeValue();
                    if(!Utils.isEmpty(key)) {
                        List<String> value = new ArrayList<>();
                        for(int index=0;index< node.getChildNodes().getLength();index++)
                        {
                            Node vnode=node.getChildNodes().item(index).getFirstChild();
                            if(vnode!=null)
                                value.add(vnode.getNodeValue());
                        }
                        sortTemplateHasMap.put(key,value);
                    }

                }

            }
        }catch (Exception ex)
        {
            LogUtil.mqErrorLoger.error(ex.getMessage());
        }

        return sortTemplateHasMap;
    }

    private HashMap<String,List<String[]>> getTemplateHasMap()
    {
        if(templateHasMap.size()>0)
            return templateHasMap;

        String xmlString= getXmlTreeNode();
        try {
            NodeList nodeList = XmlUtils.getXMLNode(xmlString, "//node[@value=\"getTemplate\"]");
            for(int i=0;i< nodeList.getLength();i++)
            {
                Node node=nodeList.item(i);
                if(node.hasAttributes())
                {
                    String key= node.getAttributes().getNamedItem("id").getNodeValue();
                    if(!Utils.isEmpty(key)) {
                        List<String[]> value = templateUtil.getMQQtemplate(key);

                        templateHasMap.put(key,value);
                    }

                }

            }
        }catch (Exception ex)
        {
            LogUtil.mqErrorLoger.error(ex.getMessage());
        }

        return templateHasMap;

    }




    private String getXmlTreeNode()
    {
        try {
            if (Utils.isEmpty(xmlTreeNode))
                xmlTreeNode = repConfigFile(TREEFILE);
            return xmlTreeNode;
        }catch (Exception ex)
        {
            LogUtil.mqErrorLoger.error("获取"+TREEFILE+"error： "+ ex.getMessage());
        }
        return "";
    }

    private String getXmlConfig()
    {
        try {
            if (Utils.isEmpty(xmlConfig))
                xmlConfig = repConfigFile(CONFIGSystemFILE);
            return xmlConfig;
        }catch (Exception ex)
        {
            LogUtil.mqErrorLoger.error("获取"+CONFIGSystemFILE+"error： "+ ex.getMessage());
        }
        return "";
    }

    private List<String> getQName(String []qNames,String nodeid)
    {
        List<String> names=new ArrayList<>();
        getCmdQNameHasMap();
        if(cmdQNameHasMap.containsKey(nodeid))
            names= cmdQNameHasMap.get(nodeid);

        for(int i=0;i< qNames.length;i++)
        {
            if(!names.contains(qNames[i].trim()))
                names.add(String.format("%s$",qNames[i]));
        }
        return  names;
    }

    public List<String> getSortTemplates()
    {
        List<String> list = new ArrayList<String>();
        try {
            if(Utils.isEmpty(xmlConfig))
                xmlConfig=repConfigFile(CONFIGSystemFILE);
            String xmlText = xmlConfig;

            NodeList nodeList = XmlUtils.getXMLNode(xmlText, "//node");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String value = node.getAttributes().getNamedItem("value").getNodeValue();
                if (value.equals("sortTemplate")) {
                    NodeList childNodes = node.getChildNodes();
                    for (int t = 0; t < childNodes.getLength(); t++) {
                        if(childNodes.item(t).hasChildNodes()&& childNodes.item(t).getFirstChild().getNodeValue().trim().length()>0)
                            list.add(childNodes.item(t).getFirstChild().getNodeValue());
                    }

                }
            }
        }catch (Exception ex)
        {

        }

        return  list;
    }



    @Autowired
    private TemplateUtil templateUtil;


    private List<String[]> sortTemplate(List<String[]> templateNameList,String pid)
    {
        getSortTemplateHasMap();
        List<String[]> result=new ArrayList<>();
        if(sortTemplateHasMap.containsKey(pid)) {


            List<String> temps =sortTemplateHasMap.get(pid);

            for (String temp : temps) {
                String[] v = getTemplateName(templateNameList, temp);
                if (v != null)
                    result.add(v);
            }

            for (String[] template : templateNameList) {
                if (!result.contains(template))
                    result.add(template);
            }
        }

        return result;

    }


    private String[] getTemplateName(List<String[]> templateNameList,String sortTemplate)
    {
        for (String[] template:templateNameList)
        {
            if(template[0].equals(sortTemplate))
                return template;
        }
        return null;
    }




    public List<TreeNode> getChildTemplateTreeNode2(String pid) throws  Exception {
        List<TreeNode> tree = new ArrayList<TreeNode>();
        getTemplateHasMap();
        List<TreeNode> list = getTreeNodebyFile(pid);
        if(templateHasMap.containsKey(pid) && templateHasMap.get(pid).size() > 0)
        {
            List<String[]> templateList = templateHasMap.get(pid) ;

            for (TreeNode node : list) {
                String nodeId=node.getId();


                List<String[]> templateNameList =sortTemplate(templateList,pid) ;
                for (int i = 0; i < templateNameList.size(); i++) {
                    String[] qTemplate =templateNameList.get(i);
                    String treeName= qTemplate[0];
                    String qName = qTemplate[1];
                    String qType = qTemplate[2];

                    String templateName = treeName;
                    String id = nodeId + i;
                    node.setId(id);
                    TreeNode treeNode = getTreeList(qName, node, i, String.format("%s_%s","Template",qType));
                    //重写labelname
                    treeNode.setLabel(templateName);
                    tree.add(treeNode);

                }

            }

        }


        return tree;
    }



    public List<TreeNode> getChildRemoteTreeNode(String pid) throws  Exception{
        List<TreeNode> tree = new ArrayList<TreeNode>();
        List<TreeNode> list = getTreeNodebyFile(pid);
        for(TreeNode node: list)
        {
            String value=node.getValue();
            String [] valueList=value.split("[,]");
            if(valueList.length==5)//10.126.53.124,1416,C_SVR_GOME02,1208
            {
                // MQUtil util=new MQUtil("10.126.53.124", "QM_TEST3_GOME02","C_SVR_GOME02",1208,1416);
                MQUtil util=new MQUtil(valueList[0],valueList[1],valueList[2],Integer.valueOf(valueList[3]),Integer.valueOf(valueList[4]));

                String[] qNames= getNamesBymqNameHash(node.getId(), CMQC.MQQT_REMOTE, util);//   util.getQName(CMQC.MQQT_LOCAL);

                List<String> qNameList=getQName(qNames,node.getId());
                for(int i=0;i< qNameList.size();i++)
                {
                    tree.add(getTreeList(qNameList.get(i).trim(), node, i, "Remote"));
                }

            }

        }
        return tree;
    }


    public  List<TreeNode> getTreeNode(String pid) throws Exception
    {
        List<TreeNode> tree=new ArrayList<>();
        List<TreeNode> list = getTreeNodebyFile(pid);

        for(TreeNode node: list)
        {
            tree.add(getTreeNode(node));
        }

        return tree;
    }

    private TreeNode getTreeNode(TreeNode info) throws  Exception
    {
        TreeNode node=new TreeNode();
        node.setId(info.getId());
        node.setLabel(info.getLabel());
        node.setValue(info.getValue());
        node.setLoadOnDemand(info.getLoadOnDemand());
        node.setType(info.getType());
        node.setChildren(getChildNode(info.getId(), node.getValue()));
        node.setUrl(info.getUrl());
        return  node;
    }


    private TreeNode getTreeList(String qName,TreeNode info,int index,String suffix) throws  Exception {
        TreeNode node=new TreeNode();
        node.setIsCmd(qName.indexOf("$")<0);
        qName=qName.trim().replace("$","");
        node.setId(info.getId()+index);
        node.setLabel(String.format("%s", qName.replace("$","")));
        node.setValue(info.getValue() + "," + qName);
        node.setLoadOnDemand(info.getLoadOnDemand());
        node.setType(info.getType());
        node.setChildren(null);
        node.setTypeValue(suffix);
        node.setUrl(info.getUrl());
        node.setUser(info.getUser());
        node.setPwd(info.getPwd());
        return  node;

    }


    public List<TreeNode> getNodes(String pid,String value) throws Exception
    {
        List<TreeNode> returnTree = new ArrayList<TreeNode>();
        if(pid.equals("0"))
        {
            returnTree = getTreeNode(pid);

        }else if(value.length()>0) {
            returnTree = getChildNode(pid, value);
        }
        return  returnTree;
    }

    public MQUser getMqUser(String pid,String value) throws Exception
    {
        MQUser user=new MQUser();

        List<TreeNode> treeNodes= getTreeNodebyFile(pid);
        for(TreeNode node: treeNodes)
        {
            if(node.getValue().equals(value))
            {
                user.set_pwd(node.getPwd());
                user.set_user(node.getUser());
            }
        }
        return user;
    }

}
