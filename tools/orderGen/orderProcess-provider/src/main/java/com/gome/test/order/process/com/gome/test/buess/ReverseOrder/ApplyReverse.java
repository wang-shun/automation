package com.gome.test.order.process.com.gome.test.buess.ReverseOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient;
import com.gome.test.order.process.com.gome.test.buess.share.Verify;
import com.gome.test.order.process.util.GUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangwan on 2017/4/18.
 */
public class ApplyReverse {
    Log log = LogFactory.getLog( this .getClass());
    MhttpClient httpclient = new MhttpClient();
    Verify verify = new Verify();
    String response;
    List<String> key;
    List<String> value;
    JSONObject jsonObject = new JSONObject();
    String is;

    String getTime() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        String time = dateFormat.format(now);
        return time;
    }
    private Pattern getMatcher(String partten) {
        Pattern pattern = Pattern.compile(partten);
        return pattern;
    }
    public Order doAuditOrder(Order order) {
        JSONObject subjson = new JSONObject();
        StringBuilder desc = new StringBuilder();
        String ss = order.getDesc();
        if (ss != null && ss != "") {
            desc.append(ss);
        } else {
            desc.append("<br />");
        }
        GUI.guiLogin();
        subjson= this.applyReverse(order);
        if (subjson.get("isSuccess").equals("true")) {
            subjson = this.apply(order);
            if (subjson.get("isSuccess").equals("true")) {
                subjson = this.applyRequest(order);
            if (subjson.get("isSuccess").equals("true")) {
                subjson = this.applySubmit(order);
                if (subjson.get("isSuccess").equals("true")) {
                    order.setIssuccess(true);
                    String descformat = String.format("<br />%s 执行商品退换货申请：%s，状态为申请成功", this.getTime(), order.getOrderNo());
                    desc.append(descformat);
                    order.setDesc(desc.toString());
                }
            }
            }
        } else {
            order.setIssuccess(false);
            String descformat = String.format("<br />%s 当前商品不能退换货", this.getTime(), order.getOrderNo());
            desc.append(descformat);
            order.setDesc(desc.toString());
        }
        return order;
    }

    public JSONObject applySubmit(Order order) {
        String applySubmituri = Constant.APPLYSUBMITURI ;
        makeEntity(order);
        try {
            response = httpclient.visit(applySubmituri,key,value);
            is = verify.doverify(response, "success\":true");
            if("success".equals(is)){
                String reg1 = "(resultObj\":\")(.*)(\",\"success\":true)";
                Pattern pattern1 = this.getMatcher(reg1);
                Matcher matcher1 = pattern1.matcher(response);
                String requestId="";
                if (matcher1.find()) {
                    requestId = matcher1.group(2);
                    order.setRequestId(requestId);
                    ContextUtils.getContext().put("requestId", requestId);
                }
            }
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }
    public JSONObject applyReverse(Order order) {
        String applyuri = Constant.APPLYREVERSEURI + order.getOrderNo();
        try {
            response = httpclient.visit(applyuri);
            is = verify.doverify(response, "申请退换货");

        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }


    public JSONObject apply(Order order) {
        String applyuri = Constant.APPLYREQUESTURI + order.getOrderNo();
        try {
            Thread.sleep(5000);
            response = httpclient.visit(applyuri);
            String reg1 = "(class=\"applyHref btn btn-mini\" href=\")(.*)(\">申请)";
            Pattern pattern1 = this.getMatcher(reg1);
            Matcher matcher1 = pattern1.matcher(response);
            String applyId="";
            if (matcher1.find()) {
                applyId = matcher1.group(2);
                ContextUtils.getContext().put("applyId", applyId);
            }
            is = verify.doverify(response, "申请");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson();
    }

    public JSONObject applyRequest(Order order) {
        String applyuri = Constant.APPLYURI + ContextUtils.getContext().get("applyId").toString();
        try {
            response = httpclient.visit(applyuri);
            String reg1 = "(returnProductAgentDto.productName\" value=\")(.*)(\"/>)";
            String reg2 = "(returnProductAgentDto.productId\" value=\")(.*)(\" />)";
            String reg3 = "(returnProductAgentDto.skuId\" value=\")(.*)(\" />)";
            String reg4 = "(returnProductAgentDto.quantity\" value=\")(.*)(\" />)";
            String reg6 = "(returnProductAgentDto.invoiceState\" value=\")(.*)(\" />)";
            String reg7 = "(orderId\" value=\")(.*)(\"/>)";
            String reg9 = "(shippingGroupId\" value=\")(.*)(\"/>)";
            String reg10 = "(commerceItemId\" value=\")(.*)(\"/>)";
            String reg11 = "(itemDetailId\" value=\")(.*)(\"/>)";
            String reg13 = "(repeatCommitStr\" value=\")(.*)(\"/>)";
            String reg14 = "(isMandatoryCollectInvoice\" value=\")(.*)(\"/>)";
            String reg15 = "(qualityCheck\" value=\")(.*)(\"/>)";
            String reg16 = "(maslocType\" value=\")(.*)(\">)";
            String reg17 = "(warrantType\" value=\")(.*)(\">)";
            String reg18 = "(returnRefundAgentDto.maxRefundAmount\" value=\")(.*)(\"/>)";
            String reg19 = "(returnRefundAgentDto.suggestRefundAmount\" value=\")(.*)(\"/>)";
            String reg20 = "(totalDisAmount\" value=\")(.*)(\"/>)";
            String reg21 = "(returnProductAgentDto.productSource\" value=\")(.*)(\" />)";


            Pattern pattern1 = this.getMatcher(reg1);
            Pattern pattern2 = this.getMatcher(reg2);
            Pattern pattern3 = this.getMatcher(reg3);
            Pattern pattern4 = this.getMatcher(reg4);
            Pattern pattern6 = this.getMatcher(reg6);
            Pattern pattern7 = this.getMatcher(reg7);
            Pattern pattern9 = this.getMatcher(reg9);
            Pattern pattern10 = this.getMatcher(reg10);
            Pattern pattern11 = this.getMatcher(reg11);
            Pattern pattern13 = this.getMatcher(reg13);
            Pattern pattern14 = this.getMatcher(reg14);
            Pattern pattern15 = this.getMatcher(reg15);
            Pattern pattern16 = this.getMatcher(reg16);
            Pattern pattern17 = this.getMatcher(reg17);
            Pattern pattern18 = this.getMatcher(reg18);
            Pattern pattern19 = this.getMatcher(reg19);
            Pattern pattern20 = this.getMatcher(reg20);
            Pattern pattern21 = this.getMatcher(reg21);

            Matcher matcher1 = pattern1.matcher(response);
            Matcher matcher2 = pattern2.matcher(response);
            Matcher matcher3 = pattern3.matcher(response);
            Matcher matcher4 = pattern4.matcher(response);
            Matcher matcher6 = pattern6.matcher(response);
            Matcher matcher7 = pattern7.matcher(response);
            Matcher matcher9 = pattern9.matcher(response);
            Matcher matcher10 = pattern10.matcher(response);
            Matcher matcher11 = pattern11.matcher(response);
            Matcher matcher13 = pattern13.matcher(response);
            Matcher matcher14 = pattern14.matcher(response);
            Matcher matcher15 = pattern15.matcher(response);
            Matcher matcher16 = pattern16.matcher(response);
            Matcher matcher17 = pattern17.matcher(response);
            Matcher matcher18 = pattern18.matcher(response);
            Matcher matcher19 = pattern19.matcher(response);
            Matcher matcher20 = pattern20.matcher(response);
            Matcher matcher21 = pattern21.matcher(response);

            String productName ="" ;
//            ContextUtils.getContext().clear();
            if (matcher1.find()) {
                productName = matcher1.group(2);
                ContextUtils.getContext().put("productName", productName);
            }
            if (matcher2.find()) {
                String productId = matcher2.group(2);
                ContextUtils.getContext().put("productId", productId);
            }
            if (matcher3.find()) {
                String skuId = matcher3.group(2);
                ContextUtils.getContext().put("skuId", skuId);
            }
            if (matcher4.find()) {
                String quantity = matcher4.group(2);
                ContextUtils.getContext().put("quantity", quantity);
            }
            if (matcher6.find()) {
                String invoiceState = matcher6.group(2);
                ContextUtils.getContext().put("invoiceState", invoiceState);
            }
            if (matcher7.find()) {
                String orderId = matcher7.group(2);
                ContextUtils.getContext().put("orderId", orderId);
            }
            if (matcher9.find()) {
                String shippingGroupId = matcher9.group(2);
                ContextUtils.getContext().put("shippingGroupId", shippingGroupId);
            }
            if (matcher10.find()) {
                String commerceItemId = matcher10.group(2);
                ContextUtils.getContext().put("commerceItemId", commerceItemId);
            }
            if (matcher11.find()) {
                String itemDetailId = matcher11.group(2);
                ContextUtils.getContext().put("itemDetailId", itemDetailId);
            }
            if (matcher13.find()) {
                String repeatCommitStr = matcher13.group(2);
                ContextUtils.getContext().put("repeatCommitStr", repeatCommitStr);
            }
            if (matcher14.find()) {
                String isMandatoryCollectInvoice = matcher14.group(2);
                ContextUtils.getContext().put("isMandatoryCollectInvoice", isMandatoryCollectInvoice);
            }
            if (matcher15.find()) {
                String qualityCheck = matcher15.group(2);
                ContextUtils.getContext().put("qualityCheck", qualityCheck);
            }
            if (matcher16.find()) {
                String maslocType = matcher16.group(2);
                ContextUtils.getContext().put("maslocType", maslocType);
            }
            if (matcher17.find()) {
                String warrantType = matcher17.group(2);
                ContextUtils.getContext().put("warrantType", warrantType);
            }
            String maxRefundAmount ="";
            if (matcher18.find()) {
                maxRefundAmount = matcher18.group(2);
                ContextUtils.getContext().put("maxRefundAmount", maxRefundAmount);
            }

            String suggestRefundAmount ="";
            if (matcher19.find()) {
                suggestRefundAmount = matcher19.group(2);
                ContextUtils.getContext().put("suggestRefundAmount", suggestRefundAmount);
            }

            String totalDisAmount ="";
            if (matcher20.find()) {
                totalDisAmount = matcher20.group(2);
                ContextUtils.getContext().put("totalDisAmount", totalDisAmount);
            }

            String productSource ="";
            if (matcher21.find()) {
                productSource = matcher21.group(2);
                ContextUtils.getContext().put("productSource", productSource);
            }
            is = verify.doverify(response, "退换货申请");
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
            jsonObject.put("message", "查询成功");
            if (response.contains("查询成功")) {
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

    private void makeEntity(Order order) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("returnProductAgentDto.productName");
        key.add("returnProductAgentDto.productSource");
        key.add("returnProductAgentDto.productId");
        key.add("returnProductAgentDto.skuId");
        key.add("returnProductAgentDto.quantity");
        key.add("returnProductAgentDto.invoiceNum");
        key.add("returnProductAgentDto.invoiceState");
        key.add("orderId");
        key.add("shippingGroupId");
        key.add("commerceItemId");
        key.add("itemDetailId");
        key.add("returnRequestId");
        key.add("status");
        key.add("repeatCommitStr");
        key.add("isMandatoryCollectInvoice");
        key.add("returnType");
        key.add("returnReason");
        key.add("attachment");
        key.add("prodAppearance");
        key.add("prodwrapper");
        key.add("problemDesc");
        key.add("returnMethod");
        if("EXCHANGE".equals(order.getExpectStatus())){
            key.add("mailByUserAddress");
        }
        key.add("qualityCheck");

        key.add("returnStoreCounty");
        key.add("returnStoreId");
        key.add("returnProdState");
        key.add("returnProdCity");
        key.add("returnProdCounty");
        key.add("returnProdTown");
        key.add("returnProdDetailAddress");
        key.add("mailByUserAddress");
        key.add("maslocType");
        key.add("warrantType");
        key.add("returnProdConsignee");
        key.add("returnProdPhone");
        key.add("returnProdPostCode");

        if("RETURN".equals(order.getExpectStatus())){
        key.add("returnRefundAgentDto.returnRefundDetailAgentDtos%5B0%5D.paymentGroupId");
        key.add("returnRefundAgentDto.returnRefundDetailAgentDtos%5B0%5D.defaultRefundMethod");
        key.add("returnRefundAgentDto.maxRefundAmount");
        key.add("returnRefundAgentDto.suggestRefundAmount");
        key.add("returnRefundAgentDto.isFastReturn");
        key.add("totalDisAmount");
        key.add("returnRefundAgentDto.debuctComment");
        key.add("roUploadImageUrls");
        }else {
            key.add("replShipConsignee");
            key.add("replShipPhone");
            key.add("replShipPostCode");
            key.add("replShipState");
            key.add("replShipCity");
            key.add("replShipCounty");
            key.add("replShipTown");
            key.add("replShipDetailAddress");
            key.add("roUploadImageUrls");
        }


        String productName = "";
        if (ContextUtils.getContext().get("productName") != null) {
            productName = ContextUtils.getContext().get("productName").toString();
        }
        System.out.println(String.format("productName：%s", productName));
        log.info(String.format("productName：%s", productName));
        String productId = "";
        if (ContextUtils.getContext().get("productId") != null) {
            productId = ContextUtils.getContext().get("productId").toString();
        }
        System.out.println(String.format("productId：%s", productId));
        log.info(String.format("productId：%s", productId));
        String skuId = "";
        if (ContextUtils.getContext().get("skuId") != null) {
            skuId = ContextUtils.getContext().get("skuId").toString();
        }
        System.out.println(String.format("skuId：%s", skuId));
        log.info(String.format("skuId：%s", skuId));
        String quantity = "";
        if (ContextUtils.getContext().get("quantity") != null) {
            quantity = ContextUtils.getContext().get("quantity").toString();
        }
        System.out.println(String.format("quantity：%s", quantity));
        log.info(String.format("quantity：%s", quantity));
        String invoiceState = "";
        if (ContextUtils.getContext().get("invoiceState") != null) {
            invoiceState = ContextUtils.getContext().get("invoiceState").toString();
        }
        System.out.println(String.format("invoiceState：%s", invoiceState));
        log.info(String.format("invoiceState：%s", invoiceState));
        String orderId = "";
        if (ContextUtils.getContext().get("orderId") != null) {
            orderId = ContextUtils.getContext().get("orderId").toString();
        }
        System.out.println(String.format("orderId：%s", orderId));
        log.info(String.format("orderId：%s", orderId));
        String shippingGroupId = "";
        if (ContextUtils.getContext().get("shippingGroupId") != null) {
            shippingGroupId = ContextUtils.getContext().get("shippingGroupId").toString();
        }
        System.out.println(String.format("shippingGroupId：%s", shippingGroupId));
        log.info(String.format("shippingGroupId：%s", shippingGroupId));
        String commerceItemId = "";
        if (ContextUtils.getContext().get("commerceItemId") != null) {
            commerceItemId = ContextUtils.getContext().get("commerceItemId").toString();
        }
        System.out.println(String.format("commerceItemId：%s", commerceItemId));
        log.info(String.format("commerceItemId：%s", commerceItemId));
        String itemDetailId = "";
        if (ContextUtils.getContext().get("itemDetailId") != null) {
            itemDetailId = ContextUtils.getContext().get("itemDetailId").toString();
        }
        System.out.println(String.format("itemDetailId：%s", itemDetailId));
        log.info(String.format("itemDetailId：%s", itemDetailId));
        String repeatCommitStr = "";
        if (ContextUtils.getContext().get("repeatCommitStr") != null) {
            repeatCommitStr = ContextUtils.getContext().get("repeatCommitStr").toString();
        }
        System.out.println(String.format("repeatCommitStr：%s", commerceItemId));
        log.info(String.format("repeatCommitStr：%s", commerceItemId));
        String isMandatoryCollectInvoice = "";
        if (ContextUtils.getContext().get("isMandatoryCollectInvoice") != null) {
            isMandatoryCollectInvoice = ContextUtils.getContext().get("isMandatoryCollectInvoice").toString();
        }
        System.out.println(String.format("isMandatoryCollectInvoice：%s", isMandatoryCollectInvoice));
        log.info(String.format("isMandatoryCollectInvoice：%s", isMandatoryCollectInvoice));
        String qualityCheck = "";
        if (ContextUtils.getContext().get("qualityCheck") != null) {
            qualityCheck = ContextUtils.getContext().get("qualityCheck").toString();
        }
        System.out.println(String.format("qualityCheck：%s", qualityCheck));
        log.info(String.format("qualityCheck：%s", qualityCheck));
        String maslocType = "";
        if (ContextUtils.getContext().get("maslocType") != null) {
            maslocType = ContextUtils.getContext().get("maslocType").toString();
        }
        System.out.println(String.format("maslocType：%s", maslocType));
        log.info(String.format("maslocType：%s", maslocType));
        String warrantType = "";
        if (ContextUtils.getContext().get("warrantType") != null) {
            warrantType = ContextUtils.getContext().get("warrantType").toString();
        }
        System.out.println(String.format("warrantType：%s", warrantType));
        log.info(String.format("warrantType：%s", warrantType));
        String maxRefundAmount = "";
        if (ContextUtils.getContext().get("maxRefundAmount") != null) {
            maxRefundAmount = ContextUtils.getContext().get("maxRefundAmount").toString();
        }
        System.out.println(String.format("maxRefundAmount：%s", maxRefundAmount));
        log.info(String.format("maxRefundAmount：%s", maxRefundAmount));
        String suggestRefundAmount = "";
        if (ContextUtils.getContext().get("suggestRefundAmount") != null) {
            suggestRefundAmount = ContextUtils.getContext().get("suggestRefundAmount").toString();
        }
        System.out.println(String.format("suggestRefundAmount：%s", suggestRefundAmount));
        log.info(String.format("suggestRefundAmount：%s", suggestRefundAmount));
        String totalDisAmount = "";
        if (ContextUtils.getContext().get("totalDisAmount") != null) {
            totalDisAmount = ContextUtils.getContext().get("totalDisAmount").toString();
        }
        System.out.println(String.format("totalDisAmount：%s", totalDisAmount));
        log.info(String.format("totalDisAmount：%s", totalDisAmount));
        String productSource = "";
        if (ContextUtils.getContext().get("productSource") != null) {
            productSource = ContextUtils.getContext().get("productSource").toString();
        }
        System.out.println(String.format("productSource：%s", productSource));
        log.info(String.format("productSource：%s", productSource));


        value.add(productName);
        value.add(productSource);
        value.add(productId);
        value.add(skuId);
        value.add(quantity);
        value.add("");
        value.add(invoiceState);
        value.add(orderId);
        value.add(shippingGroupId);
        value.add(commerceItemId);
        value.add(itemDetailId);
        value.add("");
        value.add("");
        value.add(repeatCommitStr);
        value.add(isMandatoryCollectInvoice);
        if("RETURN".equals(order.getExpectStatus())) {
            value.add("1");
        }else {
            value.add("2");
        }
        value.add("didNotLike");
        value.add("1234");
        value.add("0");
        value.add("1");
        value.add("123456");
        value.add("2");
        if("EXCHANGE".equals(order.getExpectStatus())) {
            value.add("");
        }
        value.add("0");
        value.add("");
        value.add("");
        value.add("11000000");
        value.add("11010000");
        value.add("11010200");
        value.add("110102003");
        value.add("PENGRUN");
        value.add("L503");
        value.add("2");
//        value.add("ROAD XIAOYUN NO26");
        value.add("0");

        value.add("test");
        value.add("138****1566");
        value.add("");
        if("RETURN".equals(order.getExpectStatus())){
            value.add("614329327");
            value.add("原路返回");
            value.add(maxRefundAmount);
            value.add(suggestRefundAmount);
            value.add("0");
            value.add("0");
            value.add("");
            value.add("");
        }else {
            value.add("test");
            value.add("13869541566");
            value.add("");
            value.add("11000000");
            value.add("11010000");
            value.add("11010200");
            value.add("110102003");
            value.add("PENGRUN");
            value.add("");
        }
    }



}
