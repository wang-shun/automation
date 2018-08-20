package com.gome.test.mock.entity;

import java.io.Serializable;

/**
 * Created by chaizhongbao on 2015/9/10.
 */
public class HeartBeat implements Serializable {

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
