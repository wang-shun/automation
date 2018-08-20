package com.gome.test.mock.model.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "m_template")
@DynamicUpdate(true)
public class Template implements java.io.Serializable {

    private int id;
    private String templateName;
    private String templateContent;
    private short enable;//0：否 1：是

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "TEMPLATE_NAME", nullable = true, length = 200)
    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Column(name = "TEMPLATE_CONTENT", nullable = true)
    public String getTemplateContent() {
        return this.templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    @Column(name = "ENABLE")
    public short getEnable() {
        return this.enable;
    }

    public void setEnable(short enable) {
        this.enable = enable;
    }

}
