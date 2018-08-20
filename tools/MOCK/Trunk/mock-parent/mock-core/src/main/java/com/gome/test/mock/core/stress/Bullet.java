package com.gome.test.mock.core.stress;


import com.gome.test.mock.core.Buffer;
import com.gome.test.mock.core.Message;

/**
 * 子弹
 * 
 * @author wyhanyu
 *
 * @param <Q>
 */
public interface Bullet<Q> extends Message<Q> {
    
    
    public boolean isReady();
    
    public Buffer copyBuffer();
    
    public void buffer(Buffer buf);
    
}
