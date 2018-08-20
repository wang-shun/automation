package com.gome.test.mock.exception;


/**
 * Created by chaizhongbao on 2015/9/6.
 */
public class MyRuntimeException extends RuntimeException {
    
    private static final long serialVersionUID = -2400918594219866598L;
    
    public MyRuntimeException(){
        super();
    }
    public MyRuntimeException(String msg) {
        super(msg);
    }
    public MyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    public MyRuntimeException(Throwable cause) {
        super(cause);
    }



    
}
