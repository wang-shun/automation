package com.gome.test.order.process.com.gome.test.buess.ReverseOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.utils.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/3/13.
 */
public class GetInvoice {


    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
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

    public JSONObject getInvoice(String orderId) {
        String invoiceurl = String.format("%s%s", Constant.INVOICE, orderId);
        try {
            response = httpclient.visit(invoiceurl, orderId);
            //System.out.println(String.format("修改订单状态为：%s,返回结果：%s",orderStatus,response));
            is = verify.doverify(response, orderId);
            String p = "invoiceNum=(\\d+)&orderId";
            Matcher matcher = this.getmatcher(p);
            if (matcher.find()) {
                String invoice = matcher.group(1);
                Logger.info(String.format("invoice:%s", invoice));
                ContextUtils.getContext().put("invoice", invoice);
            } else {
                ContextUtils.getContext().put("invoice", "");
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
