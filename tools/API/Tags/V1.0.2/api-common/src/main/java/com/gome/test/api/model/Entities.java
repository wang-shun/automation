package com.gome.test.api.model;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class Entities {

    private String type;
    private String data;

    public Entities() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static Entities loadFrom(String entities) {
        if (null == entities) {
            return null;
        }
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.readValue(entities, Entities.class);
        } catch (IOException ex) {
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
