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
@Table(name = "M_PORT")
public class Port implements  java.io.Serializable{

    private int id;
    private int portNumber;
    private short enable;//0：否 1：是

    /** default constructor */
    public Port() {}

    public Port(short enable, int id, int portNumber) {
        this.enable = enable;
        this.id = id;
        this.portNumber = portNumber;
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

    @Column(name = "ENABLE")
    public short getEnable() {
        return enable;
    }

    public void setEnable(short enable) {
        this.enable = enable;
    }

    @Column(name = "PORT_NUMBER")
    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}
