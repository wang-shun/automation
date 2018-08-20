package com.ofo.test.api.model;

import com.ofo.test.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;

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
