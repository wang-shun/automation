package com.gome.test.mock.core.handler;

import com.gome.test.mock.core.Codec;
import com.gome.test.mock.core.HandlerContext;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.core.Promise;
import com.gome.test.mock.core.message.SimpleRequest;
import com.gome.test.mock.core.message.SimpleResponse;
import com.gome.test.mock.utils.Logger;

;


/**
 * 负责Driver的编解码
 * 
 * @author wyhanyu
 *
 * @param <O>
 * @param <I>
 */
public class CodecDriverHandler<O, I> extends HandlerAdapter<O, I> {
    private static Logger log = new Logger(CodecDriverHandler.class);
    
    
    private Codec<O, I> codec;
    
    public CodecDriverHandler(Codec<O, I> codec){
        this.codec = codec;
    }

    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {

        SimpleResponse<I> response = new SimpleResponse<I>(msg);
        codec.decodeResponse(msg.getBuffer(), response);
        if( response.hasPojo() ){
            ctx.fireReceived(msg);
        }else{
            log.debug("Inbound interrupted because of decodeResponse fail.");
        }
        
    } 
    
    @Override
    public void doSend(HandlerContext<O, I> ctx, Message<O> msg,
            Promise<O, I> promise) throws Exception {

        SimpleRequest<O> request = new SimpleRequest<O>(msg);
        codec.encodeRequest(request, msg.getBuffer());
        
        if( msg.getBuffer().isReadable() ){
            ctx.send(msg, promise);
        }else{
            log.warn("Outbound interrupted because of encodeRequest fail.");
        }
        
    }

    
}
