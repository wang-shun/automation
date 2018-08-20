package com.gome.test.mq.model;

import org.springframework.stereotype.Repository;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by zhangjiadi on 15/12/24.
 */
@XmlRootElement
@Repository
public class Template implements Serializable {

//    path="track_n_trace/transaction_id" name="xml讯息ID" isempty="0" dtype="string" ismust="1" dec="" remark="" value=""
    public Template(String path,String name,String dec,String reark) {
    this.path=path;
        this.name=name;
        this.dec=dec;
        this.remark=reark;
    }

    public Template()
    {

    }

    private String path;
    private String name;
    private Boolean isempty;
    private String dtype;
    private Boolean ismust;
    private String dec;
    private String remark;
    private String value;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsempty() {
        return isempty;
    }

    public void setIsempty(Boolean isempty) {
        this.isempty = isempty;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public Boolean getIsmust() {
        return ismust;
    }

    public void setIsmust(Boolean ismust) {
        this.ismust = ismust;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }




}
