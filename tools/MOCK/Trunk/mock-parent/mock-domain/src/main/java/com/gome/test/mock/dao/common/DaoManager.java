package com.gome.test.mock.dao.common;

import com.gome.test.mock.dao.*;
import com.gome.test.mock.dao.utils.LoadUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DaoManager {
    @Autowired
    private ApiDao apiDao;

    @Autowired
    private HostDao hostDao;

    @Autowired
    private PortDao portDao;

    @Autowired
    private RequestResponseLogDao requestResponseLogDao;
    @Autowired
    private TemplateDao templateDao;

    public static void main(String[] args) throws Exception {
        List<String> ttList = LoadUtils.getPackageAllFileName("com.gome.test.mock.dao", ".class");
        for (String string : ttList) {
            String className = StringUtils.substringAfterLast(string, ".");
            if (className.endsWith("Dao") && (!className.equals("BaseDao"))) {
                System.out.println("@Autowired");
                System.out.println("private " + className + " " + className.substring(0, 2).toLowerCase() + className.substring(2) + ";");
                System.out.println("");
            }
        }
    }

    public ApiDao getApiDao() {
        return this.apiDao;
    }

    public HostDao getHostDao() {
        return this.hostDao;
    }

    public PortDao getPortDao() {
        return this.portDao;
    }

    public RequestResponseLogDao getRequestResponseLogDao() {
        return this.requestResponseLogDao;
    }

    public TemplateDao getTemplateDao() {
        return templateDao;
    }
}