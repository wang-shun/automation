package com.ofo.test.api.adapter;

import org.testng.ISuite;

import java.util.Map;

public abstract class IAdapter<T> {

    protected T service = null;

    public void setService(T service) {
        this.service = service;
    }

    public abstract void beforeClass();

    public abstract void beforeMethod();

    public abstract void afterClass();

    public abstract void afterMethod();

    public abstract Object adapter(Map<String, String> map);
}

