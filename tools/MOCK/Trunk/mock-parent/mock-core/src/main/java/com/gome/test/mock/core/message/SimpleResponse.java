package com.gome.test.mock.core.message;


import com.gome.test.mock.core.Message;
import com.gome.test.mock.core.Response;
import com.gome.test.mock.core.util.RequestAttr;
import com.gome.test.mock.core.util.SessionAttr;

public class SimpleResponse<R> implements Response<R> {
    
    private Message<R> msg;
    
    public SimpleResponse(Message<R> msg){
        this.msg = msg;
    }
    
    @Override
    public boolean hasPojo() {
         return msg.hasPojo();
    }

    @Override
    public R pojo() {
        return msg.getPojo();
    }

    @Override
    public void pojo(R pojo) {
        msg.setPojo(pojo);
    }

    @Override
    public RequestAttr requestAttr() {
        return msg.getRequestAttr();
    }
    
    @Override
    public SessionAttr sessionAttr() {
        return msg.getSessionAttr();
    }
    
    
    private boolean disconnectOnComplete = false;
    @Override
    public void disconnectOnComplete() {
        disconnectOnComplete = true;
    }
    public boolean isDisconnectOnComplete(){
        return disconnectOnComplete;
    }
    
    private boolean done = false;
    @Override
    public void done(){
        done = true;
    }
    public boolean isDone(){
        return done;
    }

    
}
