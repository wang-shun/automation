package com.gome.test.gtp;

/**
 * Created by wjx on 16/8/11.
 */

import com.gome.test.gtp.dao.TaskListDao;

import com.gome.test.gtp.utils.Constant;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;

import java.io.File;

/**
 * @Mojo(name = "gtp")
 * @goal endOptsJob
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 * jenkins slave 执行的第最后步骤。更新job状态，并保存结果至mongodb
 * 示例  mvn gtp:endOptsJob -DjobName=1
 */
@SpringApplicationConfiguration(classes = Application.class)
public class EndOptsJobMojo extends AbstractMojo {

    private TaskListDao responseJob = Application.getBean(TaskListDao.class);
    /**
     * @parameter
     * expression="${project.basedir}"
     */
    private File basedir;

    /**
     * @parameter
     * expression="${jobName}"
     */
    private String jobName;

    /**
     * @parameter
     * expression="${taskType}"
     */
    private int taskType = 3;

    private ReportBo reportBo = new ReportBo();

    public void execute() throws MojoExecutionException, MojoFailureException {
        StringBuffer log = new StringBuffer("[EndJobMojo] ");
        System.out.println(String.format("---------------------EndMojo begin jobName : %s---------------------", jobName));

        int status = Constant.JOB_COMPLETED;
        try {
            //save job report to mongoDB.
            saveTaskReport(log);
        } catch (Exception ex) {
            System.out.print("************************"+ ex.getStackTrace().toString());
            ex.printStackTrace();
            log.append(ex.getMessage());
            status = Constant.JOB_ERROR;
        }

        //update job status.
        if(responseJob != null && jobName!=null && jobName.length()>0) {
            responseJob.updateTaskJobStuatus(jobName, "end jenkins execution and save report.", status);
        }else{
            System.out.println("---------------responseJob is null or jobName is empty-----------------");
        }

        Application.Close();
        System.out.println("---------------------EndMojo end---------------------");
    }

    /**
     * save report to mogoDB.
     * @param log
     * @throws Exception
     */
    private void saveTaskReport(StringBuffer log) throws Exception {
        System.out.println("---------------------saveTaskReport begin---------------------");
        File file = new File(basedir.getParent(), Constant.JSON_REPORT);
        if (file.exists()) {
            String json = FileUtils.fileRead(file, "UTF-8");
            System.out.println(String.format("%s 保存开始", file.getAbsolutePath()));

            json = json.replace("\"taskId\": 0", String.format("\"taskId\": \"%s\"", jobName))
                    .replace("\"taskType\": 0", String.format("\"taskType\": %s", taskType));
            //persist the job report to mongoDB.

            System.out.println("=======>insert to mongoDB is : " + json);
            reportBo.insert(json, Constant.JOB_REPORT);
            String info = String.format("%s 保存完毕\n", file.getAbsolutePath());
            log.append(info);
            System.out.println(info);

        } else {
            throw new Exception(String.format("%s 不存在", file.getAbsolutePath()));
        }

        System.out.println("---------------------saveTaskReport end---------------------");
    }

}
