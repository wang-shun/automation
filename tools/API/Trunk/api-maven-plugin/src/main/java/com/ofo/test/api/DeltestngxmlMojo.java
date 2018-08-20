package com.ofo.test.api;

/**
 * Created by liangwei-ds on 2016/12/7.
 */

import com.ofo.test.plugin.TestProjectArch;
import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.dom4j.DocumentException;


/**
 * @Mojo(name = "api")
 * @goal move
 * @requiresDependencyResolution compile+runtime
 */

public class DeltestngxmlMojo extends AbstractMojo {
    /**
     * @parameter property="project.basedir"
     */
    private File basedir;

    public DeltestngxmlMojo() {
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        File testProjectDir = new File(this.basedir.getParentFile(), "TestProject");
        TestProjectArch arch = new TestProjectArch(testProjectDir.getAbsolutePath());
        this.deltestng(arch);
    }

    private void deltestng(TestProjectArch arch) {
        String pomPath = arch.getPomPath();

        try {
            Doc e = new Doc(pomPath);
            e.delete();
            e.dumpTo(pomPath);
        } catch (DocumentException var4) {
            var4.printStackTrace();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public static void main(String[] args) {
        DeltestngxmlMojo mojo = new DeltestngxmlMojo();
        mojo.basedir = new File("D:\\liangwei\\workSpace\\code\\http\\Helper");

        try {
            mojo.execute();
        } catch (MojoExecutionException var3) {
            var3.printStackTrace();
        } catch (MojoFailureException var4) {
            var4.printStackTrace();
        }

    }
}