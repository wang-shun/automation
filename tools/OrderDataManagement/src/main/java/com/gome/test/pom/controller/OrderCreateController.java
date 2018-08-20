package com.gome.test.pom.controller;


import com.gome.test.pom.POJO.OrderInfo;
import com.gome.test.pom.cache.OrderCache;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import test.Order.OrderMessage;
import test.Order.submitOrder;
import test.cart.BaseCart;
import test.login.Login;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hacke on 2016/9/22.
 */

@Controller
public class OrderCreateController extends  BaseController{
    private Logger logger = LoggerFactory.getLogger(getClass());
//    private CommitOrder commitOrder = CommitOrder.getInstance();

    @RequestMapping("/create")
    @ResponseBody
    public ModelAndView generateOrderView(){
        ModelAndView mav = new ModelAndView("bootstrap/pomCreate");
        return mav;
    }
    @RequestMapping("/index")
    @ResponseBody
    public ModelAndView mainOrderView(){
        ModelAndView mav = new ModelAndView("bootstrap/main");
        return mav;
    }
    @RequestMapping("/left")
    @ResponseBody
    public ModelAndView leftOrderView(){
        ModelAndView mav = new ModelAndView("bootstrap/left");
        return mav;
    }
    @RequestMapping("/nav")
    @ResponseBody
    public ModelAndView navOrderView(){
        ModelAndView mav = new ModelAndView("bootstrap/nav");
        return mav;
    }
    @RequestMapping("/fastCreate")
    @ResponseBody
    public ModelAndView fastGenerateOrderView(){
        ModelAndView mav = new ModelAndView("bootstrap/fastCreate");
        return mav;
    }
    /**
     *
     * @param orderURI
     * @param orderQuentity
     * @param payType
     * @param addressID
     * @param uid
     * @param orderType
     * @return
     */
    @RequestMapping(value="/createOrderRequest.do", method=RequestMethod.POST)
    @ResponseBody
    public String createOrderRequest(
           String orderURI ,
           String orderQuentity ,
           String payType ,
           String addressID,
           String uid ,
           String orderType
    )
    {
        logger.info("------------- request create order data. controller.----------------------------");
        logger.info("request order uri is : " + orderURI);
        logger.info("request order quentity is : " + orderQuentity);
        logger.info("request order payment type is : " + payType);
        logger.info("request order address is : " + addressID);
        logger.info("request order uid is : " + uid);
        logger.info("request order order type is : " + orderType);
        Map orderInfo = getOrderMap();
//        Map orderInfo = new HashMap();
//        orderInfo.put("orderID" ,"test1234567");
//        orderInfo.put("orderStatus", "PP");
//        orderInfo.put("orderLogs","order logs.");

        if(orderInfo != null && (orderInfo.get("orderID") != null)) {
            String oid = orderInfo.get("orderID").toString();
            logger.info("create order ID is : " + oid);
            OrderCache.getInstance().setOrderInfo2Context(oid , orderInfo);
        }else{
            logger.error("get order info is NULL . ");
            logger.error(orderInfo.get("orderLogs").toString());
        }
        String orderJson = JSONObject.toJSONString(orderInfo);
        return responseSuccess(orderJson);
    }

    @RequestMapping(value="/fastCreateOrderRequest.do", method=RequestMethod.GET)
    @ResponseBody
    public String fastCreateOrderRequest(
            String sku ,
            String pid,
            String orderQuentity ,
            String payType ,
            String addressID,
            String uid ,
            String orderType
    )
    {
        logger.info("request order sku is : " + sku);
        logger.info("request order pid is : " + pid);
        logger.info("request order quentity is : " + orderQuentity);
        logger.info("request order payment type is : " + payType);
        logger.info("request order address is : " + addressID);
        logger.info("request order uid is : " + uid);
        logger.info("request order order type is : " + orderType);
        Map orderInfo = getOrderMap(sku,pid, orderQuentity);
        if(orderInfo != null && (orderInfo.get("orderID") != null)) {
            logger.info("create order ID is : " + orderInfo.get("orderID"));
            OrderCache.getInstance().setOrderInfo2Context(orderInfo.get("orderID").toString(), orderInfo);
        }else{
            logger.error("get order info is NULL . ");
            logger.error(orderInfo.get("orderLogs").toString());
        }
        String orderJson = JSONObject.toJSONString(orderInfo);
        return responseSuccess(orderJson);
    }


