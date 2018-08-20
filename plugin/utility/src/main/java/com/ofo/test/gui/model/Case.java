package com.ofo.test.gui.model;


import com.ofo.test.utils.JsonUtils;

import java.util.List;
import java.util.TreeMap;

public class Case {
    private String id;
    private String name;
    private String owner;
    private List<Step> steps;

    public TreeMap<String, String> getCaseCategory() {
        return caseCategory;
    }

    public void setCaseCategory(TreeMap<String, String> caseCategory) {
        this.caseCategory = caseCategory;
    }

    private TreeMap<String,String> caseCategory;

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



    public static Case loadFrom(String id, String name, List<Step> steps, String owner,String caseCategory_Str) {
        Case testCase = new Case();
        testCase.setId(id);
        testCase.setName(name);
        testCase.setOwner(owner);
        testCase.setSteps(steps);
        TreeMap<String,String> _caseCategory=new TreeMap<String, String>();
        try {
            _caseCategory= JsonUtils.readValue(caseCategory_Str,TreeMap.class);
        }catch (Exception ex)
        {

        }
        testCase.setCaseCategory(_caseCategory);
        return testCase;
    }
}
