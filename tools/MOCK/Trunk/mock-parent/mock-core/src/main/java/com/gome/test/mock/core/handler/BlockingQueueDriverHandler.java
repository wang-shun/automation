package com.gome.test.mock.core.handler;

import com.gome.test.mock.core.HandlerContext;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.utils.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueDriverHandler<O, I> extends HandlerAdapter<O, I> {
    private static Logger log = new Logger(BlockingQueueDriverHandler.class);
    
    private HandlerContext<O, I> ctx;
    private BlockingQueue<I> blockingQueue;//阻塞队列，默认容量为Integer.MAX_VALUE
    private volatile O candidate;//volatile多线程访问修改后的变量
    
    public BlockingQueueDriverHandler(){
        blockingQueue = new LinkedBlockingQueue<I>();
    }
    
    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg){
        log.debug("doReceived(): Got it.");
        
        blockingQueue.add(msg.getPojo());
        ctx.fireReceived(msg);
    }
    
    @Override
    public void doConnected(HandlerContext<O, I> ctx) throws Exception {
        this.ctx = ctx;
        synchronized(this){
            if( null == candidate ){
                log.debug("doConnected(): NO candidate to send.");
            }else{
                log.debug("doConnected(): Send a candidate.");
                ctx.send(candidate);
            }
        }
        
        ctx.fireConnected();
    }
    
    /**
     * 必须在connect之后
     * @param pojo
     */
    public void send(O pojo) throws Exception {
        if( null == ctx ){
            synchronized(this){
                if( null == candidate ){
                    candidate = pojo;
                    log.debug("send(): Before connected, hold.");
                }else{
                    log.error("send(): Connection is not established.");
                }
            }
        }else{
            ctx.send(pojo);
        }
    }
    
    public I recv() throws InterruptedException{
        log.debug("recv(): Wait for receiving one.");
        return blockingQueue.take();
    }


}
