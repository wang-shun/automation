package com.gome.test.mock.core.netty;


import com.gome.test.mock.core.HandlerChain;
import com.gome.test.mock.core.Pipe;
import com.gome.test.mock.core.driver.StressDriver;
import com.gome.test.mock.core.handler.CodecDriverHandler;
import com.gome.test.mock.core.handler.HandlerInitializer;
import com.gome.test.mock.core.handler.StressDriverHandler;
import com.gome.test.mock.core.stress.BenchResult;
import com.gome.test.mock.core.stress.Clip;
import com.gome.test.mock.core.stress.Collector;

import java.net.InetSocketAddress;

public class NettyStressDriver<Q, R> extends NettySimpleDriver<Q, R> implements StressDriver<Q, R> {
    
    private StressDriverHandler<Q, R> stressDriverHandler;
    
    
    public NettyStressDriver(Pipe<Q, R> pipe) {
        super(pipe);
    }
    
    
    protected void initDriverHandler(){
        stressDriverHandler = new StressDriverHandler<Q, R>(this, pipe);
        final CodecDriverHandler<Q, R> codecDriverHandler = new CodecDriverHandler<Q, R>(pipe);
        
        stubInitializer = new HandlerInitializer<Q, R>(){
            @Override
            public void initHandler(HandlerChain<Q, R> chain) {
                chain.addLast("CodecDriverHandler", codecDriverHandler);
                chain.addLast("StressDriverHandler", stressDriverHandler);
            }
        };
    }
    
    private InetSocketAddress sockaddr;
    @Override
    public void newConnection() {
        if( sockaddr == null ){
            sockaddr = new InetSocketAddress(addr, port);
        }
        bootstrap.connect(sockaddr).channel();
    }

    @Override
    public void loadClip(Clip<Q> clip) {
        if( null == clip ) {
            throw new IllegalArgumentException("Clip should NOT be null.");
        }
        stressDriverHandler.loadClip(clip);
    }
    
    @Override
    public BenchResult bench() {
        stressDriverHandler.bench();
        return null;
    }

    @Override
    public BenchResult bench(int concurrency) {
        stressDriverHandler.bench(concurrency);
        return null;
    }
    
    @Override
    public BenchResult bench(int concurrency, boolean keepalive) {
        stressDriverHandler.bench(concurrency, keepalive);
        return null;
    }

    @Override
    public BenchResult bench(int concurrency, boolean keepalive, int tpsLimit) {
        stressDriverHandler.bench(concurrency, keepalive, tpsLimit);
        return null;
    }

    @Override
    public boolean isOver() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void hold() {
        // TODO Auto-generated method stub
    }

    @Override
    public void holdUntilFiredNum(long num) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void holdUntilSeconds(int seconds) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCollector(Collector<Q, R> collector) {
        // TODO Auto-generated method stub
        
    }

    
    @Override
    public void release() {
        // TODO Auto-generated method stub
        
    }
    
}
