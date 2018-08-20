package com.gome.test.mock.core.handler;


import com.gome.test.mock.core.*;
import com.gome.test.mock.core.message.SimpleResponse;
import com.gome.test.mock.core.stress.Bullet;
import com.gome.test.mock.core.stress.Clip;
import com.gome.test.mock.core.stress.Collector;
import com.gome.test.mock.exception.MyException;
import com.gome.test.mock.utils.Logger;

/**
 * TODO reconstruction
 * 
 * @author wyhanyu
 *
 */
public class StressStubHandler<O, I> extends HandlerAdapter<O, I> {
    private static Logger log = new Logger(StressStubHandler.class);
    
    private Collector<O, I> collector;
    public void setCollector(Collector<O, I> collector){
        this.collector = collector;
    }
    
    private Clip<O> clip;
    public void loadClip(Clip<O> clip){
        this.clip = clip;
    }
    
    private boolean keepalive;
    public void setKeepAlive(boolean keepalive){
        this.keepalive = keepalive;
    }
    
    private Pipe<I, O> pipe;
    public StressStubHandler(Pipe<I, O> pipe){
        this.pipe = pipe;
    }
    
    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {
        if( null != collector ){
            //TODO
        }
        
        Bullet<O> bullet = clip.take();
        
        if( bullet == null ){
            log.info("Clip is empty. Close connection.");
            ctx.disconnect();
            return;
        }
        
        if( ! bullet.isReady() ){
            // make fired
            synchronized(bullet){
                if( ! bullet.isReady() ){
                    Message<O> newmsg = msg.newMessage();
                    SimpleResponse<O> res = new SimpleResponse<O>(newmsg);
                    res.pojo(bullet.getPojo());
                    Buffer buf = newmsg.getBuffer();
                    pipe.encodeResponse(res, buf);
                    
                    if( ! bullet.isReady() ){
                        log.error("Fire bullet, but encodeResponse fail.");
                        throw new MyException("Fire bullet, but encodeResponse fail.");
                    }
                    
                }
            }
        }
        
        Future<O, I> future = ctx.send(bullet);
        if( ! keepalive ){
            future.addListener(FutureListener.CLOSE_ON_COMPLETE);
        }
    }
    
    
}



