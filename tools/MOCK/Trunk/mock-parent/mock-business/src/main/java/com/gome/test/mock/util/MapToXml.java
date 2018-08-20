package com.gome.test.mock.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapToXml {

    public static String listToXML(List<HashMap<String, String>> list, String bean) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + bean + "s>");
        for (Map<String, String> map : list) {
            buffer.append("<" + bean + ">");
            for (String key : map.keySet()) {
                buffer.append("<" + key + ">");
                buffer.append(map.get(key));
                buffer.append("</" + key + ">");
            }
            buffer.append("</" + bean + ">");
        }
        buffer.append("</" + bean + "s>");
        return buffer.toString();
    }

    public static String mapToXML(Map<String, String> map, String bean) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<" + bean + ">");
        for (String key : map.keySet()) {
            buffer.append("<" + key + ">");
            buffer.append(map.get(key));
            buffer.append("</" + key + ">");
        }
        buffer.append("</" + bean + ">");
        return buffer.toString();
    }
}
