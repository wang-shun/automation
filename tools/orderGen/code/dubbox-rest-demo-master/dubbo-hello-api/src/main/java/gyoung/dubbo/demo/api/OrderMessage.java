package gyoung.dubbo.demo.api;


import net.minidev.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2016/9/23.
 */
public class OrderMessage implements Serializable{
    private boolean isSuccess;
    private String orderId;
    private String message;
    Map<String ,String> map = new HashMap<String, String>();


    public void doJson(JSONObject jsonObject){
        String jsonpth = "$..isSuccess";
        jsonObject.get("提交订单");
        String values;
        Json j = new Json();
        try {
            values = j.readJson(jsonObject.toString(),jsonpth);
            if (!values.contains("false")){
                String order ="\\\\\\\"cartId\\\\\\\":\\\\\\\"(\\d+)\\\\\\\"";
                Pattern pattern = Pattern.compile(order);
                Matcher matcher = pattern.matcher(jsonObject.toString());
                if (matcher.find()){
                    orderId = matcher.group(1);
                }
                setIsSuccess(true);
                setMessage("");
                setOrderId(orderId);
/*                for (String key :jsonObject.keySet()){
                    map.put(key,jsonObject.get(key).toString());
                }*/
            }else {
                setIsSuccess(false);
                setMessage("下单失败");
                setOrderId(null);
                for (String key :jsonObject.keySet()){
                    map.put(key,jsonObject.get(key).toString());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Map<String,String>  getJson(){
        return map;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {

        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
