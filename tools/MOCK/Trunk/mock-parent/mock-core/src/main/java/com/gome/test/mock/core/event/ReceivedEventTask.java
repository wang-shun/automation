package com.gome.test.mock.core.event;


import com.gome.test.mock.core.Channel;
import com.gome.test.mock.core.Message;

public class ReceivedEventTask<O, I> extends EventTask<O, I>{

    private Message<I> msg;
    
    public ReceivedEventTask(Channel<O, I> channel, Message<I> msg){
        super(channel);
        this.msg = msg;
    }
    
    @Override
    protected void doEvent(Channel<O, I> channel) throws Exception {
        channel.fireReceived(msg);
    }

}
