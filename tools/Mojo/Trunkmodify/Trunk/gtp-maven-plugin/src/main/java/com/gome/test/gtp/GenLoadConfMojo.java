package com.gome.test.gtp;

/**
 * Created by lizonglin on 2016/1/22/0022.
 */

import com.gome.test.gtp.Load.LoadConfBo;
import com.gome.test.gtp.dao.LoadConfDao;
import com.gome.test.gtp.dao.LoadSceneDao;
import com.gome.test.gtp.dao.TaskListDao;
import com.gome.test.gtp.model.LoadConf;
import com.gome.test.gtp.model.LoadScene;
import com.gome.test.gtp.model.TaskList;
import com.gome.test.gtp.utils.Constant;
import com.gome.test.utils.FileUtils;
import com.gome.test.utils.Logger;
import com.gome.test.utils.SvnUtils;
import com.gome.test.utils.XmlUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.tmatesoft.svn.core.SVNURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Mojo(name = "gtp")
 * @goal genLoadConf
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 */
public class GenLoadConfMojo extends AbstractMojo {
    /**
     * @parameter
     * expression="${taskListId}"
     */
    private int taskListId;

    /**
     * jenkins工作区 工程根目录
     *
     * @parameter
     * expression="${workDir}"
     */
    private String workDir;

    private static String fs = File.separator;

    private LoadConfDao loadConfDao;
    private LoadSceneDao loadSceneDao;
    private TaskListDao taskListDao;

    public GenLoadConfMojo() {
        loadConfDao = (LoadConfDao) Application.getBean(LoadConfDao.class);
        loadSceneDao = (LoadSceneDao) Application.getBean(LoadSceneDao.class);
        taskListDao = (TaskListDao) Application.getBean(TaskListDao.class);
    }

    private LoadConfBo loadConfBo = new LoadConfBo();
    private FileUtils fileUtils = new FileUtils();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            /**
             * 加载load.properties
             */
            Properties props = loadConfBo.getPropsByResource("load.properties");

            /**
             * 获取对应的 LoadConf
             */
            List<LoadConf> loadConfList = loadConfDao.getLoadConfByTaskListId(taskListId);
            if (loadConfList.size() == 0 || loadConfList == null) {
                throw new Exception("ID为" + taskListId + "的TaskList找不到对应的LoadConf");
            }
            LoadConf loadConf = loadConfList.get(0);

            TaskList taskList = taskListDao.get(taskListId);

            /**
             * 获取对应的 LoadScene
             * 如果是Test获取LoadScene表里的第一个TestLoadScene
             * 不是Test的情况根据使用的LoadSceneName获取
             */
            LoadScene loadScene;
            if (taskList != null) {
                if (taskList.getTaskFrom().equals(Constant.ENQUEUE_BY_Test)) {
                    List<LoadScene> loadSceneList = loadSceneDao.getFirstTestLoadScene();
                    if (loadSceneList.size() == 0) {
                        throw new Exception("[genLoadConf] ID为" + taskListId + "的TaskList找不到Test LoadScene");
                    }
                    loadScene = loadSceneList.get(0);
                } else {
                    List<LoadScene> loadSceneList = loadSceneDao.getLoadSceneByTaskListId(taskListId);
                    if (loadSceneList.size() == 0) {
                        throw new Exception("[genLoadConf] ID为" + taskListId + "的TaskList找不到对应的LoadScene");
                    }
                    loadScene = loadSceneList.get(0);
                }
            } else {
                throw new Exception("[genLoadConf] ID为" + taskListId + "的TaskList 未找到");
            }


            /**
             * 需要生成或修改的文件路径
             */
            String rootPomXmlPath = workDir + fs + props.getProperty("gtp.load.conf.file.root.pom");
            String argsXmlPath = workDir + fs + props.getProperty("gtp.load.innerdir") + fs + props.getProperty("gtp.load.conf.file.args");
            String userXmlPath = workDir + fs + props.getProperty("gtp.load.innerdir") + fs + props.getProperty("gtp.load.conf.file.user");
            String jmxXmlPath = workDir + fs + props.getProperty("gtp.load.innerdir") + fs + props.getProperty("gtp.load.conf.file.jmx");
            String libPath = workDir + fs + props.getProperty("gtp.load.innerdir") + fs + props.getProperty("gtp.load.libdir");
            String tmplibPath = workDir + fs + props.getProperty("gtp.load.innerdir") + fs + props.getProperty("gtp.load.tmplibdir");

            /**
             *=========================================================================================================================
             */

