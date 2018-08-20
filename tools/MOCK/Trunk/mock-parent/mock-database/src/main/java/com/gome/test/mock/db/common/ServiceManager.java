package com.gome.test.mock.db.common;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gome.test.mock.db.service.ApiService;
import com.gome.test.mock.db.service.HostService;
import com.gome.test.mock.db.uitls.LoadUtils;

/**
 * Service管理类
 */
@Component("serviceManager")
public class ServiceManager {
    @Autowired
    private ApiService apiService;

    @Autowired
    private HostService hostService;

    public ApiService getApiService() {
        return this.apiService;
    }

    public HostService getHostService() {
        return this.hostService;
    }

    public static void main(String[] args) throws Exception {
        List<String> ttList = LoadUtils.getPackageAllFileName("com.gome.test.mock.db.service", ".class");
        for (String string : ttList) {
            String className = StringUtils.substringAfterLast(string, ".");
            if (className.indexOf("Hessian") == -1 && className.endsWith("Service")) {
                System.out.println("@Autowired");
                System.out.println("private " + className + " " + className.substring(0, 2).toLowerCase() + className.substring(2) + ";");
                System.out.println("");
            }
        }
    }
}
