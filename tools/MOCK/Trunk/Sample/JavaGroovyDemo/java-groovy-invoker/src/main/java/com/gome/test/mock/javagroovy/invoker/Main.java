package com.gome.test.mock.javagroovy.invoker;

import java.io.IOException;

/**
 * Created by lizonglin on 2015/9/22/0022.
 */
public class Main {

    public static void main(String[] args) throws IllegalAccessException, IOException, InstantiationException, InterruptedException {
        TestService testService = new TestService();

//        testService.service_1();
//        testService.service_2();
//        testService.service_3();
//        testService.service_4();

        /**
         * 验证Java进程执行时修改Groovy是否生效
         */
//        for (int i = 0; i < 5; i++) {
//            System.out.println(String.format("The %dth run service_1()", i+1));
//            testService.service_1();
//            TimeUnit.SECONDS.sleep(10);
//        }

        System.out.println("Begin at: " + System.currentTimeMillis());
        for (int i = 0; i < 100; i++) {
            System.out.println(String.format("The %dth run service_1()", i+1));
            testService.service_1();
        }
        System.out.println("End at: " + System.currentTimeMillis());
    }
}
