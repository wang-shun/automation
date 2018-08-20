package com.gome.test.mock.core.stress;


import com.gome.test.mock.core.Buffer;
import com.gome.test.mock.core.message.SimpleMessage;

/**
 * 子弹
 * 
 * @author wyhanyu
 *
 * @param <Q>
 */
public class SimpleBullet<Q> extends SimpleMessage<Q> implements Bullet<Q> {

    public SimpleBullet(Q pojo){
        super(pojo);
    }
    
    @Override
    public boolean isReady() {
        return hasBuffer() && buffer.isReadable();
    }

    @Override
    public Buffer copyBuffer() {
        // NOT copy()
        return getBuffer().duplicate();
    }
    
    @Override
    public void buffer(Buffer buf){
        super.buffer = buf.copy();
    }
    
}