    @RequestMapping(value="/createOrderRequestJumpPage.do", method=RequestMethod.POST )
//    @ResponseBody
    public ModelAndView createOrderRequestJumpPage(
            String orderURI ,
            String orderQuentity ,
            String payType ,
            String addressID,
            String uid ,
            String orderType
    )
    {
        logger.info("------------- request create order data.----------------------------");
        logger.info("request order uri is : " + orderURI);
        logger.info("request order quentity is : " + orderQuentity);
        logger.info("request order payment type is : " + payType);
        logger.info("request order address is : " + addressID);
        logger.info("request order uid is : " + uid);
        logger.info("request order order type is : " + orderType);

//        ModelAndView mav = new ModelAndView("redirect:select");
        ModelAndView mav = new ModelAndView("bootstrap/pomSelect");
//        OrderInfo orderInfo = getOrder();
//        if(orderInfo != null && (!orderInfo.getOrderID().isEmpty())) {
//            logger.info("create order ID is : " + orderInfo.getOrderID());
//            mav.addObject("orderInfo" , orderInfo);
//        }else{
//            logger.error(orderInfo.getOrderLogs());
//        }
        return mav;
    }

    private OrderInfo getOrder(){

        OrderMessage orderMessage = generateSingleOrder();
        OrderInfo orderInfo = new OrderInfo();
        if(orderMessage !=null && orderMessage.getOrderId()!=null){
            orderInfo.setOrderID(orderMessage.getOrderId());
            orderInfo.setOrderStatus("PP");
            orderInfo.setOrderLogs("");
        }else{
            orderInfo.setOrderLogs(orderMessage.getJson().toString());
        }
        return orderInfo;
    }
    private Map getOrderMap(){
        OrderMessage orderMessage = generateSingleOrder();
        Map orderInfo = new HashMap();
        if(orderMessage !=null && orderMessage.getOrderId()!=null){
            orderInfo.put("orderID", orderMessage.getOrderId());
            orderInfo.put("orderStatus", "PP");
        }else{
            orderInfo.put("orderLogs", orderMessage.getJson().toString());
        }
        return orderInfo;
    }

    private Map getOrderMap(String sku , String pid , String quantity){
        OrderMessage orderMessage = generateSingleOrder(sku , pid , quantity);
        Map orderInfo = new HashMap();
        if(orderMessage !=null && orderMessage.getOrderId()!=null){
            orderInfo.put("orderID", orderMessage.getOrderId());
            orderInfo.put("orderStatus", "PP");
        }else{
            orderInfo.put("orderLogs", orderMessage.getJson().toString());
        }
        return orderInfo;
    }
    private   OrderMessage generateSingleOrder() {
        Login login = new Login();
        submitOrder submitO = new submitOrder();
        BaseCart baseCart = new BaseCart();
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

    private   OrderMessage generateSingleOrder(String sku , String pid ,String quantity) {
        Login login = new Login();
        submitOrder submitO = new submitOrder();
        BaseCart baseCart = new BaseCart();
        JSONObject jsonObject ;
        JSONObject jsonObject1 = new JSONObject();
        jsonObject = login.index();
        jsonObject1.put("login index page", new JSONObject(jsonObject));
//        System.out.println(jsonObject.toString());
        jsonObject = login.doLogin();
        jsonObject1.put("login", new JSONObject(jsonObject));
//        System.out.println(jsonObject.toString());
        jsonObject = baseCart.doClearCart();
        jsonObject1.put("clean cart", new JSONObject(jsonObject));
//        System.out.println(jsonObject.toString());
        jsonObject = baseCart.doAdd(pid, sku, quantity);
        jsonObject1.put("add cart", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        jsonObject = baseCart.doLoadCart();
        jsonObject1.put("load cart", new JSONObject(jsonObject));
//        System.out.println(jsonObject.toString());
        baseCart.doSelect();
        jsonObject = baseCart.doCheckOut();
        jsonObject1.put("go to payment page", new JSONObject(jsonObject));
//        System.out.println(jsonObject.toString());
        jsonObject = submitO.initOrder();
        jsonObject1.put("init order", new JSONObject(jsonObject));
//        System.out.println(jsonObject.toString());
        jsonObject = submitO.dosubmit();
        jsonObject1.put("commit order", new JSONObject(jsonObject));
        System.out.println(jsonObject.toString());
        logger.info("create order process log: " + jsonObject1.toJSONString());
//        System.out.println(String.format("logging json:\n%s", jsonObject1.toString()));
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.doJson(jsonObject1);
        return orderMessage;
    }
}
