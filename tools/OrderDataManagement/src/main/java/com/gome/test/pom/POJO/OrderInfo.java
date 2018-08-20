package com.gome.test.pom.POJO;

/**
 * Created by hacke on 2016/9/23.
 */
public class OrderInfo {
    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        if(orderID!=null)
        this.orderID = orderID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        if(orderStatus != null)
            this.orderStatus = orderStatus;
    }

    public String getOrderLogs() {
        return orderLogs;
    }

    public void setOrderLogs(String orderLogs) {
        this.orderLogs = orderLogs;
    }

    private String orderID = "";
    private String orderStatus ="";
    private String orderLogs = "";
}
