package gyoung.dubbo.demo.consumer;

import com.gome.test.order.process.Order;
import com.gome.test.order.process.OrderService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class DemoConsumer {

    public static void main(String[] args) {
        final String port = "9999";
        final String ip = "10.126.57.2";
        final String lip = "127.0.0.1";
//        getOrder();

        //测试Rest服务 getTest
//        getOrder("http://"+lip+":" + port + "/orderProcess/detail/getOrder&orderNo=11111?productType=3pp");
        getOrder("http://"+lip+":" + port + "/services/detail/111/2222");
//        getUser("http://localhost:" + port + "/services/users/1.xml");

        //测试常规服务
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:dubbo-orderProcess-consumer.xml");
//        context.start();
//        UserService userService = context.getBean(UserService.class);
//        System.out.println(userService.getUser(1L));
    }


    private static void getOrder(){
        System.out.println("get order through dubbo.");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:dubbo-orderProcess-consumer.xml");
        context.start();
        OrderService orderService = context.getBean(OrderService.class);
        //System.out.println(orderService.processOrder("111a","222").getOrderNo());
    }

    private static void getOrder(String url) {
        System.out.println("Getting order via " + url);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Response response = target.request().get();
        try {
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + response.getStatus());
            }
            System.out.println("Successfully got result: " + response.readEntity(Order.class));
        } finally {
            response.close();
            client.close();
        }
    }
}