package diff.price.pojo;

import java.util.List;
import java.util.Map;

/**
 * Created by weijianxing on 2016/5/20.
 */
public class ResponseBase {
    String responseCode="";
    String reginID = "";
    int totalCount= 0;
    int pageSize = 0;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getReginID() {
        return reginID;
    }

    public void setReginID(String reginID) {
        this.reginID = reginID;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<String> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<String> recommend) {
        this.recommend = recommend;
    }

    public List<Map<Integer, List<String>>> getGoods() {
        return goods;
    }

    public void setGoods(List<Map<Integer, List<String>>> goods) {
        this.goods = goods;
    }

    int pageNumber = 0;
    int totalPage = 0;
    //if search match fail then recommend data can not be null.
    List<String> recommend = null;
    List<Map<Integer,List<String>>> goods = null;

    public List<String> getFirstPageProducts() {
        return firstPageProducts;
    }

    public void setFirstPageProducts(List<String> firstPageProducts) {
        this.firstPageProducts = firstPageProducts;
    }

    List<String> firstPageProducts= null;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("ResponseBase:\n");
        sb.append("totalCount:");
        sb.append(totalCount+"\n");
        sb.append("ProductIds:\n");
        for(String id: firstPageProducts){
            sb.append("ID:");
            sb.append(id);
            sb.append(" ; ");
        }
        return  sb.toString();
    }
}
