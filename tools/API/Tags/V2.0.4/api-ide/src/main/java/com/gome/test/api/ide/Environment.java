package com.gome.test.api.ide;

import com.gome.test.api.ide.bo.SvnBo;
import org.apache.commons.jxpath.ri.compiler.Constant;

import java.io.Console;

public class Environment {

    private final String baseDir;
    private final String appClassPath;
    private final String testsPath;
    private final String workspacePath;
    private final String buildDir;
    private final String svnUrl;
    private String username;
    private String password;

    public Environment() throws Exception {
        Console console = System.console();
        testsPath = System.getProperty("tests.path", DEFAULT_TESTS_PATH);
        workspacePath = System.getProperty("workspace.path", DEFAULT_BASE_DIR);//added by zonglin.li 
        appClassPath = System.getProperty("app.classpath", DEFAULT_APP_CLASSPATH);
        baseDir = System.getProperty("base.dir", DEFAULT_BASE_DIR);
        buildDir = System.getProperty("build.dir", DEFAULT_BUILD_DIR);
        SvnBo svnBo = new SvnBo(testsPath);
        svnUrl = svnBo.getSvnUrl();
        username = svnBo.getStoreUserName();
        if (null == username) {
            System.out.print("请输入SVN用户名: ");
            System.out.flush();
            username = console.readLine();
            if (null == username) {
                throw new Exception("用户终止了输入SVN用户名的操作");
            }
        } else {
            password = svnBo.getStorePassword();
        }
        if (null == password) {
            System.out.print("请输入SVN密码: ");
            System.out.flush();
            password = new String(console.readPassword());
            if (null == password) {
                throw new Exception("用户终止了输入SVN密码操作");
            }
        }
        svnBo.setUsername(username);
        svnBo.setPassword(password);
        svnBo.initLoad();
        svnBo.update();
    }

    public void initLoad() throws Exception {
        System.setProperty(com.gome.test.api.model.Constant.TESTS_PATH, testsPath);
        System.setProperty(com.gome.test.api.model.Constant.WORKSPACE_PATH, workspacePath);//added by zonglin.li
        System.setProperty(com.gome.test.api.model.Constant.APP_CLASSPATH, appClassPath);
        System.setProperty(com.gome.test.api.model.Constant.BASE_DIR, baseDir);
        System.setProperty(com.gome.test.api.model.Constant.BUILD_DIR, buildDir);
        System.setProperty(com.gome.test.api.model.Constant.SVN_USERNAME, username);
        System.setProperty(com.gome.test.api.model.Constant.SVN_PASSWORD, password);
    }

    public String getSvnUrl() {
        return svnUrl;
    }

    private static final String DEFAULT_BASE_DIR = ".";
    private static final String DEFAULT_APP_CLASSPATH = ".";
    private static final String DEFAULT_BUILD_DIR = "./build";
    private static final String DEFAULT_TESTS_PATH = "src/main/resources";
}
