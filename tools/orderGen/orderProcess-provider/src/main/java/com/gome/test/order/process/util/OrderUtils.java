package com.gome.test.order.process.util;

import com.gome.test.order.process.Order;

import java.sql.Timestamp;

/**
 * Created by hacke on 2017/2/28.
 */
public class OrderUtils {
    public static final long timeoutTime = 1800000;

    public static Timestamp getCurrentTimestamp() {
        java.util.Date date = new java.util.Date();
        Timestamp ts_now = new Timestamp(date.getTime());
        return ts_now;
    }

    public static Boolean validateOrderType(String type) {
        String types[] = {"EX", "DL"};
        Boolean exist = false;
        String uType = type.toUpperCase();
        for (String vType : types) {
            if (vType.equals(uType)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    public static Boolean validateTimeout(Timestamp start, Timestamp currentTime) {
        Boolean timeout = false;
        if (timeoutTime <= currentTime.getTime() - start.getTime()) {
            timeout = true;
        }
        return timeout;
    }

    public static Order getProductInfo(Order order) {
        switch(order.getProductCode()){
            case "000000000100253499":
                order.setDzxh("1");
                order.setDzck("BX01");
                order.setPch("0000215888");
                break;
            case "000000000100253719":
                order.setDzxh("1");
                order.setDzck("BX01");
                order.setPch("0000215886");
                break;
            case "000000000100253733":
                order.setDzxh("1");
                order.setDzck("BX01");
                order.setPch("0000215887");
                break;
        }
        return order;
    }

    public static Boolean validateProductType(String type) {
        String types[] = {"3PP", "NOPP", "G3PP", "SMI"};
        Boolean exist = false;
        String uType = type.toUpperCase();
        for (String vType : types) {
            if (vType.equals(uType)) {
                exist = true;
                break;
            }
        }
        return exist;
    }
}
