package com.gome.test.mq.model;

/**
 * Created by zhangjiadi on 15/12/22.
 */
public enum UseType {

    MessageAdd(1,"新增"),
    MessageUse(2,"消费");

    public int getValue() {
        return value;
    }

    private int value;

    public String getName() {
        return name;
    }

    private String name;

    private UseType(int value, String name){
            this.value=value;
            this.name=name;
    }

    public static UseType valueOf(int value) {
        switch (value) {
            case 1:
                return MessageAdd;
            case 2:
                return MessageUse;

            default:
                return null;
        }
    }
}
