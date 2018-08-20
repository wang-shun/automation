package com.gome.test.api;

import com.gome.test.utils.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;

/**
 * @Mojo(name = "api")
 * @goal genRichHelper
 */
public class GenRichHelperMojo extends AbstractMojo {
    /**
     * Helper的父目录
     *
     * @parameter property="project.basedir"
     */
    public File basedir;

    /**
     * @parameter property="settings.localRepository"
     */
    public String localRepository;

    /**
     * @parameter property="clients"
     */
    public String[] clients;

    /**
     * mvn api:genhelper -DclientJarGroupId=clientJarGroupId
     *
     * @parameter
     */
    private String timeout;

    public void execute() throws MojoExecutionException, MojoFailureException {
        for (String client : clients) {
            String[] jars = client.split(";");
            generate(jars[0], jars[1], jars[2]);
        }
    }

    public void generate(String clientGroupId, String clientArtifactId, String clientVersion) throws MojoFailureException {
        Logger.info("------------------------------------------------------------------------");
        Logger.info(String.format("generat Helper :\nlocalRepository:%s\ngroupId:%s\nartifactId:%s\nversion:%s\nbasedir:%s",
                localRepository, clientGroupId, clientArtifactId, clientVersion, basedir.getAbsolutePath()));
        Logger.info("------------------------------------------------------------------------");

        RicHelperGen gen = new RicHelperGen();
        try {
            gen.genTestProject(basedir, localRepository, clientGroupId, clientArtifactId, clientVersion, getLog());
        } catch (Exception ex) {
            throw new MojoFailureException("Exception: ", ex);
        }
    }
}
