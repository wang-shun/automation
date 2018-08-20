package com.gome.test.mock.core.event;

import java.util.concurrent.Executor;

/**
 * 
 * @author hannyu
 *
 */
public class CurrentThreadEventExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        command.run();
    }

}
