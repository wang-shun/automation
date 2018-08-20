package com.gome.test.order.process;

import com.gome.test.order.process.cache.ProcessedOrder;
import com.gome.test.order.process.util.OrderUtils;
import org.python.antlr.ast.Str;

/**
 * Created by liangwei on 2017/04/06.
 */

public class G3PPOrderProcessServiceImpl implements G3PPOrderService{
    private G3PPOrderService orderService;

    public Order processOrder(String orderNo, String productType,String sendTo,  String dzxh, String spbm, String dzck , String dzkq,String pch) {
        //todo


        //行号 默认1
        String dzxh_ =(!"null".equals(dzxh))?dzxh:"1";
        //商品编码
        String spbm_ = (!"null".equals(spbm))?spbm:"000000000100253733";
        //仓库编码
        String dzck_ = (!"null".equals(dzck))?dzck:"WXF0";
        //库位
        String dzkq_ = (!"null".equals(dzkq))?dzkq:"X011";
        //批次号
        String pch_ = (!"null".equals(pch))?pch:"0000215789";

        Order initOrder = new Order();
        initOrder.setOrderNo(orderNo);
        initOrder.setStartTime(OrderUtils.getCurrentTimestamp());
        initOrder.setProductType(productType);
        initOrder.setExpectStatus("DL");
        initOrder.setCurrentStatus("PP");
        initOrder.setEmailCount(sendTo);
        initOrder.setDzck(dzck_);
        initOrder.setDzxh(dzxh_);
        initOrder.setPch(pch_);
        initOrder.setProductCode(spbm_);
        initOrder.setFromStorageCode(dzkq_);
        initOrder.setNew(false);
        ProcessedOrder.getInstance().getPreOrders().add(initOrder);
        return initOrder;
    }

    public String processOrders(String orderNos, String productType, String expectStatus, String sendTo, String dzxh, String spbm, String dzck , String dzkq,String pch) {

        //行号 默认1
        String dzxh_ =(!"null".equals(dzxh))?dzxh:"1";
        //商品编码
        String spbm_ = (!"null".equals(spbm))?spbm:"000000000100253733";
        //仓库编码
        String dzck_ = (!"null".equals(dzck))?dzck:"WXF0";
        //库位
        String dzkq_ = (!"null".equals(dzkq))?dzkq:"X011";
        //批次号
        String pch_ = (!"null".equals(pch))?pch:"0000215789";

        String[] orderArray = orderNos.split(",");
        String errMsg = "";
        if (orderArray.length < 1) {
            errMsg = "orderNo format error. should split by ',' .";
            return errMsg;
        }

        if (!OrderUtils.validateOrderType(expectStatus)) {
            errMsg = "orderNo expect status error. should EX or DL .";
            return errMsg;
        }

        if (!OrderUtils.validateProductType(productType)) {
            errMsg = "order type error. should \"3PP\" , \"NOPP\" , \"G3PP\" , \"SMI\".";
            return errMsg;
        }

        for (String orderNo : orderArray) {
            Order initOrder = new Order();
            initOrder.setOrderNo(orderNo);
            initOrder.setStartTime(OrderUtils.getCurrentTimestamp());
            initOrder.setProductType(productType);
            initOrder.setExpectStatus(expectStatus);
            initOrder.setCurrentStatus("PP");
            initOrder.setEmailCount(sendTo);
            initOrder.setDzck(dzck_);
            initOrder.setDzxh(dzxh_);
            initOrder.setPch(pch_);
            initOrder.setProductCode(spbm_);
            initOrder.setFromStorageCode(dzkq_);
            initOrder.setNew(false);
            ProcessedOrder.getInstance().getPreOrders().add(initOrder);
        }

        return "success";
    }


    public Order getOrder(String orderNo) {
        //todo
        Order order = new Order();
        order.setOrderNo("test111111");
        return order;
    }

}