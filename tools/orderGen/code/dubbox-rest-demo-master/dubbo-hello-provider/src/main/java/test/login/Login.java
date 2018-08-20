package test.login;


import com.gome.test.context.ContextUtils;
import net.minidev.json.JSONObject;
import org.testng.annotations.Test;
import test.constant.Constant;
import test.httpClient.MhttpClient;
import test.share.Verify;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2016/9/22.
 */
public class Login {
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;

    @Test
    public JSONObject doLogin() {
        jsonObject = doLogin("", "") ;
        return jsonObject;
    }

    public JSONObject index(){
        String loginUrl = Constant.INDEXURI;
        try {
            response = httpclient.visit(loginUrl);
            is = verify.doverify(response, "国美电器唯一官方网上商城");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    private Pattern getMatcher(String partten){
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }

    public JSONObject doLogin(String username, String password)   {
        String loginuri = Constant.LOGINURI;
        makeEntity(username, password);
        try {
            response = httpclient.visit(loginuri, key, value);
            String reg = "\"userId\":\"(\\d+)\"";
            Pattern pattern = this.getMatcher(reg);
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()){
                String userId = matcher.group(1);
                ContextUtils.getContext().put("userId",userId);
            }

            verify.doverify(response,"登录成功");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }

    public JSONObject makeJson(){
        if (is.equals("success")){
            //成功 jsonObject
            jsonObject.put("isSuccess","true");
            jsonObject.put("message","访问成功");
            if(response.contains("登录成功")){
                jsonObject.put("data",response);
            }

        }else if (is.equals("failed_notcontains_expect")){
            //失败，不包含指定值
            jsonObject.put("isSuccess","false");
            jsonObject.put("message","访问失败，不包含指定值");
            jsonObject.put("data",response);
        }else if (is.equals("failed_response_null")){
            //返回值null
            jsonObject.put("isSuccess","false");
            jsonObject.put("message","方法调用出错");
            jsonObject.put("data",response);
        }
        return jsonObject;
    }
    private void makeEntity(String username,String password){
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("loginName");
        key.add("gomeOrCoo8");
        key.add("password");
        key.add("captcha");
        key.add("autoLoginMode");
        key.add("chkRememberUsername");
        key.add("loginType");
        key.add("login");
        if (username == null||username=="") {
            value.add("test7");
            value.add("gome");
            value.add("gome@2016");
        } else {
            value.add(username);
            value.add("gome");
            value.add(password);
        }
        value.add("");
        value.add("");
        value.add("1");
        value.add("0");
        value.add("");
    }
}