package com.gome.test.order.process.schedule;

import com.gome.test.order.process.*;

import com.gome.test.order.process.cache.ProcessedOrder;
import com.gome.test.order.process.com.gome.test.buess.OrderStatus.OrderStatus;
import com.gome.test.order.process.com.gome.test.buess.OrderStatus.OrderStatusNew;
import com.gome.test.order.process.com.gome.test.buess.ReverseOrder.*;
import com.gome.test.order.process.util.OrderUtils;
import gyoung.dubbo.demo.api.OrderMessage;
import gyoung.dubbo.demo.provider.OrderServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by hacke on 2017/2/28.
 */
public class ProcessOrderJob {

    Log log = LogFactory.getLog( this .getClass());
    private Timestamp start = null;
    private Timestamp current = null;
/*
    public void test(){
        Date date = new Date();

        System.out.println(date.getTime());
    }*/

//    @Test
//    public void test() {
//        ApplicationContext context = OrderProcessProvider.getContext();//new ClassPathXmlApplicationContext("classpath*:dubbo-orderProcess-provider.xml");
//        OrderServiceImpl test = (OrderServiceImpl) context.getBean("orderServicetest");
//        StringBuilder oid = new StringBuilder();
//        for (int i = 0; i < 3; i++) {
//            //test.getOrder()
//            OrderMessage orderMessage = test.defaultOrder();
//            oid.append(orderMessage.getOrderId());
//            if (i < 2) {
//                oid.append(",");
//            }
//        }
//        OrderProcessServiceImpl orderProcessService = new OrderProcessServiceImpl();
//        orderProcessService.processOrders(oid.toString(), "3PP", "DL", "liujinhu@gomeplus.com,liangwei-ds@gomeplus.com,weijianxing@gomeplus.com");
//    //orderProcessService.processOrders("15103127957,15103127956".toString(), "3PP", "DL", "liujinhu@gomeplus.com,liangwei-ds@gomeplus.com,weijianxing@gomeplus.com");
//
//    }


    public void testG3PP(){

        G3PPOrderProcessServiceImpl g3pp = new G3PPOrderProcessServiceImpl();
        g3pp.processOrders("15103157566","G3PP","DL","liangwei-ds@gomeplus.com","","","","","");

    }

//    @Test
//    public void test1() {
//        OrderProcessServiceImpl orderProcessService = new OrderProcessServiceImpl();
//        orderProcessService.processOrderss("19103167898", "DL", "zhangwan@gomeplus.com");
//        while (true) {
//            processOrder();
//        }
//    }


