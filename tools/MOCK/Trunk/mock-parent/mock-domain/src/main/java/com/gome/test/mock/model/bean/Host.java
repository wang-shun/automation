package com.gome.test.mock.model.bean;

/**
 * Created by chaizhongbao on 2015/9/25.
 */

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name="m_host")
@DynamicUpdate(true)
public class Host implements java.io.Serializable {

    private int id;
    private String domain;
    private int portId;
    private String serviceName;
    private String url;
    private short hostType;
    private short protocolType;
    private short enable;

    /** default constructor */
    public Host() {}

    public Host(short enable, String domain, String serviceName, short hostType, int id, int portId, short protocolType, String url) {
        this.enable = enable;
        this.domain = domain;
        this.serviceName = serviceName;
        this.hostType = hostType;
        this.id = id;
        this.portId = portId;
        this.protocolType = protocolType;
        this.url = url;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = true)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "ENABLE")
    public short getEnable() {
        return this.enable;
    }

    public void setEnable(short enable) {
        this.enable = enable;
    }

    @Column(name = "Service_NAME", nullable = true, length = 50)
    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Column(name = "DOMAIN", nullable = true, length = 200)
    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Column(name = "HOST_TYPE")
    public short getHostType() {
        return this.hostType;
    }

    public void setHostType(short hostType) {
        this.hostType = hostType;
    }

    @Column(name = "PORT_ID")
    public int getPortId() {
        return this.portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    @Column(name = "PROTOCOL_TYPE")
    public short getProtocolType() {
        return this.protocolType;
    }

    public void setProtocolType(short protocolType) {
        this.protocolType = protocolType;
    }

    @Column(name = "URL", nullable = true, length = 500)
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
