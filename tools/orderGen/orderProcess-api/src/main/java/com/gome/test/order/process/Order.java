package com.gome.test.order.process;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by hacke on 2017/2/27.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Order  implements Serializable {

    @JsonProperty("orderNo")
    @XmlElement(name = "orderNo")
    //订单号
    private String orderNo;
    @JsonProperty("expectStatus")
    @XmlElement(name = "expectStatus")
    @NotNull
    private String expectStatus;

    @JsonProperty("orderNumber")
    @XmlElement(name = "orderNumber")
    //配送单号
    String orderNumber;

    @JsonProperty("URL")
    @XmlElement(name = "URL")
    private String URL;

    @JsonProperty("emailCount")
    @XmlElement(name = "emailCount")
    private String emailCount;

    @JsonProperty("currentStatus")
    @XmlElement(name = "currentStatus")
    private String currentStatus;

    @JsonProperty("startTime")
    @XmlElement(name = "startTime")
    private Timestamp startTime;

    @JsonProperty("endStatus")
    @XmlElement(name = "endStatus")
    private String endStatus;

    private Boolean issuccess;
    private Boolean isNew;
    /**
     * 行号
     */
    private String  dzxh;

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    /**

     * 仓库编码
     */
    private String  dzck;


    /**
     * 商品编号
     */
    private String productCode; //

    public String getDzxh() {
        return dzxh;
    }

    public void setDzxh(String dzxh) {
        this.dzxh = dzxh;
    }

    public String getDzck() {
        return dzck;
    }

    public void setDzck(String dzck) {
        this.dzck = dzck;
    }

    /**
     * 库位

     */
    private String fromStorageCode;//
    /**
     * 相关单号
     */
    private String relatedBill1;//
    /**
     * 批次号
     */
    private String pch ;//
    /**
     * 安迅运单号
     */
    private String gWaybillNo;//
    public String getPch() {
        return pch;
    }

    public void setPch(String pch) {
        this.pch = pch;
    }


    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getFromStorageCode() {
        return fromStorageCode;
    }

    public void setFromStorageCode(String fromStorageCode) {
        this.fromStorageCode = fromStorageCode;
    }

    public String getRelatedBill1() {
        return relatedBill1;
    }

    public void setRelatedBill1(String relatedBill1) {
        this.relatedBill1 = relatedBill1;
    }

    public String getgWaybillNo() {
        return gWaybillNo;
    }

    public void setgWaybillNo(String gWaybillNo) {
        this.gWaybillNo = gWaybillNo;
    }


    private String  requestId;

    private String  returnType;

    private String  username;

    private String  password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }


    public Boolean getIssuccess() {
        return issuccess;
    }

    public void setIssuccess(Boolean issuccess) {
        this.issuccess = issuccess;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(String endStatus) {
        this.endStatus = endStatus;
    }

    @JsonProperty("desc")
    @XmlElement(name = "desc")
    private String desc;

//    @JsonProperty("desc")
//    @XmlElement(name = "desc")
//    private String desc;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getExpectStatus() {
        return expectStatus;
    }

    public void setExpectStatus(String expectStatus) {
        this.expectStatus = expectStatus;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getEmailCount() {
        return emailCount;
    }

    public void setEmailCount(String emailCount) {
        this.emailCount = emailCount;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JsonProperty("productType")
    @XmlElement(name = "productType")
    private String productType;
}
