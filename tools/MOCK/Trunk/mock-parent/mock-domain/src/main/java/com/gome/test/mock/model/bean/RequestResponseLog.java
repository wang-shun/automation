package com.gome.test.mock.model.bean;
/**
 * Created by chaizhongbao on 2015/9/25.
 */

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "M_REQUEST_RESPONSE_LOG")
@DynamicUpdate(true)
public class RequestResponseLog implements  java.io.Serializable {

    private int id;
    private String sessionId;
    private int apiId;
    private String clientAddress;
    private long requestTime;
    private long responseTime;
    private String requestData;
    private String responseData;
    private int sequency;
    /** default constructor */
    public RequestResponseLog() {}

    public RequestResponseLog(int apiId, String clientAddress, int id, String requestData, long requestTime, String responseData, long responseTime, int sequency, String sessionId) {
        this.apiId = apiId;
        this.clientAddress = clientAddress;
        this.id = id;
        this.requestData = requestData;
        this.requestTime = requestTime;
        this.responseData = responseData;
        this.responseTime = responseTime;
        this.sequency = sequency;
        this.sessionId = sessionId;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //@Column(name = "HOST_NAME", nullable = true, length = 50)

    @Column(name = "API_ID")
    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    @Column(name = "CLIENT_ADDRESS", nullable = true, length = 50)
    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    @Column(name = "REQUEST_DATA", nullable = true)
    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    @Column(name = "REQUEST_TIME")
    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    @Column(name = "RESPONSE_DATA", nullable = true)
    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    @Column(name = "RESPONSE_TIME")
    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    @Column(name = "SEQUENCY")
    public int getSequency() {
        return sequency;
    }

    public void setSequency(int sequency) {
        this.sequency = sequency;
    }

    @Column(name = "SESSION_ID", nullable = true, length = 200)
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
