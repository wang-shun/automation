package com.gome.test.mock.core.handler;


import com.gome.test.mock.core.Filter;
import com.gome.test.mock.core.HandlerContext;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.utils.Logger;

public class FilterStubHandler<O, I> extends HandlerAdapter<O, I> {
    private static final Logger log = new Logger(FilterStubHandler.class);
    
    private Filter<I, O> filter;
    public FilterStubHandler(Filter<I, O> filter){
        this.filter = filter;
    }

    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {
        if( filter.filterReceivedInStub(ctx, msg) ){
            // do nothing
            log.debug("Filtered #{}.", msg.getRequestAttr().order());
        }else{
            ctx.fireReceived(msg);
        }
    }

}
