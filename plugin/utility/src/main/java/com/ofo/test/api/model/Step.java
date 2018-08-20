package com.ofo.test.api.model;

import com.ofo.test.utils.JsonUtils;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

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

        try {
            return JsonUtils.readValue(step, Step.class);
        } catch (IOException ex) {
            try {
                List<String> l = JsonUtils.readValue(step, new TypeReference<List<String>>() {
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
        try {
            return JsonUtils.toJson(this);
        } catch (IOException ex) {
            return null;
        }
    }
}
