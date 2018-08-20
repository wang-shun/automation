package com.gome.test.gui;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;

/**
 * @Mojo(name = "gui")
 * @goal genTests
 */
public class GenTestsMojo extends AbstractMojo {

    /**
     * 2
     *
     * @parameter property="project.groupId"
     */
    private String groupId;

    /**
     * @parameter property="project.artifactId"
     */
    private String artifactId;

    /**
     * @parameter property="project.version"
     */
    private String version;

    /**
     * @parameter property="project.basedir"
     */
    private File basedir;

    /**
     * @parameter
     */
    private String timeout;

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (PluginUtils.checkParam(artifactId) == false)
            throw new MojoFailureException("artifactId:" + artifactId + "Exception: artifactId 不合法!!!", null);

        Log logger = getLog();
        String caseCategoryPath = System.getProperty(Constant.CASE_CATEGORY_PATH);
        File testProjectOutputDirectory = new File(basedir.getParentFile(), Constant.TEST_PROJECT_FOLDER);
        File testCaseDir = new File(basedir,
                String.format("src%smain%sresources%scom%sgome%stest%sgui%scase",
                        File.separator, File.separator, File.separator, File.separator, File.separator, File.separator, File.separator));

        PluginUtils.delete(testProjectOutputDirectory);
        TestProjectGen testProjectGen = new TestProjectGen();
        try {
            if (null == timeout)
                timeout = "300000";

            testProjectGen.genTestProject(groupId, artifactId, version,
                    testProjectOutputDirectory, testCaseDir,
                    timeout, caseCategoryPath, logger);
        } catch (Exception ex) {
            PluginUtils.delete(testProjectOutputDirectory);
            throw new MojoExecutionException("Exception: ", ex);
        }
    }

    public static void main(String[] args) throws Exception {
        GenTestsMojo m = new GenTestsMojo();

        m.groupId = "com.gome.test.gui";
        m.artifactId = "sample";
        m.version = "0.0.1";
        m.basedir = new File("/Users/zhangliang/SourceCode/sample/Trunk/GUITest/Helper/");
        m.timeout = "30000";

        m.execute();
    }
}
