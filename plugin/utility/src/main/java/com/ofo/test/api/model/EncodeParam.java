package com.ofo.test.api.model;

/**
 * Created by Administrator on 2015/5/19.
 */
public class EncodeParam {

    public EncodeParam(String key,String value,EncodeTypeEnum encodeType)
    {
        this.key = key;
        this.value = value;
        this.encodeType=encodeType;
    }


    public EncodeTypeEnum getEncodeType() {
        return encodeType;
    }

    public String getKey() {
        return key;
    }


    public String getValue() {
        return value;
    }


    private String key;

    private String value;

    private EncodeTypeEnum encodeType;
}
