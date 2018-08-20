package com.gome.test.mock.core.event;

import java.util.concurrent.Executor;

public interface ExecutorFactory {
    
    public Executor newExecutor();

}
