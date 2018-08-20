package com.gome.test.api;

import java.io.File;

import com.gome.test.api.model.Constant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * @Mojo(name = "api")
 * @goal genTests
 */
public class GenTestsMojo extends AbstractMojo {

    /**
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
    private File testsDir;

    /**
     * @parameter
     */
    private String timeout;

    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
    	if(PluginUtils.checkParam(artifactId) == false)
    		throw new MojoFailureException("Exception: artifactId 不合法!!!", null);
    	
        Log logger = getLog();
        String caseCategoryPath = System.getProperty(Constant.CASE_CATEGORY_PATH);
        File testProjectOutputDirectory = new File(basedir.getParentFile(), Constant.TEST_PROJECT_FOLDER);

        PluginUtils.delete(testProjectOutputDirectory);
        TestProjectGen testProjectGen = new TestProjectGen();
        try {
            testProjectGen.genTestProject(groupId, artifactId, version,
                    testProjectOutputDirectory, testsDir,
                    timeout, caseCategoryPath, logger);
        } catch (Exception ex) {
            throw new MojoFailureException("Exception: ", ex);
        }
    }

    public static void main(String[] args) throws Exception {
        GenTestsMojo m = new GenTestsMojo();

        m.groupId = "com.gome.test.api";
        m.artifactId = "sample";
        m.version = "0.0.1";
        m.basedir = new File("/Users/zhangliang/SourceCode/sample/Branches/APITestV2.0.0/Helper");
        m.testsDir = new File("/Users/zhangliang/SourceCode/sample/Branches/APITestV2.0.0/TestCase");
        m.timeout = "30000";

        m.execute();
    }

}
