package com.gome.test;

import com.gome.test.pom.POJO.OrderInfo;
import com.gome.test.pom.util.CommitOrder;
import org.junit.Test;

/**
 * Created by hacke on 2016/9/23.
 */
public class testCommonCommitOrderHelper {
    @Test
    public void testHelper(){
        CommitOrder commitOrder = CommitOrder.getInstance();
        OrderInfo info = commitOrder.getOrder();
        System.out.println("get order id is : " + info.getOrderID());

    }
}
