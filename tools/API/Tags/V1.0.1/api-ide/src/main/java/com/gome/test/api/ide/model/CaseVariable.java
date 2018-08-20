/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gome.test.api.ide.model;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author shan.tan
 */
public class CaseVariable {
    private String key;
    private String value;

    public CaseVariable() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //将json字符串转化成类对象
    public static CaseVariable loadFrom(String caseVariable) {
        if (null == caseVariable) {
            return null;
        }
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.readValue(caseVariable, CaseVariable.class);
        } catch (IOException ex) {
            return null;
        }
    }

    //将类对象主转成Json串
    public String toJsonString() {
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.writeValueAsString(this);
        } catch (IOException ex) {
            return null;
        }
    }

}
