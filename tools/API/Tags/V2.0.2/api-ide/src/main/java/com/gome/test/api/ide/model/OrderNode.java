package com.gome.test.api.ide.model;


public class OrderNode {

    private String name;
    private String id;
    private String selected;

    public OrderNode() {
    }

    public OrderNode(String name, String id, String selected) {
        this.name = name;
        this.id = id;
        this.selected = selected;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }


}
