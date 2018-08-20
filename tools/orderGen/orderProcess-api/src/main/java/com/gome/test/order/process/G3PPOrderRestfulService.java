package com.gome.test.order.process;

/**
 * Created by liangwei-ds on 2017/3/27.
 */
public interface G3PPOrderRestfulService {
    Order processOrder(String orderNos, String productType,  String sendTo,  String dzxh, String spbm, String dzck , String dzkq,String pch);
    String processOrders(String orderNos, String productType, String expectStatus, String sendTo,  String dzxh, String spbm, String dzck , String dzkq,String pch);

}
