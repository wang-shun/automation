package com.gome.test.mock.core.handler;


import com.gome.test.mock.core.*;
import com.gome.test.mock.core.driver.StressDriver;
import com.gome.test.mock.core.message.SimpleRequest;
import com.gome.test.mock.core.stress.Bullet;
import com.gome.test.mock.core.stress.Clip;
import com.gome.test.mock.exception.MyException;
import com.gome.test.mock.utils.Logger;

/**
 * 负责收集压力数据
 *
 * @author wyhanyu
 *
 * @param <O>
 * @param <I>
 */
public class StressDriverHandler<O, I> extends HandlerAdapter<O, I> {
    private static final Logger log = new Logger(StressDriverHandler.class);

    private final StressDriver<O, I> stressDriver;
    private final Pipe<O, I> pipe;

    public StressDriverHandler(StressDriver<O, I> stressDriver, Pipe<O, I> pipe) {
        this.stressDriver = stressDriver;
        this.pipe = pipe;
    }

    public static final int DEFAULT_CONCURRENCY_INT = 10;

    private int concurrency = DEFAULT_CONCURRENCY_INT;
    private int tpsLimit = 0;
    private boolean keepalive = false;

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public void setTpsLimit(int tpsLimit) {
        this.tpsLimit = tpsLimit;
    }

    public void setKeepalive(boolean keepalive) {
        this.keepalive = keepalive;
    }

    private Clip<O> clip;

    public void loadClip(Clip<O> clip) {
        this.clip = clip;
    }

    private void sendBullet(HandlerContext<O, I> ctx) throws Exception {
        Bullet<O> bullet = this.clip.take();
        if (bullet == null) {
            log.info("Clip is empty, connection SHOULD close.");
            ctx.disconnect();
        } else {
            if (!bullet.isReady()) {
                synchronized (bullet) {
                    if (!bullet.isReady()) {
                        Message<O> newmsg = ctx.channel().newMessage(bullet.getPojo());
                        SimpleRequest<O> res = new SimpleRequest<O>(newmsg);
                        Buffer buf = newmsg.getBuffer();
                        this.pipe.encodeRequest(res, buf);

                        if (!bullet.isReady()) {
                            log.error("Fire bullet, but encodeRequest fail.");
                            throw new MyException("Fire bullet, but encodeRequest fail.");
                        }
                    }
                }
            }

            Future<O, I> future = ctx.send(bullet);
            if (!this.keepalive) {
                future.addListener(FutureListener.CLOSE_ON_COMPLETE);
            }
        }
    }

    private void newConnection() {
        this.stressDriver.newConnection();
    }

    @Override
    public void doConnected(HandlerContext<O, I> ctx) throws Exception {
        // do send
        this.sendBullet(ctx);
    }

    @Override
    public void doDisconnected(HandlerContext<O, I> ctx) throws Exception {
        if (!this.clip.empty()) {
            this.newConnection();
        }
    }

    @Override
    public void doReceived(HandlerContext<O, I> ctx, Message<I> msg) throws Exception {

        if (this.keepalive) {
            this.sendBullet(ctx);
        } else {
            ctx.disconnect();
        }

        ctx.fireReceived(msg);
    }

    @Override
    public void doCaught(HandlerContext<O, I> ctx, Throwable cause) throws Exception {
        log.error("what????", cause);
        ctx.fireCaught(cause);
    }

    public void bench() {
        this.bench(this.concurrency, false, 0);
    }

    public void bench(int concurrency) {
        this.bench(concurrency, false, 0);
    }

    public void bench(int concurrency, boolean keepalive) {
        this.bench(concurrency, keepalive, 0);
    }

    public void bench(int concurrency, boolean keepalive, int tpsLimit) {
        this.concurrency = concurrency;
        this.keepalive = keepalive;
        this.tpsLimit = tpsLimit;

        for (int i = 0; i < concurrency; i++) {
            this.newConnection();
        }
    }
}
