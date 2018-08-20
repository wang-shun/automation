package com.ofo.test.utils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    /**
     * MD5 加密
     *
     * @param input 被加密串
     * @param key   加密key
     * @throws Exception
     */
    public static String encodeMessage(String input, String key) throws Exception {
        byte[] data = (input + key).getBytes("UTF-8");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return toHex(md5.digest());
    }

    public static String encodeMessage(String input) throws Exception {
        byte[] bytes = encryptMD5(input);
        return byte2hex(bytes);
    }

    /**
     * json转Map，Map.key字典排序，keyvalue罗列后，两端拼接扰码，再进行MD5加密
     *
     * @param input
     * @param secret
     * @throws Exception
     */
    public static String encodeJsonMapMessage(String input, String secret) throws Exception {
        /**
         * 第一步：把字典按Key的字母顺序排序
         */
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> sortedParams = objectMapper.readValue(input, TreeMap.class);
        sortedParams.remove("accessToken");
        sortedParams.remove("sign");

        System.out.println(sortedParams);
        /**
         * 第二步：把所有参数名和参数值串在一起
         */
        Set<Map.Entry<String, Object>> paramSet = sortedParams.entrySet();

        StringBuilder query = new StringBuilder();
        query.append(secret);
        for (Map.Entry<String, Object> param : paramSet) {

            String value = null;
            if (param.getValue().getClass().isArray()) {
                value = ((String[]) param.getValue())[0];
            } else {
                value = param.getValue().toString();
            }

            if (StringUtils.areNotEmpty(param.getKey(), value)) {
                query.append(param.getKey()).append(value);
            }
        }
        query.append(secret);

        /**
         * 第三步：使用MD5加密
         */

        byte[] bytes = encryptMD5(query.toString());

        /**
         * 第四步：把二进制转化为大写的十六进制
         */
        return byte2hex(bytes);
    }

    private static String toHex(byte[] buffer) {
        byte[] result = new byte[buffer.length * 2];
        byte[] temp;
        for (int i = 0; i < buffer.length; i++) {
            temp = getHexValue(buffer[i]);
            result[i * 2] = temp[0];
            result[i * 2 + 1] = temp[1];
        }
        return new String(result).toUpperCase();


    }

    private static byte[] getHexValue(byte b) {
        int value = b;
        if (value < 0) {
            value = (int) (256 + b);
        }
        String s = Integer.toHexString(value);
        if (s.length() == 1) {
            return new byte[]{'0', (byte) s.charAt(0)};
        }
        return new byte[]{(byte) s.charAt(0), (byte) s.charAt(1)};
    }


    private static byte[] encryptMD5(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
        return bytes;
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static void main(String[] args) throws Exception {
//        String encode = encodeMessage("000320150811100308Jls1dyRZWwV3fvLeW8enUBib0E6LyojS4GkYQHcjfR7If91ucWtCGxAKZTNjpMyJlaNUYWSqEGYyqiPWgziPAR5kVOOiM9DjcXIOVBd/n6Yr0NbUgld16irciZhAOEkQtV9mlegg14DTJtHllhTQNA==","MD5KEY0003123456");
//        String encode = encodeMessage("version=0.1&siteAccount=0025010010&cashierCode=45&payMode=020&key=","3663");

//        String encode = encodeMessage("9d58de5df1f44ee9a4103eb93316cb77carriersName韵达formatjsonlogisticsNumber1900213612969methodcoo8.order.sendorderid20120813153333290816signMethodmd5timestamp2012-08-23 14:33:01v2.0venderId20120709165357969d58de5df1f44ee9a4103eb93316cb77");
//        System.out.println(encode);
//        byte[] bytes= encryptMD5(encode+"9d58de5df1f44ee9a4103eb93316cb77");
//
//        System.out.println(byte2hex(bytes)) ;

        String data = "{\"timestamp\":\"2015-09-10 11:07:44\",\"v\":\"2.0\",\"venderId\":\"80006095\",\"method\":\"coo8.product.update\",\"signMethod\":\"md5\",\"format\":\"json\",\"productId\":\"A0005489723\",\"description\":\"大声道\"}";

        System.out.println(encodeJsonMapMessage(data, "fea6210da2ef41b6"));

        System.out.println(encodeMessage("000120151118181323NzOob9SHJHlz9V76qFzLijCJh56bMHeGhD1n2fOPtRVKxx1G7FIpzvT/vSZhddCUMweBD3S7sAVLCThEy6b0EqZLzOu972R91U/ZfsGkJkI=","MD5KEY1001ABCDEF"));

    }

}
