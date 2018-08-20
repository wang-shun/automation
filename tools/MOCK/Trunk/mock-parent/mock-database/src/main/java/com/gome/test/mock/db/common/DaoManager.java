package com.gome.test.mock.db.common;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gome.test.mock.db.dao.ApiDao;
import com.gome.test.mock.db.dao.HostDao;
import com.gome.test.mock.db.uitls.LoadUtils;

@Component("daoManager")
public class DaoManager {
    @Autowired
    private ApiDao apiDao;

    @Autowired
    private HostDao hostDao;

    public ApiDao getApiDao() {
        return this.apiDao;
    }

    public HostDao getHostDao() {
        return this.hostDao;
    }

    public static void main(String[] args) throws Exception {
        List<String> ttList = LoadUtils.getPackageAllFileName("com.gome.test.mock.db.dao", ".class");
        for (String string : ttList) {
            String className = StringUtils.substringAfterLast(string, ".");
            if (className.endsWith("Dao")) {
                System.out.println("@Autowired");
                System.out.println("private " + className + " " + className.substring(0, 2).toLowerCase() + className.substring(2) + ";");
                System.out.println("");
            }
        }
    }
}