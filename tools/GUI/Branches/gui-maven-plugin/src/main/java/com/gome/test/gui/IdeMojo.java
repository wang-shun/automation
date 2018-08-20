package com.gome.test.gui;

import com.gome.test.Constant;
import com.gome.test.api.ide.Application;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @Mojo(name = "gui")
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
     * @parameter property="settings.localRepository"
     */
    public String localRepository;

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (null == testCaseDir) {
            testCaseDir = new File(baseDir, "../Helper/src/main/resources/com/gome/test/gui/");
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
        System.out.println(sb.toString());
        System.setProperty(Constant.BASE_DIR, baseDir.getAbsolutePath());
        System.setProperty(Constant.BUILD_DIR, new File(baseDir, "build").getAbsolutePath());
        System.setProperty(Constant.LOCAL_REPOSITORY, localRepository);
        Application app = new Application();
        try {
            app.run();
        } catch (Exception ex) {
            throw new MojoExecutionException("Exception: ", ex);
        }
    }
}

