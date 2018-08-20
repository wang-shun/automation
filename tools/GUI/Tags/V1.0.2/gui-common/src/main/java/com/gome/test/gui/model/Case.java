package com.gome.test.gui.model;


import java.util.List;

public class Case {
    private String id;
    private String name;
    private String owner;
    private List<Step> steps;

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public static Case loadFrom(String id, String name, List<Step> steps, String owner) {
        Case testCase = new Case();
        testCase.setId(id);
        testCase.setName(name);
        testCase.setOwner(owner);
        testCase.setSteps(steps);
        return testCase;
    }
}
