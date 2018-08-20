package com.gome.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Created by liangwei on 2016/10/27.
 */

/**
 * @Mojo(name="test")
 * @goal mymojo
 *
 * @requiresDependencyResolution test compile runtime
 * @requiresProject false
 */


public class myMojo extends AbstractMojo {

    /**
     * @parameter expression="${id}"
     */

    private String id="1";
    public void execute() throws MojoExecutionException{
        System.out.println(id);
    }

}