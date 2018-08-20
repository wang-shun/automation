package com.gome.test.mock.bo;

import com.gome.test.mock.dao.ApiDao;
import com.gome.test.mock.dao.HostDao;
import com.gome.test.mock.dao.PortDao;
import com.gome.test.mock.model.TreeNode;
import com.gome.test.mock.model.bean.Api;
import com.gome.test.mock.model.bean.Host;
import com.gome.test.mock.model.bean.Port;
import com.gome.test.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjiadi on 15/10/13.
 */
@Component
public class LeftTreeService {

    private final  String GETAPI="getAPI";
    private final  String GETLOG="getLog";
    private final  String GETHOST="getHost";
    private final  String GETPORT="getPort";
    private final  String GETWORKFLOW="getWorkFlow";
    private final  String GETCHILD="getChild";


    private String repConfigFile() throws Exception {
        InputStream fis = this.getClass().getResourceAsStream(String.format("/%s", "TreeNode.xml"));
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        String resultCfgStr = "";
        while ((line = br.readLine()) != null) {
            resultCfgStr = resultCfgStr + line + "\r\n";
        }
        br.close();
        isr.close();
        System.out.println(resultCfgStr);
        return resultCfgStr;
    }

    private List<TreeNode> getTreeNodebyFile(String pid) throws Exception
    {

        String xmlText = repConfigFile();
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
                Boolean loadOnDemand=Boolean.valueOf(node.getAttributes().getNamedItem("loadOnDemand").getNodeValue()) ;
                TreeNode info = new TreeNode(id, label, Pid,value,type,url,loadOnDemand);
                list.add(info);
            }
        }

        return  list;
    }

    @Autowired
    private ApiDao apiDao;

    @Autowired
    private HostDao hostDao;
    @Autowired
    private PortDao portDao;

    public List<TreeNode> getChildNode(String _id,String value) throws  Exception{

        List<TreeNode> nodeList = new ArrayList<TreeNode>();
        if (value.equals(GETAPI))//找到API
        {
           nodeList=getApiChild();

        } else if(value.equals(GETHOST))
        {
            nodeList=getHostChild();

        }else  if(value.equals(GETLOG))
        {

        }else if(value.equals(GETPORT))
        {
            nodeList=getPortChild();
        }else if(value.equals(GETWORKFLOW))
        {

        }else if(value.equals(GETCHILD))
        {
            nodeList = getTreeNode(_id);
        }
        return nodeList;
    }

    private  List<TreeNode> getApiChild()
    {
        List<TreeNode> nodeList=new ArrayList<TreeNode>();

        List<Api> apiList = apiDao.getAll();

        for(Api api : apiList)
        {
            String id=String.format("api_%s",api.getId());
            TreeNode node=createTreeNode(id,api.getApiName(),String.format("/apiView/%s", api.getId()));
            nodeList.add(node);
        }
        return nodeList;
    }

    private  List<TreeNode> getHostChild()
    {
        List<TreeNode> nodeList=new ArrayList<TreeNode>();

        List<Host> hostsList = hostDao.getAll();

        for(Host host : hostsList)
        {
            String id=String.format("host_%s",host.getId());
            TreeNode node=createTreeNode(id,String.format("%s(domain:%s;url:%s)",host.getServiceName(),host.getDomain(),host.getUrl())  ,String.format("/hostView/%s", host.getId()));
            nodeList.add(node);
        }
        return nodeList;
    }

    private  List<TreeNode> getPortChild()
    {
        List<TreeNode> nodeList=new ArrayList<TreeNode>();

        List<Port> portList = portDao.getAll();

        for(Port port : portList)
        {
            String id=String.format("port_%s",port.getId());
            TreeNode node=createTreeNode(id,String.valueOf(port.getPortNumber()),String.format("%s", port.getId()),"jsnodetree");
            nodeList.add(node);
        }
        return nodeList;
    }

    private TreeNode createTreeNode(String id,String name,String value)
    {
        TreeNode node=new TreeNode();
        node.setId(id);
        node.setLabel(name);
        node.setChildren(null);
        node.setLoadOnDemand(false);
        node.setType("nodetree");
        node.setValue(value);
        node.setChildren(new ArrayList<TreeNode>());
        return node;
    }

    private TreeNode createTreeNode(String id,String name,String value,String nodeType)
    {
        TreeNode node=new TreeNode();
        node.setId(id);
        node.setLabel(name);
        node.setChildren(null);
        node.setLoadOnDemand(false);
        node.setType(nodeType);
        node.setValue(value);
        node.setChildren(new ArrayList<TreeNode>());
        return node;
    }

    public  List<TreeNode> getTreeNode(String pid) throws Exception
    {
        List<TreeNode> tree = new ArrayList<TreeNode>();
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
        node.setChildren(getChildNode(info.getId(),info.getValue()));
        node.setUrl(info.getUrl());
        return  node;
    }


    public List<TreeNode> getNodes(String pid,String value) throws Exception
    {
        List<TreeNode> returnTree = new ArrayList<TreeNode>();
        if(pid.equals("0"))
        {
            returnTree = getTreeNode(pid);

        }else if(value.length()>0) {

            returnTree = getChildNode(pid,value);
        }
        return  returnTree;
    }


}
