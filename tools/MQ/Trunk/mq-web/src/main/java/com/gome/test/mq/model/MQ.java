package com.gome.test.mq.model;

/**
 * Created by zhangjiadi on 15/12/17.
 */
public class MQ {
    public MQ(String hostName,String qmgName,String channel,int ccsid,int port){
        this.hostName=hostName;
        this.qmgName=qmgName;
        this.channel=channel;
        this.CCSID=ccsid;
        this.port=port;

    }

    public String getqName() {
        return qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
    }

    public String getQmgName() {
        return qmgName;
    }

    public void setQmgName(String qmgName) {
        this.qmgName = qmgName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getCCSID() {
        return CCSID;
    }

    public void setCCSID(int CCSID) {
        this.CCSID = CCSID;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    private String qName = null; // 队列名 通过参数传递
    private String qmgName = null; // 队列管理器名 通过参数传递
    private String channel =null ; //管道名称参数传递
    private int CCSID ; //编码
    private int port ;//端口
    private String hostName=null;//链接主机ip

    public MQUser getMqUser() {
        return mqUser;
    }

    public void setMqUser(MQUser mqUser) {
        this.mqUser = mqUser;
    }

    private MQUser mqUser;
}
