package com.gome.test.mock.exception;


/**
 * Created by chaizhongbao on 2015/9/6.
 */
public class MyException extends Exception {
    
    private static final long serialVersionUID = -2400918594219866598L;
    
    public MyException(){
        super();
    }
    public MyException(String msg) {
        super(msg);
    }
    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
    public MyException(Throwable cause) {
        super(cause);
    }

    
}
