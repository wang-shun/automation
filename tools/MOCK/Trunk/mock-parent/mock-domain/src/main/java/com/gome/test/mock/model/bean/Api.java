package com.gome.test.mock.model.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

/**
 * Created by Administrator on 2015/10/14.
 */

@Entity
@Table(name = "m_api")
@DynamicUpdate(true)
public class Api implements java.io.Serializable {
    private int id;
    private int hostId;
    private int templateId;
    private String apiName;
    private String keyWords;
    private String interceptParam;
    private String descript;
    private String flowContent;
    private short enable;//0：否 1：是

    /** default constructor */
    public Api() {}

    public Api(String apiName, String descript, short enable, int hostId, int id, String keyWords, String flowContent) {
        this.apiName = apiName;
        this.descript = descript;
        this.enable = enable;
        this.hostId = hostId;
        this.id = id;
        this.keyWords = keyWords;
        this.flowContent = flowContent;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "API_NAME", nullable = true, length = 200)
    public String getApiName() {
        return this.apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    @Column(name = "DESCRIPT", nullable = true, length = 500)
    public String getDescript() {
        return this.descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    @Column(name = "ENABLE")
    public short getEnable() {
        return this.enable;
    }

    public void setEnable(short enable) {
        this.enable = enable;
    }

    @Column(name = "HOST_ID")
    public int getHostId() {
        return this.hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    @Column(name = "TEMPLATE_ID")
    public int getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    @Column(name = "KEY_WORDS", nullable = true, length = 500)
    public String getKeyWords() {
        return this.keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    @Column(name = "INTERCEPT_PARAM", nullable = true)
    public String getInterceptParam() {
        return this.interceptParam;
    }

    public void setInterceptParam(String interceptParam) {
        this.interceptParam = interceptParam;
    }

    @Column(name = "FLOW_CONTENT", nullable = true)
    public String getFlowContent() {
        return this.flowContent;
    }

    public void setFlowContent(String flowContent) {
        this.flowContent = flowContent;
    }

}
