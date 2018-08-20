package com.gome.test.api.model;

import com.gome.test.utils.*;
import java.io.IOException;

import org.codehaus.jackson.annotate.JsonIgnore;

public class UrlParam {

    private String key;
    private String value;
    private String encryption;
    private String secret;
    private String doc;
    private String comments;

    public UrlParam() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    @JsonIgnore
    public String getEncryptionValue() {
        if ("NONE".equals(encryption)) {
            return value;
        } else if ("AES".equals(encryption)) {
            return AESUtils.encryptByKey(value, secret);
        } else if ("MD5".equals(encryption)) {
            return MD5Utils.encrypt(value);
        } else {
            return value;
        }
    }

    public static UrlParam loadFrom(String urlParam) {
        if (null == urlParam) {
            return null;
        }

        try {
            return JsonUtils.readValue(urlParam, UrlParam.class);
        } catch (IOException ex) {
            return null;
        }
    }

    public String toJsonString() {
        try {
            return JsonUtils.toJson(this);
        } catch (IOException ex) {
            return null;
        }
    }
}
