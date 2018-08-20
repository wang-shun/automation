package com.gome.test.mock;

import com.gome.test.mock.common.WorkContext;

public abstract class Computer {

    //流程上下文对象
    public static WorkContext workContext = null;

    //待实现的计算方法
    public abstract Object compute(Object... args);

    public static WorkContext getWorkContext() {
        return workContext;
    }

    public static void setWorkContext(WorkContext workContext) {
        Computer.workContext = workContext;
    }

}
