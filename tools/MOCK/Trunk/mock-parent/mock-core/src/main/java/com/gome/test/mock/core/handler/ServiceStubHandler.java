package com.gome.test.mock.core.handler;

import com.gome.test.mock.core.*;
import com.gome.test.mock.core.message.SimpleRequest;
import com.gome.test.mock.core.message.SimpleResponse;
import com.gome.test.mock.utils.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 用于实现service，
 * 在HandlerChain中，必须在CodecStubHandler的后面。
 *
 * @author hannyu
 *
 * @param <O>
 * @param <I>
 */
public class ServiceStubHandler<O, I> extends HandlerAdapter<O, I> {
    private static final Logger log = new Logger(ServiceStubHandler.class);

    private final Map<String, Service<I, O>> serviceChain;

    public ServiceStubHandler() {
        this.serviceChain = new LinkedHashMap<String, Service<I, O>>();
    }

    public void addService(String name, Service<I, O> service) {
        this.serviceChain.put(name, service);
    }

    public void removeService(String name) {
        this.serviceChain.remove(name);
    }

    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {

        if (this.serviceChain.size() == 0) {
            log.warn("Ignored because there is NO service.");
        } else {
            SimpleRequest<I> request = new SimpleRequest<I>(msg);
            Message<O> resmsg = msg.newMessage();
            SimpleResponse<O> response = new SimpleResponse<O>(resmsg);

            Iterator<Entry<String, Service<I, O>>> it = this.serviceChain.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Service<I, O>> en = it.next();
                String name = en.getKey();
                Service<I, O> service = en.getValue();
                log.debug("doService {}", name);
                service.doService(request, response);
                if (response.isDone()) {
                    break;
                }
            }

            if (response.hasPojo()) {
                Future<O, I> future = ctx.send(resmsg);
                if (response.isDisconnectOnComplete()) {
                    future.addListener(FutureListener.CLOSE_ON_COMPLETE);
                }
            } else {
                log.debug("Do all service done, but no response.");
                if (response.isDisconnectOnComplete()) {
                    ctx.disconnect();
                }
            }

        }

        // why do this?
        ctx.fireReceived(msg);
    }

}
