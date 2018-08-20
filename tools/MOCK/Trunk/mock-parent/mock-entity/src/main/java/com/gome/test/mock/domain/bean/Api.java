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
@Table(name = "M_API")
public class Api implements java.io.Serializable {

    private int id;
    private int hostId;
    private String apiName;
    private String keyWords;
    private String descript;
    private short enable;//0：否 1：是

    /** default constructor */
    public Api() {}

    public Api(String apiName, String descript, short enable, int hostId, int id, String keyWords) {
        this.apiName = apiName;
        this.descript = descript;
        this.enable = enable;
        this.hostId = hostId;
        this.id = id;
        this.keyWords = keyWords;
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

    @Column(name = "KEY_WORDS", nullable = true, length = 500)
    public String getKeyWords() {
        return this.keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }
}
