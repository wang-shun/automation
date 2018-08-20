package com.gome.test.order.process;

/**
 * Created by zengjiyang on 2016/2/16.
 */

import com.gome.test.order.process.schedule.ProcessOrderJob;
import com.gome.test.order.process.util.GUI;
import gyoung.dubbo.demo.api.OrderMessage;
import gyoung.dubbo.demo.provider.OrderServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class OrderProcessProvider {

    private static ClassPathXmlApplicationContext context;

    public static ClassPathXmlApplicationContext getContext() {
        return context;
    }

    public static void main(String[] args) throws IOException {
        context = new ClassPathXmlApplicationContext("classpath*:dubbo-orderProcess-provider.xml");
        context.start();
        System.out.println("服务已经启动...");
        GUI.guiLogin();
        //test();
        System.in.read();
    }


    public static void test(){
        ProcessOrderJob ps = new ProcessOrderJob();
        ps.testG3PP();
//        ps.processOrder();
    }



}