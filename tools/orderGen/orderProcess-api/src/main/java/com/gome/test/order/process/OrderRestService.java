package com.gome.test.order.process;

/**
 * Created by hacke on 2017/2/27.
 */
public interface OrderRestService {

    Order processOrder(String orderNo , String productType,String sendTo);
    String processOrdersNew(String orderNo , String expectStatus,String sendTo,  String dzxh,  String dzck ,String pch);

    String  processOrders(String orderNo , String productType,String expectStatus,String sendTo);

    String processReturnOrders(String username ,String  password ,String orderNos , String returnType ,String sendTo);
}