    public void processOrder() {

        List<Order> ordersList = ProcessedOrder.getInstance().getPreOrders();
        System.out.println("當前list表cach数据:" + ordersList.size());
        log.info("當前list表cach数据:" + ordersList.size());
        if (ordersList != null && ordersList.size() > 0) {
            for (int i = 0; i < ordersList.size(); i++) {
                boolean dupflg = false;
                Order order = ordersList.get(i);
                for (int j = 0; j < ordersList.size(); j++) {
                    if (order.getOrderNumber().equals(ordersList.get(j).getOrderNumber()) && i!= j) {
                        dupflg = true;
                        break;
                    }
                }
                if (dupflg == true) {
                    ProcessedOrder.getInstance().removePreOrder(order);
                }
            }
        }
        List<Order> preOrdersList = ProcessedOrder.getInstance().getPreOrders();
        if (preOrdersList != null && preOrdersList.size() > 0) {
            try {
                for (Order order : preOrdersList) {
                    if (order.getProductType().equalsIgnoreCase("3PP")){
                        //处理3PP商品的订单状态流转
                        //todo 1 validate timeout
                        start = order.getStartTime();
                        if (OrderUtils.validateTimeout(start, OrderUtils.getCurrentTimestamp())) {
                            order.setEndStatus("timeout");
                            ProcessedOrder.getInstance().getProcessedOrders().add(order);
                            ProcessedOrder.getInstance().getPreOrders().remove(order);
                        }
                        //todo 2 compare order current status and expect status
                        if ("PR".equals(order.getCurrentStatus()) && "DL".equals(order.getExpectStatus())) {
                            //order.setCurrentStatus("EX");
                            //todo 3 execute status update.
                            Order result = new Order();
                            if(order.getNew() == false) {
                                OrderStatus orderStatus = new OrderStatus();
                                result = orderStatus.doModStatus(order);
                            }else {
                                OrderStatusNew orderStatus = new OrderStatusNew();
                                result = orderStatus.doModStatus(order);
                            }
                            if (result.getIssuccess()) {
                                order.setCurrentStatus("EX");
                            }
                            System.out.println(String.format("订单号：%s,订单出库状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                            log.info(String.format("订单号：%s,订单出库状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                        } else if ("EX".equals(order.getCurrentStatus()) && "DL".equals(order.getExpectStatus())) {
                            //todo 3 execute status update.
                            //order.setCurrentStatus("DL");
                            Order result = new Order();
                            if(order.getNew() == false) {
                                OrderStatus orderStatus = new OrderStatus();
                                result = orderStatus.doModStatus(order);
                            }else {
                                OrderStatusNew orderStatus = new OrderStatusNew();
                                result = orderStatus.doModStatus(order);
                            }
                            if (result.getIssuccess()) {
                                order.setCurrentStatus("DL");
                                order.setEndStatus("success");
                                ProcessedOrder.getInstance().addFinalOrder(order);
                                ProcessedOrder.getInstance().removePreOrder(order);
                                System.out.println(String.format("订单号：%s,订单妥投状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                                log.info(String.format("订单号：%s,订单妥投状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                            }
                            System.out.println(String.format("订单号：%s,订单妥投成功，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));
                            log.info(String.format("订单号：%s,订单妥投成功，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));

                        }else if ("PR".equals(order.getCurrentStatus()) && "EX".equals(order.getExpectStatus())) {
                            //todo 3 execute status update.
                            //order.setCurrentStatus("DL");
                            Order result = new Order();
                            if(order.getNew() == false) {
                                OrderStatus orderStatus = new OrderStatus();
                                result = orderStatus.doModStatus(order);
                            }else {
                                OrderStatusNew orderStatus = new OrderStatusNew();
                                result = orderStatus.doModStatus(order);
                            }
                            if (result.getIssuccess()) {
                                order.setCurrentStatus("EX");
                                order.setEndStatus("success");
                                ProcessedOrder.getInstance().addFinalOrder(order);
                                ProcessedOrder.getInstance().removePreOrder(order);
                                System.out.println(String.format("订单号：%s,订单出库状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                                log.info(String.format("订单号：%s,订单出库状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                            }
                            System.out.println(String.format("订单号：%s,订单出库状态，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));
                            log.info(String.format("订单号：%s,订单出库状态，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));

                        } else if ("DL".equals(order.getCurrentStatus()) && ("EXCHANGE".equals(order.getExpectStatus()) || "RETURN".equals(order.getExpectStatus()))) {
                        // 商城退换货申请
                        ApplyReverse  applyReverse = new ApplyReverse();
                        Order result =applyReverse.doAuditOrder(order);
                        if (result.getIssuccess()) {
                            order.setCurrentStatus("SUBMITAPPLY");
                        } else {
                            if(order.getDesc().contains("当前商品不能退换货")){
                                order.setEndStatus("fail");
                                ProcessedOrder.getInstance().addFinalOrder(order);
                                ProcessedOrder.getInstance().removePreOrder(order);
                                System.out.println(String.format("订单号：%s,当前商品不能退换货，当前cach队列有：%d订单", result.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));
                                log.info(String.format("订单号：%s,当前商品不能退换货，当前cach队列有：%d订单", result.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));String.format("订单号：%s,当前商品不能退换货，当前cach队列有：%d订单", result.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size());
                            }
                        }
                        System.out.println(String.format("订单号：%s,订单退换货申请状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                        log.info(String.format("订单号：%s,订单退换货申请状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                    }
                    else if ("SUBMITAPPLY".equals(order.getCurrentStatus()) && ("EXCHANGE".equals(order.getExpectStatus()) || "RETURN".equals(order.getExpectStatus()))) {
                        // ERM审核退换货申请
                        AuditOrder auditOrder = new AuditOrder();
                        Order result = auditOrder.doAuditOrder(order);
                        if (result.getIssuccess()) {
                            order.setCurrentStatus("AUDITAPPLY");
                        }
                        System.out.println(String.format("订单号：%s,退换货订单审核状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                        log.info(String.format("订单号：%s,退换货订单审核状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                    } else if ("AUDITAPPLY".equals(order.getCurrentStatus()) && ("EXCHANGE".equals(order.getExpectStatus()) || "RETURN".equals(order.getExpectStatus()))) {
                        // 修改退货状态
                        OrderStatusReverse staReverse = new OrderStatusReverse();
                        Order result = staReverse.doModReturnStatus(order);
                        if (result.getIssuccess()) {
                            order.setCurrentStatus("RETURNSTATUSRAP");
                       }
                          System.out.println(String.format("订单号：%s,退换货订单状态RAP：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                          log.info(String.format("订单号：%s,退换货订单状态RAP：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                        } else if ("RETURNSTATUSRAP".equals(order.getCurrentStatus()) && ("EXCHANGE".equals(order.getExpectStatus()) || "RETURN".equals(order.getExpectStatus()))) {
                        // 修改退货状态
                        OrderStatusReverse staReverse = new OrderStatusReverse();
                        Order result = staReverse.doModReturnStatus(order);
                        if (result.getIssuccess()) {
                            order.setCurrentStatus("RETURNSTATUSRCP");
                        }
                            System.out.println(String.format("订单号：%s,退换货订单状态RCP：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                            log.info(String.format("订单号：%s,退换货订单状态RCP：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                        } else if ("RETURNSTATUSRCP".equals(order.getCurrentStatus()) && "EXCHANGE".equals(order.getExpectStatus())) {
                        // 修改换货状态EX
                        REOrderStatus reOrderStatus = new REOrderStatus();
                        Order result = reOrderStatus.doReOrderStatus(order);
                        if (result.getIssuccess()) {
                            order.setCurrentStatus("RETURNEXSTATUS");
                        }
                        System.out.println(String.format("订单号：%s,换货订单妥投状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                        log.info(String.format("订单号：%s,换货订单妥投状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                    } else if ("RETURNEXSTATUS".equals(order.getCurrentStatus()) && "EXCHANGE".equals(order.getExpectStatus())) {
                        // 修改换货状态DL
                        REOrderStatus reOrderStatus = new REOrderStatus();
                        Order result = reOrderStatus.doReOrderStatus(order);
                        if (result.getIssuccess()) {
                            order.setCurrentStatus("EXCHANGE");
                            order.setEndStatus("success");
                            ProcessedOrder.getInstance().addFinalOrder(order);
                            ProcessedOrder.getInstance().removePreOrder(order);
                            System.out.println(String.format("订单号：%s,换货订单妥投成功，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));
                            log.info(String.format("订单号：%s,换货订单妥投成功，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));
                        }
                            System.out.println(String.format("订单号：%s,换货订单妥投状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                            log.info(String.format("订单号：%s,换货订单妥投状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                    } else if ("RETURNSTATUSRCP".equals(order.getCurrentStatus()) && "RETURN".equals(order.getExpectStatus())) {
                        // 退货审核
                        AuditRefund auditRefund = new AuditRefund();
                        Order result = auditRefund.doAuditRefund(order);
                        if (result.getIssuccess()) {
                            order.setCurrentStatus("RETURN");
                            order.setEndStatus("success");
                            ProcessedOrder.getInstance().addFinalOrder(order);
                            ProcessedOrder.getInstance().removePreOrder(order);
                            System.out.println(String.format("订单号：%s,退货审核成功，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));
                            log.info(String.format("订单号：%s,退货审核成功，当前cach队列有：%d订单", order.getOrderNo(), ProcessedOrder.getInstance().getPreOrders().size()));
                        }
                        System.out.println(String.format("订单号：%s,退货审核状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                        log.info(String.format("订单号：%s,退货审核状态：%s，当前cach队列有：%d订单", result.getOrderNo(), result.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                    }
                        if (preOrdersList.isEmpty()) {
                            break;
                        }
                        //todo 4 update status to cache (if done the update process remove from preList to processedList . else
                        //todo 4 update preList status)
                    }
                    if (order.getProductType().equalsIgnoreCase("G3PP")){
                        //处理G3PP商品订单状态流转
                        //todo 1 validate timeout
                        start = order.getStartTime();
                        if (OrderUtils.validateTimeout(start, OrderUtils.getCurrentTimestamp())) {
                            order.setEndStatus("timeout");
                            ProcessedOrder.getInstance().getProcessedOrders().add(order);
                            ProcessedOrder.getInstance().getPreOrders().remove(order);
                        }
                        if (order.getExpectStatus().equalsIgnoreCase("DL")||order.getExpectStatus().equalsIgnoreCase("EX")){
                            if(order.getNew() == false) {
                                OrderStatus orderStatus = new OrderStatus();
                                orderStatus.g3ppOrderModify(order);
                            }else {
                                OrderStatusNew orderStatus = new OrderStatusNew();
                                orderStatus.g3ppOrderModify(order);
                            }

                        }
                    }

                }
            } catch (Exception e) {
                try {
                    log.error("for 循环出错了");
                    throw new Exception("for 循环出错了");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

}

