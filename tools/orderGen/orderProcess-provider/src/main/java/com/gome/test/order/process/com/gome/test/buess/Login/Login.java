package com.gome.test.order.process.com.gome.test.buess.Login;

import com.alibaba.fastjson.JSONObject;
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
 * Created by liangwei-ds on 2017/2/27.
 */
public class Login {

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

        jsonObject = doLogin("", "");
        return jsonObject;
    }

    public JSONObject index() {
        String loginUrl = Constant.INDEXURI;
        try {
            response = httpclient.visit(loginUrl);
            is = verify.doverify(response, "国美电器唯一官方网上商城");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }

    public JSONObject doLogin(String username, String password) {
        this.index();
        String loginuri = Constant.LOGINURI;
        makeEntity(username, password);
        try {
            response = httpclient.visit(loginuri, key, value);
//            System.out.println("登录返回信息：" + response);
            String reg = "\"userId\":\"(\\d+)\"";
            Pattern pattern = this.getMatcher(reg);
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                String userId = matcher.group(1);
                ContextUtils.getContext().put("userId", userId);
            }

            is = verify.doverify(response, "登录成功");
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

        key.add("userName");
        key.add("passWord");
        key.add("passWordSms");
        key.add("checkCode");
        key.add("redirectUrl");
        key.add("mode");
        key.add("smsIp");
        if (username == null || username == "") {
            value.add("ermadmin");
            //"34c458b3597a0a3447c180916cd61d05525eaf49175f3234b57d4d9b6029b2b253f693b70a0e585e59a1fae35da6b469e6093c46e6c890cac9f82416d34f9b2cbaf1309d3f3d8354665f17a576a26fba93772094a8ab0ee3a9b4a0d1eca32d4dda60a0c7822e72cb96e6caab39b3a248f27b3e56adff80d8d1d0069cede61be6";
            value.add("4aa9a72211d9810ad6ff24b7adf3d8954819410be5ae0bc2bccc56032399a819e372a91913fdc3b97ab4e37ba673241e85c039f6f0dc252d9f7dca24c95d1ba3d3f4d9af0ceba2a88a234b2737a3e265d094ce413123d78f4b0b14e3a988c452fd70e24fbfc390b847840b9bd72e6838407d7e6311e8876c0e5ff07d9b6b16a9");
            value.add("");
        } else {
            value.add(username);
            value.add("4aa9a72211d9810ad6ff24b7adf3d8954819410be5ae0bc2bccc56032399a819e372a91913fdc3b97ab4e37ba673241e85c039f6f0dc252d9f7dca24c95d1ba3d3f4d9af0ceba2a88a234b2737a3e265d094ce413123d78f4b0b14e3a988c452fd70e24fbfc390b847840b9bd72e6838407d7e6311e8876c0e5ff07d9b6b16a9");
            value.add("");
        }
        value.add("");
        value.add("");
        value.add("pwd");
        value.add("");
    }

}
