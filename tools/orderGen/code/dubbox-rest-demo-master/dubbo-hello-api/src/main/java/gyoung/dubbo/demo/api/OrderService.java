package gyoung.dubbo.demo.api;

import java.util.List;

/**
 * Created by liangwei-ds on 2016/9/23.
 */
public interface OrderService {
    OrderMessage defaultOrder();
    //OrderMessage doTest();
    OrderMessage getOrder(String User, String password, String orderType, String procount, String address, List<String> pro);
}
