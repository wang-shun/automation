package com.gome.test.order.process.com.gome.test.buess.SearchOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangwan on 2017/4/20.
 */
public class AutoAudit {

    String getTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        String time = dateFormat.format(now);
        return time;
    }
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;
    public Order doAutoAudit(Order order) {
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
        subjson= this.autoAudit(order);
        if (subjson.get("isSuccess").equals("true")) {
            order.setIssuccess(true);
            String descformat = String.format("<br />%s 执行自动审核：%s，状态为审核成功", this.getTime(), order.getOrderNo());
            desc.append(descformat);
            order.setDesc(desc.toString());
        } else {
            order.setIssuccess(false);
            String descformat = String.format("<br />%s 执行自动审核：%s，状态为审核失败", this.getTime(), order.getOrderNo());
            desc.append(descformat);
            order.setDesc(desc.toString());
        }

        return order;
    }

    public JSONObject autoAudit(Order order) {
        String statusuri = Constant.AUTOAUDITURI;
        makeEntity(order.getOrderNo());
        try {
            response = httpclient.visit(statusuri, key, value);
            is = verify.doverify(response, "code\":\"sucess");
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
            jsonObject.put("message","自动审核成功");
            if (response.contains("自动审核成功")) {
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

    private void makeEntity(String orderNo) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("orderIds");

        value.add(orderNo);

    }
}
