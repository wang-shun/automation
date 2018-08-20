package com.gome.test.order.process;

/**
 * Created by hacke on 2017/2/27.
 */
public interface OrderService {

    //3PP订单处理
    Order processOrder(String orderNo , String productType ,String sendTo);
    String processOrders(String orderNos , String productType , String expectStatus,String sendTo);
    String processOrdersNew(String orderNo , String expectStatus,String sendTo,String dzxh,  String dzck , String pch);

    String processReturnOrders(String username ,String  password ,String orderNos , String returnType ,String sendTo);
}
