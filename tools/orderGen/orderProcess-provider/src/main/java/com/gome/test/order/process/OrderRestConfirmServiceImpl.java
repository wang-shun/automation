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

@Path("confirm")
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})
public class OrderRestConfirmServiceImpl implements OrderRestConfirmService {
    Log log = LogFactory.getLog( this .getClass());

    private OrderConfirmService orderConfirmService;

    public void setOrderConfirmService(OrderConfirmService orderConfirmService) {
        this.orderConfirmService = orderConfirmService;
    }
    @GET
    @Path("{username: .*}/{password: .*}/{orderNos: .*}")
    public String processConfirmReceipt(@PathParam("username") String username,@PathParam("password") String password,@PathParam("orderNos") String orderNos) {
        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
             log.info("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
        }
        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
            log.info("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
        }
        return orderConfirmService.processConfirmReceipt(username,password,orderNos);
    }


}