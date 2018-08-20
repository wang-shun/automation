package com.gome.test.api;

import java.io.File;

import com.gome.test.api.model.Constant;
import com.gome.test.utils.Logger;
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

    private static final String TIMES ="[执行完毕，耗时:%s ms ]";


    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        long start = System.currentTimeMillis();
    	if(PluginUtils.checkParam(artifactId) == false)
    		throw new MojoFailureException("artifactId:"+artifactId+"Exception: artifactId 不合法!!!", null);
    	
        Log logger = getLog();
        String caseCategoryPath = System.getProperty(Constant.CASE_CATEGORY_PATH);
        File testProjectOutputDirectory = new File(basedir.getParentFile(), Constant.TEST_PROJECT_FOLDER);
        //helper/com/gome/test/api
        File helpJavaApiDirectory=new File(new File(basedir,Constant.MAIN_DIR_JAVA),Constant.HELPER_JAVAAPIPATH)  ;

        PluginUtils.delete(testProjectOutputDirectory);
        TestProjectGen testProjectGen = new TestProjectGen();
        try {
            testProjectGen.genTestProject(groupId, artifactId, version,
                    testProjectOutputDirectory, testsDir,
                    timeout, caseCategoryPath, logger,helpJavaApiDirectory);
        } catch (Exception ex) {
            PluginUtils.delete(testProjectOutputDirectory);
            throw new MojoExecutionException("Exception: ", ex);
        }
        long end = System.currentTimeMillis();
        long result=end-start;
        getLog().info(String.format(TIMES,result));
    }

    public static void main(String[] args) throws Exception {
        GenTestsMojo m = new GenTestsMojo();

        m.groupId = "com.gome.test.api";
        m.artifactId = "sample";




        m.version = "0.0.1";
        m.basedir = new File("D:\\GOME\\SourceCode\\sample\\Branches\\APITestV2.0.0\\Helper");
        m.testsDir = new File("D:\\GOME\\SourceCode\\sample\\Branches\\APITestV2.0.0\\TestCase");
        m.timeout = "30000";

        m.execute();
    }

}
