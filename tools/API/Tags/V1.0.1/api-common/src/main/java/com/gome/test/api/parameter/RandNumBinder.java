package com.gome.test.api.parameter;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandNumBinder extends IDataBinder {

    private final Random random = new Random();
    private final String seed = "0123456789";
    private final Pattern p = Pattern.compile("\\$\\{randNum\\(([0-9]+)\\)?\\}");

    public String bind(String v) {
        Matcher matcher = p.matcher(v);
        while (matcher.find()) {
            String target = matcher.group(0);
            String m = matcher.group(1);
            Integer len = Integer.parseInt(m);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; ++i) {
                sb.append(seed.charAt(random.nextInt(seed.length())));
            }
            v = v.replace(target, sb.toString());
        }
        return v;
    }
}
