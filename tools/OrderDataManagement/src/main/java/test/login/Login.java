package test.login;

import net.minidev.json.JSONObject;
import test.constant.Constant;
import test.httpClient.MhttpClient;
import test.share.Verify;

import java.util.ArrayList;
import java.util.List;

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


    public JSONObject doLogin() {
        jsonObject = doLogin("", "") ;
        return jsonObject;
    }

    public JSONObject index(){
        String loginUrl = Constant.INDEXURI;
        try {
            response = httpclient.visit(loginUrl);
            is = verify.doverify(response, "��������Ψһ�ٷ������̳�");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    public JSONObject doLogin(String username, String password)   {
        String doLogin = Constant.LOGINURI;
        makeEntity(username, password);
        try {
            response = httpclient.visit(doLogin, key, value);
            verify.doverify(response,"��¼�ɹ�");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }

    public JSONObject makeJson(){
        if (is.equals("success")){
            //�ɹ� jsonObject
            jsonObject.put("isSuccess","true");
            jsonObject.put("message","���ʳɹ�");
            if(response.contains("��¼�ɹ�")){
                jsonObject.put("data",response);
            }

        }else if (is.equals("failed_notcontains_expect")){
            //ʧ�ܣ�������ָ��ֵ
            jsonObject.put("isSuccess","false");
            jsonObject.put("message","����ʧ�ܣ�������ָ��ֵ");
            jsonObject.put("data",response);
        }else if (is.equals("failed_response_null")){
            //����ֵnull
            jsonObject.put("isSuccess","false");
            jsonObject.put("message","�������ó���");
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
        if (username == "") {
            value.add("rainman");
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