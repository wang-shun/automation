package com.gome.test.pom.cache;

import com.gome.test.pom.POJO.OrderInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hacke on 2016/9/25.
 */
public class OrderCache {
    private static OrderCache ourInstance = new OrderCache();
    private Map<String , Map> orders = new HashMap<String , Map>();
    public static OrderCache getInstance() {
        return ourInstance;
    }

    private OrderCache() {
    }

    public void setOrderInfo2Context(String orderID , Map orderInfo){
        orders.put(orderID , orderInfo);
    }

    public Map getOrderInfoFromContext(String orderID){
        return orders.get(orderID);
    }

}
