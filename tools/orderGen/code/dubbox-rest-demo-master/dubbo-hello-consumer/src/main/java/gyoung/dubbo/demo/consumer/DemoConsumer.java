package gyoung.dubbo.demo.consumer;

/**
 * Created by zengjiyang on 2016/2/16.
 */
import gyoung.dubbo.demo.api.OrderMessage;
import gyoung.dubbo.demo.api.OrderService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import gyoung.dubbo.demo.api.UserService;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class DemoConsumer {

    public static void main(String[] args) {
        final String port = "8899";

        //测试Rest服务
        getUser("http://localhost:" + port + "/services/users/1.json");
        getUser("http://localhost:" + port + "/services/users/1.xml");
        //getUser("http://localhost:" + port + "/services/liangwei");

        //测试常规服务
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:dubbo-hello-consumer.xml");
        context.start();
        UserService userService = context.getBean(UserService.class);

        System.out.println(userService.getUser(1L));

        OrderService orderService = context.getBean(OrderService.class);
        OrderMessage ss = orderService.defaultOrder();
        System.out.println(ss.getOrderId());
        List<String> pro = new ArrayList<String>();
        pro.add("http://item.atguat.com.cn/9010006305-1000059964.html?intcmp=list-9000000700-1_3_1");
        String count = "1";
        OrderMessage orderId = orderService.getOrder("","","",count,"",pro);
        System.out.println(orderId.getOrderId());
        context.destroy();
    }


    private static void getUser(String url) {
        System.out.println("Getting user via " + url);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Response response = target.request().get();
        try {
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed with HTTP error code : " + response.getStatus());
            }
            System.out.println("Successfully got result: " + response.readEntity(String.class));
        } finally {
            response.close();
            client.close();
        }
    }
}