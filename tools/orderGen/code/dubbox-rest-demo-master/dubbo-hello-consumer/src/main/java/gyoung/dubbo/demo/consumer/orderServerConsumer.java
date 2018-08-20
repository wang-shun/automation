package gyoung.dubbo.demo.consumer;

import gyoung.dubbo.demo.api.OrderMessage;
import gyoung.dubbo.demo.api.OrderService;
import gyoung.dubbo.demo.api.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangwei-ds on 2017/2/25.
 */
public class orderServerConsumer {


    public static void main(String[] args){

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:dubbo-hello-consumer.xml");

        OrderService userService = context.getBean(OrderService.class);
        OrderMessage ss = userService.defaultOrder();
        System.out.println(ss.toString());

    }
}
