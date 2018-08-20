package com.gome.test.order.process;

/**
 * Created by zengjiyang on 2016/2/16.
 */

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.gome.test.order.process.com.gome.test.buess.ConfirmReceipt.ConfirmReceipt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("detail")
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})
public class OrderRestServiceImpl implements OrderRestService {
    Log log = LogFactory.getLog( this .getClass());
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    private OrderService orderService;

//    //String orderNo , String productType
//    @GET
//    @Path("{orderNo: .*}/{productType: .*}/{sendTo: .*}")
//    public Order processOrder(@PathParam("orderNo") String orderNo, @PathParam("productType") String productType, @PathParam("sendTo") String sendTo) {
//        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
//            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
//        }
//        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
//            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
//        }
//
///*        switch (productType.toUpperCase()){
//            case "3PP":
//                order3pp order3pp = new order3pp();
//                order3pp.getOrderservice().processOrder(orderNo, productType,sendTo);
//            break;
//            case "G3PP":
//                G3PPOrderProcessServiceImpl G3PP = new G3PPOrderProcessServiceImpl();
//                G3PP.processOrder(orderNo, productType,sendTo);
//        }*/
//        return orderService.processOrder(orderNo, productType, sendTo);
//    }

    @GET
    @Path("{orderNos: .*}/{productType: .*}/{expectStatus: .*}/{sendTo: .*}")
    public String processOrders(@PathParam("orderNos") String orderNos, @PathParam("productType") String productType, @PathParam("expectStatus") String expectStatus, @PathParam("sendTo") String sendTo) {
        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
            log.info("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
        }
        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
            log.info("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
        }

        return orderService.processOrders(orderNos, productType, expectStatus, sendTo);
    }


    @GET
    @Path("{username: .*}/{password: .*}/{orderNos: .*}/{returnType: .*}/{sendTo: .*}")
    public String processReturnOrders(@PathParam("username") String username,@PathParam("password") String password,@PathParam("orderNos") String orderNos, @PathParam("returnType") String returnType, @PathParam("sendTo") String sendTo) {
        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
            log.info("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
        }
        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
            log.info("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
        }

        return orderService.processReturnOrders(username,password,orderNos, returnType, sendTo);
    }

//    @POST
//    @Path("{orderNo: \\d+}")
//    public Order getOrder(@PathParam("orderNo") String orderNo ) {
//        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
//            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
//        }
//        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
//            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
//        }
//        return orderService.processOrder(orderNo, "");
//    }
@GET
@Path("{orderNo: .*}/{productType: .*}/{sendTo: .*}")
public Order processOrder(@PathParam("orderNo") String orderNo, @PathParam("productType") String productType,@PathParam("sendTo") String sendTo) {
    if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
        log.info("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
        System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
    }
    if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
        log.info("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
        System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
    }

    return orderService.processOrder( orderNo,  productType,   sendTo);
}

    @GET
    public String processOrdersNew(@QueryParam("orderNos") String orderNos, @QueryParam("expectStatus") String expectStatus,@QueryParam("sendTo") String sendTo ,@QueryParam("dzxh") String dzxh ,@QueryParam("dzck") String dzck,@QueryParam("pch") String pch) {
        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
            log.info("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
        }
        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
            log.info("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
        }
        return orderService.processOrdersNew( orderNos,  expectStatus, sendTo,dzxh,dzck,pch);
    }
}