package com.gome.test.order.process;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangwei-ds on 2017/3/21.
 */

@Path("G3PPdetail")
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})
public class G3PPOrderRestServiceImlp implements G3PPOrderRestfulService {


    public void setOrderService(G3PPOrderService orderService) {
        this.orderService = orderService;
    }

    private G3PPOrderService orderService;

    //String orderNo , String productType
    @GET
    @Path("{orderNo: .*}/{productType: .*}/{sendTo: .*}/{dzxH: .*}/{spbM: .*}/{dzcK: .*}/{dzkQ: .*}/{pch: .*}")
    public Order processOrder(@PathParam("orderNo") String orderNo, @PathParam("productType") String productType,@PathParam("sendTo") String sendTo, @PathParam("dzxH") String dzxH, @PathParam("spbM")String spbM,@PathParam("dzcK") String dzcK , @PathParam("dzkQ")String dzkQ, @PathParam("pch")String pch) {
        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
        }
        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
        }

        return orderService.processOrder( orderNo,  productType,   sendTo,   dzxH,  spbM,  dzcK ,  dzkQ, pch);
    }

    @GET
    @Path("{orderNos: .*}/{productType: .*}/{expectStatus:.*}/{sendTo: .*}/{dzxH: .*}/{spbM: .*}/{dzcK: .*}/{dzkQ: .*}/{pch: .*}")
    public String processOrders(@PathParam("orderNos") String orderNos, @PathParam("productType") String productType, @PathParam("expectStatus") String expectStatus, @PathParam("sendTo") String sendTo , @PathParam("dzxH") String dzxH, @PathParam("spbM")String spbM,@PathParam("dzcK") String dzcK , @PathParam("dzkQ")String dzkQ, @PathParam("pch")String pch) {
        if (RpcContext.getContext().getRequest(HttpServletRequest.class) != null) {
            System.out.println("Client IP address from RpcContext: " + RpcContext.getContext().getRequest(HttpServletRequest.class).getRemoteAddr());
        }
        if (RpcContext.getContext().getResponse(HttpServletResponse.class) != null) {
            System.out.println("Response object from RpcContext: " + RpcContext.getContext().getResponse(HttpServletResponse.class));
        }

        return orderService.processOrders(orderNos,  productType,  expectStatus,  sendTo,   dzxH,  spbM,  dzcK ,  dzkQ, pch);
    }


}
