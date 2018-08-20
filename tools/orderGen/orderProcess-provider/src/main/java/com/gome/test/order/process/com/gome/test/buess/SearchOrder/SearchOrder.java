package com.gome.test.order.process.com.gome.test.buess.SearchOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/2/28.
 */
public class SearchOrder {
    Log log = LogFactory.getLog( this .getClass());
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;


    @Test
    JSONObject test(Order order) {
        jsonObject = this.searchOrderCheck(order);
        return jsonObject;
    }

    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }

    public JSONObject searchOrderCheck(Order order) {
        String searchuri = Constant.SEARCHORDER;
        makeEntity(order.getOrderNo());
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
            System.out.println(String.format("查询订单保存配送号返回值：%s", response));
            log.info(String.format("查询订单保存配送号返回值：%s", response));
            String reg = "\"orderNumber\" : \"(\\d+)\",";
            Pattern pattern = this.getMatcher(reg);
            Matcher matcher = pattern.matcher(response);
            ContextUtils.getContext().clear();
            if (matcher.find()) {
                String orderNumber = matcher.group(1);
                order.setOrderNumber(orderNumber);
                //ContextUtils.getContext().put("orderNumber", orderNumber);
            }


            is = verify.doverify(response, " \"orderNumber\"");
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
}
