package com.ofo.test.api.model;

import com.ofo.test.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;

public class Headers extends ArrayList<Header> {

    public static Headers loadFrom(String headers) {
        if (null == headers) {
            return null;
        }

        try {
            return JsonUtils.readValue(headers, Headers.class);
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
