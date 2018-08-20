package com.gome.test.mq.model;

/**
 * Created by zhangjiadi on 15/12/18.
 */
public class MQUser {

    public String get_user() {
        return _user;
    }

    public void set_user(String _user) {
        this._user = _user;
    }

    public String get_pwd() {
        return _pwd;
    }

    public void set_pwd(String _pwd) {
        this._pwd = _pwd;
    }

    private String  _user;
    private String  _pwd;

}
