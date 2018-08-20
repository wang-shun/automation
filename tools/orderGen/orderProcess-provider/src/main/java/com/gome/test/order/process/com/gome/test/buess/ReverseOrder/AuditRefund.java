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
 * Created by zhangwan on 2017/3/20.
 */
public class AuditRefund {

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
    public JSONObject refundList(String returnRequestId) {
        String listuri = Constant.REFUNDURI;
        makeEntity1(returnRequestId);
        try {
            Thread.sleep(5000);
            response = httpclient.visit(listuri, key, value);
            String reg1 = "(refundRequestId=\")(.*)(\" auditRefundId)";
            String reg2 = "(auditRefundId=\")(.*)(\" refundType)";
            String reg3 = "(refundType=\")(.*)(\" num)";

            Pattern pattern1 = this.getMatcher(reg1);
            Pattern pattern2 = this.getMatcher(reg2);
            Pattern pattern3 = this.getMatcher(reg3);

            Matcher matcher1 = pattern1.matcher(response);
            Matcher matcher2 = pattern2.matcher(response);
            Matcher matcher3 = pattern3.matcher(response);
            String refundId="";
            String refundType="";
            String auditId="";
            if (matcher1.find()) {
                refundId = matcher1.group(2);
                ContextUtils.getContext().put("refundId", refundId);
            }
            if (matcher2.find()) {
                auditId = matcher2.group(2);
                ContextUtils.getContext().put("auditId", auditId);
            }
            if (matcher3.find()) {
                refundType = matcher3.group(2);
                ContextUtils.getContext().put("refundType", refundType);
            }
            is = verify.doverify(response, "逆向审核退款单列表");
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

    private void makeEntity1(String returnRequestId) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("refundRequestId");
        key.add("refundMethod");
        key.add("refundStatus");
        key.add("auditStatus");
        key.add("orderId");
        key.add("shippingGroupId");
        key.add("accountCode");
        key.add("refundLever");
        key.add("login");
        key.add("returnRequestId");
        key.add("startCreateDate");
        key.add("endCreateDate");
        key.add("startUpdateDate");
        key.add("endUpdateDate");
        key.add("auditSource");
        key.add("currentPage");
        key.add("pageSize");

        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add(returnRequestId);
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("0");
        value.add("1");
        value.add("20");
    }
    public Order doAuditRefund(Order order) {
        JSONObject subjson = new JSONObject();
        StringBuilder desc = new StringBuilder();
        String ss = order.getDesc();
        if (ss != null && ss != "") {
            desc.append(ss);
        } else {
            desc.append("<br />");
        }
//        SCN.clear();
//        Login ermlogin = new Login();
//        subjson = ermlogin.doLogin("", "");
        GUI.guiLogin();
        subjson= this.auditRefund(order);
        if (subjson.get("isSuccess").equals("true")) {
            order.setIssuccess(true);
            String descformat = String.format("<br />%s 执行退货审核：%s，状态为审核成功", this.getTime(), order.getOrderNo());
            desc.append(descformat);
            order.setDesc(desc.toString());
        } else {
            order.setIssuccess(false);
            String descformat = String.format("<br />%s 执行退货审核：%s，状态为审核失败", this.getTime(), order.getOrderNo());
            desc.append(descformat);
            order.setDesc(desc.toString());
        }
        return order;
    }


    public JSONObject auditRefund(Order order) {
        this.refundList(order.getRequestId());
        String refunduri = Constant.AUDITREFUNDURI;
        makeEntity();
        try {
            response = httpclient.visit(refunduri, key, value);
            is = verify.doverify(response, "true");
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
            jsonObject.put("message", "退款审核成功");
            if (response.contains("退款审核成功")) {
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

    private void makeEntity() {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("refundId");
        key.add("refundType");
        key.add("auditId");
        key.add("auditResult");
        key.add("contextNote");

        String refundId = "";
        if (ContextUtils.getContext().get("refundId") != null) {
            refundId = ContextUtils.getContext().get("refundId").toString();
        }
        System.out.println(String.format("refundId：%s", refundId));
        log.info(String.format("refundId：%s", refundId));
        String refundType = "";
        if (ContextUtils.getContext().get("refundType") != null) {
            refundType = ContextUtils.getContext().get("refundType").toString();
        }
        System.out.println(String.format("refundType：%s", refundType));
        log.info(String.format("refundType：%s", refundType));
        String auditId = "";
        if (ContextUtils.getContext().get("auditId") != null) {
            auditId = ContextUtils.getContext().get("auditId").toString();
        }
        System.out.println(String.format("auditId：%s", auditId));
        log.info(String.format("auditId：%s", auditId));
        value.add(refundId);
        value.add(refundType);
        value.add(auditId);
        value.add("yes");
        value.add("");

    }
}
