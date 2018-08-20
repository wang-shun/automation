package com.gome.test.order.process.com.gome.test.buess.SearchOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.ReverseOrder.REOrderStatus;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangwan on 2017/5/8.
 */
public class SearchStatusReverse {

    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;

    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }

    public Order searchReverseStatus(Order order) {
            GUI.guiLogin();
            this.reverseList(order);
            if(response.contains("暂无数据")){
                order.setCurrentStatus("DL");
            }else {
                String reg1 = "(reverseOrderId=)(\\d+)(\")";
                Pattern pattern1 = this.getMatcher(reg1);
                Matcher matcher1 = pattern1.matcher(response);
                if (matcher1.find()) {
                    String requestID = matcher1.group(2);
                    order.setRequestId(requestID);
                }
                this.reverseOrder(order);
                if(response.contains("\"total\" : 0.0,")){
                    order.setCurrentStatus("SUBMITAPPLY");
                }else {
                    if(response.contains("\"orderStatus\" : \"RPP\",")) {
                        order.setCurrentStatus("AUDITAPPLY");
                    } else if(response.contains("\"orderStatus\" : \"RAP\",")){
                        order.setCurrentStatus("RETURNSTATUSRAP");
                    }else if(response.contains("\"orderStatus\" : \"RCP\",")){
                        if("EXCHANGE".equals(order.getExpectStatus())) {
                            REOrderStatus reorder = new REOrderStatus();
                            reorder.searchTransportId(order);
                            String transportId = "";
                            if (ContextUtils.getContext().get("transportId") != null) {
                                transportId = ContextUtils.getContext().get("transportId").toString();
                            }
                            this.getStatus(transportId);
                            if (response.contains("PR")) {
                                order.setCurrentStatus("RETURNSTATUSRCP");
                            } else if (response.contains("EX")) {
                                order.setCurrentStatus("RETURNEXSTATUS");
                            } else if (response.contains("DL")) {
                                order.setCurrentStatus("EXCHANGE");
                            }
                        } else {
                            order.setCurrentStatus("RETURNSTATUSRCP");
                        }
                    }
                }
            }


        return order;
    }
    public JSONObject reverseList(Order order) {
        String searchuri = Constant.REVERSRSTATUSURI;
        makeEntity(order.getOrderNumber());
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
            is = verify.doverify(response, "退换货系统");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }
    public JSONObject getStatus(String   transportId){
        // TODO: 2017/3/27 获取发送sap报文后的状态
        String searchuri = Constant.SEARCHORDER;
        makeEntitysearch(transportId);
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
           is = verify.doverify(response, "查询成功");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }
    private void makeEntitysearch(String orderId) {
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
        value.add("1494233273956");
        value.add("10");
        value.add("1");
        value.add("");
        value.add("asc");
        value.add("");
        value.add("");
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
    }
    public JSONObject reverseOrder(Order order) {
        String searchuri = Constant.SEARCHORDER;
        makeEntity2(order.getOrderNumber());
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
            is = verify.doverify(response, "查询成功");
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
    private void makeEntity2(String orderNumber) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("orderFlag");
        key.add("orderType");
        key.add("_search");
        key.add("nd");
        key.add("rows");
        key.add("page");
        key.add("sidx");
        key.add("sord");
        key.add("beginDay");
        key.add("endDay");
        key.add("orderStatus");
        key.add("orderNumber");
        key.add("originalOrderNumber");




        value.add("G3PP");
        value.add("RO,PR");
        value.add("false");
        value.add("1494228466306");
        value.add("10");
        value.add("1");
        value.add("");
        value.add("asc");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add(orderNumber);
    }

    private void makeEntity(String orderNumber) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("reverseOrderNum");
        key.add("orgSgId");
        key.add("postTime1");
        key.add("postTime2");
        key.add("profileName");
        key.add("memberName");
        key.add("auditDateTime1");
        key.add("auditDateTime2");
        key.add("mobileNumber");
        key.add("contactName");
        key.add("orderState");
        key.add("masLocType");
        key.add("returnShippingMethodList");
        key.add("returnShippingMethodList");
        key.add("returnShippingMethodList");
        key.add("returnShippingMethodList");
        key.add("returnShippingMethodList");
        key.add("currentPage");
        key.add("pageSize");



        value.add("");
        value.add(orderNumber);
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
        value.add("2");
        value.add("3");
        value.add("4");
        value.add("5");
        value.add("1");
        value.add("15");
    }

}
