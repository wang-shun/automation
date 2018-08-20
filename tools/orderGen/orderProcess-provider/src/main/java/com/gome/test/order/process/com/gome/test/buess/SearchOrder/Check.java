package com.gome.test.order.process.com.gome.test.buess.SearchOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.Login.Login;
import com.gome.test.order.process.com.gome.test.buess.ReverseOrder.SubmitreturnOrder;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;
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
public class Check {
    Log log = LogFactory.getLog( this .getClass());

    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;


//    @Test
//    JSONObject test(String orderId) {
//        jsonObject = this.doCheck(orderId);
//        return jsonObject;
//    }


    public static void main(String[] args) {
        Login login = new Login();

        login.index();
        login.doLogin("", "");
        Check submitreturnOrder = new Check();
        submitreturnOrder.doCheck("15103163134");
    }

    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }

    public JSONObject doCheck(String orderId) {
        GUI.guiLogin();
        String searchuri = Constant.CHECK;
        makeEntity(orderId);
        try {
            Thread.sleep(3000);
            response = httpclient.visit(searchuri, key, value);
            System.out.println(String.format("审核返回值：%s", response));
            log.info(String.format("审核返回值：%s", response));
            String reg = "\"userId\":\"(\\d+)\"";
            Pattern pattern = this.getMatcher(reg);
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                String userId = matcher.group(1);
                ContextUtils.getContext().put("userId", userId);
            }

            is = verify.doverify(response, "本次审核通过的订单号为" + orderId);
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

        key.add("auditOrderIds");
        key.add("auditFlag");
        key.add("auditReason");


        value.add(orderId);
        value.add("1");
        value.add("");

    }

}
