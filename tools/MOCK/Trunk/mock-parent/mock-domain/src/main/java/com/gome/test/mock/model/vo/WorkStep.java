package com.gome.test.mock.model.vo;

import java.util.List;

public class WorkStep implements java.io.Serializable {
    private List<Step> steps;

    public List<Step> getSteps() {
        return this.steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

}
