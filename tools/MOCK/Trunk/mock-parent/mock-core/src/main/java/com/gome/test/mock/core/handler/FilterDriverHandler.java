package com.gome.test.mock.core.handler;


import com.gome.test.mock.core.Filter;
import com.gome.test.mock.core.HandlerContext;
import com.gome.test.mock.core.Message;
import com.gome.test.mock.utils.Logger;

;


public class FilterDriverHandler<O, I> extends HandlerAdapter<O, I> {
    private static final Logger log = new Logger(FilterDriverHandler.class);
    
    private Filter<O, I> filter;
    public FilterDriverHandler(Filter<O, I> filter){
        this.filter = filter;
    }

    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {
        if( filter.filterReceivedInDriver(ctx, msg) ){
            // do nothing
            log.debug("Filtered #{}.", msg.getRequestAttr().order());
        }else{
            ctx.fireReceived(msg);
        }
    }

}
