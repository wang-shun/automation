package com.gome.test.order.process.com.gome.test.buess.ReverseOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.Login.Login;
import com.gome.test.order.process.com.gome.test.buess.Login.SCLogin;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.utils.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient.SCN;

/**
 * Created by liangwei-ds on 2017/3/13.
 */
public class SubmitreturnOrder {
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

    private Matcher getmatcher(String p) {
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(response);
        return matcher;
    }

    private Matcher getmatcher(String p, String target) {
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(target);
        return matcher;
    }

    public static void main(String[] args) {
        SubmitreturnOrder submitreturnOrder = new SubmitreturnOrder();
//        submitreturnOrder.returnOrder("15103151737");
    }

    public Order returnOrder(Order order) {
        StringBuilder desc = new StringBuilder();
        String ss = order.getDesc();
        if (ss != null && ss != "") {
            desc.append(ss);
        } else {
            desc.append("<br />");
        }
        JSONObject subjson = new JSONObject();
        GetReturnType returnType = new GetReturnType();
        subjson = returnType.getReturnType(order);
        String returnTypeStr = ContextUtils.getContext().get("returnType").toString();
        String   shipGroupId="";
        if (ContextUtils.getContext().get("shipId") != null) {
            shipGroupId = ContextUtils.getContext().get("shipId").toString();
        }
        if("true".equals(returnTypeStr)) {
            SCLogin login = new SCLogin();
            subjson = login.doLogin(order.getUsername(), order.getPassword());
            GetReturnOrder getReturnOrder = new GetReturnOrder();
            subjson = getReturnOrder.doReturnOrder(order);
            if ("true".equals(subjson.get("isSuccess"))) {
                GetInvoice getInvoice = new GetInvoice();
                subjson = getInvoice.getInvoice(order.getOrderNo());
                GetMD5 getMD5 = new GetMD5();
                SubmitreturnOrder submitreturnOrder = new SubmitreturnOrder();
                try {
                    String commerceItemId = ContextUtils.getContext().get("commerceItemId").toString();
                    String shipId = ContextUtils.getContext().get("shipId").toString();
                    String detailId = ContextUtils.getContext().get("detailId").toString();
                    subjson = getMD5.getMd5(order.getOrderNo(), commerceItemId, shipId, detailId);
                    String invoice = ContextUtils.getContext().get("invoice").toString();
                    String md5str = ContextUtils.getContext().get("md5str").toString();

                    subjson = submitreturnOrder.submitOrder(order.getOrderNo(), commerceItemId, shipId, detailId, invoice, md5str, order.getReturnType());
                    if (subjson.get("isSuccess").equals("true")) {
                        order.setOrderNumber(shipGroupId);
                        order.setIssuccess(true);
                        String descformat = String.format("<br />%s 执行商城申请退换货：%s，状态为申请成功", this.getTime(), order.getOrderNo());
                        desc.append(descformat);
                        order.setDesc(desc.toString());
                        String requestId = ContextUtils.getContext().get("ermId").toString();
                        order.setRequestId(requestId);
                    } else {
                        order.setIssuccess(false);
                        String descformat = String.format("<br />%s 执行商城申请退换货：%s，状态为申请失败", this.getTime(), order.getOrderNo());
                        desc.append(descformat);
                        order.setDesc(desc.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                order.setOrderNumber(shipGroupId);
                order.setIssuccess(false);
                String descformat = String.format("<br />%s 执行申请退换货：%s，该订单没找到", this.getTime(), order.getOrderNo());
                desc.append(descformat);
                order.setDesc(desc.toString());
            }
        }else {
            order.setOrderNumber(shipGroupId);
           order.setIssuccess(false);
           String descformat = String.format("%s 当前商品不能退换货", this.getTime(), order.getOrderNo());
           desc.append(descformat);
           order.setDesc(desc.toString());
       }
        return order;
    }

    public JSONObject submitOrder(String orderId, String commerceItemId, String shipId, String detailId, String invoiceNum, String md5str, String returnType) {
        String statusuri = String.format("%sorderId=%s&commerceItemId=%s&shippingGroupId=%s&itemDetailId=%s&invoiceNum=%s&md5str=%s&returnType=%s", Constant.SUBMIT, orderId, commerceItemId, shipId, detailId, invoiceNum, md5str, returnType);
        try {
            response = httpclient.visit(statusuri, orderId);
            //System.out.println(String.format("修改订单状态为：%s,返回结果：%s",orderStatus,response));
            is = verify.doverify(response, "\"success\":true");
            String p = "\"message\":\"(\\d+)\"";
            Matcher matcher = this.getmatcher(p);
            if (matcher.find()) {
                String ermId = matcher.group(1);
                Logger.info(String.format("ermId:%s", ermId));
                ContextUtils.getContext().put("ermId", ermId);
            }
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
}
