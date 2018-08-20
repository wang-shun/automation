package com.gome.test.mock.util;

import com.gome.test.mock.common.Command;
import com.gome.test.mock.utils.Logger;

public class TimeoutThread extends Thread {

    private static final Logger logger = new Logger(Command.class);
    /**
     * 计时器超时时间
     */
    private long timeout = 0;
    /**
     * 计时是否被取消
     */
    //private boolean isCanceled = false;
    /**
     * 当计时器超时时抛出的异常
     */
    private final TimeoutException timeoutException;

    /**
     * 构造器
     * @param timeout 指定超时的时间
     */
    public TimeoutThread(long timeout, TimeoutException timeoutErr) {
        super();
        this.timeout = timeout;
        this.timeoutException = timeoutErr;
        //设置本线程为守护线程
        //this.setDaemon(true);
    }

    /**
     * 取消计时
     */
    public synchronized void cancel() {
        //this.isCanceled = true;
    }

    /**
     * 启动超时计时器
     */
    @Override
    public void run() {
        try {
            Thread.sleep(this.timeout);
            //if (!this.isCanceled) {
            throw this.timeoutException;
            //}
        } catch (InterruptedException e) {
            logger.error("超时线程错误：", e.getMessage());
            e.printStackTrace();
        }
    }
}
