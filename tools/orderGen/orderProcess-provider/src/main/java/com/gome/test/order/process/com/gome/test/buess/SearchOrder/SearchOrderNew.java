package com.gome.test.order.process.com.gome.test.buess.SearchOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;
import com.gome.test.order.process.util.OrderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/2/28.
 */
public class SearchOrderNew {
    Log log = LogFactory.getLog( this .getClass());
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;
    private Timestamp start = null;

    @Test
    JSONObject test(Order order) {
        jsonObject = this.searchOrderCheck(order);
        return jsonObject;
    }
    String getTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        String time = dateFormat.format(now);
        return time;
    }
    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }


    public Order doSearchCheck(Order order) {
        GUI.guiLogin();
        JSONObject subjson = new JSONObject();
        order.setStartTime(OrderUtils.getCurrentTimestamp());
        subjson= this.search(order);
        int i=(Integer) ContextUtils.getContext().get("OrderNum");
        subjson= this.searchOrderCheck(order);
        List<Order> orderArray = new ArrayList();
        orderArray = (List<Order>) ContextUtils.getContext().get("orderList");
        while (!(subjson.get("isSuccess").equals("true") && orderArray.size() == i)) {
            start = order.getStartTime();
            if (OrderUtils.validateTimeout(start, OrderUtils.getCurrentTimestamp())) {
                return order;
            }
            subjson= this.searchOrderCheck(order);
            orderArray = (List<Order>) ContextUtils.getContext().get("orderList");
        }
        order.setCurrentStatus("SEARCH");
        return order;
    }

    public JSONObject search(Order order) {
        String searchuri = Constant.AUDITSEARCHURI;
        makeEntity2(order.getOrderNo());
        try {
            Thread.sleep(15000);
            int i = 0;
            response = httpclient.visit(searchuri, key, value);
            System.out.println(String.format("查询返回值：%s", response));
            log.info(String.format("查询返回值：%s", response));
            String pp = "(<a href=\"http://omscsc.atguat.com.cn/csc/order/detail.action(.*?))";
            Pattern ppp = Pattern.compile(pp);
            Matcher m = ppp.matcher(response);
            while (m.find()) {
                i++;
            }
            is = verify.doverify(response, order.getOrderNo());
            ContextUtils.getContext().put("OrderNum", i);
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }
    public JSONObject searchOrderCheck(Order order) {
        String searchuri = Constant.SEARCHORDER;
        makeEntity(order.getOrderNo());
//        try {
//            Thread.sleep(5000);
//            response = httpclient.visit(searchuri, key, value);
//            System.out.println(String.format("查询订单保存配送号返回值：%s", response));
//            String reg = "\"orderNumber\" : \"(\\d+)\",";
//            Pattern pattern = this.getMatcher(reg);
//            Matcher matcher = pattern.matcher(response);
//            ContextUtils.getContext().clear();
//            if (matcher.find()) {
//                String orderNumber = matcher.group(1);
//                order.setOrderNumber(orderNumber);
//                //ContextUtils.getContext().put("orderNumber", orderNumber);
//            }

                try {
                    Thread.sleep(10000);
                    List<Order> list = new ArrayList();
                    response = httpclient.visit(searchuri, key, value);
                    is = verify.doverify(response, order.getOrderNo());
                        if ("success".equals(is)) {
                            String pp = "(orderNumber\" : \")(.*?)(\",)";
                            Pattern ppp = Pattern.compile(pp);
                            Matcher m = ppp.matcher(response);
                            String pp2 = "(orderFlag\" : \")(.*?)(\",)";
                            Pattern ppp2 = Pattern.compile(pp2);
                            Matcher m2 = ppp2.matcher(response);
                            String pp3 = "(orderStatus\" : \")(.*?)(\",)";
                            Pattern ppp3 = Pattern.compile(pp3);
                            Matcher m3 = ppp3.matcher(response);
                            while (m.find() && m2.find() && m3.find()) {
                                String str = m.group(2);
                                String str2 = m2.group(2);
                                String str3 = m3.group(2);
                                Order neworder = new Order();
                                neworder.setOrderNumber(str);
                                neworder.setProductType(str2);
                                neworder.setCurrentStatus(str3);
                                list.add(neworder);
                            }
                        }
                    ContextUtils.getContext().put("orderList", list);
                } catch (Exception e) {
                    is = "failed_response_null";
                    response = e.toString();
                }
            return makeJson();
    }

    public JSONObject makeJson() {
        if (is.equals("success")) {
            //成功 jsonObject
            jsonObject.put("isSuccess", "true");
            jsonObject.put("message", "访问成功");
            if (response.contains("搜索订单成功")) {
                jsonObject.put("data", response);
            }

        } else if (is.equals("failed_notcontains_expect")) {
            //失败，不包含指定值
            jsonObject.put("isSuccess", "false");
            jsonObject.put("message", "访问失败，不包含指定值");
            jsonObject.put("data", response);
        } else if (is.equals("failed_response_null")) {
            //返回值null
            jsonObject.put("isSuccess", "false");
            jsonObject.put("message", "方法调用出错");
            jsonObject.put("data", response);
        }
        return jsonObject;
    }

    private void makeEntity(String orderId) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("orderFlag");
        key.add("orderType");
        key.add("search");
        key.add("nd");
        key.add("rows");
        key.add("page");
        key.add("sidx");
        key.add("sord");
        key.add("beginDay");
        key.add("endDay");
        key.add("customerName");
        key.add("abFlag");
        key.add("masterOrderNumber");
        key.add("orderStatus");
        key.add("orderNumber");
        key.add("city");
        key.add("unCcoReason");
        key.add("eddFlag");
        key.add("deliveryWeekendFlag");
        key.add("orderChannel");
        key.add("paymentType");
        key.add("payPosCash");
        key.add("installFlag");
        key.add("invoiceType");
        key.add("invoiceMediaType");
        key.add("invoiceStatus");
        key.add("allowanceFlag");
        key.add("cellPhone");


        value.add("G3PP");
        value.add("SO,CSO,PS");
        value.add("FALSE");
        value.add("1488454521797");
        value.add("10");
        value.add("1");
        value.add("");
        value.add("asc");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add(orderId);
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
    }

    private void makeEntity2(String orderId) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("orderId");
        key.add("login");
        key.add("startDate");
        key.add("endDate");
        key.add("shippingGroupId");
        key.add("firstName");
        key.add("orderState");
        key.add("skuNo");
        key.add("externalSiteOrderId");
        key.add("mobileNumber");
        key.add("siteId");
        key.add("masLocType");
        key.add("state");
        key.add("city");
        key.add("county");
        key.add("town");
        key.add("address");
        key.add("orderField");
        key.add("orderFieldType");
        key.add("resetBtn");
        key.add("currentPage");
        key.add("pageSize");


        value.add(orderId);
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("1");
        value.add("15");


    }
}
