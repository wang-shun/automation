package com.gome.test.api;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.rule.Pattern;

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
    private String messageClient;

    /**
     * @parameter
     */
    private String timeout;

    public final static String TestProjectFolder = "TestProject";
    
    /**
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
    	if(checkParam(artifactId) == false)
    		throw new MojoFailureException("Exception: artifactId 不合法!!!", null);
    	
        Log logger = getLog();
        String caseCategoryPath = System.getProperty("CaseCategoryPath");
        File testProjectOutputDirectory = new File(basedir.getParentFile(), TestProjectFolder);

        APIUtils.delete(testProjectOutputDirectory);
        TestProjectGen testProjectGen = new TestProjectGen();
        try {
            testProjectGen.genTestProject(groupId, artifactId, version,
                    testProjectOutputDirectory, testsDir, messageClient,
                    timeout, caseCategoryPath, logger);
        } catch (Exception ex) {
            throw new MojoFailureException("Exception: ", ex);
        }
    }
    
    private boolean checkParam(String str)
    {
    	java.util.regex.Pattern  pattern = java.util.regex.Pattern.compile("^[A-Za-z0-9]+$");
    	java.util.regex.Matcher matcher = pattern.matcher(str);
    	return matcher.matches();
    }
}
