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
@Table(name = "M_TEMPLATE_CLASS_DETAIL")
public class TemplateClassDetail implements  java.io.Serializable {




    private int id;
    private int classId;
    private String fieldName;
    private String fieldType;
    private short listed;
    private String bridgeFieldName;
    private short relead;
    private String relaTableName;
    private String attributeName;
    private short texted;

    /** default constructor */
    public TemplateClassDetail() {}

    public TemplateClassDetail(String attributeName, String bridgeFieldName, int classId, String fieldName, String fieldType, int id, short listed, String relaTableName, short relead, short texted) {
        this.attributeName = attributeName;
        this.bridgeFieldName = bridgeFieldName;
        this.classId = classId;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.id = id;
        this.listed = listed;
        this.relaTableName = relaTableName;
        this.relead = relead;
        this.texted = texted;
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

    @Column(name = "ATTRIBUTE_NAME", nullable = true, length = 50)
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Column(name = "BRIDGE_FIELD_NAME", nullable = true, length = 50)
    public String getBridgeFieldName() {
        return bridgeFieldName;
    }

    public void setBridgeFieldName(String bridgeFieldName) {
        this.bridgeFieldName = bridgeFieldName;
    }

    @Column(name = "CLASS_ID")
    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    @Column(name = "FIELD_NAME", nullable = true, length = 50)
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Column(name = "FIELD_TYPE", nullable = true, length = 50)
    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    @Column(name = "LISTED")
    public short getListed() {
        return listed;
    }

    public void setListed(short listed) {
        this.listed = listed;
    }

    @Column(name = "RELA_TABLE_NAME", nullable = true, length = 50)
    public String getRelaTableName() {
        return relaTableName;
    }

    public void setRelaTableName(String relaTableName) {
        this.relaTableName = relaTableName;
    }

    @Column(name = "RELAED")
    public short getRelead() {
        return relead;
    }

    public void setRelead(short relead) {
        this.relead = relead;
    }

    @Column(name = "TEXTED")
    public short getTexted() {
        return texted;
    }

    public void setTexted(short texted) {
        this.texted = texted;
    }
}
