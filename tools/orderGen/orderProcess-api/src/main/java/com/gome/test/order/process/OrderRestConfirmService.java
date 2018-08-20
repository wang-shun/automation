package com.gome.test.order.process;

/**
 * Created by hacke on 2017/2/27.
 */
public interface OrderRestConfirmService {

    String processConfirmReceipt(String username, String password, String orderNos);
}
