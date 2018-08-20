package diff.price.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hacke on 2016/10/8.
 */
public class PriceAnalyseByDom {
    private static final Logger logger = LoggerFactory.getLogger(PriceAnalyseByDom.class);


    /**
     * parse html dom then get promotion type.
     * @param doc
     * @return
     */


    public static float getPrice(Document doc){
        Elements products = doc.getElementsByClass("item-tab-warp");
        float price= 0.0f;
        if(products == null && products.size()<1){
            logger.error("Parse DOM fail getElementsByClass item-tab-warp return null");
            return -1;
        }
        price = getsearchProductLowestPrice(products , 0);
        return price;
    }

    /**
     *
     * @param title
     * @return -1 is no promotion.
     *          1 group buy.
     */

    /**
     * get search list price.
     * @param products
     * @param index
     * @return
     */
    public static float getsearchProductLowestPrice(Elements products , int index){
        float price = 0;
        if((products.size()-1)<index){
            logger.error("get product price dom parase fail.");
            return  -1;
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
}
