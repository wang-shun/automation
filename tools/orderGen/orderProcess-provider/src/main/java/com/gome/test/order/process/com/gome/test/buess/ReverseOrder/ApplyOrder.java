package com.gome.test.order.process.com.gome.test.buess.ReverseOrder;

import com.alibaba.fastjson.JSONObject;
import com.gome.test.context.ContextUtils;
import com.gome.test.order.process.Order;
import com.gome.test.order.process.com.gome.test.buess.Content.Constant;
import com.gome.test.order.process.com.gome.test.buess.Login.Login;
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

import static com.gome.test.order.process.com.gome.test.buess.httpClient.MhttpClient.SCN;

/**
 * Created by zhangwan on 2017/3/27.
 */
public class ApplyOrder {
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
    public JSONObject searchRequest(String requestId) {
        JSONObject subjson = new JSONObject();
        String searchuri = Constant.APPLYNEWURI;
        makeEntity1(requestId);
        try {
            Thread.sleep(5000);
            response = httpclient.visit(searchuri, key, value);
            System.out.println(String.format("查询退换货订单申请参数返回值：%s", response));
            log.info(String.format("查询退换货订单申请参数返回值：%s", response));
            String reg1 = "(returnProductAgentDto.productName\")([\\s\\S]*)";
            String reg2 = "(returnProductAgentDto.productId\" value=\")(.*)(\" />)";
            String reg3 = "(returnProductAgentDto.skuId\" value=\")(.*)(\" />)";
            String reg4 = "(returnProductAgentDto.quantity\" value=\")(.*)(\" />)";
//            String reg5 = "(returnProductAgentDto.invoiceNum\" value=\")(.*)(\" />)";
            String reg6 = "(returnProductAgentDto.invoiceState\" value=\")(.*)(\" />)";
            String reg7 = "(name=\"orderId\" value=\")(.*)(\"/>)";
            String reg8 = "(name=\"returnItemId\" value=\")(.*)(\">)";
            String reg9 = "(name=\"returnShipGroupId\" value=\")(.*)(\">)";
            String reg10 = "(name=\"shippingGroupId\" value=\")(.*)(\"/>)";
            String reg11 = "(name=\"commerceItemId\" value=\")(.*)(\"/>)";
            String reg12 = "(name=\"itemDetailId\" value=\")(.*)(\"/>)";
            String reg13 = "(name=\"returnRequestId\" value=\")(.*)(\"/>)";
            String reg14 = "(name=\"originOfReturn\" value=\")(.*)(\">)";
            String reg15 = "(name=\"status\" value=\")(.*)(\"/>)";
            String reg16 = "(name=\"repeatCommitStr\" value=\")(.*)(\"/>)";
            String reg17 = "(name=\"isMandatoryCollectInvoice\" value=\")(.*)(\"/>)";
            String reg18 = "(name=\"returnType\" id=\")(.*)(\" value=\")(.*)(\")";

            Pattern pattern1 = this.getMatcher(reg1);
            Pattern pattern2 = this.getMatcher(reg2);
            Pattern pattern3 = this.getMatcher(reg3);
            Pattern pattern4 = this.getMatcher(reg4);
//            Pattern pattern5 = this.getMatcher(reg5);
            Pattern pattern6 = this.getMatcher(reg6);
            Pattern pattern7 = this.getMatcher(reg7);
            Pattern pattern8 = this.getMatcher(reg8);
            Pattern pattern9 = this.getMatcher(reg9);
            Pattern pattern10 = this.getMatcher(reg10);
            Pattern pattern11 = this.getMatcher(reg11);
            Pattern pattern12 = this.getMatcher(reg12);
            Pattern pattern13 = this.getMatcher(reg13);
            Pattern pattern14 = this.getMatcher(reg14);
            Pattern pattern15 = this.getMatcher(reg15);
            Pattern pattern16 = this.getMatcher(reg16);
            Pattern pattern17 = this.getMatcher(reg17);
            Pattern pattern18 = this.getMatcher(reg18);

            Matcher matcher1 = pattern1.matcher(response);
            Matcher matcher2 = pattern2.matcher(response);
            Matcher matcher3 = pattern3.matcher(response);
            Matcher matcher4 = pattern4.matcher(response);
//            Matcher matcher5 = pattern5.matcher(response);
            Matcher matcher6 = pattern6.matcher(response);
            Matcher matcher7 = pattern7.matcher(response);
            Matcher matcher8 = pattern8.matcher(response);
            Matcher matcher9 = pattern9.matcher(response);
            Matcher matcher10 = pattern10.matcher(response);
            Matcher matcher11 = pattern11.matcher(response);
            Matcher matcher12 = pattern12.matcher(response);
            Matcher matcher13 = pattern13.matcher(response);
            Matcher matcher14 = pattern14.matcher(response);
            Matcher matcher15 = pattern15.matcher(response);
            Matcher matcher16 = pattern16.matcher(response);
            Matcher matcher17 = pattern17.matcher(response);
            Matcher matcher18 = pattern18.matcher(response);

            String productName ="" ;
            ContextUtils.getContext().clear();
            if (matcher1.find()) {
                String productNames = matcher1.group(2);
                Pattern p=Pattern.compile("( value=\")(.*)(\"/>)");
                Matcher m=p.matcher(productNames);
                if(m.find()){
                    productName= m.group(2);
                }
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
//            if (matcher5.find()) {
//                String invoiceNum = matcher5.group(2);
//                ContextUtils.getContext().put("invoiceNum", invoiceNum);
//            }
            if (matcher6.find()) {
                String invoiceState = matcher6.group(2);
                ContextUtils.getContext().put("invoiceState", invoiceState);
            }
            if (matcher7.find()) {
                String orderId = matcher7.group(2);
                ContextUtils.getContext().put("orderId", orderId);
            }
            if (matcher8.find()) {
                String returnItemId = matcher8.group(2);
                ContextUtils.getContext().put("returnItemId", returnItemId);
            }
            if (matcher9.find()) {
                String returnShipGroupId = matcher9.group(2);
                ContextUtils.getContext().put("returnShipGroupId", returnShipGroupId);
            }
            if (matcher10.find()) {
                String shippingGroupId = matcher10.group(2);
                ContextUtils.getContext().put("shippingGroupId", shippingGroupId);
            }
            if (matcher11.find()) {
                String commerceItemId = matcher11.group(2);
                ContextUtils.getContext().put("commerceItemId", commerceItemId);
            }
            if (matcher12.find()) {
                String itemDetailId = matcher12.group(2);
                ContextUtils.getContext().put("itemDetailId", itemDetailId);
            }
            if (matcher13.find()) {
                String returnRequestId = matcher13.group(2);
                ContextUtils.getContext().put("returnRequestId", returnRequestId);
            }
            if (matcher14.find()) {
                String originOfReturn = matcher14.group(2);
                ContextUtils.getContext().put("originOfReturn", originOfReturn);
            }
            if (matcher15.find()) {
                String status = matcher15.group(2);
                ContextUtils.getContext().put("status", status);
            }
            if (matcher16.find()) {
                String repeatCommitStr = matcher16.group(2);
                ContextUtils.getContext().put("repeatCommitStr", repeatCommitStr);
            }
            if (matcher17.find()) {
                String isMandatoryCollectInvoice = matcher17.group(2);
                ContextUtils.getContext().put("isMandatoryCollectInvoice", isMandatoryCollectInvoice);
            }
            String returnType ="";
            if (matcher18.find()) {
                returnType = matcher18.group(4);
                ContextUtils.getContext().put("returnType", returnType);
            }
            is = verify.doverify(response, "退换货申请");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }

        return makeJson1();
    }

    public JSONObject makeJson1() {
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

    private void makeEntity1(String requestId) {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("returnRequestId");
        value.add(requestId);
    }

    public Order doApplyOrder(Order order) {
        JSONObject subjson = new JSONObject();
        StringBuilder desc = new StringBuilder();
        String ss = order.getDesc();
        if (ss != null && ss != "") {
            desc.append(ss);
        } else {
            desc.append("<br />");
        }
//        SCN.clear();
//        Login ermlogin = new Login();
//        subjson = ermlogin.doLogin("", "");
        GUI.guiLogin();
        subjson= this.applyOrder(order);
        String shippingGroupId = "";
        if (subjson.get("isSuccess").equals("true")) {
            order.setIssuccess(true);
            String descformat = String.format("<br />%s 执行ERM提交退换货申请：%s，状态为申请成功", this.getTime(), order.getOrderNo());
            desc.append(descformat);
            order.setDesc(desc.toString());
        } else {
            order.setIssuccess(false);
            String descformat = String.format("<br />%s 执行ERM提交退换货申请：%s，状态为申请失败", this.getTime(), order.getOrderNo());
            desc.append(descformat);
            order.setDesc(desc.toString());
        }
        return order;
    }

    public JSONObject applyOrder(Order order) {
        this.searchRequest(order.getRequestId());
        String applyuri = Constant.SUBMITREVERSEURI;
        makeEntity2();
        try {
            response = httpclient.visit(applyuri, key, value);
            is = verify.doverify(response, "\"success\":true");
        } catch (Exception e) {
            is = "failed_response_null";
            response = e.toString();
        }
        return makeJson2();
    }

    public JSONObject makeJson2() {
        if (is.equals("success")) {
            //成功 jsonObject
            jsonObject.put("isSuccess", "true");
            jsonObject.put("message", "申请成功");
            if (response.contains("申请成功")) {
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

    private void makeEntity2() {
        key = new ArrayList<String>();
        value = new ArrayList<String>();

        key.add("returnProductAgentDto.productName");
        key.add("returnProductAgentDto.productId");
        key.add("returnProductAgentDto.skuId");
        key.add("returnProductAgentDto.quantity");
        key.add("returnProductAgentDto.invoiceNum");
        key.add("returnProductAgentDto.invoiceState");
        key.add("orderId");
        key.add("returnItemId");
        key.add("returnShipGroupId");
        key.add("shippingGroupId");
        key.add("commerceItemId");
        key.add("itemDetailId");
        key.add("returnRequestId");
        key.add("originOfReturn");
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
        key.add("qualityCheck");
        key.add("returnStoreCounty");
        key.add("returnStoreId");
        key.add("returnProdState");
        key.add("returnProdCity");
        key.add("returnProdCounty");
        key.add("returnProdTown");
        key.add("returnProdDetailAddress");
        key.add("mailToMasLocCode");
        key.add("maslocType");
        key.add("warrantType");
        key.add("returnProdConsignee");
        key.add("returnProdPhone");
        key.add("returnProdPostCode");
        String returnType = "";
        //退货：1 or 换货:2
        if (ContextUtils.getContext().get("returnType") != null) {
            returnType = ContextUtils.getContext().get("returnType").toString();
            if ("1".equals(returnType)) {
                key.add("returnRefundAgentDto.returnRefundDetailAgentDtos%5B0%5D.paymentGroupId");
                key.add("returnRefundAgentDto.returnRefundDetailAgentDtos%5B0%5D.defaultRefundMethod");
                key.add("returnRefundAgentDto.maxRefundAmount");
                key.add("returnRefundAgentDto.suggestRefundAmount");
                key.add("returnRefundAgentDto.isFastReturn");
                key.add("totalDisAmount");
                key.add("returnRefundAgentDto.debuctComment");
            } else {
                key.add("replShipConsignee");
                key.add("replShipPhone");
                key.add("replShipPostCode");
                key.add("replShipState");
                key.add("replShipCityn");
                key.add("replShipCounty");
                key.add("replShipTown");
                key.add("replShipDetailAddress");
            }
        } else {
            System.out.println(String.format("获取退换货类型失败"));
            log.info(String.format("获取退换货类型失败"));
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
//        String invoiceNum= ContextUtils.getContext().get("invoiceNum").toString();
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
        String returnItemId = "";
        if (ContextUtils.getContext().get("returnItemId") != null) {
            returnItemId = ContextUtils.getContext().get("returnItemId").toString();
        }
        System.out.println(String.format("returnItemId：%s", returnItemId));
        log.info(String.format("returnItemId：%s", returnItemId));
        String returnShipGroupId = "";
        if (ContextUtils.getContext().get("returnShipGroupId") != null) {
            returnShipGroupId = ContextUtils.getContext().get("returnShipGroupId").toString();
        }
        System.out.println(String.format("returnShipGroupId：%s", returnShipGroupId));
        log.info(String.format("returnShipGroupId：%s", returnShipGroupId));
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
        String returnRequestId = "";
        if (ContextUtils.getContext().get("returnRequestId") != null) {
            returnRequestId = ContextUtils.getContext().get("returnRequestId").toString();
        }
        System.out.println(String.format("returnRequestId：%s", returnRequestId));
        log.info(String.format("returnRequestId：%s", returnRequestId));
        String originOfReturn = "";
        if (ContextUtils.getContext().get("originOfReturn") != null) {
            originOfReturn = ContextUtils.getContext().get("originOfReturn").toString();
        }
        System.out.println(String.format("originOfReturn：%s", originOfReturn));
        log.info(String.format("originOfReturn：%s", originOfReturn));
        String status = "";
        if (ContextUtils.getContext().get("status") != null) {
            status = ContextUtils.getContext().get("status").toString();
        }
        System.out.println(String.format("status：%s", status));
        log.info(String.format("status：%s", status));
        String repeatCommitStr = "";
        if (ContextUtils.getContext().get("repeatCommitStr") != null) {
            repeatCommitStr = ContextUtils.getContext().get("repeatCommitStr").toString();
        }
        System.out.println(String.format("repeatCommitStr：%s", repeatCommitStr));
        log.info(String.format("repeatCommitStr：%s", repeatCommitStr));
        String isMandatoryCollectInvoice = "";
        if (ContextUtils.getContext().get("isMandatoryCollectInvoice") != null) {
            isMandatoryCollectInvoice = ContextUtils.getContext().get("isMandatoryCollectInvoice").toString();
        }
        System.out.println(String.format("isMandatoryCollectInvoice：%s", repeatCommitStr));
        log.info(String.format("isMandatoryCollectInvoice：%s", repeatCommitStr));
        value.add(productName);
        value.add(productId);
        value.add(skuId);
        value.add(quantity);
//        value.add(invoiceNum);
        value.add("");
        value.add(invoiceState);
        value.add(orderId);
        value.add(returnItemId);
        value.add(returnShipGroupId);
        value.add(shippingGroupId);
        value.add(commerceItemId);
        value.add(itemDetailId);
        value.add(returnRequestId);
        value.add(originOfReturn);
        value.add(status);
        value.add(repeatCommitStr);
        value.add(isMandatoryCollectInvoice);
        value.add(returnType);
        value.add("didNotLike");
        value.add("123456");
        value.add("0");
        value.add("3");
        value.add("123456");
        value.add("2");
        value.add("1");
        value.add("");
        value.add("");
        value.add("11000000");
        value.add("11010000");
        value.add("11010200");
        value.add("");
//        value.add("%E9%9C%84%E4%BA%91%E8%B7%AF+26%E5%8F%B7%E9%B9%8F%E6%B6%A6%E5%A4%A7%E5%8E%A6");
        value.add("ROAD XIAOYUN NO26");
//        try {
//            value.add(URLEncoder.encode("霄云路 26号鹏润大厦", "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        value.add("L1074");
        value.add("2");
        value.add("0");
        value.add("test7");
        value.add("136****1123");
        value.add("");
        if (ContextUtils.getContext().get("returnType") != null) {
            if ("1".equals(returnType)) {
                value.add("614117426");
                value.add("%E5%8E%9F%E8%B7%AF%E8%BF%94%E5%9B%9E");
////            try {
////                value.add(URLEncoder.encode("原路返回", "UTF-8"));
////            } catch (UnsupportedEncodingException e) {
////                e.printStackTrace();
////            }
//                value.add("原路返回");
                value.add("0.1");
                value.add("0.1");
                value.add("0");
                value.add("0");
                value.add("");
            } else {
                value.add("test7");
                value.add("13645671123");
                value.add("");
                value.add("11000000");
                value.add("11010000");
                value.add("11010200");
                value.add("110102001");
                value.add("ROAD XIAOYUN NO26");
////            try {
////                value.add(URLEncoder.encode("霄云路 26号鹏润大厦", "UTF-8"));
////            } catch (UnsupportedEncodingException e) {
////                e.printStackTrace();
////            }
//            value.add("%E9%9C%84%E4%BA%91%E8%B7%AF+26%E5%8F%B7%E9%B9%8F%E6%B6%A6%E5%A4%A7%E5%8E%A6");
            }
        }else {
            System.out.println(String.format("获取退换货类型失败"));
            log.info(String.format("获取退换货类型失败"));
        }
    }
}
