package com.gome.test.mock.core.stub;


import com.gome.test.mock.core.stress.Clip;

/**
 * 压力Stub
 * 
 * @author wyhanyu
 *
 * @param <Q>
 * @param <R>
 */
public interface StressStub<Q, R> extends Stub<Q, R> {
    
    public void loadClip(Clip<R> clip);
    
    public void setKeepAlive(boolean keepalive);
    
}
