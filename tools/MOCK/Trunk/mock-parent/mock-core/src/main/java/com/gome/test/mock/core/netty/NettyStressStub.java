package com.gome.test.mock.core.netty;


import com.gome.test.mock.core.stress.Clip;
import com.gome.test.mock.core.stub.StressStub;
import com.gome.test.mock.core.HandlerChain;
import com.gome.test.mock.core.Pipe;
import com.gome.test.mock.core.handler.CodecStubHandler;
import com.gome.test.mock.core.handler.HandlerInitializer;
import com.gome.test.mock.core.handler.StressStubHandler;


/**
 * 压力Stub
 * 
 * @author wyhanyu
 * 
 * @param <Q>
 * @param <R>
 */
public class NettyStressStub<Q, R> extends NettySimpleStub<Q, R> implements StressStub<Q, R> {
    
    private StressStubHandler<R, Q> stressStubHandler;

    public NettyStressStub(final Pipe<Q, R> pipe) {
        super(pipe);
    }
    
    @Override
    protected void initStubHandler(){

        stressStubHandler = new StressStubHandler<R, Q>(pipe);
        final CodecStubHandler<R, Q> codecStubHandler = new CodecStubHandler<R, Q>(pipe);
        
        stubInitializer = new HandlerInitializer<R, Q>() {
            @Override
            public void initHandler(HandlerChain<R, Q> chain) {
                chain.addLast("codecStubHandler", codecStubHandler);
                chain.addLast("stressStubHandler", stressStubHandler);
            }
        };
    }
    
    @Override
    public void loadClip(Clip<R> clip) {
        if( null == clip ) {
            throw new IllegalArgumentException("Clip should NOT be null.");
        }
        stressStubHandler.loadClip(clip);
    }
    
    @Override
    public void setKeepAlive(boolean keepalive){
        stressStubHandler.setKeepAlive(keepalive);
    }
    
	
}
