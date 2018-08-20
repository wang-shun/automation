package com.ofo.test.api.model;

import com.ofo.test.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;


public class NameValuePairs extends ArrayList<NameValuePair> {

    public static NameValuePairs loadFrom(String nameValuePairs) {
        if (null == nameValuePairs) {
            return null;
        }

        try {
            return JsonUtils.readValue(nameValuePairs, NameValuePairs.class);
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
