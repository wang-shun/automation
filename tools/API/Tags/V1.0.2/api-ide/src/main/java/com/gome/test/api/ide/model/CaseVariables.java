/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gome.test.api.ide.model;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author shan.tan
 */
public class CaseVariables extends ArrayList<CaseVariable> {

    public static CaseVariables loadFrom(String caseVariables) {
        if (null == caseVariables) {
            return null;
        }
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.readValue(caseVariables.trim(), CaseVariables.class);
        } catch (IOException ex) {
            System.out.println("error message: " + ex.getMessage());
            return null;

        }
    }

    public String toJsonString() {
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.writeValueAsString(this);
        } catch (IOException ex) {
            return null;
        }
    }


}
