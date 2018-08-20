package com.gome.test.gtp;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @Mojo(name = "gtp")
 * @goal endCheck
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 * Created by lizonglin on 2016/2/18/0018.
 */
public class EndCheckMojo extends AbstractMojo {
    /**
     * @parameter
     * expression="${taskListId}"
     */
    private int taskListId;

    /**
     * @parameter
     * expression="${taskType}"
     */
    private int taskType;

    private TaskInfoBo taskInfoBo = new TaskInfoBo();
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            taskInfoBo.endCheck(taskListId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
