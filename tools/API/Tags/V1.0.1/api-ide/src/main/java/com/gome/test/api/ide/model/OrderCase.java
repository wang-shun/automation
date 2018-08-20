package com.gome.test.api.ide.model;

import java.util.List;

public class OrderCase {

    private String id;
    private String caseDesc;
    private boolean continueAfterFailure;
    private String owner;
    private List<String> steps;
    private List<OrderNode> orderNodes;

    public OrderCase(String id, String caseDesc,
                     boolean continueAfterFailure,
                     String owner, List<String> steps, List<OrderNode> orderNodes) {
        this.id = id;
        this.caseDesc = caseDesc;
        this.continueAfterFailure = continueAfterFailure;
        if (null == owner) {
            this.owner = "Unknown";
        } else {
            this.owner = owner;
        }
        this.steps = steps;
        this.orderNodes = orderNodes;
    }

    public OrderCase() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaseDesc() {
        return caseDesc;
    }

    public void setCaseDesc(String caseDesc) {
        this.caseDesc = caseDesc;
    }

    public boolean isContinueAfterFailure() {
        return continueAfterFailure;
    }

    public void setContinueAfterFailure(boolean continueAfterFailure) {
        this.continueAfterFailure = continueAfterFailure;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<OrderNode> getOrderNodes() {
        return orderNodes;
    }

    public void setOrderNodes(List<OrderNode> orderNodes) {
        this.orderNodes = orderNodes;
    }


}
