package com.gome.test.gtp;

import com.gome.test.gtp.dao.ReplaceInfoDao;
import com.gome.test.gtp.dao.TaskListDao;
import com.gome.test.gtp.jenkins.JenkinsBo;
import com.gome.test.gtp.model.ReplaceInfo;
import com.gome.test.gtp.model.TaskList;
import com.gome.test.gtp.utils.Constant;
import com.gome.test.gtp.utils.Util;
import com.gome.test.utils.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizonglin on 2016/4/6/0006.
 */
/**
 * @Mojo(name = "gtp")
 * @goal genGuiConf
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 */
public class GenGuiConfMojo extends AbstractMojo {
    /**
     * @parameter
     * expression="${taskListId}"
     */
    private int taskListId;

    /**
     * @parameter
     * expression="${workDir}"
     */
    private String workDir;

    private static String fs = File.separator;

    private TaskListDao taskListDao;
    private ReplaceInfoDao replaceInfoDao;
    private JenkinsBo jenkinsBo;

    GenGuiConfMojo() {
        taskListDao = (TaskListDao) Application.getBean(TaskListDao.class);
        replaceInfoDao = (ReplaceInfoDao) Application.getBean(ReplaceInfoDao.class);
        jenkinsBo = (JenkinsBo) Application.getBean(JenkinsBo.class);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        TaskList taskList = taskListDao.get(taskListId);

        int taskId = taskList.getTaskID();

        List<ReplaceInfo> replaceInfoList = replaceInfoDao.getReplaceInfoListByTaskId(taskId);
        Map<String, Map<String, String>> fileReplaceMap = new HashMap<String, Map<String, String>>();

        //将浏览器名称转换成GUI规定的关键字
        String browser = taskList.getBrowser().toLowerCase();//每个TaskList只对应一个Browser
//        if (browser.startsWith("ie"))
//            browser = "ie";

        //默认的需要替换的字段
        Map<String, String> browserMap = new HashMap<String, String>();
        browserMap.put(Constant.GUI_SELENIUM_BROWSER, browser);
        fileReplaceMap.put(Constant.GUI_SELENIUM_PROPERTIES, browserMap);

        //ReplaceInfo中需要替换的字段
        if (replaceInfoList != null && replaceInfoList.size() > 0) {
            //生成replacemap <filename, <key, value>>
            for (ReplaceInfo replaceInfo : replaceInfoList) {
                if (fileReplaceMap.keySet().contains(replaceInfo.getFileName())) {//文件名已包含
                    fileReplaceMap.get(replaceInfo.getFileName()).put(replaceInfo.getReplacekey(), replaceInfo.getReplaceValue());//覆盖
                } else {//文件名不包含
                    Map<String, String> replaceMap = new HashMap<String, String>();
                    replaceMap.put(replaceInfo.getReplacekey(), replaceInfo.getReplaceValue());
                    fileReplaceMap.put(replaceInfo.getFileName(), replaceMap);
                }
            }
        }

        String resourcesPath = workDir + fs + "Helper" + fs + "src" + fs + "main" + fs + "resources" + fs;
        //替换文件中的字段
        for (String fileName : fileReplaceMap.keySet()) {
            try {
                InputStream is = new FileInputStream(new File(resourcesPath + fileName));
                String fileContent = Util.replacePropsFromIs(fileReplaceMap.get(fileName), is);
                FileUtils.copyInputStream2File(new ByteArrayInputStream(fileContent.getBytes()), new File(resourcesPath + fileName), true);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
