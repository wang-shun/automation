package com.gome.test.order.process.com.gome.test.buess.ReverseOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.OrderUtils;
import com.gome.test.utils.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/3/13.
 */
public class GetReturnOrder {
    Log log = LogFactory.getLog( this .getClass());
    private Timestamp start = null;
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

    public JSONObject doReturnOrder(Order order) {
        boolean find = false;
        int k = 1;
        while (!find) {
            String statusuri = Constant.ORDERLIST + "&currentpage=" + k;
            try {
                response = httpclient.visit(statusuri, order.getOrderNo());
                if (response.contains("responseDesc\":\"成功")) {
                    //System.out.println(String.format("修改订单状态为：%s,返回结果：%s",orderStatus,response));
                    is = verify.doverify(response, order.getOrderNo());
                    if ("success".equals(is)) {
                        find = true;
                        List list = new ArrayList();
                        String pp = "(commerceItemId(.*?)submitDateTime)";
                        Pattern ppp = Pattern.compile(pp);
                        Matcher m = ppp.matcher(response);
                        while (m.find()) {
                            String str = m.group();
                            list.add(str);
                        }
                        for (int i = 0; i < list.size(); i++) {
                            String p = "commerceItemId\":\"(\\d+)\",\"detailId\":\"(\\d+)\".*\"shipId\":\"(\\d+)\",\"skuid\":\".*" + order.getOrderNo();
                            Matcher matcher = this.getmatcher(p, list.get(i).toString());
                            if (matcher.find()) {
                                String commerceItemId = matcher.group(1);
                                String detailId = matcher.group(2);
                                String shipId = matcher.group(3);
                                Logger.info(String.format("commerceItemId:%s\ndetailId=%s\nshipId=%s", commerceItemId, detailId, shipId));
                                ContextUtils.getContext().put("commerceItemId", commerceItemId);
                                ContextUtils.getContext().put("detailId", detailId);
                                ContextUtils.getContext().put("shipId", shipId);
                                break;
                            }
                        }
                    }
                } else {
                    is = "failed_notcontains_expect";
                    break;
                }
            } catch (Exception e) {
                is = "failed_response_null";
                response = e.toString();
            }
            k++;
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
