package com.gome.test.order.process.com.gome.test.buess.ReverseOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.Login.SCLogin;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.utils.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangwan on 2017/4/5.
 */
public class GetReturnType {


    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    JSONObject jsonObject = new JSONObject();
    String is;

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

    public JSONObject getReturnType(Order order) {
        JSONObject subjson = new JSONObject();
        SCLogin login = new SCLogin();
        subjson = login.doLogin(order.getUsername(), order.getPassword());
        String statusuri =Constant.RETURNTYPEURI + "&orderId=" + order.getOrderNo();
        try {
            response = httpclient.visit(statusuri);
            is = verify.doverify(response, "success\":true");
            String p = "(showReturnBtn\":)(.*)(,\"showSharebtn)";
            String pp = "(shipId\":\")(.*)(\",\"showDecipheringCode)";
            Matcher matcher = this.getmatcher(p);
            Matcher matcher1 = this.getmatcher(pp);
            if (matcher.find()) {
                String returnTypestr = matcher.group(2);
                Logger.info(String.format("returnType:%s", returnTypestr));
                ContextUtils.getContext().put("returnType", returnTypestr);
            }
            if (matcher1.find()) {
                String shipId = matcher1.group(2);
                Logger.info(String.format("shipId:%s", shipId));
                ContextUtils.getContext().put("shipId", shipId);
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

}
