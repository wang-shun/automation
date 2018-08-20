package test.Order;

import net.minidev.json.JSONObject;
import test.constant.Constant;
import test.httpClient.MhttpClient;
import test.share.Verify;

/**
 * Created by liangwei-ds on 2016/9/22.
 */
public class submitOrder {
    String is;
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    JSONObject jsonObject = new JSONObject();

    public JSONObject dosubmit() {
        String uri = Constant.SUBMITURI;
        try{
            response = httpclient.visit(uri);
            is = verify.doverify(response, "success\":true");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }

    public JSONObject initOrder() {
        String uri = Constant.INITORDER;
        try {
            response = httpclient.visit(uri);
            is = verify.doverify(response, "success\":true");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }


    public JSONObject makeJson() {
        if (is.equals("success")) {
            //�ɹ� jsonObject
            jsonObject.put("isSuccess","true");
            jsonObject.put("message","���������ɹ�");
            if (!response.contains("phoneNumber")){
                jsonObject.put("data",response);
            }
        }else if (is.equals("failed_notcontains_expect")) {
                //ʧ�ܣ�������ָ��ֵ
                jsonObject.put("isSuccess","false");
                jsonObject.put("message",response);
        } else if (is.equals("failed_response_null")) {
                //����ֵnull
                jsonObject.put("isSuccess","false");
                jsonObject.put("message",response);
        }

        return jsonObject;
    }
}