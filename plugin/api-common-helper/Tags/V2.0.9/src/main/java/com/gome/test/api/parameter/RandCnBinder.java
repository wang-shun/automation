package com.gome.test.api.parameter;

import org.testng.annotations.Test;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RandCnBinder extends IDataBinder {

    private final Random random = new Random();
    private final String seed = "打算做名开发个人认为要学的东西真的很多而且我觉得有些知识最好是开始就应当讲清楚具体咱们慢慢说";
    private final Pattern p = Pattern.compile("\\$\\{randCn\\(([0-9]+)\\)?\\}");

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
        System.out.print( bind("${randCn(5)}"));
    }
}
