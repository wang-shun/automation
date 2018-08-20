package com.ofo.test.api.model;

import com.ofo.test.utils.AESUtils;
import com.ofo.test.utils.JsonUtils;
import com.ofo.test.utils.MD5Utils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.IOException;

public class Header {

    private String key;
    private String value;
    private String encryption;
    private String secret;
    private String doc;
    private String comments;

    public Header() {
    	
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

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getDoc() {
        return doc;
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

    public static Header loadFrom(String header) {
        if (null == header) {
            return null;
        }

        try {
            return JsonUtils.readValue(header, Header.class);
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
