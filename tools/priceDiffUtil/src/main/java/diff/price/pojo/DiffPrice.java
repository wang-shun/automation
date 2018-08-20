package diff.price.pojo;

public class DiffPrice {
    private int termId;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    private String term;
    private float firstPSellPrice;

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public float getFirstPSellPrice() {
        return firstPSellPrice;
    }

    public void setFirstPSellPrice(float firstPSellPrice) {
        this.firstPSellPrice = firstPSellPrice;
    }

    public float getDetailPSellPrice() {
        return detailPSellPrice;
    }

    public void setDetailPSellPrice(float detailPSellPrice) {
        this.detailPSellPrice = detailPSellPrice;
    }

    public String getProductURI() {
        return productURI;
    }

    public void setProductURI(String productURI) {
        this.productURI = productURI;
    }

    public String getSkuno() {
        return skuno;
    }

    public void setSkuno(String skuno) {
        this.skuno = skuno;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(int promotionType) {
        this.promotionType = promotionType;
    }

    private float detailPSellPrice;

    public float getDetailPromPrice() {
        return detailPromPrice;
    }

    public void setDetailPromPrice(float detailPromPrice) {
        this.detailPromPrice = detailPromPrice;
    }

    private float detailPromPrice;
    private String productURI;
    private String skuno;
    private int owner;
    private int promotionType;

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }

    private String searchDate;


    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("json string is : { ");
        sb.append("firstPSellPrice = ");
        sb.append(firstPSellPrice);
        sb.append(" , detailPSellPrice = ");
        sb.append(detailPSellPrice);
        sb.append(" , detailPromPrice = ");
        sb.append(detailPromPrice);
        sb.append(", productURI = ");
        sb.append(productURI);
        sb.append(" } ");
        return sb.toString();
    }
}
