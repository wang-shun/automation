package com.gome.test.mock.core.event;


import com.gome.test.mock.core.Channel;
import com.gome.test.mock.utils.Logger;

public abstract class EventTask<O, I> implements Runnable {
    private static final Logger log = new Logger(EventTask.class);
    
    private Channel<O, I> channel;
    
    public EventTask(Channel<O, I> channel){
        this.channel = channel;
    }
    
    @Override
    public void run(){
        try{
            doEvent(channel);
        }catch(Exception e){
            log.warn("{}.doEvent got an Exception.", this.getClass(), e);
        }
    }

    protected abstract void doEvent(Channel<O, I> channel) throws Exception;
    
}
