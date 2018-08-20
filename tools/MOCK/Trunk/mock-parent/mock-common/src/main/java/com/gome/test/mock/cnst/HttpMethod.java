package com.gome.test.mock.cnst;

public enum HttpMethod {
    GET, POST, HEAD, OPTIONS, PUT, DELETE, CONNECT, NOMETHOD;

    public static HttpMethod toHttpMethod(String str) {
        try {
            return valueOf(str);
        } catch (Exception ex) {
            return NOMETHOD;
        }
    }
}