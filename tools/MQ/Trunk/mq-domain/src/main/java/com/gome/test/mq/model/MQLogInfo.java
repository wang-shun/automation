package com.gome.test.mq.model;

import com.gome.test.mq.Constant.Constant;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhangjiadi on 15/12/22.
 */
@Entity
@Table(name="logformock_mqinfo")
@DynamicUpdate(true)
public class MQLogInfo implements Serializable {
    public MQLogInfo()
    {

    }

    public MQLogInfo(String host,String port,String channel,String jndiName,String qName,String userName,String userIP,String useType,String template)
    {
        this.mq_host=host;
        this.mq_port=port;
        this.mq_channel=channel;
        this.mq_jndiName=jndiName;
        this.mq_UserName=userName;
        this.user_IP=userIP;
        this.mq_useType=useType;
        this.createTime=new Date();
        this.mq_QName=qName;
        this.template=template;
    }


//    public String getLogMess()
//    {
//        StringBuilder stringBuilder=new StringBuilder("MQLogInfo：");
//        stringBuilder.append(String.format("host:%s,",this.mq_host));
//        stringBuilder.append(String.format("mq_port:%s,",this.mq_port));
//        stringBuilder.append(String.format("mq_channel:%s,",this.mq_channel));
//        stringBuilder.append(String.format("mq_jndiName:%s,",this.mq_jndiName));
//        stringBuilder.append(String.format("mq_UserName:%s,",this.mq_UserName));
//        stringBuilder.append(String.format("user_IP:%s,",this.user_IP));
//        stringBuilder.append(String.format("mq_useType:%s,",this.mq_useType));
//        stringBuilder.append(String.format("createTime:%s,",this.createTime));
//        stringBuilder.append(String.format("mq_QName:%s,",this.mq_QName));
//        stringBuilder.append(String.format("createTime:%s,",this.createTime));
//        return stringBuilder.toString();
//    }

    private int id;
    private String mq_host;
    private String mq_port;
    private String mq_channel;
    private String mq_jndiName;
    private String mq_UserName;
    private String user_IP;
    private String mq_message;
    private String mq_useType;
    private String mq_QName;
    private Date createTime;

    @Column(name="Template",nullable = false)
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    private String template;

    @Column(name="CreateTime",nullable = false)
    public Date getCreateTime() {
        return createTime;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }



    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name="Mq_Host",nullable = false)
    public String getMq_host() {
        return mq_host;
    }

    public void setMq_host(String mq_host) {
        this.mq_host = mq_host;
    }

    @Column(name="Mq_Port",nullable = false)
    public String getMq_port() {
        return mq_port;
    }

    public void setMq_port(String mq_port) {
        this.mq_port = mq_port;
    }

    @Column(name="Mq_Channel",nullable = false)
    public String getMq_channel() {
        return mq_channel;
    }

    public void setMq_channel(String mq_channel) {
        this.mq_channel = mq_channel;
    }

    @Column(name="Mq_JNDIName",nullable = false)
    public String getMq_jndiName() {
        return mq_jndiName;
    }

    public void setMq_jndiName(String mq_jndiName) {
        this.mq_jndiName = mq_jndiName;
    }

    @Column(name="Mq_UserName",nullable = false)
    public String getMq_UserName() {
        return mq_UserName;
    }

    public void setMq_UserName(String mq_UserName) {
        this.mq_UserName = mq_UserName;
    }

    @Column(name="User_IP",nullable = false)
    public String getUser_IP() {
        return user_IP;
    }

    public void setUser_IP(String user_IP) {
        this.user_IP = user_IP;
    }

    @Column(name="Mq_Message",nullable = false)
    public String getMq_message() {
        return mq_message;
    }

    public void setMq_message(String mq_message) {
        this.mq_message = mq_message;
    }
//
//    @Transient
//    @DictionaryMap(parent = Constant.MQ_USETYPE,  keyColName = "Mq_UseType")
    @Column(name="Mq_UseType",nullable = false)
    public String getMq_useType() {
        return mq_useType;
    }

    public void setMq_useType(String mq_userType) {
        this.mq_useType = mq_userType;
    }

    @Column(name="Mq_QName",nullable = false)
    public String getMq_QName() {
        return mq_QName;
    }

    public void setMq_QName(String mq_QName) {
        this.mq_QName = mq_QName;
    }



}
