package com.gome.test.mock.utils;

import java.util.HashMap;
import java.util.Map;

public class SimpleMessageFormatUtil {

    private static final Logger logger = new Logger(MessageFormatUtil.class);

    public static final String PREFIX = "${";

    public static final String SUFFIX = "}";

    public static String messageFormatTo(String pattern, Map<String, String> paraMap) {
        if (paraMap == null || paraMap.isEmpty()) {
            return pattern;
        }
        StringBuffer buf = new StringBuffer(pattern);
        int startIndex = buf.indexOf(PREFIX);
        while (startIndex != -1) {
            int endIndex = buf.indexOf(SUFFIX, startIndex + PREFIX.length());
            if (endIndex != -1) {
                String replaced = buf.substring(startIndex + PREFIX.length(), endIndex);
                int nextIndex = endIndex + SUFFIX.length();
                try {
                    String propVal = paraMap.get(replaced);
                    if (propVal != null) {
                        buf.replace(startIndex, endIndex + SUFFIX.length(), propVal);
                        nextIndex = startIndex + propVal.length();
                    } else {
                        logger.warn("Could not Format '" + replaced + "' in [" + pattern + "] ");
                    }
                } catch (Exception ex) {
                    logger.warn("Could not Format '" + replaced + "' in [" + pattern + "]: " + ex);
                }
                startIndex = buf.indexOf(PREFIX, nextIndex);
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        String parttern = "把大象放冰箱分${count}步，${name}好像知道这事！";
        Map<String, String> map = new HashMap<String, String>();
        map.put("count", "3");
        map.put("name", "宋丹丹");

        System.out.println(messageFormatTo(parttern, map));
    }
}
