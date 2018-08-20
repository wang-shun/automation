package com.gome.test.gui;

import com.gome.test.Constant;
import org.apache.maven.plugin.*;

import java.io.File;


/**
 * @Mojo(name = "gui")
 * @goal genPage
 * @requiresDependencyResolution compile+runtime
 */
public class GenPageMojo extends AbstractMojo {
    /**
     * @parameter property="project.basedir"
     */
    private File basedir;

    /**
     * @parameter
     */
    private File testCaseDir;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {

            if(testCaseDir ==null || !testCaseDir.exists())
            {
                throw new MojoFailureException("testCaseDir 不存在！");
            }


            File pageFolder = new File(testCaseDir.getAbsolutePath(),
                    String.format("%spage", File.separator));

            if(!pageFolder.exists())
            {
                throw new MojoFailureException("page文件夹不存在！");
            }

            File dataFolder = new File(testCaseDir.getAbsolutePath(),
                    String.format("%sdata", File.separator));
            if(!dataFolder.exists())
            {
                throw new MojoFailureException("data文件夹不存在！");
            }

            GenPage.baseDirPath=basedir.getAbsolutePath();
            GenDomain.baseDirPath=basedir.getAbsolutePath();
            new GenPage().genByFolder(pageFolder);
            new GenDomain().genByFolder(dataFolder);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        GenPageMojo m = new GenPageMojo();
        m.basedir = new File("/Users/zhangjiadi/Documents/GOME/SourceCode_svn/SourceCode/sample/Trunk/GUITest/Helper");
        m.testCaseDir=new File("/Users/zhangjiadi/Documents/GOME/SourceCode_svn/SourceCode/sample/Trunk/GUITest/TestCase");
        m.execute();
    }
}