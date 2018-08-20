package com.gome.test.api.model;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;

public class UrlParams extends ArrayList<UrlParam> {

    public static UrlParams loadFrom(String urlParams) {
        if (null == urlParams) {
            return null;
        }
        ObjectMapper objMapper = new ObjectMapper();
        try {
            return objMapper.readValue(urlParams, UrlParams.class);
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
