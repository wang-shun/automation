package com.ofo.test.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public final static String DATE_WITHOUT_TIME_FORMAT = "yyyy-MM-dd";

    public static Date parseData(String str, String format) throws ParseException {
        if (str == null || str.isEmpty())
            return null;

        return new SimpleDateFormat(format).parse(str);
    }

    public static String parseString(Date date, String format) {
        if (date == null)
            return null;

        return new SimpleDateFormat(format).format(date);
    }

    public static String parseStringWithoutTime(Date date) {
        return parseString(date, DATE_WITHOUT_TIME_FORMAT);
    }

    public static Date parseDateWithoutTime(String str) throws ParseException {
        return parseData(str, DATE_WITHOUT_TIME_FORMAT);
    }
}
