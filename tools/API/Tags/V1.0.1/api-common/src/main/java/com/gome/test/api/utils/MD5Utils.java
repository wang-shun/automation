package com.gome.test.api.utils;

import java.security.MessageDigest;

public class MD5Utils {

    public static String encrypt(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes("UTF-8"));
            byte[] bs = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bs.length; ++i) {
                int v = bs[i] & 0xff;
                if (v < 16) {
                    sb.append(0);
                    sb.append(v);
                } else {
                    sb.append(Integer.toHexString(v));
                }
            }
            return sb.toString();
        } catch (Exception ex) {
            return text;
        }
    }
}
