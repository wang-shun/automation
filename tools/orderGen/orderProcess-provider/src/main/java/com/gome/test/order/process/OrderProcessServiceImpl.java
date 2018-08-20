package com.gome.test.order.process;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.cache.ProcessedOrder;
import com.gome.test.order.process.com.gome.test.buess.Login.Login;
import com.gome.test.order.process.com.gome.test.buess.SearchOrder.*;
import com.gome.test.order.process.util.GUI;
import com.gome.test.order.process.util.OrderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengjiyang on 2016/2/16.
 */

public class OrderProcessServiceImpl implements OrderService {
    Log log = LogFactory.getLog( this .getClass());
    private Timestamp start = null;
    private OrderService orderService;

    public String processOrdersNew(String orderNos, String expectStatus, String sendTo, String dzxh, String dzck ,String pch) {
        String errMsg = "";
        if ("".equals(orderNos) || orderNos== null ) {
            errMsg = "orderNos is must.";
            return errMsg;
        }
        String[] orderNoArray = orderNos.split(",");
        if (orderNoArray.length < 1) {
            errMsg = "orderNo format error. should split by ',' .";
            return errMsg;
        }
        if ("".equals(sendTo) || sendTo== null ) {
            errMsg = "sendTo is must.";
            return errMsg;
        }
        if ("".equals(expectStatus) || expectStatus== null ) {
            expectStatus="DL";
        }
        for (String orderNo : orderNoArray) {
            Order orders = new Order();
            orders.setOrderNo(orderNo);
            start = orders.getStartTime();
            JSONObject subjson = new JSONObject();
            AutoAudit auto = new AutoAudit();
            auto.doAutoAudit(orders);
            if("DL".equals(expectStatus) || "EX".equals(expectStatus)) {
                Check check = new Check();
                subjson = check.doCheck(orderNo);
            }
            SearchOrderNew search = new SearchOrderNew();
            orders =search.doSearchCheck(orders);
             if("SEARCH".equals(orders.getCurrentStatus())) {
                 List<Order> orderArray = new ArrayList();
                 orderArray = (List<Order>) ContextUtils.getContext().get("orderList");
                 for (Order order : orderArray) {
                     Order initOrder = new Order();
                     initOrder.setOrderNo(orderNo);
                     initOrder.setOrderNumber(order.getOrderNumber());
                     initOrder.setStartTime(OrderUtils.getCurrentTimestamp());
                     initOrder.setProductType(order.getProductType());
                     initOrder.setExpectStatus(expectStatus);
                     if ("RETURN".equals(expectStatus)) {
                         SearchStatusReverse searchStatusReverse = new SearchStatusReverse();
                         initOrder = searchStatusReverse.searchReverseStatus(initOrder);
                         initOrder.setReturnType("1");
                     } else if ("EXCHANGE".equals(expectStatus)) {
                         SearchStatusReverse searchStatusReverse = new SearchStatusReverse();
                         initOrder = searchStatusReverse.searchReverseStatus(initOrder);
                         initOrder.setReturnType("2");
                     } else {
                         initOrder.setCurrentStatus(order.getCurrentStatus());
                     }
                     initOrder.setEmailCount(sendTo);
                     initOrder.setNew(true);
                     ProcessedOrder.getInstance().addPreOrder(initOrder);
                 }
             } else {
                 Order order = new Order();
                 order.setOrderNo(orderNo);
                 order.setEndStatus("timeout");
                 order.setDesc("没有获取到配送单号");
                 order.setNew(true);
                 ProcessedOrder.getInstance().getProcessedOrders().add(order);
                 System.out.println(String.format("timeout!没有获取到配送单号"));
                 log.info("timeout!没有获取到配送单号!订单号："+orderNo);
             }
        }
        return "success";
    }

    public Order processOrder(String orderNo, String productType, String sendTo) {
        //todo
        Order initOrder = new Order();
        initOrder.setOrderNo(orderNo);
        initOrder.setStartTime(OrderUtils.getCurrentTimestamp());
        initOrder.setProductType(productType);
        initOrder.setExpectStatus("DL");
        initOrder.setCurrentStatus("PR");
        initOrder.setEmailCount(sendTo);
        initOrder.setNew(false);
        ProcessedOrder.getInstance().addPreOrder(initOrder);
        return initOrder;
    }

    public String processOrders(String orderNos, String productType, String expectStatus, String sendTo) {
        String[] orderArray = orderNos.split(",");
        String errMsg = "";
        if (orderArray.length < 1) {
            errMsg = "orderNo format error. should split by ',' .";
            return errMsg;
        }

        if (!OrderUtils.validateOrderType(expectStatus)) {
            errMsg = "orderNo expect status error. should EX or DL .";
            return errMsg;
        }

        if (!OrderUtils.validateProductType(productType)) {
            errMsg = "order type error. should \"3PP\" , \"NOPP\" , \"G3PP\" , \"SMI\".";
            return errMsg;
        }

        for (String orderNo : orderArray) {
            Order initOrder = new Order();
            initOrder.setOrderNo(orderNo);
            initOrder.setStartTime(OrderUtils.getCurrentTimestamp());
            initOrder.setProductType(productType);
            initOrder.setExpectStatus(expectStatus);
            initOrder.setCurrentStatus("PR");
            initOrder.setEmailCount(sendTo);
            initOrder.setNew(false);
            ProcessedOrder.getInstance().getPreOrders().add(initOrder);
        }

        return "success";
    }

    public String processReturnOrders(String username ,String  password ,String orderNos, String returnType, String sendTo) {
        String[] orderArray = orderNos.split(",");
        String errMsg = "";
        if (orderArray.length < 1) {
            errMsg = "orderNo format error. should split by ',' .";
            return errMsg;
        }

//        if (!OrderUtils.validateOrderType(expectStatus)) {
//            errMsg = "orderNo expect status error. should EX or DL .";
//            return errMsg;
//        }
//
//        if (!OrderUtils.validateProductType(productType)) {
//            errMsg = "order type error. should \"3PP\" , \"NOPP\" , \"G3PP\" , \"SMI\".";
//            return errMsg;
//        }

        for (String orderNo : orderArray) {
            Order initOrder = new Order();
            initOrder.setProductType("3pp");
            initOrder.setOrderNo(orderNo);
            initOrder.setStartTime(OrderUtils.getCurrentTimestamp());
//            initOrder.setProductType(productType);
            if ("1".equals(returnType)) {
                // 退货
                initOrder.setExpectStatus("RETURN");
            } else {
                // 换货
                initOrder.setExpectStatus("EXCHANGE");
            }
            initOrder.setCurrentStatus("DL");
            initOrder.setReturnType(returnType);
            initOrder.setUsername(username);
            initOrder.setPassword(password);
            initOrder.setEmailCount(sendTo);
            initOrder.setNew(false);
            ProcessedOrder.getInstance().getPreOrders().add(initOrder);
        }
        return "success";
    }

    public Order getOrder(String orderNo) {
        //todo
        Order order = new Order();
        order.setOrderNo("test111111");
        return order;
    }

}
