package com.gome.test.api.ide;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {

    private CountDownLatch latch;

    public void setCountDownLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public void onApplicationEvent(ContextClosedEvent event) {
        if (null != latch) {
            latch.countDown();
        }
    }
}
