package com.gome.test.order.process.com.gome.test.buess.ConfirmReceipt;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.Login.SCLogin;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by zhangwan on 2017/3/31.
 */
public class ConfirmReceipt {
    Log log = LogFactory.getLog( this .getClass());
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    JSONObject jsonObject = new JSONObject();
    String is;

    String getTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        String time = dateFormat.format(now);
        return time;
    }

    public static void main(String[] args){
        ConfirmReceipt confirm = new ConfirmReceipt();
        confirm.doConfirmReceipt("test7","gome@2016","15103152543");
    }

    public String doConfirmReceipt(String username, String password , String orderNos) {
        String descformat = "";
        String[] orderArray = orderNos.split(",");
        for (String orderNo : orderArray) {
            JSONObject subjson = new JSONObject();
            SCLogin login = new SCLogin();
            subjson = login.doLogin(username, password);
            subjson = this.confirmReceipt(orderNo);
            if (subjson.get("isSuccess").equals("true")) {
                descformat = String.format("<br />%s 执行确认收货：%s，状态为确认收货成功", this.getTime(), orderNo);
                log.info(descformat);
                System.out.println(descformat);
            } else {
                descformat = String.format("<br />%s 执行确认收货：%s，状态为确认收货失败", this.getTime(), orderNo);
                log.info(descformat);
                System.out.println(descformat);
            }
        }
        return descformat;
    }
    public JSONObject confirmReceipt(String orderNo) {
        String confirmReceipturi = Constant.CONFIRMRECEIPT + "&orderId=" + orderNo;
        try {
            response = httpclient.visit(confirmReceipturi);
            is = verify.doverify(response, "\"result\":{\"pOrderConfirm\":true},\"success\":true");
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
            jsonObject.put("message", "确认收货成功");
            if (response.contains("确认收货成功")) {
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
