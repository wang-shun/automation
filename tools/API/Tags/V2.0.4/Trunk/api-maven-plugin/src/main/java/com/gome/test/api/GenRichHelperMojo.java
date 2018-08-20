package com.gome.test.api;

import com.gome.test.api.model.DependencyEnum;
import com.gome.test.utils.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


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
     * @parameter property="dependencies"
     */
    public List<Dependency> dependencies;


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
    private static final String TIMES ="[执行完毕，耗时:%s ms ]";
    public void execute() throws MojoExecutionException, MojoFailureException {
        long start = System.currentTimeMillis();
        if (clients != null) {
            for (String client : clients) {

                String[] jars = client.split(";");
                //取到配置的相匹配的的依赖包，暂时不去对应匹配。
                //如果去配置匹配包，则使用(getDependenciesByoptional来读取匹配配置)
                List<Dependency> dependencyList = dependencies; //getDependenciesByoptional(client);
                //加入相关的主包的信息
                ArrayList arrayListClientGroupId = getDependencyDetails(DependencyEnum.clientGroupId, dependencyList);
                arrayListClientGroupId.add(0, jars[0]);
                System.out.println("arrayListClientGroupId   Count:  " + arrayListClientGroupId.size());
                ArrayList arrayListclientArtifactId = getDependencyDetails(DependencyEnum.clientArtifactId, dependencyList);
                arrayListclientArtifactId.add(0, jars[1]);
                System.out.println("arrayListClientGroupId   Count:  " + arrayListclientArtifactId.size());
                ArrayList arrayListclientVersion = getDependencyDetails(DependencyEnum.clientVersion, dependencyList);
                arrayListclientVersion.add(0, jars[2]);

                generate(arrayListClientGroupId, arrayListclientArtifactId, arrayListclientVersion);
            }
        }
        long end = System.currentTimeMillis();
        long result=end-start;
        Logger.info(String.format(TIMES,result) );
    }

    /*
    * 根据配置的optional得到需要的包
    * ****/
    private List<Dependency> getDependenciesByoptional(String optional) {
        List<Dependency> dependencyList = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            System.out.println("是否一致:" + dependency.getType() + ";" + optional.toString());
            if (dependency.getType().equals(optional)) {
                dependencyList.add(dependency);
            }
        }
        return dependencyList;
    }


    public void generate(ArrayList clientGroupId, ArrayList clientArtifactId, ArrayList clientVersion) throws MojoFailureException {
        Logger.info("------------------------------------------------------------------------");
        for (int i = 0; i < clientGroupId.size(); i++) {
            String groupId = clientGroupId.get(i).toString();
            String artifactId = clientArtifactId.get(i).toString();
            String version = clientVersion.get(i).toString();
            Logger.info(String.format("generat Helper :\nlocalRepository:%s\ngroupId:%s\nartifactId:%s\nversion:%s\nbasedir:%s",
                    localRepository, groupId, artifactId, version, basedir.getAbsolutePath()));
        }

        Logger.info("------------------------------------------------------------------------");


        RicHelperGen gen = new RicHelperGen();
        try {
            gen.genTestProject(basedir, localRepository, clientGroupId, clientArtifactId, clientVersion, getLog());
        } catch (Exception ex) {
            throw new MojoFailureException("Exception: ", ex);
        }
    }

    private ArrayList getDependencyDetails(DependencyEnum dependencyEnum, List<Dependency> dependencyList) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < dependencyList.size(); i++) {
            switch (dependencyEnum) {
                case clientVersion:
                    result.add(dependencyList.get(i).getVersion());
                    break;
                case clientGroupId:
                    result.add(dependencyList.get(i).getGroupId());
                    break;
                case clientArtifactId:
                    result.add(dependencyList.get(i).getArtifactId());
                    break;

            }
        }
        return result;
    }
}
