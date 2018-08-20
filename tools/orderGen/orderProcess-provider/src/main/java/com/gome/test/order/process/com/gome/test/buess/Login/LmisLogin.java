package com.gome.test.order.process.com.gome.test.buess.Login;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.api.utils.HttpClient;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/3/22.
 */
public class LmisLogin {
    Log log = LogFactory.getLog( this .getClass());
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;


    public static void main(String[] args) {
        Login login = new Login();
        login.index();
        login.doLogin();

    }

    JSONObject doLogin() {

        jsonObject = doLogin("fengjiangwei", "123123");
        return jsonObject;
    }

    public JSONObject index() {
        String loginUrl = Constant.LIMSINDEX;
        try {
            response = httpclient.visit(loginUrl);
            is = verify.doverify(response, "安迅物流-物流信息管理系统");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    private Matcher getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        Matcher matcher = pattern.matcher(response);
        return matcher;
    }

    public JSONObject doLogin(String username, String password) {

        //this.index();
        MhttpClient.SCN.clear();
        String loginuri = Constant.LMISLOGINURI;
        makeEntity(username, password);
        try {
            response = httpclient.visit(loginuri, key, value);
            System.out.println("登录返回信息：" + response);
            log.info("登录返回信息：" + response);
            String reg = "name=\"Login_SessionId\" value=\"(\\w+)\"";
            Matcher matcher = this.getMatcher(reg);
            if (matcher.find()) {
                String sessionid = matcher.group(1);
                ContextUtils.getContext().put("sessionId", sessionid);
            }
                is = verify.doverify(response, "冯江伟");
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
            if (response.contains("登录成功")) {
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

    private void makeEntity(String username, String password) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("username");
        key.add("password");
        key.add("imageId");
        key.add("validationCode");
        key.add("x");
        key.add("y");
        if (username == null || username == "") {
            value.add("fengjiangwei");
            value.add("123123");
            value.add("582eeab5a1e0483194f837146e0dfd64");
        } else {
            value.add(username);
            value.add(password);
            value.add("582eeab5a1e0483194f837146e0dfd64");
        }
        value.add("");
        value.add("43");
        value.add("7");
    }

}
