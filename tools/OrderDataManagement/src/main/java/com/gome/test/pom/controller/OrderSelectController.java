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

import java.util.Map;

/**
 * Created by hacke on 2016/9/23.
 */
@Controller
public class OrderSelectController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/select")
    @ResponseBody
    public ModelAndView bootStrapTest1(){
        ModelAndView mav = new ModelAndView("bootstrap/pomSelect");
        return mav;
    }


    @RequestMapping(value="/selectOrderInfo", method= RequestMethod.GET )
    @ResponseBody
    public String selectOrderRequest(
            String systemENV,
            String orderID ,
            String uid ,
            String addressID ,
            String payType,
            String orderStatus
    )
    {
        logger.info("------------- request select order data.----------------------------");
        logger.info("get request system env is : " + systemENV);
        logger.info("request order orderID is : " + orderID);
        logger.info("request user id  is : " + uid);
        logger.info("request order address is : " + addressID);
        logger.info("request order payType is : " + payType);
        logger.info("request order orderStatus  is : " + orderStatus);

        if(orderID !=null && orderID.length()>0) {
            Map order = OrderCache.getInstance().getOrderInfoFromContext(orderID);
            if(order != null) {
                String orderJson = JSONObject.toJSONString(order);
                return responseSuccess(orderJson);
            }else{
                return responseFail("not data found by " + orderID);
            }
        }else{
            return responseFail("must input orderID.");
        }

    }
}
