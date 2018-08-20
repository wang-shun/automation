package test.Order;


import com.gome.test.context.ContextUtils;
import net.minidev.json.JSONObject;
import test.constant.Constant;
import test.httpClient.MhttpClient;
import test.share.Verify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2016/9/22.
 */
public class SubmitOrder {
    String is;
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    JSONObject jsonObject = new JSONObject();

    public JSONObject dosubmit() {
        String uri = Constant.SUBMITURI;
        try{
            response = httpclient.visit(uri);

            String reg = "\"cartId\":\"(\\d+)\"";
            Pattern pattern = this.getMatcher(reg);
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()){
                String orderId = matcher.group(1);
                ContextUtils.getContext().put("orderId",orderId);
            }
            is = verify.doverify(response, "success\":true");
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
            //成功 jsonObject
            jsonObject.put("isSuccess","true");
            jsonObject.put("message","订单操作成功");
            if (!response.contains("phoneNumber")){
                jsonObject.put("data",response);
            }
        }else if (is.equals("failed_notcontains_expect")) {
                //失败，不包含指定值
                jsonObject.put("isSuccess","false");
                jsonObject.put("message",response);
        } else if (is.equals("failed_response_null")) {
                //返回值null
                jsonObject.put("isSuccess","false");
                jsonObject.put("message",response);
        }

        return jsonObject;
    }
}