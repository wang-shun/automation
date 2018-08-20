package com.gome.test.api.parameter;

import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/5/20.
 */
public class DateTimeBinder extends IDataBinder {

    private final Date d = new Date();
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Pattern p = Pattern.compile("\\$\\{nowForTime([+-][0-9]+)?\\}");

    public String bind(String v) {
        Matcher matcher = p.matcher(v);
        while (matcher.find()) {
            String target = matcher.group(0);
            String m = matcher.group(1);
            if (null == m) {
                m = "0";
            }
            Integer delta = Integer.parseInt(m);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.DATE, delta);
            v = v.replace(target,
                    fmt.format(c.getTime()));
        }
        return v;
    }


    @Test
    public void test()
    {
        System.out.print( bind("${nowForTime}"));
    }

}
