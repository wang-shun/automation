package com.gome.test.api.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gome.test.utils.JsonUtils;
import org.codehaus.jackson.type.TypeReference;

public class Steps extends ArrayList<Step> {

    public static Steps loadFrom(String steps) {
        if (null == steps) {
            return null;
        }

        try {
            return JsonUtils.readValue(steps, Steps.class);
        } catch (IOException ex) {
            try {
                Steps stepList = new Steps();
                List<List<String>> ls = JsonUtils.readValue(steps, new TypeReference<List<List<String>>>() {
                });
                for (List<String> l : ls) {
                    stepList.add(Step.loadFrom(l));
                }
                return stepList;
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
