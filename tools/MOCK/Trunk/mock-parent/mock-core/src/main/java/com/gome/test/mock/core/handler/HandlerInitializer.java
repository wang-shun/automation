package com.gome.test.mock.core.handler;


import com.gome.test.mock.core.HandlerChain;
import com.gome.test.mock.core.HandlerContext;

public abstract class HandlerInitializer<O, I> extends HandlerAdapter<O, I> {
    

    public abstract void initHandler(HandlerChain<O, I> chain);
    
    @Override
    public void doHandlerAdded(HandlerContext<O, I> ctx) {
        HandlerChain<O, I> chain = ctx.chain();
        
        initHandler(chain);
        chain.remove(this);
    }
    
    
}
