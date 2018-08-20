package com.gome.test.api.environment;


import com.gome.test.api.annotation.SetUp;
import com.gome.test.api.environment.EnvManager;

public class SamplesEnvManager extends EnvManager {

    @SetUp(description = "延迟[millis]毫秒")
    public void delay(long millis) throws Exception {
        Thread.sleep(millis);
    }
}
