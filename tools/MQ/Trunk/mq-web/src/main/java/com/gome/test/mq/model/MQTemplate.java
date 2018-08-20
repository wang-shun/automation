package com.gome.test.mq.model;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhangjiadi on 15/12/24.
 */

@Repository
public class MQTemplate {

    public List<Template> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<Template> templateList) {
        this.templateList = templateList;
    }

    public MQ getMq() {
        return mq;
    }

    public void setMq(MQ mq) {
        this.mq = mq;
    }

    private List<Template> templateList;
    public MQ mq;
}
