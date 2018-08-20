package com.ofo.test.api.model;

import com.ofo.test.utils.JsonUtils;

import java.io.IOException;

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

        try {
            return JsonUtils.readValue(entities, Entities.class);
        } catch (IOException ex) {
            return null;
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
