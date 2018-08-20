package com.gome.test.api;

import com.gome.test.api.ide.Application;

import java.io.File;
import java.util.List;

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
        System.setProperty("tests.path", testCaseDir.getAbsolutePath());

//        added by zonglin.li 目录树的根目录 JAPITest
        if (null == workspaceDir) {
            workspaceDir = new File(baseDir, "../");
        }
        System.setProperty("workspace.path", workspaceDir.getAbsolutePath());

        StringBuilder sb = new StringBuilder();
        if (null != classpaths) {
            for (String classpath : classpaths) {
                sb.append(classpath);
                sb.append(";");
            }
        }
        System.setProperty("app.classpath", sb.toString());
        System.setProperty("base.dir", baseDir.getAbsolutePath());
        System.setProperty("build.dir", new File(baseDir, "build").getAbsolutePath());
        Application app = new Application();
        try {
            app.run(new String[]{});
        } catch (Exception ex) {
            throw new MojoExecutionException("Exception: ", ex);
        }
    }
}
