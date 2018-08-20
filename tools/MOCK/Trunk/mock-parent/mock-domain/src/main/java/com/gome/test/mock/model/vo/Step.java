package com.gome.test.mock.model.vo;

import java.util.List;

public class Step implements java.io.Serializable {
    private int index;
    private String keyWork;
    private List<String> params;

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getParams() {
        return this.params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getKeyWork() {
        return this.keyWork;
    }

    public void setKeyWork(String keyWork) {
        this.keyWork = keyWork;
    }
}
