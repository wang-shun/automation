package com.gome.test.api.model;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

public class NameValuePairs extends ArrayList<NameValuePair> {

    public static NameValuePairs loadFrom(String nameValuePairs) {
        if (null == nameValuePairs) {
            return null;
        }
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.readValue(nameValuePairs, NameValuePairs.class);
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
