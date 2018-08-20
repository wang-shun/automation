package com.gome.test.api.ide.model;

import java.util.List;

public class MenuDescriptor implements Comparable<MenuDescriptor> {

    private String description;
    private String method;
    private List<String> params;

    public MenuDescriptor() {
    }

    public MenuDescriptor(String description, String method,
                          List<String> params) {
        this.description = description;
        this.method = method;
        this.params = params;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public int compareTo(MenuDescriptor aThat) {
        return method.compareTo(aThat.method);
    }
}
