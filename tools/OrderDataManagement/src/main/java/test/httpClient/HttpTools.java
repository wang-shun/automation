package test.httpClient;

//import org.apache.commons.httpclient.HttpHost;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by weijianxing on 2015/10/28.
 */
public class HttpTools {

    private static Logger logger = LoggerFactory.getLogger(HttpTools.class);
    private static final Pattern COLON = Pattern.compile("%3A", Pattern.LITERAL);
    private static final Pattern SLASH = Pattern.compile("%2F", Pattern.LITERAL);
    private static final Pattern QUEST_MARK = Pattern.compile("%3F", Pattern.LITERAL);
    private static final Pattern EQUAL = Pattern.compile("%3D", Pattern.LITERAL);
    private static final Pattern AMP = Pattern.compile("%26", Pattern.LITERAL);
    /**
     *
     * @return Closeable HttpClient instance.
     */
//    public static CloseableHttpClient getHttpClient(){
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        return httpClient;
//    }

    /**
     *
     * @param Key
     * @param uri
     * @return
     */
    public static HttpPost getHttpPost(List<String > Key ,List<String>Value , String uri){
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
       for(int i=0;i<Key.size();i++) {
           String key = Key.get(i);
           String value = Value.get(i);
           urlParameters.add(new BasicNameValuePair(key, value));
       }

        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return post;
    }

    public static boolean checkForExternal(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (str.charAt(i) > 0x7F) {
                return true;
            }
        }
        return false;
    }

    public static String encodeUrl(String url) {
        if (checkForExternal(url)) {
            try {
                String value = URLEncoder.encode(url, "UTF-8");
                value = COLON.matcher(value).replaceAll(":");
                value = SLASH.matcher(value).replaceAll("/");
                value = QUEST_MARK.matcher(value).replaceAll("?");
                value = EQUAL.matcher(value).replaceAll("=");
                return AMP.matcher(value).replaceAll("&");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return url;
        }
    }

//    public static Document getHttpGet( String uri , String key ) throws  IOException{
//        Document doc= null;
//        doc= Jsoup.connect(uri).data("question",key).get();
//        return doc;
//    }
//
//    public static Document getHttpGet( String uri ){
//        Document doc= null;
//        try {
//            doc= Jsoup.connect(uri).get();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//        return doc;
//    }
    public static HttpGet getHttpGet( String uri , String key , String pType){
        //HttpPost post = new HttpPost(uri);
//        StringBuilder uriRequest = new StringBuilder(uri);
//        uriRequest.append("?question=");
//        uriRequest.append(key);
//        uriRequest.append("&pageType=json");
//        String uristr = uri.toString();

//        String nullFragment = null;
//        URI _uri = null;
//        URL url=null;
        URIBuilder builder = null;
        try {
//            url = new URL(uristr);
//
//            _uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            builder = new URIBuilder(uri);
            builder.addParameter("question" , key);
            builder.addParameter("pageType",pType);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("URI new " + builder.toString() + " is OK");

//        GetMethod method = new GetMethod("http://www.example.com/page");
        HttpGet get = new HttpGet(builder.toString());
        return get;
    }

    public static HttpGet getHttpGetForJson( String uri){
        HttpGet get = new HttpGet(uri);
        return get;
    }

    /**
     *
     *
     * @param request
     * @return if call sucess return response body.
     */
    public static Header[]heads = null;


    public static String getHttpResponse( final HttpUriRequest request){
        String strResp = "";

        final CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        try {
            strResp = httpClient.execute(request , responseHandler);
//            System.out.println("Get response is : " + strResp);

        } catch (Exception e) {

        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strResp;
    }


    public static String getHttpResponseInfo( HttpUriRequest request){
        String jsonBody = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            logger.debug("request uri: "+ request.getURI());
            response = httpClient.execute(request);
            heads = response.getAllHeaders();
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode==200 && entity != null) {
                jsonBody =  EntityUtils.toString(entity);
//                System.out.println("Get response is : " + jsonBody);
            }else{
                logger.error("·µ»ØµÄcode£º"+statusCode+"entity is :"+entity);
                logger.error("Http request fail. response status code no equal 200 or entity is null.");
                jsonBody="{}";
            }
        } catch (Exception e) {
            logger.error("request uri: "+ request.getURI());
            logger.error("http request exception : " + e.getMessage());
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonBody;
    }
}
