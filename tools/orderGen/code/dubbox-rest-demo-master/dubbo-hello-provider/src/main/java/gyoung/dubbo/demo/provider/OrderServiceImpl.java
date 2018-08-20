package gyoung.dubbo.demo.provider;

import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import gyoung.dubbo.demo.api.OrderMessage;
import gyoung.dubbo.demo.api.OrderService;
import net.minidev.json.JSONObject;
import org.testng.annotations.Test;
import test.Order.SubmitOrder;
import test.cart.BaseCart;
import test.login.Login;
import test.mongoldb.MongoDBDao;
import test.pay.Pay;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangwei-ds on 2017/2/25.
 */

@Path("liangwei")
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})

public class OrderServiceImpl implements OrderService {
    Login login = new Login();
    SubmitOrder submitO = new SubmitOrder();
    BaseCart baseCart = new BaseCart();
    Pay pay = new Pay();
    OrderMessage orderMessage = new OrderMessage();
/*    @Test
    public void test(){
        orderMessage = defaultOrder();
        Map<String,String> json=new HashMap<String, String>();
        json = orderMessage.getJson();
       *//* MongoDBDao mogol = new MongoDBDao();
        mogol.init("10.144.32.129",27017);
        mogol.insert(JSONObject.toJSONString(json),"liangweitest");*//*
        System.out.println("End");
    }*/

    public OrderMessage getOrder(String UerName,String passWord, String orderType, String procount, String address, List<String> pro) {
        JSONObject jsonObject ;
        JSONObject jsonObject1 = new JSONObject();
        jsonObject = login.index();
        jsonObject1.put("登录首页", new JSONObject(jsonObject));

        jsonObject = login.doLogin();
        jsonObject1.put("登录", new JSONObject(jsonObject));

        jsonObject = baseCart.doClearCart();
        jsonObject1.put("清空购物车", new JSONObject(jsonObject));

        try {
            jsonObject = baseCart.doAdd(pro,procount);
            jsonObject1.put("添加购物车", new JSONObject(jsonObject));
        }catch (Exception e){
            e.printStackTrace();
        }

/*
        jsonObject = baseCart.doAdd("9134300246", "1123240414", "1");
        jsonObject1.put("添加购物车", new JSONObject(jsonObject));
*/

        jsonObject = baseCart.doLoadCart();
        jsonObject1.put("加载购物车", new JSONObject(jsonObject));

        baseCart.doSelect();
        jsonObject = baseCart.doCheckOut();
        jsonObject1.put("去结算", new JSONObject(jsonObject));

        jsonObject = submitO.initOrder();
        jsonObject1.put("初始化订单", new JSONObject(jsonObject));

        jsonObject = submitO.dosubmit();
        jsonObject1.put("提交订单", new JSONObject(jsonObject));

        jsonObject = pay.dopay();
        jsonObject1.put("支付订单",new JSONObject(jsonObject));

        System.out.println(String.format("最终json：\n%s", jsonObject1.toString()));

        orderMessage.doJson(jsonObject1);
        return orderMessage;
    }

    @GET
    public OrderMessage defaultOrder() {
        List<String> s = new ArrayList<String>();
        s.add("http://item.atguat.com.cn/9200001304-1000088237.html?intcmp=list-9000000700-1_2_1");
/*        s.add("http://item.gome.com.cn/9134402203-1123341828.html?intcmp=tongxun-1000047421-2");
        s.add("http://item.gome.com.cn/9134360175-1123300075.html?intcmp=tongxun-1000047425-4");*/
        orderMessage = getOrder("","","","1","",s);
        return orderMessage;
    }

}
