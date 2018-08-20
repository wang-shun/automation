package com.gome.test.order.process.com.gome.test.buess.ReverseOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.Login.Login;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient.SCN;

/**
 * Created by zhangwan on 2017/3/21.
 */
public class REOrderStatus {

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
        return time;
    }
    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }
    public JSONObject searchEx(String returnRequestId) {
        String searchuri = Constant.SEARCHEXURI;
        makeEntity1(returnRequestId);
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
            String reg1 = "(换货配送单号:\\(<a href=\")(.*)(\" target=)";

            Pattern pattern1 = this.getMatcher(reg1);
            Matcher matcher1 = pattern1.matcher(response);

            String detailURI="";
            if (matcher1.find()) {
                detailURI = matcher1.group(2);
                ContextUtils.getContext().put("detailURI", detailURI);
            }
            is = verify.doverify(response, "逆向订单详情");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson1();
    }

    public JSONObject makeJson1() {
        if (is.equals("success")) {
            //成功 jsonObject
            jsonObject.put("isSuccess", "true");
            jsonObject.put("message", "查询成功");
            if (response.contains("查询成功")) {
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

    private void makeEntity1(String reverseOrderId) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("reverseOrderId");
        value.add(reverseOrderId);

    }

    public JSONObject searchTransportId(Order order) {
        this.searchEx(order.getRequestId());
        String searchuri = "";
        if (ContextUtils.getContext().get("detailURI") != null) {
            searchuri = ContextUtils.getContext().get("detailURI").toString();
        }
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri);
            String reg1 = "(配送单号：<a href=\")(.*)(\">)(.*)(</a>)";

            Pattern pattern1 = this.getMatcher(reg1);
            Matcher matcher1 = pattern1.matcher(response);

            String transportId="";
            if (matcher1.find()) {
                transportId = matcher1.group(4);
                ContextUtils.getContext().put("transportId", transportId);
            }
            is = verify.doverify(response, "订单详情");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson2();
    }

    public JSONObject makeJson2() {
        if (is.equals("success")) {
            //成功 jsonObject
            jsonObject.put("isSuccess", "true");
            jsonObject.put("message", "查询成功");
            if (response.contains("查询成功")) {
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
    public Order doReOrderStatus(Order order) {
        JSONObject subjson = new JSONObject();
        StringBuilder desc = new StringBuilder();
        String ss = order.getDesc();
        if (ss != null && ss != "") {
            desc.append(ss);
        } else {
            desc.append("<br />");
        }
        String status="";
        if("RETURNEXSTATUS".equals(order.getCurrentStatus())){
            status ="DL";
        } else {
            status ="EX";
        }
//        SCN.clear();
//        Login ermlogin = new Login();
//        subjson = ermlogin.doLogin("", "");
        GUI.guiLogin();
        subjson= this.reOrderStatus(order,status);
        if (subjson.get("isSuccess").equals("true")) {
            order.setIssuccess(true);
            String descformat = String.format("<br />%s 执行修改换货订单妥投状态：%s，状态为(%s)修改成功", this.getTime(), order.getOrderNo(),status);
            desc.append(descformat);
            order.setDesc(desc.toString());
        } else {
            order.setIssuccess(false);
            String descformat = String.format("<br />%s 执行修改换货订单妥投状态：%s，状态为(%s)修改失败", this.getTime(), order.getOrderNo(),status);
            desc.append(descformat);
            order.setDesc(desc.toString());
        }
        return order;
    }
    public JSONObject reOrderStatus(Order order,String status) {
        this.searchTransportId(order);
        String statusuri = Constant.STATUSURI;
       String transportId = ContextUtils.getContext().get("transportId").toString();
        makeEntity(transportId, status);
        try {
            response = httpclient.visit(statusuri, key, value);
            System.out.println(String.format("修改订单状态为：%s,返回结果：%s", status, response));
            log.info(String.format("修改订单状态为：%s,返回结果：%s", status, response));
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
            jsonObject.put("message", "换货妥投成功");
            if (response.contains("换货妥投成功")) {
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

    private void makeEntity(String orderNumber,String orderStatus) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        if("EX".equals(orderStatus)) {
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
        }else if("DL".equals(orderStatus)){
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
        }
    }
}
