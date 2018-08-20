package com.ofo.test.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.Reporter;

public class Logger {

    public static enum Level {

        INFO(" INFO"),
        WARN(" WARN"),
        ERROR("ERROR");

        private final String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static Level fromString(String value) {
            if (null != value) {
                for (Level l : Level.values()) {
                    if (value.equals(l.value)) {
                        return l;
                    }
                }
            }
            return null;
        }
    }

    public static void info(String format, Object... args) {
        try {
            log(Level.INFO, String.format(format, args));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void info(String msg) {
        try {
            log(Level.INFO, msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void warn(String msg) throws UnsupportedEncodingException {
        log(Level.WARN, msg);
    }

    public static void warn(String format, Object... args) {
        try {
            log(Level.WARN, String.format(format, args));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void error(String msg) {
        try {
            log(Level.ERROR, msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void error(String format, Object... args) {
        try {
            log(Level.ERROR, String.format(format, args));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void log(Level level, String msg) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        sb.append(sdf.format(new Date()));
        sb.append(" [");
        sb.append(level.value);
        sb.append("]\t");
        sb.append(msg);
        System.out.println(sb.toString());
        Reporter.log(sb.toString());
    }
}
