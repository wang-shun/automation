package com.ofo.test.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

public class JsonUtils {

    static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) throws IOException {
        if(object == null)
            return null;

        return mapper.writeValueAsString(object);
    }

    public static <T> T readValue(String content, Class<T> valueType) throws IOException {
        if(content == null)
            return null;

        return mapper.readValue(content, valueType);
    }

    public static <T> T readValue(String content, TypeReference valueTypeRef) throws IOException {
        if(content == null)
            return null;

        return mapper.readValue(content, valueTypeRef);
    }
}
