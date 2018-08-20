package com.gome.test.gtp;

import com.gome.test.gtp.model.TaskList;
import com.gome.test.gtp.utils.Constant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.boot.test.SpringApplicationConfiguration;

/**
 * @Mojo(name = "gtp")
 * @goal start
 * @requiresDependencyResolution compile+runtime
 * jenkins slave 执行的第一个步骤。更新Task状态
 * 示例 mvn gtp:start -DtaskListId=1
 */
@SpringApplicationConfiguration(classes = Application.class)
public class StartMojo extends AbstractMojo {
    /**
     * @parameter
     * expression="${taskListId}"
     */
    private int taskListId;


    private TaskInfoBo taskInfoBo = new TaskInfoBo();

    public void execute() throws MojoExecutionException, MojoFailureException {


        getLog().info(String.format("国美自动化测试平台，自动化测试框架startMojo开始执行！taskListId:%s", taskListId));
        TaskList list = taskInfoBo.getTaskListByIdAndCheck(taskListId, Constant.JOB_SENT);

        taskInfoBo.updateTaskStatus(taskListId, Constant.JOB_SENT, Constant.JOB_RUNNING, "[StartMojo]");

        getLog().info("国美自动化测试平台，自动化测试框架startMojo执行完毕！taskListId:%s");

        Application.Close();
    }



}