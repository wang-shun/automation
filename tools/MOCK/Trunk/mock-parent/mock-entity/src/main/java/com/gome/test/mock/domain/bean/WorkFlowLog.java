package com.gome.test.mock.domain.bean;
/**
 * Created by chaizhongbao on 2015/9/25.
 */

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name = "M_WORK_FLOW_LOG")
public class WorkFlowLog implements  java.io.Serializable {

    private int id;
    private int apiId;
    private String classTemplate;
    private short defalutTemplate;
    private String jarTemplate;
    private String keyWord;
    private String hql;
    private String beforeSendScript;
    private String afterSendScript;
    private String callBackIpMaping;
    private int callBackSleep;
    /** default constructor */
    public WorkFlowLog() {}

    public WorkFlowLog(String afterSendScript, int apiId, String beforeSendScript, String callBackIpMaping, int callBackSleep, String classTemplate, short defalutTemplate, String hql, int id, String jarTemplate, String keyWord) {
        this.afterSendScript = afterSendScript;
        this.apiId = apiId;
        this.beforeSendScript = beforeSendScript;
        this.callBackIpMaping = callBackIpMaping;
        this.callBackSleep = callBackSleep;
        this.classTemplate = classTemplate;
        this.defalutTemplate = defalutTemplate;
        this.hql = hql;
        this.id = id;
        this.jarTemplate = jarTemplate;
        this.keyWord = keyWord;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //@Column(name = "HOST_NAME", nullable = true, length = 50)

    @Column(name = "AFTER_SEND_SCRIPT", nullable = true)
    public String getAfterSendScript() {
        return afterSendScript;
    }

    public void setAfterSendScript(String afterSendScript) {
        this.afterSendScript = afterSendScript;
    }

    @Column(name = "API_ID")
    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    @Column(name = "BEFORE_SEND_SCRIPT", nullable = true)
    public String getBeforeSendScript() {
        return beforeSendScript;
    }

    public void setBeforeSendScript(String beforeSendScript) {
        this.beforeSendScript = beforeSendScript;
    }

    @Column(name = "CALL_BACK_IP_MAPING", nullable = true)
    public String getCallBackIpMaping() {
        return callBackIpMaping;
    }

    public void setCallBackIpMaping(String callBackIpMaping) {
        this.callBackIpMaping = callBackIpMaping;
    }

    @Column(name = "CALL_BACK_SLEEP")
    public int getCallBackSleep() {
        return callBackSleep;
    }

    public void setCallBackSleep(int callBackSleep) {
        this.callBackSleep = callBackSleep;
    }

    @Column(name = "CLASS_TEMPLATE", nullable = true)
    public String getClassTemplate() {
        return classTemplate;
    }

    public void setClassTemplate(String classTemplate) {
        this.classTemplate = classTemplate;
    }

    @Column(name = "DEFALUT_TEMPLATE")
    public short getDefalutTemplate() {
        return defalutTemplate;
    }

    public void setDefalutTemplate(short defalutTemplate) {
        this.defalutTemplate = defalutTemplate;
    }

    @Column(name = "HQL", nullable = true)
    public String getHql() {
        return hql;
    }

    public void setHql(String hql) {
        this.hql = hql;
    }

    @Column(name = "JAR_TEMPLATE", nullable = true)
    public String getJarTemplate() {
        return jarTemplate;
    }

    public void setJarTemplate(String jarTemplate) {
        this.jarTemplate = jarTemplate;
    }

    @Column(name = "KEY_WORD", nullable = true)
    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
