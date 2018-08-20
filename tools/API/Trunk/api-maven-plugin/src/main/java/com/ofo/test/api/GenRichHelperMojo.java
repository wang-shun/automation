package com.ofo.test.api;

import com.ofo.test.api.model.DependencyEnum;
import com.ofo.test.utils.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public static void main(String[] arg) throws Exception
    {
        GenRichHelperMojo gen=new GenRichHelperMojo();
        gen.basedir=new File("D:\\GOME\\SourceCode\\sample\\Branches\\APITestV2.0.0");
        gen.localRepository="D:\\MAVEN\\repository";

        ArrayList arrayListClientGroupId=new ArrayList();
        arrayListClientGroupId.add(0,"cn.com.ofo.finance.gfs.bill");
        ArrayList arrayListclientArtifactId =new ArrayList();
        arrayListclientArtifactId.add(0,"bill-service-interface");
        ArrayList arrayListclientVersion =new ArrayList();
        arrayListclientVersion.add(0,"0.0.1");

        String[] jars = {"cn.com.ofo.finance.gfs.bill", "bill-service-interface", "0.0.1"};

        Map<String, List<String>> clientInterfaceMethodMap = gen.getClientInterfaceMethod(jars);
        gen.generate(arrayListClientGroupId, arrayListclientArtifactId, arrayListclientVersion, clientInterfaceMethodMap);

    }
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
                arrayListClientGroupId.add(0, jars[0].trim());
                System.out.println("arrayListClientGroupId   Count:  " + arrayListClientGroupId.size());
                ArrayList arrayListclientArtifactId = getDependencyDetails(DependencyEnum.clientArtifactId, dependencyList);
                arrayListclientArtifactId.add(0, jars[1].trim());
                System.out.println("arrayListClientGroupId   Count:  " + arrayListclientArtifactId.size());
                ArrayList arrayListclientVersion = getDependencyDetails(DependencyEnum.clientVersion, dependencyList);
                arrayListclientVersion.add(0, jars[2].trim());

                Map<String, List<String>> clientInterfaceMethodMap = getClientInterfaceMethod(jars);

                generate(arrayListClientGroupId, arrayListclientArtifactId, arrayListclientVersion, clientInterfaceMethodMap);
            }
        }
        long end = System.currentTimeMillis();
        long result=end-start;
        Logger.info(String.format(TIMES,result) );
        Logger.info("localRepository:"+localRepository);
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


    public void generate(ArrayList clientGroupId, ArrayList clientArtifactId, ArrayList clientVersion, Map<String, List<String>> clientInterfaceMethodMap) throws MojoFailureException {
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
            gen.genTestProject(basedir, localRepository, clientGroupId, clientArtifactId, clientVersion, clientInterfaceMethodMap, getLog());
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

    //根据每个client节点内容按;截断后的数组，获取进行interface:method过滤的map
    private Map<String, List<String>> getClientInterfaceMethod(String[] clients) {
        Map<String, List<String>> clientInterfaceMethodMap = new HashMap<String, List<String>>();
        List<String> interfaceMethods = new ArrayList<String>();
        if (3 == clients.length) {
            interfaceMethods.add("*:*");
            clientInterfaceMethodMap.put(String.format("%s-%s", clients[0], clients[1]), interfaceMethods);
        } else if (clients.length > 3){
            for (int i = 3; i < clients.length; i++) {
                if (clients[i].trim().contains(":"))
                    interfaceMethods.add(clients[i].trim());
            }
            clientInterfaceMethodMap.put(String.format("%s-%s", clients[0], clients[1]), interfaceMethods);
        }

        return clientInterfaceMethodMap;
    }
}
