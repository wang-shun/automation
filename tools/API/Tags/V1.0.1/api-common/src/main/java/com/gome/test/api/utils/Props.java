package com.gome.test.api.utils;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

public class Props {

    public Props() {
        m = new HashMap<String, String>();
    }

    public void loadFrom(InputStream is) throws IOException {
        Properties properites = new Properties();
        properites.load(is);
        for (String propName : properites.stringPropertyNames()) {
            m.put(propName, properites.getProperty(propName));
        }
        is.close();
    }

    public boolean containsKey(String key) {
        return m.containsKey(key);
    }

    public String get(String key) {
        return m.get(key);
    }

    public Map<String, String> toMap() {
        return m;
    }

    private final Map<String, String> m;
}
