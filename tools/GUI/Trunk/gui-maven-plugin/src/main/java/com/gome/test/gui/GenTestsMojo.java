package com.gome.test.gui;


import com.gome.test.Constant;
import com.gome.test.plugin.Utils;
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
     * @parameter
     */
    private int testNGCount;

    /**
     * @parameter
     */
    private File testCaseDir;


    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Utils.checkParam(artifactId) == false)
            throw new MojoFailureException("artifactId:" + artifactId + "Exception: artifactId 不合法!!!", null);

        if(testCaseDir ==null || !testCaseDir.exists())
        {
            throw new MojoFailureException("testCaseDir 不存在！");
        }

        String testCasePath=testCaseDir.getAbsolutePath();

        Log logger = getLog();

        File testProjectOutputDirectory = new File(basedir.getParentFile(), Constant.TEST_PROJECT_FOLDER);
        Utils.delete(testProjectOutputDirectory);

        File testCaseDir = new File(String.format("%s%scase",testCasePath,File.separator));
        if(!testCaseDir.exists())
        {
            throw new MojoFailureException("case文件夹不存在！");
        }


        TestProjectGen testProjectGen = new TestProjectGen();
        try {
            if (null == timeout)
                timeout = "300000";

            testNGCount = testNGCount>0?this.testNGCount:Constant.TESTNGCOUNT;

            testProjectGen.genTestProject(groupId, artifactId, version,
                    testProjectOutputDirectory, testCaseDir,
                    timeout, logger,testNGCount);
        } catch (Exception ex) {
            Utils.delete(testProjectOutputDirectory);
            throw new MojoExecutionException("Exception: ", ex);
        }
    }

    public static void main(String[] args) throws Exception {
        GenTestsMojo m = new GenTestsMojo();

        m.groupId = "com.gome.test.gui";
        m.artifactId = "sample";
        m.version = "0.0.1";
        m.basedir = new File("/Users/zhangjiadi/Documents/GOME/SourceCode_svn/SourceCode/sample/Trunk/GUITest/Helper");
        m.timeout = "30000";
        m.testCaseDir=new File("/Users/zhangjiadi/Documents/GOME/SourceCode_svn/SourceCode/sample/Trunk/GUITest/TestCase");
        m.execute();
    }
}
