package com.gome.test.mock.core.message;


import com.gome.test.mock.core.Message;
import com.gome.test.mock.core.Request;
import com.gome.test.mock.core.util.RequestAttr;
import com.gome.test.mock.core.util.SessionAttr;

public class SimpleRequest<Q> implements Request<Q> {
    
    private long order;
    private Message<Q> msg;
    
    public SimpleRequest(Message<Q> msg){
        this.msg = msg;
        order = msg.getRequestAttr().order();
    }

    @Override
    public long order() {
        return order;
    }

    @Override
    public boolean hasPojo() {
         return msg.hasPojo();
    }

    @Override
    public Q pojo() {
        return msg.getPojo();
    }

    @Override
    public void pojo(Q pojo) {
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
    
}
