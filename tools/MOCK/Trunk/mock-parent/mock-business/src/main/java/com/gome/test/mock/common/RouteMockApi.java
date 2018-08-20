package com.gome.test.mock.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPConstants;

import org.apache.commons.lang.StringEscapeUtils;

import com.gome.test.mock.cnst.ConstDefine;
import com.gome.test.mock.model.bean.Api;

public class RouteMockApi {

    //根据请求体或URL定位API
    public static List<Api> getMatchApi(List<Api> apis, String bodyOrUrl) throws Exception {
        List<Api> apiList = new ArrayList<Api>();

        for (Api api : apis) {
            if (checkKeyWordMatch(api.getKeyWords(), bodyOrUrl) && checkApiNameMatch(api.getApiName(), bodyOrUrl)) {
                apiList.add(api);
            }
        }
        return apiList;
    }

    //根据正则匹配API
    public static boolean checkKeyWordMatch(String keyword, String bodyOrUrl) throws Exception {
        String reqBodyLower = bodyOrUrl.toLowerCase();
        String[] tempArr = null;
        if (keyword.startsWith("[") && keyword.endsWith("]")) {
            keyword = keyword.trim().substring(1, keyword.length() - 1);
            Pattern pn = Pattern.compile(StringEscapeUtils.unescapeJava(keyword));
            Matcher mr = pn.matcher(reqBodyLower);
            if (!mr.find()) {
                return false;
            }
        } else {
            if (keyword.contains("|")) {
                tempArr = keyword.split("\\|");
            } else {
                tempArr = new String[1];
                tempArr[0] = keyword;
            }
            for (String str : tempArr) {
                if (str == null) {
                    return false;
                } else {
                    str = str.toLowerCase();
                    if (!reqBodyLower.contains(str.trim())) {
                        return false;
                    }

                }

            }
        }

        return true;
    }

    //根据ApiName匹配API 
    public static boolean checkApiNameMatch(String apiName, String reqBody) throws Exception {
        String reqBodyLower = reqBody.toLowerCase();
        if (!reqBodyLower.contains(apiName.trim().toLowerCase())) {
            return false;
        }
        return true;
    }

    public static Api getServiceApi(List<Api> apis, String bodyOrUrl) throws Exception {
        Api api = null;
        if (apis != null && apis.size() > 0) {
            apis = getMatchApi(apis, bodyOrUrl);
            if (apis.size() == 1) {
                api = apis.get(0);
            }
        }
        return api;
    }

    //判断是否为SAOPWEBservice
    public static boolean isWebService(Map<String, List<String>> headerMap, String body, String protocol) throws Exception {
        boolean mark = false;
        List<String> conTypeList = new ArrayList<String>();
        if (headerMap != null) {
            conTypeList = headerMap.get(ConstDefine.CONTENT_TYPE);
        }
        if (headerMap.containsKey(ConstDefine.SOAP_ONE_ONE_MARK)) {//Soap11
            mark = true;
            protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
        } else if (conTypeList != null && conTypeList.contains(ConstDefine.CONTENT_TYPE_APP_SOAP_XML)) {//Soap12
            if (body != null && body.contains(ConstDefine.SOAP_ONE_TWO_MARK)) {
                mark = true;
                protocol = SOAPConstants.SOAP_1_1_PROTOCOL;
            }
        }
        return mark;
    }

}
