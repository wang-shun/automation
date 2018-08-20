package com.gome.test.mock.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.gome.test.mock.cnst.ConstDefine;
import com.gome.test.mock.pipes.http.SimpleHttpRequest;

public class ParseRequest {
    public static final String DEFAULT_KEY = "default_key";

    public static Map<String, List<String>> getHeaderMap(SimpleHttpRequest shReq) {
        Map<String, List<String>> headerMap = shReq.getHeaders();
        headerMap.put(ConstDefine.FIRST_LINE, Arrays.asList(getFirstLineArray(shReq)));
        return shReq.getHeaders();
    }

    public static Map<String, String> getParamsMap(SimpleHttpRequest shReq, String regex) throws Exception {

        Map<String, String> paramMap = new HashMap<String, String>();
        String[] strArray = getFirstLineArray(shReq);
        if (regex != null && !"".equals(regex)) {//带参数的RestWS，需要特殊处理的
            String[] arr = regex.split("\\|");
            if (regex.startsWith("@Path")) {//@PathParam|参数名,参数名|正则
                if (arr.length == 2) {//没有参数名//@PathParam|正则
                    String regEx = arr[1];
                    Pattern pn = Pattern.compile(StringEscapeUtils.unescapeJava(regEx.trim()));
                    Matcher mr = pn.matcher(strArray[1]);

                    for (int i = 0; i < mr.groupCount(); i++) {
                        if (mr.matches()) {
                            paramMap.put(String.valueOf(i + 1), mr.group(i + 1));
                        }
                    }
                } else if (arr.length == 3) {//有参数名
                    String[] names = arr[1].split(",");
                    String regEx = arr[2];
                    Pattern pn = Pattern.compile(StringEscapeUtils.unescapeJava(regEx.trim()));
                    Matcher mr = pn.matcher(strArray[1]);
                    for (int i = 0; i < names.length; i++) {
                        if (mr.matches()) {
                            paramMap.put(names[i], mr.group(i + 1));
                        }
                    }
                } else {//没有参数
                }
            } else if (regex.startsWith("@Header")) {//@HeaderParam|参数名,参数名
                String[] names = arr[1].split(",");
                Map<String, List<String>> headerMap = getHeaderMap(shReq);
                for (int i = 0; i < names.length; i++) {
                    paramMap.put(names[i], headerMap.get(names[i]).get(0));
                }
            } else if (regex.startsWith("@Cookie")) {//@CookieParam|参数名,参数名//未测试？？？
                String[] names = arr[1].split(",");
                Map<String, List<String>> headerMap = getHeaderMap(shReq);
                List<String> cookies = headerMap.get("Cookie");
                if (cookies != null && cookies.size() > 0) {
                    for (int i = 0; i < cookies.size(); i++) {
                        String cookie = cookies.get(i);
                        if (Arrays.asList(names).contains(cookie.split("=")[0])) {
                            paramMap.put(cookie.split("=")[0], cookie.split("=")[1]);
                        }
                    }
                }
            } else if (regex.startsWith("@Matrix")) {//@PathParam|参数名(为实现)

            }
        } else {
            if (ConstDefine.HTTP_METHOD_GET.equalsIgnoreCase(strArray[0]) && strArray[1].contains("?")) {
                String[] paramArray = strArray[1].substring(strArray[1].indexOf("?") + 1).split("&");
                for (int i = 0; i < paramArray.length; i++) {
                    paramMap.put(paramArray[i].split("=")[0], paramArray[i].split("=")[1]);
                }
            } else if (ConstDefine.HTTP_METHOD_POST.equalsIgnoreCase(strArray[0])) {
                String body = shReq.getContent() != null ? new String(shReq.getContent(), "UTF-8") : null;
                if (body != null) {
                    String[] paramArray = body.split("&");
                    for (int i = 0; i < paramArray.length; i++) {
                        String[] kvArray = paramArray[i].split("=");
                        if (kvArray.length > 2) {
                            paramMap.put(kvArray[0], paramArray[i].replace(kvArray[0] + "=", ""));
                        } else if (kvArray.length == 1) {
                            paramMap.put(DEFAULT_KEY, paramArray[i]);
                        } else {
                            paramMap.put(kvArray[0], kvArray[1]);
                        }
                    }

                }
            }
        }
        return paramMap;
    }

    public static String getUrl(SimpleHttpRequest shReq) {
        String url = "";
        String[] strArray = getFirstLineArray(shReq);
        url = strArray[1].contains("?") ? strArray[1].substring(0, strArray[1].indexOf("?")) : strArray[1];
        return url;
    }

    public static String getRealUrl(SimpleHttpRequest shReq) {
        String realUrl = "/";
        String[] strArray = getFirstLineArray(shReq);
        String url = strArray[1].contains("?") ? strArray[1].substring(0, strArray[1].indexOf("?")) : strArray[1];
        String[] strArr = url.split("/");
        if (strArr != null && strArr.length > 2) {
            realUrl = url.substring(getCharacterPosition(url), url.length());
            realUrl = url.substring(0, url.length());
        }
        return realUrl;
    }

    public static int getCharacterPosition(String string) {
        //这里是获取"/"符号的位置
        Matcher slashMatcher = Pattern.compile("/").matcher(string);
        int mIdx = 0;
        while (slashMatcher.find()) {
            mIdx++;
            //当"/"符号第三次出现的位置
            if (mIdx == 2) {
                break;
            }
        }
        return slashMatcher.start();
    }

    private static String[] getFirstLineArray(SimpleHttpRequest httpreq) {
        return httpreq.getStartLine().split(" ");
    }

    public static String getClientAddress(SimpleHttpRequest shReq) {
        String ipAddress = "127.0.0.1";

        ipAddress = getHeaderMap(shReq).get("x-forwarded-for") != null ? getHeaderMap(shReq).get("x-forwarded-for").get(0) : null;
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = getHeaderMap(shReq).get("Proxy-Client-IP") != null ? getHeaderMap(shReq).get("Proxy-Client-IP").get(0) : null;
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = getHeaderMap(shReq).get("WL-Proxy-Client-IP") != null ? getHeaderMap(shReq).get("WL-Proxy-Client-IP").get(0) : null;
        }

        return ipAddress;
    }

    public static void main(String[] args) {
        String url = "/rest/person/delete/2";
        String id = "id";
        Pattern pattern = Pattern.compile("\\/person\\/delete\\/([0-9]*)$");
        Matcher m = pattern.matcher(url);
        String[] params = id.split(",");
        for (int i = 0; i < params.length; i++) {
            if (m.find()) {
                System.out.println(111);
            }
        }

        /* Pattern pattern = Pattern.compile("<(.*?)>");
         Matcher matcher = pattern.matcher(url);
         String[] params = matcher.group(1).split(",");

         String regEx = matcher.group(2);
         Pattern pn = Pattern.compile(regEx);
         Matcher mr = pattern.matcher("/blog/aaa/20");
         for (int i = 0; i < params.length; i++) {
             params[i] = mr.group(i + 1);
             System.out.println(params[i]);
         }*/
    }
}
