package com.gome.test.order.process.com.gome.test.buess.Lmis;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/3/22.
 */
public class Send {
    Log log = LogFactory.getLog( this .getClass());
    /**
     *  妥投
     */

    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;

    public JSONObject postDL(Order order) {
        String searchuri = Constant.SAP;
        String xml = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ns0:MT_ELC_Req xmlns:ns0=\"http://gome.com/LMIS/ELC/Outbound\">\n" +
                "   <HEADER>\n" +
                "      <INTERFACE_ID>ELC010</INTERFACE_ID>\n" +
                "      <MESSAGE_ID>12345678901234567890123456789022</MESSAGE_ID>\n" +
                "      <SENDER>LMIS</SENDER>\n" +
                "      <RECEIVER>ELC</RECEIVER>\n" +
                "      <DTSEND>20170325173611000</DTSEND>\n" +
                "   </HEADER>\n" +
                "   <XML_DATA>\n" +
                "      <ELC010>\n" +
                "         <COUNTRYID>8270</COUNTRYID>\n" +
                "         <PARTNERSID>HQTMS</PARTNERSID>\n" +
                "         <JLDOCCODE>%s</JLDOCCODE>\n" +
                "         <STATUSNAME>SIGNED</STATUSNAME>\n" +
                "         <STATUSTIME>20170324183611000</STATUSTIME>\n" +
                "         <LEGID>%s</LEGID>\n" +
                "         <PLAN_ARRIVE_S_TIME>20170324172011000</PLAN_ARRIVE_S_TIME>\n" +
                "         <PLAN_ARRIVE_E_TIME>20170324173611000</PLAN_ARRIVE_E_TIME>\n" +
                "         <DELAY_CAUSE></DELAY_CAUSE>\n" +
                "         <LSP_CODE>0082000512</LSP_CODE>\n" +
                "         <LSP_NAME>test</LSP_NAME>\n" +
                "         <LSP_TEL>15710062555</LSP_TEL>\n" +
                "         <COURIER_NAME>name</COURIER_NAME>\n" +
                "         <COURIER_TEL>17001056285</COURIER_TEL>\n" +
                "      </ELC010>\n" +
                "   </XML_DATA>\n" +
                "</ns0:MT_ELC_Req>",order.getOrderNumber(),order.getgWaybillNo());
        try {
            Thread.sleep(3000);
            response = httpclient.visitPostXML(searchuri,xml);
            System.out.println(String.format("安迅发送妥投报文返回值：%s", response));
            log.info(String.format("安迅发送妥投报文返回值：%s", response));
            is = verify.doverify(response, "");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return getStatus(order);
    }

    public JSONObject makeJson() {
        if (is.equals("success")) {
            //成功 jsonObject
            jsonObject.put("isSuccess", "true");
            jsonObject.put("message", "访问成功");
            if (response.contains("取派计划操作成功")) {
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

    public JSONObject getStatus(Order order ){
        // TODO: 2017/3/27 获取发送sap报文后的状态
        GUI.guiLogin();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String searchuri = Constant.SEARCHORDER;
        makeEntitysearch(order.getOrderNo());
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
            System.out.println(String.format("查询订单状态返回值：%s", response));
            log.info(String.format("查询订单状态返回值：%s", response));
            is = verify.doverify(response, "DL");
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

}

