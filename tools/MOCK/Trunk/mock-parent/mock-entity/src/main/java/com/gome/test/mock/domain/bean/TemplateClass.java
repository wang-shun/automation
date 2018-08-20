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
@Table(name = "M_TEMPLATE_CLASS")
public class TemplateClass implements  java.io.Serializable {

    private int id;
    private int apiId;
    private String className;
    private String nameSpace;
    private String elementName;

    /** default constructor */
    public TemplateClass() {}

    public TemplateClass(int apiId, String className, String elementName, int id, String nameSpace) {
        this.apiId = apiId;
        this.className = className;
        this.elementName = elementName;
        this.id = id;
        this.nameSpace = nameSpace;
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

    //@Column(name = "HOST_NAME", nullable = true, length = 50)

    @Column(name = "API_ID")
    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    @Column(name = "CLASS_NAME", nullable = true, length = 50)
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Column(name = "ELEMENT_NAME", nullable = true, length = 200)
    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    @Column(name = "NAME_SPACE", nullable = true, length = 200)
    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
}
