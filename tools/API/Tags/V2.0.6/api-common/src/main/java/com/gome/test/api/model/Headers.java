package com.gome.test.api.model;

import java.io.IOException;
import java.util.ArrayList;

import com.gome.test.utils.JsonUtils;

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
