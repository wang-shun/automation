package com.gome.test.mock.dao;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.gome.test.mock.dao.base.BaseDao;
import com.gome.test.mock.model.bean.Template;

@Repository
@Transactional
public class TemplateDao extends BaseDao<Template> {
    public List queryTemplateListByApiId(int apiId) {
        String sql = String.format("select ID,TEMPLATE_NAME,TEMPLATE_CONTENT,ENABLE from m_template where API_ID = '%s' and enable = 1 ", apiId);
        List templates = this.sqlQuery(sql);
        return templates == null ? new ArrayList<Template>() : templates;
    }

    public List queryTemplateListBySearch() {

        String sql = "select template.ID,template.TEMPLATE_NAME,template.TEMPLATE_CONTENT,template.ENABLE from m_template template where template.enable = 1";
        List result = this.sqlQuery(sql);
        return result == null ? new ArrayList() : result;
    }

    public Object[] getTemplateDetailById(int templateId) {
        String sql = String.format("select ID,TEMPLATE_NAME,TEMPLATE_CONTENT,ENABLE from m_template where ID = '%s'", templateId);
        List templates = this.sqlQuery(sql);
        if (!templates.isEmpty()) {
            return (Object[]) templates.get(0);
        } else {
            return null;
        }
    }
}
