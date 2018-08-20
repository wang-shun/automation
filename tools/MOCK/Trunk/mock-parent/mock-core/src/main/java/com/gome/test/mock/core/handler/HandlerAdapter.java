package com.gome.test.mock.core.handler;

import com.gome.test.mock.core.Handler;
import com.gome.test.mock.core.HandlerContext;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.core.Promise;

/**
 *
 * @author hannyu
 *
 * @param <O>
 * @param <I>
 */
public class HandlerAdapter<O, I> implements Handler<O, I> {

    @Override
    public void doHandlerAdded(HandlerContext<O, I> ctx) {
        // do nothing
    }

    @Override
    public void doHandlerRemoved(HandlerContext<O, I> ctx) {
        // do nothing
    }

    @Override
    public void doSend(HandlerContext<O, I> ctx, Message<O> msg, Promise<O, I> promise) throws Exception {
        ctx.send(msg, promise);
    }

    @Override
    public void doDisconnect(HandlerContext<O, I> ctx, Promise<O, I> promise) throws Exception {
        ctx.disconnect(promise);
    }

    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {
        ctx.fireReceived(msg);
    }

    @Override
    public void doConnected(HandlerContext<O, I> ctx) throws Exception {
        ctx.fireConnected();
    }

    @Override
    public void doDisconnected(HandlerContext<O, I> ctx) throws Exception {
        ctx.fireDisconnected();
    }

    @Override
    public void doCaught(HandlerContext<O, I> ctx, Throwable cause) throws Exception {
        ctx.fireCaught(cause);
    }

}
