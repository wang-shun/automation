package com.gome.test.mock.core.message;


import com.gome.test.mock.core.Buffer;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.core.netty.NettyBuffer;
import com.gome.test.mock.core.util.RequestAttr;
import com.gome.test.mock.core.util.SessionAttr;

public class SimpleMessage<T> implements Message<T> {
    
    protected T pojo;
    protected Buffer buffer;
    protected RequestAttr requestAttr;
    protected SessionAttr sessionAttr;

    public SimpleMessage(){
        this(null, new NettyBuffer());
    }

    public SimpleMessage(T pojo){
        this(pojo, new NettyBuffer());
    }
    
    public SimpleMessage(Buffer buffer){
        this(null, buffer);
    }
    
    public SimpleMessage(T pojo, Buffer buffer){
        this.pojo = pojo;
        this.buffer = buffer;
    }

    @Override
    public boolean hasPojo() {
        return pojo != null;
    }

    @Override
    public T getPojo() {
        return pojo;
    }

    @Override
    public void setPojo(T t) {
        this.pojo = t;
    }

    @Override
    public Buffer getBuffer() {
        return buffer;
    }

    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }
    
    @Override
    public void clear(){
        this.buffer.clear();
        this.pojo = null;
    }
    
    @Override
    public boolean hasBuffer() {
        return buffer != null;
    }

    @Override
    public RequestAttr getRequestAttr() {
        return requestAttr;
    }
    
    public void setRequestAttr(RequestAttr requestAttr) {
        this.requestAttr = requestAttr;
    }

    @Override
    public SessionAttr getSessionAttr() {
        return sessionAttr;
    }

    public void setSessionAttr(SessionAttr sessionAttr) {
        this.sessionAttr = sessionAttr;
    }

    @Override
    public <K> Message<K> newMessage() {
        SimpleMessage<K> msg = new SimpleMessage<K>();
        msg.setRequestAttr(requestAttr);
        msg.setSessionAttr(sessionAttr);
        return msg;
    }

}
