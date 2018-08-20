package com.gome.test.mock.domain.bean;

/**
 * Created by chaizhongbao on 2015/9/25.
 */

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicInsert(true)
@DynamicUpdate(true)
@Table(name = "M_WORK_FLOW")
public class WorkFlow implements java.io.Serializable {

    private int id;
    private int apiId;
    private String flowName;
    private String flowContent;
    private short enable;//0：否 1：是

    /** default constructor */
    public WorkFlow() {}

    public WorkFlow(int id, int apiId, String flowName, String flowContent, short enable) {
        super();
        this.id = id;
        this.apiId = apiId;
        this.flowName = flowName;
        this.flowContent = flowContent;
        this.enable = enable;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", unique = true, nullable = true)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApiId() {
        return this.apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    @Column(name = "FLOW_NAME", nullable = true, length = 200)
    public String getFlowName() {
        return this.flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    @Column(name = "FLOW_CONTENT", nullable = true)
    public String getFlowContent() {
        return this.flowContent;
    }

    public void setFlowContent(String flowContent) {
        this.flowContent = flowContent;
    }

    @Column(name = "ENABLE")
    public short getEnable() {
        return this.enable;
    }

    public void setEnable(short enable) {
        this.enable = enable;
    }

    //@Column(name = "HOST_NAME", nullable = true, length = 50)

}
