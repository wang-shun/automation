package com.gome.test.api.ide.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class TreeNode {

    private String id;
    private String label;
    private String type;
    private Boolean loadOnDemand;
    private List<TreeNode> children;

    public TreeNode() {
    }

    public TreeNode(String id, String label, String type,
                    Boolean loadOnDemand, List<TreeNode> children) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.loadOnDemand = loadOnDemand;
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty(value = "load_on_demand")
    public Boolean getLoad_on_demand() {
        return loadOnDemand;
    }

    public void setLoadOnDemand(Boolean loadOnDemand) {
        this.loadOnDemand = loadOnDemand;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
