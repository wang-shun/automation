package com.gome.test.api.model;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class Step {

    private String doc;
    private String keyword;
    private List<String> args;

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public static Step loadFrom(List<String> l) {
        Step s = new Step();
        s.setKeyword(l.remove(0));
        s.setArgs(l);
        return s;
    }

    public static Step loadFrom(String step) {
        if (null == step) {
            return null;
        }
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.readValue(step, Step.class);
        } catch (IOException ex) {
            try {
                List<String> l = objMapper.readValue(step, new TypeReference<List<String>>() {
                });
                Step s = new Step();
                s.setKeyword(l.remove(0));
                s.setArgs(l);
                return s;
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
