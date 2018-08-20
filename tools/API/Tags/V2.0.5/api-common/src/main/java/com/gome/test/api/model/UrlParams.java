package com.gome.test.api.model;

import java.io.IOException;
import java.util.ArrayList;

import com.gome.test.utils.JsonUtils;

public class UrlParams extends ArrayList<UrlParam> {

    public static UrlParams loadFrom(String urlParams) {
        if (null == urlParams) {
            return null;
        }

        try {
            return JsonUtils.readValue(urlParams, UrlParams.class);
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