            /**
             * ================================== 1、修改root pom.xml 的<environment></environment>环境节点==================================
             */
            InputStream rootPomTplIs = this.getClass().getResourceAsStream("/" + props.getProperty("gtp.load.conf.tpl.root.pom"));
            File rootPomFile = new File(rootPomXmlPath);
            FileUtils.copyInputStream2File(rootPomTplIs, rootPomFile, true);//如果已存在则先删再新建复制

            XmlUtils.modifySingleNodeContent(workDir + fs + "pom.xml", "environment", 0, loadConf.getEnv());

            /**
             * ================================== 2、修改ExecuteTest/pom.xml 的<template></template>环境节点==================================
             */
            XmlUtils.modifySingleNodeContent(workDir + fs + props.getProperty("gtp.load.innerdir") + fs + "pom.xml","template",0,loadScene.isTemplate()?loadScene.getName():"");

            /**
             * ================================== 3、复制args-tpl.xml到ExecuteTest文件夹下的args.xml，并修改args.xml文件==================================
             */
            InputStream argTplIs = this.getClass().getResourceAsStream("/" + props.getProperty("gtp.load.conf.tpl.args"));
            File argTargetFile = new File(argsXmlPath);
            FileUtils.copyInputStream2File(argTplIs, argTargetFile, true);//如果已存在则先删再新建复制

            List<String> argsScriptSvnList = Arrays.asList(loadConf.getScriptSvn().split(","));
            Document argsXmlDoc = XmlUtils.parseXmlFile2Doc(argsXmlPath);
            NodeList parentNodeList = argsXmlDoc.getElementsByTagName(props.getProperty("gtp.load.args.parenttag"));
            if (parentNodeList.getLength() == 0) {
                throw new Exception(argsXmlPath + "中未找到父节点<" + props.getProperty("gtp.load.args.parenttag") + ">");
            }
            Node parentNode = parentNodeList.item(0);
            for (String scriptSvn : argsScriptSvnList) {
                Node svnNode = argsXmlDoc.createElement(props.getProperty("gtp.load.args.svntag"));
                svnNode.setTextContent(scriptSvn);
                Element newScriptNode = argsXmlDoc.createElement(props.getProperty("gtp.load.args.scripttag"));
                newScriptNode.appendChild(svnNode);
                parentNode.appendChild(newScriptNode);
            }
            XmlUtils.reSaveXmlFile(argsXmlDoc, argsXmlPath);

            /**
             * ==================================4、新建jmxs.xml文件，并复制jmxContent内容==================================
             */
            File jmxsFile = new File(jmxXmlPath);
            String jmxContent = loadConf.getJmxContent();
            FileUtils.appendContent2File(jmxsFile, jmxContent);//如果已存在则先删除再新建复制

            /**
             * ==================================5、复制user-tpl.xml文件到ExecuteTest下的user.xml，并修改user.xml内容==================================
             */
            InputStream userTplIs = this.getClass().getResourceAsStream("/" + props.getProperty("gtp.load.conf.tpl.user"));
            File targetUserFile = new File(userXmlPath);
            FileUtils.copyInputStream2File(userTplIs, targetUserFile, true);//如果已存在则先删除再新建复制
            Document userXmlDoc = XmlUtils.parseXmlFile2Doc(userXmlPath);
            loadConfBo.insertLoadScene2UserXml(props, loadScene, userXmlDoc, userXmlPath);

            /**
             * ==================================6、源代码打包，放入lib文件夹下==================================
             */
            List<String> sourceSvnList = Arrays.asList(loadConf.getSourceSvn().split(","));

            //清空tmplibPath中的文件夹
            File tmpLib = new File(tmplibPath);
            if (!tmpLib.exists()) {//如果tmplib不存在则新建tmplib文件夹
                tmpLib.mkdir();
            }
            File lib = new File(libPath);//如果lib不存在则新建tmplib文件夹
            if (!lib.exists()) {
                lib.mkdir();
            }
            String[] subLibDir = new File(tmplibPath).list();
            for (String subDir : subLibDir) {//删除tmplib中的所有子文件夹
                fileUtils.deleteDir(new File(tmplibPath + fs + subDir));
            }
            for (int i = 0; i < sourceSvnList.size(); i++) {//checkout源代码，每一条SVN地址下载到一个文件夹i
                String sourceSvn = sourceSvnList.get(i);
                URL url = new URL(sourceSvn);
                SvnUtils.export(SVNURL.create(url.getProtocol(), "", url.getHost(), url.getPort(), url.getPath(), true), tmplibPath + fs + i, props.getProperty("gtp.load.svn.username"), props.getProperty("gtp.load.svn.password"));
            }
        } catch (Exception ex) {
            Logger.error("生成Load配置文件失败！");
            Logger.error(ex.getMessage());
        }

    }
}
