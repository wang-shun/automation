package com.gome.test.order.process.com.gome.test.buess.Lmis;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangwei-ds on 2017/3/22.
 */
public class WayBillStatusController {
    Log log = LogFactory.getLog( this .getClass());
    /**
     * 根据金立运单号查询获取参数
     * AX安迅运单号、相关订单1、商品编码、库位、
     */
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;


    private Matcher getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        Matcher matcher = pattern.matcher(response);
        return matcher;
    }

    public JSONObject findBillStatus(Order order ) {
        //jlCode 对应运单号

        //makeEntity(order.getOrderNumber());
        /**
         * 总部DC查询
         */

        //String searchuri = String.format("%swbStatusJlDocCode=%s",Constant.WAYBILLSTATUS,order.getOrderNumber());
        try {
            String searchuri = String.format("%s%s&Login_SessionId=%s&currentDCId=FB68C5CEEC1640C3B1D09BEBCD99FD5E",Constant.WAYBILLSTATUS,order.getOrderNumber(),ContextUtils.getContext().get("sessionId"));
            Thread.sleep(3000);
            response = httpclient.visit(searchuri);
            System.out.println(String.format("安迅系统查询返回值：%s", response));
            log.info(String.format("安迅系统查询返回值：%s", response));
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 北分北京配送中心
         */

        if(response.contains("total\":0")){
            try {
                String bf = String.format("%s&Login_SessionId=%s",Constant.BFWAYBILLURI,ContextUtils.getContext().get("sessionId"));
                response = httpclient.visitPost(bf);
                String searchuri = String.format("%s%s&Login_SessionId=%s&currentDCId=D2BC6672CA154A28B06CA3AC4CF1142F",Constant.BFWAYBILLSTATUS,order.getOrderNumber(),ContextUtils.getContext().get("sessionId"));
                Thread.sleep(3000);
                response = httpclient.visit(searchuri);
                System.out.println(String.format("安迅系统查询返回值：%s", response));
                log.info(String.format("安迅系统查询返回值：%s", response));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        /**
         * 北京大中配送中心
         */
        if(response.contains("total\":0")){
            try {
                String searchuri = String.format("%s%s&Login_SessionId=%s&currentDCId=9A2FBDE9FE98438BAF4FC54E9CDF6E22",Constant.BFWAYBILLSTATUS,order.getOrderNumber(),ContextUtils.getContext().get("sessionId"));
                Thread.sleep(3000);
                response = httpclient.visit(searchuri);
                System.out.println(String.format("安迅系统查询返回值：%s", response));
                log.info(String.format("安迅系统查询返回值：%s", response));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try {

            //保存安迅运单号
            String reg = "\"gWaybillNo\":\"(AX\\d+)\"";
            Matcher matcher = this.getMatcher(reg);
            String gWaybillNo = null;
            if (matcher.find()) {
                gWaybillNo = matcher.group(1);
                order.setgWaybillNo(gWaybillNo);
                //ContextUtils.getContext().put("gWaybillNo", gWaybillNo);
            }

            //保存相关单号1
            String relatedBill1 = "\"relatedBill1\":\"(\\d+)\"";
            Matcher matcher_relatedBill = getMatcher(relatedBill1);
            if (matcher_relatedBill.find()){
                String relate = matcher_relatedBill.group(1);
                order.setRelatedBill1(relate);
                //ContextUtils.getContext().put("relatedBill1",relate);
            }

            //保存商品编号
            String productCode = "\"productCode\":\"(\\d+)\"";
            Matcher matcher_productCode = getMatcher(productCode);
            if (matcher_productCode.find()){
                String productId = matcher_productCode.group(1);
                order.setProductCode(productId);
                // ContextUtils.getContext().put("productCode",productId);
            }

            //保存库位号

            String fromStorageCode = "\"fromStorageCode\":\"(\\w+)\"";
            Matcher matcher_fromStorageCode = getMatcher(fromStorageCode);
            if (matcher_fromStorageCode.find()){
                String fromStorageCodeID = matcher_fromStorageCode.group(1);
                order.setFromStorageCode(fromStorageCodeID);
                //ContextUtils.getContext().put("fromStorageCode",fromStorageCodeID);
            }
/*            String waybillId_P = "\"waybillId\":\"(\\w+)\"";
            Matcher matcher_waybillId = getMatcher(waybillId_P);
            if (matcher_waybillId.find()){
                String waybillId = matcher_waybillId.group(1);
                ContextUtils.getContext().put("waybillId",waybillId);
            }*/
            //String cangku;
            //获取操作仓库名字，用于下面获取仓库id
/*
            String p = "\"operateDcId\":\"([u4E00-u9FA5])\"";
            Matcher matcher1 = getMatcher(p);
            if (matcher1.find()){
                cangku = matcher.group(1);
                getDcCode(cangku);//完成发货仓库切换
            }
*/

            is = verify.doverify(response, gWaybillNo);
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson();
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

    private void makeEntity(String jlcode) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("orderNo");
        key.add("relatedBill1");
        key.add("orderBeginTime");
        key.add("orderEndTime");
        key.add("waybillNo");
        key.add("relatedBill2");
        key.add("waybillBeginTime");
        key.add("waybillEndTime");
        key.add("consigneeName");
        key.add("consigneePhone");
        key.add("consigneeAddress");
        key.add("wbstatusCarrierNo");
        key.add("wbStatusJlDocCode");
        key.add("wbStatusJlsoCode");
        key.add("schedulesStartTime");
        key.add("schedulesEndTime");
        key.add("currentDCId");
        key.add("readOnly");
        key.add("Login_SessionId");
        key.add("page");
        key.add("rows");
        key.add("_");



        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add("2017-01-01");
        value.add("2018-01-01");
        value.add("");
        value.add("");
        value.add("");
        value.add("");
        value.add(jlcode);
        value.add("");
        value.add("");
        value.add("");
        value.add("D2BC6672CA154A28B06CA3AC4CF1142F");
        value.add("YES");
        value.add("SESSION_630636A26943409BBDD04D2879642D14");
        value.add("1");
        value.add("20");
        value.add("1490156133010");
    }

    //获取平台的ID号
    public void getDcCode (String DcName){
        String uri = Constant.GETDICODE;
        uri = uri + "Login_SessionId="+ContextUtils.getContext().get("Login_SessionId").toString();
        response = httpclient.visit(uri);
        String reg = String.format("\"dcId\":\"(\\w+)\",\"dcName\":\"%s\"",DcName);
        Matcher matcher= this.getMatcher(reg);

        String DcCode = null;
        if (matcher.find()) {
            DcCode = matcher.group(1);
        }
        modifyDC(DcCode);
    }

    //修改平台
    public void modifyDC(String DcCode){
        String uri = Constant.CURRENTDC;
        makeEntityformodifyDC(DcCode);
        try {
            response = httpclient.visit(uri,key,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        is = verify.doverify(response,"\"retcode\":0");

    }

    private void makeEntityformodifyDC(String DCcode) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();
        key.add("currentDC");
        key.add("Login_SessionId");

        value.add(DCcode);
        value.add(ContextUtils.getContext().get("Login_SessionId").toString());


    }
}
