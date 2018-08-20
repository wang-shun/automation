package com.gome.test.mock.core.event;


import com.gome.test.mock.core.Channel;

public class DisconnectedEventTask<O, I> extends EventTask<O, I>{
    
    public DisconnectedEventTask(Channel<O, I> channel) {
        super(channel);
    }
    
    @Override
    protected void doEvent(Channel<O, I> channel) throws Exception {
        channel.fireDisconnected();
    }
    
}
