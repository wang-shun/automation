package com.gome.test.mock.cnst;

public enum Protocol {
    HTTP, WEBSERVICE, DUBBO, SOCKET, NOPROTOCOL;
    public static Protocol toProtocol(String str) {
        try {
            return valueOf(str);
        } catch (Exception ex) {
            return NOPROTOCOL;
        }
    }
}