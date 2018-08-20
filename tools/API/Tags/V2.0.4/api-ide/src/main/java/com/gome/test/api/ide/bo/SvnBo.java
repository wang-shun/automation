package com.gome.test.api.ide.bo;

import java.io.*;

import com.gome.test.utils.SvnUtils;
import org.springframework.beans.factory.annotation.Value;

public class SvnBo {

    @Value(value = "${svn.username}")
    private String username;

    @Value(value = "${svn.password}")
    private String password;

    private SvnUtils svnUtils;

    public SvnBo(String svnPath) throws Exception {
        this.svnUtils = SvnUtils.newInstance(svnPath);
    }

    public void setUsername(String username) {
        svnUtils.setUsername(username);
    }

    public void setPassword(String password) {
        svnUtils.setPassword(password);
    }

    public String getSvnUrl() {
        return svnUtils.getSvnUrl();
    }

    public void initLoad() throws Exception {
        svnUtils.initLoad();
    }

    public void update() throws Exception {
        svnUtils.update();
    }

    public void commit() throws Exception {
        svnUtils.commit("非交互模式提交所有修改");
    }

    public void delete(File destPath) throws Exception {
        svnUtils.delete(destPath);
    }

    public void add(File destPath) throws Exception {
        svnUtils.add(destPath);
    }

    public String getStoreUserName() throws Exception {
        return svnUtils.getStoreUserName();
    }

    public String getStorePassword() throws Exception {
        return svnUtils.getStorePassword();
    }
}
