/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gome.test.mq.model;

import org.springframework.stereotype.Repository;

/**
 *
 * @author Zonglin.Li
 */
@Repository
public class TableResult {

    private boolean isError;
    private String message;
    private int sEcho;
    private int iTotalRecords;
    private int iTotalDisplayRecords;
    private int iDisplayStart;
    private int iDisplayLength;
    private Object[] data;

    public TableResult() {
        this.isError = false;
        this.sEcho = 1;
        this.iTotalRecords = 0;
        this.iTotalDisplayRecords = 0;
        this.data = new int[0][0];
    }

    public TableResult(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public TableResult(boolean isError, int sEcho, int iTotalRecords, int iTotalDisplayRecords, Object[] data) {
        this.isError = isError;
        this.message = "success";
        this.sEcho = sEcho;
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        this.data = data;
    }

    public TableResult(boolean isError, int sEcho, int iTotalRecords, int iTotalDisplayRecords, int iDisplayStart, int iDisplayLength, Object[] data) {
        this.isError = isError;
        this.message = "success";
        this.sEcho = sEcho;
        this.iTotalRecords = iTotalRecords;
        this.iDisplayStart = iDisplayStart;
        this.iDisplayLength = iDisplayLength;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        this.data = data;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getsEcho() {
        return sEcho;
    }

    public void setsEcho(int sEcho) {
        this.sEcho = sEcho;
    }

    public int getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(int iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public int getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    public int getiDisplayStart() {
        return iDisplayStart;
    }

    public void setiDisplayStart(int iDisplayStart) {
        this.iDisplayStart = iDisplayStart;
    }

    public int getiDisplayLength() {
        return iDisplayLength;
    }

    public void setiDisplayLength(int iDisplayLength) {
        this.iDisplayLength = iDisplayLength;
    }
}
