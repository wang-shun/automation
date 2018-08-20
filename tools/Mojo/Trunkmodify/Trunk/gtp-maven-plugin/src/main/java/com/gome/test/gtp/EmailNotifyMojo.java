package com.gome.test.gtp;

import com.gome.test.gtp.utils.Constant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.boot.test.SpringApplicationConfiguration;

/**
 * @Mojo(name = "email")
 * @goal email
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 * Created by lizonglin on 2016/1/22/0022.
 */
@SpringApplicationConfiguration(classes = Application.class)
public class EmailNotifyMojo extends AbstractMojo{
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

    /**
     * @parameter
     * expression="${splitTime}"
     */
    private Long splitTime;

    /**
     * @parameter
     * expression="${fileName}"
     */
    private String fileName;

    private TaskInfoBo taskInfoBo = new TaskInfoBo();
    private EmailNotifyBo emailNotifyBo = new EmailNotifyBo();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        /**
         * 根据所有拆分的TaskList状态决定是否发送邮件
         */
//        if (Constant.END_STATUS_SET.contains(taskInfoBo.getRealStatusByTaskListId(taskListId))) {
            try {
                emailNotifyBo.endMojoSendEmail(taskListId, taskType,splitTime,fileName);
//                emailNotifyBo.updateTaskListLogAfterEmail(taskListId);
            } catch (Exception e) {
                System.out.println("发送邮件失败！");
//                log.append(e.getMessage());
                e.printStackTrace();
            }
//        }
    }

    public static void main(String[] args){
        EmailNotifyMojo email = new EmailNotifyMojo();

            email.splitTime=20170220073531L;
            email.taskType=1;
            email.taskListId=1000006;
            email.fileName="CartBussiness2017-02-20-13H5958";
            try {
                email.execute();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

}

