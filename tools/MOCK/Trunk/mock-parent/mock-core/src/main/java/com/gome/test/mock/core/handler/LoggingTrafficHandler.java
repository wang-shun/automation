package com.gome.test.mock.core.handler;


import com.gome.test.mock.core.HandlerContext;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.core.Promise;
import com.gome.test.mock.utils.Logger;

public class LoggingTrafficHandler<O, I> extends HandlerAdapter<O, I> {
    private static final Logger log = new Logger(LoggingTrafficHandler.class);

    //入站方法
    @Override
    public void doSend(HandlerContext<O, I> ctx, Message<O> msg, Promise<O, I> promise) throws Exception {
        log.info("Send {} bytes to {}.", msg.getBuffer().readableBytes(), ctx.channel().getRemoteAddress().toString());
        ctx.send(msg, promise);
    }

    //出站方法
    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {
        log.info("Received {} bytes from {}.", msg.getBuffer().readableBytes(), ctx.channel().getRemoteAddress().toString());
        ctx.fireReceived(msg);
    }

}
