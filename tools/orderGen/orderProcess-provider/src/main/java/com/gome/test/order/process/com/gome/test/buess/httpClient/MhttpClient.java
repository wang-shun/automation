package com.gome.test.order.process.com.gome.test.buess.httpClient;

import com.gome.test.context.ContextUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangwei-ds on 2016/9/20.
 */
public class MhttpClient {
    public static Map<String, String> SCN = new HashMap<String, String>();
    HttpRequestBase request;

    public String visit(String url, List<String> KEY, List<String> VALUE) throws Exception {
        request = HttpTools.getHttpPost(KEY, VALUE, url);
        setCookes();
        return HttpTools.getHttpResponseInfo(request);
    }

    public String visit(String url) {
        request = HttpTools.getHttpGetForJson(url);
        setCookes();
        return HttpTools.getHttpResponseInfo(request);
    }
    public String visitPost(String url) {
        request = HttpTools.getHttpPost(url);
        setCookes();
        return HttpTools.getHttpResponseInfo(request);
    }
    public String visit(String url, String order) {
        request = HttpTools.getHttpGetForJson(url);
        setCookes(order, url);
        return HttpTools.getHttpResponseInfo(request);
    }

    public String visitPostXML(String url,String xml){
        request = HttpTools.getHttpPost(url,xml);
        setCookes();
        return HttpTools.getHttpResponseInfo(request);
    }

    public void setCookes() {
        if (HttpTools.heads != null) {
            StringBuilder cookieContent = new StringBuilder();
            for (Header header : HttpTools.heads) {
                if (header.getName().equals("Set-Cookie")) {
                    if (header.getValue().contains("_erm_sso")) {
                        SCN.put(header.getName(), header.getValue());
                    }
                    if (ContextUtils.getContext().get("Set-Cookie") !=null){
                        SCN.put("Set-Cookie", ContextUtils.getContext().get("Set-Cookie").toString()
                        );
                    }
                    String value = header.getValue();
                    String subvlue = value.substring(0, value.indexOf(";")).trim();
                    cookieContent.append(subvlue).append(";");
                }
            }
            if (SCN.isEmpty() == false) {
                if (!cookieContent.toString().contains("_erm_sso")) {
                    String value = SCN.get("Set-Cookie");
                    String subvlue = value.substring(0, value.indexOf(";")).trim();
                    cookieContent.append(subvlue);
                }

            }
            request.setHeader("Cookie", cookieContent.toString());
            request.setHeader("Referer", "http://erm.atguat.com.cn");
        }
    }

    public void setCookes(String order, String url) {
        if (HttpTools.heads != null) {
            StringBuilder cookieContent = new StringBuilder();
            for (Header header : HttpTools.heads) {
                if (header.getName().equals("Set-Cookie")) {
                    if (header.getValue().contains("_erm_sso")) {
                        SCN.put("erm", header.getValue());
                    }
                    if (header.getValue().contains("SCN")) {
                        SCN.put("SCN", header.getValue());
                    }
                    String value = header.getValue();
                    String subvlue = value.substring(0, value.indexOf(";")).trim();
                    cookieContent.append(subvlue).append(";");
                }
            }
            if (SCN.isEmpty() == false) {
                if (!cookieContent.toString().contains("_erm_sso")) {
                    String value = SCN.get("erm");
                    if (value != null) {
                        String subvlue = value.substring(0, value.indexOf(";")).trim();
                        cookieContent.append(subvlue);
                    }
                }
                if (!cookieContent.toString().contains("SCN")) {
                    String value = SCN.get("SCN");
                    if (value != null) {
                        String subvlue = value.substring(0, value.indexOf(";")).trim();
                        cookieContent.append(subvlue);
                    }

                }

            }
            request.setHeader("Cookie", cookieContent.toString());
            if (url.contains("getOrderDetailRightInfo")) {
                request.setHeader("Referer", String.format("http://myhome.atguat.com.cn/member/shippingGroupDetailInfo/%s/null", order));
            } else if (url.contains("getReturnOrderList")) {
                request.setHeader("Referer", "http://myhome.atguat.com.cn/member/myReturnGoodList?orderNo=" + order);
            } else if (url.contains("getSubmitROPageInitInfo")) {
                request.setHeader("Referer", "http://myhome.atguat.com.cn/member/myReturnGoodApply?returnOrderId=15103117108&shipId=2569299193&commerceItemId=2511483386&detailId=1148885707");
            }

        }
    }

}
