package com.gome.test.mock.model;

import java.util.List;

/**
 * Created by Administrator on 2015/10/22.
 */
public class MenuDescriptor implements Comparable<MenuDescriptor> {

    private String description;
    private String method;
    private String type;//Given When Then
    private List<String> params;

    public MenuDescriptor() {
    }

    public MenuDescriptor(String description, String method, String type,
                          List<String> params) {
        this.description = description;
        this.method = method;
        this.type = type;
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
