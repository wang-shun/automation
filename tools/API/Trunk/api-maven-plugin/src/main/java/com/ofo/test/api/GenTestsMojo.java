package com.ofo.test.api;

import java.io.File;

import com.ofo.test.Constant;
import com.ofo.test.plugin.Utils;
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
     * @parameter
     */
    private int testNGCount;

    private static final String TIMES = "[执行完毕，耗时:%s ms ]";


    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        long start = System.currentTimeMillis();
        if (Utils.checkParam(artifactId) == false)
            throw new MojoFailureException("artifactId:" + artifactId + "Exception: artifactId 不合法!!!", null);

        Log logger = getLog();
        String caseCategoryPath = System.getProperty(Constant.CASE_CATEGORY_PATH);
        File testProjectOutputDirectory = new File(basedir.getParentFile(), Constant.TEST_PROJECT_FOLDER);

        Utils.delete(testProjectOutputDirectory);
        TestProjectGen testProjectGen = new TestProjectGen();
        try {
            testNGCount = this.testNGCount>0?testNGCount : Constant.TESTNGCOUNT;

            testProjectGen.genTestProject(groupId, artifactId, version,
                    testProjectOutputDirectory, testsDir,
                    timeout, caseCategoryPath, logger,testNGCount);
        } catch (Exception ex) {
            Utils.delete(testProjectOutputDirectory);
            throw new MojoExecutionException("Exception: ", ex);
        }
        long end = System.currentTimeMillis();
        long result = end - start;
        getLog().info(String.format(TIMES, result));
    }

    public static void main(String[] args) throws Exception {
        GenTestsMojo m = new GenTestsMojo();

        m.groupId = "com.ofo.test.api";
        m.artifactId = "sample";


        m.version = "0.0.1";
        m.basedir = new File("//Users/liangwei//workspace//liangwei//Source//sample//Trunk//APITest//Helper");
        m.testsDir = new File("/Users/liangwei/workspace/liangwei/Source/Framwork/GM/API-sample/LiveMonitoring/http/TestCase");
        m.timeout = "30000";

        m.execute();
    }

}
