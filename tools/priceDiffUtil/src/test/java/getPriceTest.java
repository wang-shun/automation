import diff.price.pojo.DiffPrice;
import diff.price.util.HttpTools;
import diff.price.util.PriceAnalyseByDom;
import org.jsoup.nodes.Document;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by hacke on 2016/10/22.
 */
public class getPriceTest {
    public final  static String URI = "http://search.gome.com.cn/search";

    @Test
    public void testPriceGet(){

        try {
            String searchsid = "skuId:1122340153";
            Document doc = HttpTools.getHttpGet(URI, searchsid);
            float price  = PriceAnalyseByDom.getPrice(doc);
            DiffPrice priceInfo = HttpTools.getSearchResponseInfo(doc);
            System.out.println("Get price is : " + priceInfo);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
