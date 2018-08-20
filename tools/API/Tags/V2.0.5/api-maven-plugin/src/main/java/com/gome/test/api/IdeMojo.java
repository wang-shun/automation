package com.gome.test.api;

import com.gome.test.api.ide.Application;

import java.io.File;
import java.util.List;

import com.gome.test.api.model.Constant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

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
        Application app = new Application();
        try {
            app.run(new String[]{});
        } catch (Exception ex) {
            throw new MojoExecutionException("Exception: ", ex);
        }
    }
}
