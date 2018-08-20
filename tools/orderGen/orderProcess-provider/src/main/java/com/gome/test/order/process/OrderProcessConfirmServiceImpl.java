package com.gome.test.order.process;

import com.gome.test.order.process.cache.ProcessedOrder;
import com.gome.test.order.process.com.gome.test.buess.ConfirmReceipt.ConfirmReceipt;
import com.gome.test.order.process.util.OrderUtils;

/**
 * Created by zengjiyang on 2016/2/16.
 */

public class OrderProcessConfirmServiceImpl implements OrderConfirmService {
    private OrderConfirmService orderConfirmService;

    public String processConfirmReceipt(String username ,String  password ,String orderNos) {
        ConfirmReceipt confirmReceipt = new ConfirmReceipt();
        confirmReceipt.doConfirmReceipt(username,password,orderNos);
        return "success";
    }


}
