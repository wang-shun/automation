package test.cart;


import net.minidev.json.JSONObject;
import test.constant.Constant;
import test.httpClient.MhttpClient;
import test.share.Verify;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2016/9/22.
 */
public class BaseCart {
    JSONObject jsonObject = new JSONObject();
    Verify verify = new Verify();
    MhttpClient httpclient = new MhttpClient();
    String response;
    String is;

    public JSONObject doClearCart(){
        String url = Constant.CLEARURI;
        try {
            response = httpclient.visit(url);
            is = verify.doverify(response, "success\":true");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    public JSONObject doAdd(List<String> porUri,String pcount) throws Exception{
        JSONObject subjsonObject = new JSONObject();
        String p = "cn/(\\d+)-(\\d+)\\.html";
        String value = "";
        String porductId = "";
        String skuId = "";
        Pattern pattern = Pattern.compile(p);

        for (int i=0;i<porUri.size();i++){
            value = porUri.get(i);
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()){
                porductId = matcher.group(1);
                skuId = matcher.group(2);
                jsonObject = doAdd(porductId, skuId, pcount);
                subjsonObject.put(String.format("第%d件商品%s-%s",i+1,porductId,skuId),jsonObject);
            }else {
                throw new Exception("链接中没有找到productId");
            }
        }
        return subjsonObject;
    }
    public JSONObject doAdd(String product,String sku,String pcount){
        String baseUrl = Constant.ADDTOCARTURI;
        StringBuilder uri = new StringBuilder(baseUrl);
        uri.append("callback=cart&type=0&sid=");
        uri.append(sku);
        uri.append("&pid=");
        uri.append(product);
        uri.append("&pcount=");
        uri.append(pcount);
        uri.append("&wpid=&wsid=");
        baseUrl = uri.toString();
        try {
            response = httpclient.visit(baseUrl);
            is = verify.doverify(response, "success\":true,");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }

    public JSONObject doLoadCart(){
        String loadCartUri = Constant.LOADCARTURI;
        try {
            response = httpclient.visit(loadCartUri);
            is = verify.doverify(response,"success\":true,");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    public JSONObject doCheckOut(){
        String checkouturi = Constant.CHECKURI;
        try {
            response = httpclient.visit(checkouturi);
            is = verify.doverify(response, "success\":true,");
        }catch (Exception e){
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    public void doSelect(){
        String uri = Constant.SELECTITRM;
        httpclient.visit(uri);
    }

    public JSONObject makeJson(){
        if (is.equals("success")){
            //成功 jsonObject
            jsonObject.put("isSuccess","true");
            jsonObject.put("message","");
        }else if (is.equals("failed_notcontains_expect")){
            //失败，不包含指定值
            jsonObject.put("isSuccess","flase");
            jsonObject.put("message",response);
        }else if (is.equals("failed_response_null")){
            //返回值null
            jsonObject.put("isSuccess","flase");
            jsonObject.put("message",response);
        }
        return jsonObject;
    }
}
