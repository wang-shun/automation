package com.gome.test.mock.model;

/**
 * Created by Administrator on 2015/10/14.
 */
public class Result {

    private Boolean isError;
    private String message;
    private Object data;

    public Result() {
        this(false, null, null);
    }

    public Result(Boolean isError, String message) {
        this(isError, message, null);
    }

    public Result(Object data) {
        this(false, null, data);
    }

    public Result(Boolean isError, String message, Object data) {
        this.isError = isError;
        this.message = message;
        this.data = data;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

}
