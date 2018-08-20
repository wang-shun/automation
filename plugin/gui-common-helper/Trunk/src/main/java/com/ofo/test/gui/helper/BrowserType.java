package com.ofo.test.gui.helper;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum BrowserType {
    FIREFOX,
    IE,
    CHROME,
//    OPERA,
    SAFARI,
    HTMLUNIT,
    EDGE,
    IPAD_SAFARI;

    private static Map<String, BrowserType> browsersMap = new HashMap<String, BrowserType>();

    static {
        browsersMap.put("firefox", BrowserType.FIREFOX);
        browsersMap.put("ie", BrowserType.IE);
        browsersMap.put("chrome", BrowserType.CHROME);
//        browsersMap.put("opera", BrowserType.OPERA);
        browsersMap.put("htmlunit", BrowserType.HTMLUNIT);
        browsersMap.put("safari", BrowserType.SAFARI);
        browsersMap.put("edge",BrowserType.EDGE);
        browsersMap.put("ipad_safari",BrowserType.IPAD_SAFARI);
    }

    public static BrowserType Browser(String name) {
        if(name.toLowerCase().trim().startsWith("ie"))
            name = "ie";

        BrowserType browserType = browsersMap.get(name.toLowerCase().trim());
        if (browserType == null) {
            throw new UnknownBrowserException("Unknown browser [" + name + "]. Use one of following: "
                    + StringUtils.join(browsersMap.keySet().toArray(), ", "));
        }
        return browserType;
    }
}
