package com.gome.test;


import net.minidev.json.JSONObject;
import org.junit.Test;
import test.Order.OrderMessage;
import test.Order.submitOrder;
import test.cart.BaseCart;
import test.login.Login;

/**
 * Created by hacke on 2016/9/23.
 */
public class HelperTest {
    Login login = new Login();
    submitOrder submitO = new submitOrder();
    BaseCart baseCart = new BaseCart();
    @Test
    public void test(){
        OrderMessage o ;
        o = doTest();
        o.getJson();
        System.out.println("End");

    }

    public OrderMessage doTest() {
        JSONObject jsonObject ;
        JSONObject jsonObject1 = new JSONObject();
        jsonObject = login.index();
        jsonObject1.put("登录首页", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        jsonObject = login.doLogin();
        jsonObject1.put("登录", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        jsonObject = baseCart.doClearCart();
        jsonObject1.put("清空购物车", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        jsonObject = baseCart.doAdd("9134300246", "1123240414", "1");
        jsonObject1.put("添加购物车", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        jsonObject = baseCart.doLoadCart();
        jsonObject1.put("加载购物车", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        baseCart.doSelect();
        jsonObject = baseCart.doCheckOut();
        jsonObject1.put("去结算", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        jsonObject = submitO.initOrder();
        jsonObject1.put("初始化订单", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        jsonObject = submitO.dosubmit();
        jsonObject1.put("提交订单", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        System.out.println(String.format("最终json：\n%s", jsonObject1.toString()));
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.doJson(jsonObject1);
        return orderMessage;
    }
}
