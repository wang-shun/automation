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

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File resourceFolder = new File(basedir.getAbsolutePath(),
                    String.format("%s%s%s%s%s%s%scom%sgome%stest%sgui%spage",
                            File.separator, Constant.SRC, File.separator, Constant.MAIN, File.separator, Constant.RESOURCES,File.separator,
                            File.separator,File.separator,File.separator,File.separator));

            new GenPage().genByFolder(resourceFolder);
            new GenDomain().genByFolder(resourceFolder);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        GenPageMojo m = new GenPageMojo();
        m.basedir = new File("/Users/zhangliang/SourceCode/sample/Trunk/GUITest/Helper/");
        m.execute();
    }
}