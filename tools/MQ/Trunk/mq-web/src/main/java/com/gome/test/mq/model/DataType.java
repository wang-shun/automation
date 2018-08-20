package com.gome.test.mq.model;

/**
 * Created by zhangjiadi on 15/12/25.
 */
public enum DataType {
    DataType_String("String","String"),
    DataType_Int("Int","Int"),
    DataType_Date("Date","yyyy-MM-dd'T'HH:mm:ssZZ"),
    DataType_Boolean("Boolean","Boolean"),
    DataType_Double("Double","Double"),
    DataType_Date2("Date2","yyyy-MM-dd'T'HH:mm:ssSSSZ");

    public String getValue() {
        return value;
    }

    private String value;
    private String key;

    private DataType(String key, String value){
        this.value=value;
        this.key=key;
    }

    public static DataType getValueOf(String key) {
        switch (key) {
            case "String":
                return DataType_String;
            case "Int":
                return DataType_Int;
            case "Date":
                return DataType_Date;
            case  "Boolean":
                return DataType_Boolean;
            case  "Double":
                return DataType_Double;
            case  "Date2":
                return DataType_Date2;
            default:
                return DataType_String;
        }
    }
}

