package com.gome.test.api.model;

import java.util.List;

import com.gome.test.api.model.Step;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StepTest {

    @Test
    public void testLoadFromStringList() {
        JSONArray arr = new JSONArray();
        arr.put(keyword);
        for (int i = 0; i < args.length; ++i) {
            arr.put(args[i]);
        }
        Step step = Step.loadFrom(arr.toString());
        Assert.assertNull(step.getDoc());
        Assert.assertEquals(step.getKeyword(), keyword);
        List<String> arguments = step.getArgs();
        Assert.assertEquals(arguments.size(), args.length);
        for (int i = 0; i < args.length; ++i) {
            Assert.assertEquals(arguments.get(i), args[i]);
        }
    }

    @Test
    public void testLoadFromStepJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("doc", doc);
        obj.put("keyword", keyword);
        JSONArray arr = new JSONArray();
        for (int i = 0; i < args.length; ++i) {
            arr.put(args[i]);
        }
        obj.put("args", arr);
        Step step = Step.loadFrom(obj.toString());
        Assert.assertEquals(step.getDoc(), doc);
        Assert.assertEquals(step.getKeyword(), keyword);
        List<String> arguments = step.getArgs();
        Assert.assertEquals(arguments.size(), args.length);
        for (int i = 0; i < args.length; ++i) {
            Assert.assertEquals(arguments.get(i), args[i]);
        }
    }

    @Test
    public void testLoadFromStepJsonObjectWithNotExistField() {
        JSONObject obj = new JSONObject();
        obj.put("keyword", keyword);
        JSONArray arr = new JSONArray();
        for (int i = 0; i < args.length; ++i) {
            arr.put(args[i]);
        }
        obj.put("args", arr);
        Step step = Step.loadFrom(obj.toString());
        Assert.assertNull(step.getDoc());
        Assert.assertEquals(step.getKeyword(), keyword);
        List<String> arguments = step.getArgs();
        Assert.assertEquals(arguments.size(), args.length);
        for (int i = 0; i < args.length; ++i) {
            Assert.assertEquals(arguments.get(i), args[i]);
        }
    }

    private static final String doc = "document of step";
    private static final String keyword = "keyword";
    private static final String[] args = new String[]{"arg1", "arg2"};
}
