package com.gome.test.order.process.com.gome.test.buess.Lmis;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/3/22.
 */
public class SubSystemModefy {
    Log log = LogFactory.getLog( this .getClass());
    /**
     * 切换到对应的DC
     */

    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;


    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }

    public JSONObject modifySystem(String SubSystem_Name) {
        String searchuri = Constant.TRANSFEROS;
        makeEntity(SubSystem_Name);
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

            is = verify.doverify(response, "");
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

    private void makeEntity(String SubSystem_Name) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("Login_SessionId");
        key.add("Login_UserId");
        key.add("Login_UserName");
        key.add("Login_CurrentDC");
        key.add("MD_Domain_Name");
        key.add("SubSystem_Name");
        key.add("Login_Url");
        key.add("YN_Domain_Name");
        key.add("isHistory");
        key.add("HDS_Domain_Name");

        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("10.128.11.107");
        value.add(SubSystem_Name);
        value.add("");
        value.add("10.128.37.88%3A8079");
        value.add("");
        value.add("10.128.36.61%3A8080");
    }

}
