package test.pay;

import ch.qos.logback.core.util.ContextUtil;
import com.gome.test.context.ContextUtils;
import net.minidev.json.JSONObject;
import test.constant.Constant;
import test.httpClient.MhttpClient;
import test.share.Verify;

/**
 * Created by liangwei-ds on 2017/2/27.
 */
public class Pay {

    String is;
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    JSONObject jsonObject = new JSONObject();

    public JSONObject dopay() {
        String uri = Constant.Pay;
        uri = uri.concat(String.format("userId=%s&orderIdsStr=%s", ContextUtils.getContext().get("userId"),ContextUtils.getContext().get("orderId")));
        try{
            response = httpclient.visit("http://10.126.45.48/mockinterstatus?maction=true");
            Thread.sleep(2000);
            response = httpclient.visit(uri);
            System.out.print(uri);
            is = verify.doverify(response, "SUCCESS");
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
            jsonObject.put("message","支付成功");
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
