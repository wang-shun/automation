package com.gome.test.mock.core.stub;


/**
 * TODO
 * 异步代理，可用于一些性能场景。
 * 
 * @author wyhanyu
 * 
 */
public interface ProxyStub<Q, R> extends Stub<Q, R> {

    public void setForwardAddr(String addr);
    
    public void setForwardAddr(int port);
    
}
