package com.gome.test.api.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class Steps extends ArrayList<Step> {

    public static Steps loadFrom(String steps) {
        if (null == steps) {
            return null;
        }
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.readValue(steps, Steps.class);
        } catch (IOException ex) {
            try {
                Steps stepList = new Steps();
                List<List<String>> ls = objMapper.readValue(steps, new TypeReference<List<List<String>>>() {
                });
                for (List<String> l : ls) {
                    stepList.add(Step.loadFrom(l));
                }
                return stepList;
            } catch (IOException ex1) {
                return null;
            }
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
