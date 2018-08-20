package com.gome.test.order.process.com.gome.test.buess.OrderStatus;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.cache.ProcessedOrder;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.Lmis.SAP;
import com.gome.test.order.process.com.gome.test.buess.Lmis.Send;
import com.gome.test.order.process.com.gome.test.buess.Lmis.WayBillStatusController;
import com.gome.test.order.process.com.gome.test.buess.SearchOrder.SearchCurrentStatus;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zhangwan on 2017/2/28.
 */
public class OrderStatusNew {
    Log log = LogFactory.getLog( this .getClass());
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;

    String getTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        String time = dateFormat.format(now);
//        System.out.println(hehe);
        return time;
    }

    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }

    static int i=1;
    public Order doModStatus(Order order) {
        OrderStatusNew status = new OrderStatusNew();
        JSONObject subjson = new JSONObject();
        StringBuilder desc = new StringBuilder();
        String ss = order.getDesc();
        if (ss != null && ss != "") {
            desc.append(ss);
        } else {
            desc.append("<br />");
        }
        boolean flg = false;
/*        if (i%50==0){
            GUI.guiLogin();
        }*/
        SearchCurrentStatus currstatus = new SearchCurrentStatus();
        order=currstatus.searchStatus(order);
        if (order.getCurrentStatus().equals("PR")) {
            flg=true;
            // TODO: 2017/3/2 审核订单
//            Check check = new Check();
//            subjson = check.doCheck(order.getOrderNo());
//
//            SearchOrder search = new SearchOrder();
//            subjson = search.searchOrderCheck(order);
            if (order.getOrderNumber() != null && order.getOrderNumber() != "") {
//            if (ContextUtils.getContext().get("orderNumber") != null && ContextUtils.getContext().get("orderNumber") != "") {
                System.out.println("获取到的配送单号为：" + order.getOrderNumber().toString());
                log.info("获取到的配送单号为：" + order.getOrderNumber().toString());
                subjson = status.updateOrder(order.getOrderNumber().toString(), "EX");
                if (subjson.get("isSuccess").equals("true")) {
                    order.setIssuccess(true);
                    String descformat = String.format("<br />%s 3PP执行修改订单：%s，状态为出库（EX）成功", this.getTime(), order.getOrderNo());
                    desc.append(descformat);
                    order.setDesc(desc.toString());
                    order.setOrderNumber(order.getOrderNumber().toString());
                } else {
                    order.setIssuccess(false);
                    String descformat = String.format("<br />%s 3PP执行修改订单：%s，状态为出库（EX）失败", this.getTime(), order.getOrderNo());
                    desc.append(descformat);
                    order.setDesc(desc.toString());
                }
            } else {
                System.out.println(String.format("获取配送单号失败"));
                log.info(String.format("获取配送单号失败"));
                order.setIssuccess(false);
    /*            String descformat = String.format("<br />%s 执行修改订单：%s状态为出库时，销售订单根据订单号查找订单失败", this.getTime(), order.getOrderNo());
                desc.append(descformat);
                order.setDesc(desc.toString());*/
            }

        } else if (order.getCurrentStatus().equals("EX") && order.getExpectStatus().equals("DL")  ) {
            flg=true;
//            SearchOrder search = new SearchOrder();
//            subjson = search.searchOrderCheck(order);
            if (order.getOrderNumber() != null && order.getOrderNumber() != "") {
//            if (ContextUtils.getContext().get("orderNumber") != null && ContextUtils.getContext().get("orderNumber") != "") {
                subjson = status.updateOrder(order.getOrderNumber().toString(), "DL");
                if (subjson.get("isSuccess").equals("true")) {
                    order.setIssuccess(true);
                    String descformat = String.format("<br />%s 3PP执行修改订单：%s，状态为妥投（DL）成功", this.getTime(), order.getOrderNo());
                    desc.append(descformat);
                    order.setDesc(desc.toString());

                } else {
                    order.setIssuccess(false);
                    String descformat = String.format("<br />%s 3PP执行修改订单：%s，状态为妥投（DL）失败", this.getTime(), order.getOrderNo());
                    desc.append(descformat);
                    order.setDesc(desc.toString());
                }
            } else {
                System.out.println(String.format("获取配送单号失败"));
                log.info(String.format("获取配送单号失败"));
                order.setIssuccess(false);
                String descformat = String.format("<br />%s 3PP执行修改订单：%s状态为妥投时，销售订单根据订单号查找订单失败", this.getTime(), order.getOrderNo());
                desc.append(descformat);
                order.setDesc(desc.toString());
            }
        }
        if(flg == false){
            ProcessedOrder.getInstance().getPreOrders().remove(order);
        }
        return order;
    }

    public JSONObject updateOrder(String orderNumber, String orderStatus) {
        String statusuri = Constant.STATUSURI;
        makeEntity(orderNumber, orderStatus);
        try {
            response = httpclient.visit(statusuri, key, value);
            System.out.println(String.format("修改订单状态为：%s,返回结果：%s", orderStatus, response));
            log.info(String.format("修改订单状态为：%s,返回结果：%s", orderStatus, response));
            is = verify.doverify(response, "0");
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
            jsonObject.put("message", "修改成功");
            if (response.contains("修改成功")) {
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



    public Order g3ppOrderModify(Order order){
        StringBuilder desc = new StringBuilder();
        String ss = order.getDesc();
        if (ss != null && ss != "") {
            desc.append(ss);
        } else {
            desc.append("<br />");
        }
       boolean flg = false;

        // TODO: 2017/3/21  G3pp订单流程整体流程入口
        JSONObject subjson = new JSONObject();
        SearchCurrentStatus currstatus = new SearchCurrentStatus();
        order=currstatus.searchStatus(order);
        if (order.getCurrentStatus().equalsIgnoreCase("PP")){
            flg =true;
              GUI.guiLogin();
//            Check check = new Check();
//            jsonObject = check.doCheck(order.getOrderNo());
//            SearchOrder searchOrder = new SearchOrder();
//            jsonObject = searchOrder.searchOrderCheck(order);
            jsonObject = updateG3pp(order.getOrderNumber(),"CCO");
            if (jsonObject.get("isSuccess").equals("true")){
                order.setIssuccess(true);
                order.setCurrentStatus("CCO");
                String descformat = String.format("<br />%s G3PP执行修改订单：%s，状态为客户确认订单（CCO）成功", this.getTime(), order.getOrderNo());
                desc.append(descformat);
                order.setDesc(desc.toString());
                //order.setExpectS tatus("EX");//获取安迅运单号AX开头单号
            }else {
                order.setIssuccess(false);
            }
        }
        if (order.getCurrentStatus().equalsIgnoreCase("CCO")){
            flg =true;
//            LmisLogin lmisLogin = new LmisLogin();
//            jsonObject = lmisLogin.doLogin("fengjiangwei","123123");
            GUI.guiLmisLogin();
            WayBillStatusController wbsc = new WayBillStatusController();
            jsonObject = wbsc.findBillStatus(order);
            if(order.getgWaybillNo()!=null && order.getgWaybillNo()!=""){
                SAP sap = new SAP();
                jsonObject = sap.submit(order);
            }
            if (jsonObject.get("isSuccess").equals("true")){
                order.setIssuccess(true);
                order.setCurrentStatus("EX");
                String descformat = String.format("<br />%s G3PP执行修改订单：%s，状态为出库（EX）成功", this.getTime(), order.getOrderNo());
                desc.append(descformat);
                order.setDesc(desc.toString());
                if (order.getCurrentStatus().equalsIgnoreCase(order.getExpectStatus())){
                    ProcessedOrder.getInstance().addFinalOrder(order);
                    ProcessedOrder.getInstance().removePreOrder(order);
                    order.setEndStatus("success");
                    System.out.println(String.format("订单号：%s,G3PP订单出库状态：%s，当前cach队列有：%d订单", order.getOrderNo(), order.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                    log.info(String.format("订单号：%s,G3PP订单出库状态：%s，当前cach队列有：%d订单", order.getOrderNo(), order.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                }
               // order.setExpectStatus("DL");//获取安迅运单号AX开头单号
            }else {
                order.setIssuccess(false);
            }
        }
        if (order.getCurrentStatus().equalsIgnoreCase("EX")&&order.getExpectStatus().equalsIgnoreCase("DL")){
            GUI.guiLmisLogin();
            WayBillStatusController wbsc = new WayBillStatusController();
            jsonObject = wbsc.findBillStatus(order);
            flg =true;
            Send send = new Send();
            try {
                jsonObject = send.postDL(order);
            }catch (Exception e){
                e.printStackTrace();
            }

            if (jsonObject.get("isSuccess").equals("true")){
                order.setIssuccess(true);
                order.setCurrentStatus("DL");
                String descformat = String.format("<br />%s G3PP执行修改订单：%s，状态为妥投（DL）成功", this.getTime(), order.getOrderNo());
                desc.append(descformat);
                order.setDesc(desc.toString());
                ProcessedOrder.getInstance().addFinalOrder(order);
                ProcessedOrder.getInstance().removePreOrder(order);
                order.setEndStatus("success");
                System.out.println(String.format("订单号：%s,G3PP订单妥投状态：%s，当前cach队列有：%d订单", order.getOrderNo(), order.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
                log.info(String.format("订单号：%s,G3PP订单妥投状态：%s，当前cach队列有：%d订单", order.getOrderNo(), order.getIssuccess(), ProcessedOrder.getInstance().getPreOrders().size()));
            }else {
                order.setIssuccess(false);
            }
        }
        if(flg==false){
            ProcessedOrder.getInstance().removePreOrder(order);
        }
        return order;
    }

    public JSONObject updateG3pp(String orderNumber,String orderStatus){
        // TODO: 2017/3/21  erm修改确认订单
        String statusuri = Constant.ORDERCONFIRM;
        makeG3ppEntity(orderNumber);
        try {
            response = httpclient.visit(statusuri, key, value);
            System.out.println(String.format("修改订单状态为：CCO,返回结果：%s",response));
            log.info(String.format("修改订单状态为：CCO,返回结果：%s",response));
            is = verify.doverify(response, "0");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    private void makeG3ppEntity(String orderNumber){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);

        key = new ArrayList<String>();
        value = new ArrayList<String>();
        key.add("reasonCode");
        key.add("rvReason");
        key.add("rvType");
        key.add("isTimeWindowClosed");
        key.add("jinLiCode");
        key.add("installType");
        key.add("eddDate");
        key.add("eddTimeSlot");
        key.add("eddFlag");
        key.add("shippingCondition");
        key.add("orderNumber");
        key.add("orderStatus");
        key.add("distanceFee");
        key.add("areaId");
        key.add("districtId");
        key.add("installInfoShow");
        key.add("tmsCode");
        key.add("timeWindowDay");
        key.add("timeWindow");
        key.add("appointmentDate");
        key.add("appointmentTimeChoice");
        key.add("gomeReason");
        key.add("customerReason");
        key.add("gomeReason2");
        key.add("customerReason2");

        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add(dateNowStr);
        value.add("C");
        value.add("");
        value.add("14");
        value.add(orderNumber);
        value.add("CCO");
        value.add("N");
        value.add("");
        value.add("");
        value.add("");
        value.add("BJ0001");
        value.add("");
        value.add("");
        value.add(dateNowStr);
        value.add("C");
        value.add("-- 请选择 --");
        value.add("-- 请选择 --");
        value.add("-- 请选择 --");
        value.add("-- 请选择 --");

    }
    private void makeEntity(String orderNumber, String orderStatus) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        if ("DL".equals(orderStatus) || "RT".equals(orderStatus)) {
            key.add("orderNumber");
            key.add("orderStatus");
            key.add("reasonCode");
            key.add("expressName");
            key.add("expressNumber");
            key.add("expressCode");

            value.add(orderNumber);
            value.add(orderStatus);
            value.add("");
            value.add("%E5%95%86%E5%AE%B6%E8%87%AA%E6%9C%89%E7%89%A9%E6%B5%81");
            value.add("");
            value.add("99900046");
        } else if ("EX".equals(orderStatus)) {
            key.add("orderNumber");
            key.add("orderStatus");
            key.add("reasonCode");
            key.add("expressName");
            key.add("expressNumber");
            key.add("expressCode");

            value.add(orderNumber);
            value.add(orderStatus);
            value.add("");
            value.add("EMS");
            value.add("1324");
            value.add("99900005");

        } else if ("CL".equals(orderStatus)) {
            key.add("orderNumber");
            key.add("orderStatus");
            key.add("reasonCode");
            key.add("expressName");
            key.add("expressNumber");
            key.add("expressCode");

            value.add(orderNumber);
            value.add(orderStatus);
            value.add("");
            value.add("tgr18");
            value.add("%E5%95%86%E5%AE%B6%E8%87%AA%E6%9C%89%E7%89%A9%E6%B5%81");
            value.add("99900046");
        }


    }

}
