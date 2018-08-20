package com.gome.test.api;


import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gome.test.Constant;
import com.gome.test.api.ide.Application;
import com.gome.test.utils.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * @Mojo(name = "api")
 * @goal ide
 * @requiresDependencyResolution compile+runtime
 */
public class IdeMojo extends AbstractMojo {

    /**
     * @parameter
     */
    private File testCaseDir;

    /**
     * @parameter
     */
    private File workspaceDir;

    /**
     * @parameter property="project.compileClasspathElements"
     */
    private List<String> classpaths;

    /**
     * @parameter property="project.basedir"
     */
    private File baseDir;

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        long start = System.currentTimeMillis();
        if (null == testCaseDir) {
            testCaseDir = new File(baseDir, "../TestCase");
        }
        System.setProperty(Constant.TESTS_PATH, testCaseDir.getAbsolutePath());

//        added by zonglin.li 目录树的根目录 JAPITest
        if (null == workspaceDir) {
            workspaceDir = new File(baseDir, "../");
        }
        System.setProperty(Constant.WORKSPACE_PATH, workspaceDir.getAbsolutePath());

        StringBuilder sb = new StringBuilder();
        if (null != classpaths) {
            for (String classpath : classpaths) {
                sb.append(classpath);
                sb.append(";");
            }
        }

        System.setProperty(Constant.APP_CLASSPATH, sb.toString());
        System.setProperty(Constant.BASE_DIR, baseDir.getAbsolutePath());
        System.setProperty(Constant.BUILD_DIR, new File(baseDir, "build").getAbsolutePath());

//        ClassLoader cl = Thread.currentThread().getContextClassLoader();

//        try {
//            URL[] urls = new URL[classpaths.size()];
//            for (int i = 0; i < classpaths.size(); i++) {
//                if (!classpaths.get(i).isEmpty()) {
//                    urls[i] = new File(classpaths.get(i)).toURI().toURL();
//                }
//            }
//
//            URLClassLoader ucl = new URLClassLoader(urls, cl);
//            Thread.currentThread().setContextClassLoader(ucl);
//
//            Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass("com.gome.test.api.ide.Application");
//            Constructor constructor = cls.getConstructor();
//            Object app = constructor.newInstance();
//            cls.getDeclaredMethod("run").invoke(app);
//        } catch (Throwable ex) {
//            throw new MojoExecutionException(ex.getMessage(), ex);
//        } finally {
//            Thread.currentThread().setContextClassLoader(cl);
//        }

        Application app = new Application();
        try {
            app.run();
        } catch (Exception ex) {
            throw new MojoExecutionException("Exception: ", ex);
        }

        long end = System.currentTimeMillis();
        long result = end - start;
        getLog().info("执行完毕，耗时:" + result + "ms ~ " + result / 1000 + "s");

    }
}
