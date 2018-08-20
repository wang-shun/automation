package test.httpClient;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangwei-ds on 2016/9/20.
 */
public class MhttpClient {
    public static Map<String,String> SCN = new HashMap<String, String>();
    HttpRequestBase request;
    public String visit(String url ,List<String>KEY,List<String>VALUE ) throws Exception {
            request = HttpTools.getHttpPost(KEY, VALUE, url);
            setCookes();
            return HttpTools.getHttpResponseInfo(request);
    }

    public String visit(String url){
        request = HttpTools.getHttpGetForJson(url);
        setCookes();
        return HttpTools.getHttpResponseInfo(request);
    }

    public void setCookes(){
        if (HttpTools.heads != null){
            StringBuilder cookieContent = new StringBuilder();
            for (Header header :HttpTools.heads){
                if(header.getName().equals("Set-Cookie")){
                    if(header.getValue().contains("SCN")){
                        SCN.put(header.getName(), header.getValue());
                    }
                    String value = header.getValue();
                    String subvlue = value.substring(0,value.indexOf(";")).trim();
                    cookieContent.append(subvlue).append(";");
                }
            }
            if (SCN.isEmpty()==false){
                if (!cookieContent.toString().contains("SCN")){
                    String value = SCN.get("Set-Cookie");
                    String subvlue = value.substring(0,value.indexOf(";")).trim();
                    cookieContent.append(subvlue);
                }

            }
            request.setHeader("Cookie",cookieContent.toString());
            request.setHeader("Referer","http://login.atguat.com.cn/login?");
        }
    }

}
