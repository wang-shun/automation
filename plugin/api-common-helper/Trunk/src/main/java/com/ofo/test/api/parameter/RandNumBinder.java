package com.ofo.test.api.parameter;

import com.ofo.test.api.parameter.*;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandNumBinder extends com.ofo.test.api.parameter.IDataBinder {

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

    @Test
    public void test()
    {
        System.out.print( bind("${randNum(5)}"));
    }
}
