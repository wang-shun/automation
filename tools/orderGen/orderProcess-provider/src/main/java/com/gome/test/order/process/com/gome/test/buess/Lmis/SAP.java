package com.gome.test.order.process.com.gome.test.buess.Lmis;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.SearchOrder.SearchOrder;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;
import com.gome.test.order.process.util.OrderUtils;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/3/23.
 */
public class SAP {
    Log log = LogFactory.getLog( this .getClass());
    /**
     * 出库
     */

    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;

    public JSONObject submit(Order order) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        order=OrderUtils.getProductInfo(order);
        String sap = Constant.EX;
        String xml = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<MT_WMS002_Req>\n" +
                "<HEADER>\n" +
                "\t<DZDBD01>%s</DZDBD01>\n" +//相关单号1
                "    <SHRRQ>%s</SHRRQ>\n" +
                "</HEADER>\n" +
                "<ITEMS>\n" +
                "    <ITEM>\n" +
                "        <DZXH>%s</DZXH>\n" +//行号
                "        <SPBM>%s</SPBM>\n" +//商品编号
                "        <CKSL>1</CKSL>\n" +
                "        <DZCK01>%s</DZCK01>\n" + //仓库编号
                "        <DZKQ01>%s</DZKQ01>\n" +//库位
                "        <PCH>%s</PCH>\n" +//批次号
                "    </ITEM>\n" +
                "</ITEMS>\n" +
                "</MT_WMS002_Req>",order.getRelatedBill1(),dateNowStr+" 23:00:00",order.getDzxh(),
                order.getProductCode(),order.getDzck(),order.getFromStorageCode(),order.getPch());
        try {
            Thread.sleep(3000);
            response = httpclient.visitPostXML(sap,xml);
            /*PostMethod method = new PostMethod(sap);
            response = httpclient.visit(sap, key, value);*/
            is = verify.doverify(response, "");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }

        return getStatus(order);
    }

    public JSONObject getStatus(Order order ){
        GUI.guiLogin();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // TODO: 2017/3/27 获取发送sap报文后的状态
        String searchuri = Constant.SEARCHORDER;
        makeEntitysearch(order.getOrderNo());
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
            System.out.println(String.format("查询订单状态返回值：%s", response));
            log.info(String.format("查询订单状态返回值：%s", response));
            is = verify.doverify(response, "EX");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
    }


    private void makeEntitysearch(String orderId) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("orderFlag");
        key.add("orderType");
        key.add("search");
        key.add("nd");
        key.add("rows");
        key.add("page");
        key.add("sidx");
        key.add("sord");
        key.add("beginDay");
        key.add("endDay");
        key.add("customerName");
        key.add("abFlag");
        key.add("masterOrderNumber");
        key.add("orderStatus");
        key.add("orderNumber");
        key.add("city");
        key.add("unCcoReason");
        key.add("eddFlag");
        key.add("deliveryWeekendFlag");
        key.add("orderChannel");
        key.add("paymentType");
        key.add("payPosCash");
        key.add("installFlag");
        key.add("invoiceType");
        key.add("invoiceMediaType");
        key.add("invoiceStatus");
        key.add("allowanceFlag");
        key.add("cellPhone");


        value.add("G3PP");
        value.add("SO,CSO,PS");
        value.add("FALSE");
        value.add("1488454521797");
        value.add("10");
        value.add("1");
        value.add("");
        value.add("asc");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add(orderId);
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
    }

    public JSONObject makeJson() {
        if (is.equals("success")) {
            //成功 jsonObject
            jsonObject.put("isSuccess", "true");
            jsonObject.put("message", "访问成功");
            if (response.contains("搜索订单成功")) {
                jsonObject.put("data", response);
            }

        } else if (is.equals("failed_notcontains_expect")) {
            //失败，不包含指定值
            jsonObject.put("isSuccess", "false");
            jsonObject.put("message", "访问失败，不包含指定值");
            jsonObject.put("data", response);
        } else if (is.equals("failed_response_null")) {
            //返回值null
            jsonObject.put("isSuccess", "false");
            jsonObject.put("message", "方法调用出错");
            jsonObject.put("data", response);
        }
        return jsonObject;
    }

}
