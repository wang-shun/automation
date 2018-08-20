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
@Table(name = "M_WCF_WEB_TEMPLATE")
public class WcfWebTemplate implements  java.io.Serializable {

    private int id;
    private int apiId;
    private String sessionGuid;
    private String workTlowName;
    private long dateTime;
    private String detail;
    private int sequenceId;

    /** default constructor */
    public WcfWebTemplate() {}

    public WcfWebTemplate(int apiId, long dateTime, String detail, int id, int sequenceId, String sessionGuid, String workTlowName) {
        this.apiId = apiId;
        this.dateTime = dateTime;
        this.detail = detail;
        this.id = id;
        this.sequenceId = sequenceId;
        this.sessionGuid = sessionGuid;
        this.workTlowName = workTlowName;
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

    @Column(name = "API_ID")
    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    @Column(name = "DATE_TIME")
    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
    @Column(name = "DETAIL",nullable = true)
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Column(name = "SEQUENCE_ID")
    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    @Column(name = "SESSION_GUID", nullable = true, length = 200)
    public String getSessionGuid() {
        return sessionGuid;
    }

    public void setSessionGuid(String sessionGuid) {
        this.sessionGuid = sessionGuid;
    }

    @Column(name = "WORK_FLOW_NAME", nullable = true, length = 50)
    public String getWorkTlowName() {
        return workTlowName;
    }

    public void setWorkTlowName(String workTlowName) {
        this.workTlowName = workTlowName;
    }
}
