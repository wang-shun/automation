package com.gome.test.mock.core.handler;


import com.gome.test.mock.core.Codec;
import com.gome.test.mock.core.HandlerContext;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.core.Promise;
import com.gome.test.mock.core.message.SimpleRequest;
import com.gome.test.mock.core.message.SimpleResponse;
import com.gome.test.mock.utils.Logger;

public class CodecStubHandler<O, I> extends HandlerAdapter<O, I> {
    private static Logger log = new Logger(CodecStubHandler.class);
    
    
    private Codec<I, O> codec;
    
    public CodecStubHandler(Codec<I, O> codec){
        this.codec = codec;
    }

    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {
        
        SimpleRequest<I> request = new SimpleRequest<I>(msg);
        codec.decodeRequest(msg.getBuffer(), request);
        if( request.hasPojo() ){
            ctx.fireReceived(msg);
        }else{
            log.debug("Inbound interrupted because of decodeRequest fail.");
        }
        
    }

    @Override
    public void doSend(HandlerContext<O, I> ctx, Message<O> msg, Promise<O, I> promise) throws Exception {
        
        SimpleResponse<O> response = new SimpleResponse<O>(msg);
        codec.encodeResponse(response, msg.getBuffer());
        
        if( msg.getBuffer().isReadable() ){
            ctx.send(msg, promise);
        }else{
            log.warn("Outbound interrupted because of encodeResponse fail.");
        }
        
    }

    
}
