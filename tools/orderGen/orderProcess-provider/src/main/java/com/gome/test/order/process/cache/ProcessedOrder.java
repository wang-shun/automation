package com.gome.test.order.process.cache;

import com.gome.test.order.process.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hacke on 2017/2/28.
 */
public class ProcessedOrder {
    private static ProcessedOrder ourInstance = new ProcessedOrder();
    //cache list all the status from get user request to end process orders
    private static List<Order> processedOrders = new ArrayList<Order>();
    //cache list all the orders when finish process orders lifecycle.
    private static List<Order> preOrders = new ArrayList<Order>();

    public static ProcessedOrder getInstance() {
        return ourInstance;
    }

    private ProcessedOrder() {
    }

    public synchronized List<Order> getProcessedOrders() {
        return processedOrders;
    }

    public synchronized Boolean addFinalOrder(Order order) {
        return processedOrders.add(order);
    }

    public synchronized Boolean removeFinalOrder(Order order) {
        return processedOrders.remove(order);
    }

    public synchronized void removeAllFinalOrders() {
        processedOrders.clear();
    }


    public synchronized List<Order> getPreOrders() {
        return preOrders;
    }

    public synchronized Boolean addPreOrder(Order order) {
        return preOrders.add(order);
    }

    public synchronized Boolean removePreOrder(Order order) {
        return preOrders.remove(order);
    }


}
