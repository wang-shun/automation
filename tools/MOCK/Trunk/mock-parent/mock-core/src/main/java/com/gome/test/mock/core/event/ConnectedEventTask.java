package com.gome.test.mock.core.event;


import com.gome.test.mock.core.Channel;

public class ConnectedEventTask<O, I> extends EventTask<O, I>{
    
    public ConnectedEventTask(Channel<O, I> channel) {
        super(channel);
    }
    
    @Override
    protected void doEvent(Channel<O, I> channel) throws Exception {
        channel.fireConnected();
    }
    
}
