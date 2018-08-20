package diff.price.util;

import diff.price.pojo.DiffPrice;
import diff.price.pojo.ResponseBase;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
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
     * @param map
     * @param uri
     * @return
     */
    public static HttpPost getHttpPost(TreeMap<String , String> map , String uri){
        HttpPost post = new HttpPost(uri);
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
       for(Map.Entry<String , String> entry : map.entrySet()){
           String key = entry.getKey();
           String value = entry.getValue();
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

    /**
     *
     * @param uri
     * @param key (search term.)
     * @return
     * @throws IOException
     */
    public static Document getHttpGet( String uri , String key ) throws  IOException{
        Document doc= null;
        doc= Jsoup.connect(uri).data("question",key).get();
        return doc;
    }

    public static Document getHttpGet( String uri ){
        Document doc= null;
        try {
            doc= Jsoup.connect(uri).get();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return doc;
    }

    public static HttpGet getHttpGetForJson( String uri){
        HttpGet get = new HttpGet(uri);
        return get;
    }

    public static String getHttpResponseInfo( HttpUriRequest request){
        String jsonBody = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            logger.debug("request uri: "+ request.getURI());
            response = httpClient.execute(request);
//            System.out.println("Get response is : " + strResp);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode==200 && entity != null) {
                jsonBody =  EntityUtils.toString(entity);
            }else{
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
    private static int getTotalCount(Elements totalNum ){
        int result = -1;
        if(totalNum != null){
            String strTotal = totalNum.text();
            if(strTotal!=null && strTotal.length()>0){
                result = Integer.parseInt(strTotal);
            }
        }
        return result;
    }
    public static int getProductOwner(Elements product , int index){
        int owner = 0;
        if((product.size()-1)<index){
            logger.error("get product owner dom parase fail.");
            owner = -1;
        }
        Element element = product.get(index);
        String pid = element.attr("pid");
        String prefix = pid.substring(0, 1).toUpperCase();
        if(prefix.equals("A")){
//            System.out.println("---------pid is : "+pid);
            owner = 1;
        }else{
//            System.out.println("---------pid is : "+pid);
            owner = 0;
        }

        return owner;
    }


    /**
     * get search list product's lowest price.
     * dom node sequence : item-pic->item-price-info->sb-item-price-warp->sb-item-price->defaultLi
     * @param products
     * @param index
     * @return
     */
    public static float getsearchProductLowestPrice(Elements products , int index){
        float price = 0;
        if((products.size()-1)<index){
            logger.error("get product price dom parase fail.");
            price = -1;
        }
        Element product = products.get(index);
        if(product != null) {
            Elements itemPriceInfo = product.select("div.item-price-info");
            Elements priceTag =   itemPriceInfo.get(0).getElementsByTag("span");
            String strp = priceTag.text();
//            strp = strp.substring(1);
            int offset = strp.indexOf(".")+3;
            strp = strp.substring(1 , offset);
            try {
                price = Float.parseFloat(strp);
            }catch (Exception e){
                logger.error("getsearchProductLowestPrice parse fail. span tag text is : " + priceTag.text());
                logger.error(e.getStackTrace().toString());
            }
        }
        return  price;
    }

    /**
     * get sku number.
     * @param skuinfos
     * @param index
     * @return
     */
    public static String getSKUNO(Elements skuinfos , int index){
        String skuno =  "";
        if((skuinfos.size()-1)<index){
            logger.error("get sku no dom parase fail.");
            return skuno;
        }

        Element element = skuinfos.get(index);
        Elements elements = element.getElementsByClass("productInfo");
        if(elements != null && elements.size()>0){
            skuno = elements.get(0).attr("skuno");
        }else{
            logger.error("getSKUNO fail.");
        }
        return skuno;
    }
    //emcodeItem item-link
    private static String getProductURI(Elements product , int index ){
        StringBuilder sb = new StringBuilder("http://item.gome.com.cn/");
        if(product != null && (product.size()<(index+1))){
            logger.error("getProductURI dom parse fail.");
            return "";
        }

        Element element = product.get(index);
        String pid = element.attr("pid");
        String sid = element.attr("sid");
        sb.append(pid);
        sb.append("-");
        sb.append(sid);
        sb.append(".html");
        return sb.toString();
    }


    private static int getProductFPSellStatus(Elements fpSellStatus , int index){
        int result = 0;
        if(fpSellStatus != null){

            String text = fpSellStatus.get(index).text();
            if(text != null && text.equals("北京市有货")){
                result = 1;
            }else if(text != null && text.equals("北京市无货")){
                result = 0;
            }else if(text != null && text.equals("正在抢购中")){
                result = 3;
            }else if(text != null && text.equals("正在预约中")){
                result = 4;
            }else{
                logger.error("fpSellStatus : " + text + " not expect.");
                result = 5;
            }
        }
        return result;
    }
    //todo the district code is fixed .
    public static  String getSearchDetailURI(String pid , String sid){
        // _http://ss.gome.com.cn/item/v1/d/m/store/9134182118/1123130377/N/11010200/110102001/72000369617item  


        StringBuilder sb = new StringBuilder("http://ss.gome.com.cn/item/v1/d/m/store/");
        sb.append(pid);
        sb.append("/");
        sb.append(sid);
        sb.append("/N/11010200/");
        sb.append("110102001/72000369617/flag/item");
        return sb.toString();
    }

    private static String getProductDetailJSon(Elements product , int index ){
        String  result = "";
        StringBuilder sb = new StringBuilder("http://item.gome.com.cn/");
        if(product != null && (product.size()<(index+1))){
            logger.error("getProductURI dom parse fail.");
            return result;
        }

        Element element = product.get(index);
        String pid = element.attr("pid");
        String sid = element.attr("sid");

        String url = getSearchDetailURI(pid,  sid);
        try {
            HttpGet get = getHttpGetForJson(url);
            result = getHttpResponseInfo(get);
//            System.out.println("get response json is : " + json);
        }catch (Exception e){
            result = "";
            e.printStackTrace();
        }finally {
            return result;
        }

    }
    public static  Map<String , Float>  getSearchProductDetailPrice(String jsonStr){
        Map<String , Float> result = new HashMap();

        if(jsonStr!=null & jsonStr.length()>0) {
            ResponseBase base = new ResponseBase();
            JSONObject jsonObject = JSONObject.fromObject(jsonStr);
            JSONObject resultObj = jsonObject.getJSONObject("result");
            //raw price
            float listPrice = 0.0F;
            //gome sale price.
            float salePrice = 0.0F;
            //promotion price .
            float saleProm = 0.0F;
            float promotionType = 0;

            if(jsonObject != null && resultObj !=null) {
                JSONObject gomePrice = resultObj.getJSONObject("gomePrice");
                JSONObject promPrice = resultObj.getJSONObject("saleProm");
                if(gomePrice != null){
                    listPrice = (float)gomePrice.getDouble("listPrice");
                    salePrice= (float)gomePrice.getDouble("salePrice");
                    result.put("listPrice" , listPrice);
                    result.put("salePrice" , salePrice);

                }
                if(promPrice != null){
                    saleProm = (float)promPrice.getDouble("price");
                    //get promotion price like group buy.
                    result.put("saleProm" , saleProm);
                    Object o =promPrice.get("type");
                    if(o != null){
                        try {
                            promotionType = Integer.parseInt(o.toString());
                            result.put("type",promotionType);
                        }catch (Exception e){
                            logger.error("parse promotion type fail. " + e.getMessage());
                        }

                    }
                }
            }
        }
        return result;
    }

    public static DiffPrice getSearchResponseInfo(Document doc){
        int MAX_SEARCH_TIMES = 6;
        DiffPrice response = new DiffPrice();
        if(doc == null){
            return null;
        }
        Elements product = doc.getElementsByClass("item-tab-warp");
        Elements totalNums = doc.getElementsByClass("nSearch-crumb-searchNum");
        Elements totalNum = totalNums.select("#searchTotalNumber");
        Elements skuinfos = doc.getElementsByClass("product-item");

        int total = getTotalCount(totalNum);
        //not data found.
        if(total<=0){
            logger.error(">>>>>>>>>>>>>>>not data found getTotalCount . : " + total);
            return response;
        }
        if(total < MAX_SEARCH_TIMES){
            MAX_SEARCH_TIMES = total;
        }


        int owner = getProductOwner(product, 0);
        String productURI = getProductURI(product, 0);
        String detailJSon =  getProductDetailJSon(product, 0);


        String skuno = getSKUNO(skuinfos, 0);
        float fpPrice = getsearchProductLowestPrice(product , 0);
        Map<String , Float> priceMap = getSearchProductDetailPrice(detailJSon);

        response.setOwner(owner);
        response.setProductURI(productURI);
        response.setFirstPSellPrice(fpPrice);
        response.setSkuno(skuno);
        response.setProductURI(productURI);

        for(Map.Entry<String , Float> pric : priceMap.entrySet()){
            if(pric.getKey().equals("salePrice")){
                response.setDetailPSellPrice(pric.getValue());
            }else if(pric.getKey().equals("saleProm")){
                response.setDetailPromPrice(pric.getValue());
            }else  if(pric.getKey().equals("type")){
//                    System.out.println("********** get promotion tppe is : " + pric.getValue().intValue());
                response.setPromotionType(pric.getValue().intValue());
            }
        }
        return response;
    }

}
