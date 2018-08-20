package com.gome.test.mock.model;

import java.util.List;

/**
 * Created by zhangjiadi on 15/10/13.
 */
public class TreeNode  {
    private  String Id;
    private  String label;

    public void setId(String id) {
        Id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPid(String pid) {
        Pid = pid;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLoadOnDemand(Boolean loadOnDemand) {
        this.loadOnDemand = loadOnDemand;
    }

    private  String Pid;
    private String value;
    private String type;
    private String url;
    private Boolean loadOnDemand;

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    private List<TreeNode> children;

    public TreeNode(String id,String label,String pid,String value,String type,String url,Boolean loadOnDemand)
    {
        this.Id=id;
        this.label=label;
        this.Pid=pid;
        this.value=value;
        this.type=type;
        this.url=url;
        this.loadOnDemand=loadOnDemand;
    }

    public TreeNode()
    {

    }


    public String getId() {
        return Id;
    }

    public String getLabel() {
        return label;
    }

    public String getPid() {
        return Pid;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getLoadOnDemand() {
        return loadOnDemand;
    }
}
