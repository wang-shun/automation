package com.gome.test.mock.util;

import java.text.MessageFormat;

import com.gome.test.mock.utils.Logger;

public class Delayer {
    private static final Logger logger = new Logger(Delayer.class);
    private static final int MILIS_IN_SEC = 1000;

    public void delaySec(int sec) {
        if (sec == 0) {
            return;
        }
        logger.debug(MessageFormat.format("Delaying request for {0} seconds.", sec));
        try {
            Thread.sleep(sec * MILIS_IN_SEC);
        } catch (InterruptedException e) {
            logger.error("延时异常：" + e.getMessage());
        }
    }
}
