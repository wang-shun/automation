package com.gome.test.gtp;

import com.gome.test.gtp.model.CaseRunTime;
import com.gome.test.gtp.model.TaskList;
import com.gome.test.gtp.utils.Constant;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Mojo(name = "gtp")
 * @goal end
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 * jenkins slave 执行的第最后步骤。更新task状态，并保存结果至mongodb
 * 示例  mvn gtp:end -DtaskListId=1 -DtaskType=22
 */
@SpringApplicationConfiguration(classes = Application.class)
public class EndMojo extends AbstractMojo {
    /**
     * @parameter
     * expression="${project.basedir}"
     */
    private File basedir;

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

    private ReportBo reportBo = new ReportBo();
    private TaskInfoBo taskInfoBo = new TaskInfoBo();

    public void execute() throws MojoExecutionException, MojoFailureException {
        StringBuffer log = new StringBuffer("[EndMojo] ");
        System.out.println(String.format("---------------------EndMojo begin taskListId : %d---------------------", taskListId));

        int status = Constant.JOB_COMPLETED;
        try {
                saveTaskReport(log);
        } catch (Exception ex) {
            System.out.print(ex.getStackTrace());
            log.append(ex.getMessage());
            status = Constant.JOB_ERROR;
        }

        taskInfoBo.updateTaskStatus(this.taskListId, Constant.JOB_RUNNING, status, log.toString());

        Application.Close();
        System.out.println("---------------------EndMojo end---------------------");
    }

    private void saveTaskReport(StringBuffer log) throws Exception {
        System.out.println("---------------------saveTaskReport begin---------------------");

        TaskList list = taskInfoBo.getTaskListByIdAndCheck(this.taskListId, Constant.JOB_RUNNING);

        File file = new File(basedir.getParent(), Constant.JSON_REPORT);
        if (file.exists()) {
            String json = FileUtils.fileRead(file,"UTF-8");
            System.out.println(String.format("%s 保存开始", file.getAbsolutePath()));

            json = json.replace("\"taskId\": 0", String.format("\"taskId\": %s", list.getTaskID()))
                    .replace("\"taskType\": 0", String.format("\"taskType\": %s", taskType))
                    .replace("\"splitTime\": 0", String.format("\"splitTime\": %d", list.getSplitTime().getTime()));

            reportBo.insert(json, Constant.TASK_REPORT);
            String info = String.format("%s 保存完毕\n", file.getAbsolutePath());
            log.append(info);
            System.out.println(info);

            saveLastRunTime(json);
            info = "LastRunTime保存完毕";
            log.append(info);

            System.out.println(info);
        } else {
            throw new Exception(String.format("%s 不存在", file.getAbsolutePath()));
        }

        System.out.println("---------------------saveTaskReport end---------------------");
    }

    private void saveLastRunTime(String json) {
        List<CaseRunTime> caseRunTimes = new ArrayList<CaseRunTime>();
        DBObject dbObject = (DBObject) JSON.parse(json);
        int date = Integer.valueOf(dbObject.get("date").toString());
        BasicDBList inCaseName = new BasicDBList();

        for (Object c : ((BasicDBList) dbObject.get("details"))) {
            DBObject lastCase = (DBObject) c;
            if (lastCase.get("testResult").toString().equals("pass")) {
                CaseRunTime caseRunTime = new CaseRunTime();
                caseRunTime.setCaseName(lastCase.get("testCaseName").toString());
                caseRunTime.setDuration(Long.valueOf(lastCase.get("duration").toString()));
                caseRunTime.setLastrunDate(date);
                caseRunTimes.add(caseRunTime);

                if (inCaseName.contains(caseRunTime.getCaseName()) == false)
                    inCaseName.add(caseRunTime.getCaseName());
            }
        }

        DBObject in = new BasicDBObject();
        in.put("$in", inCaseName);
        DBObject query = new BasicDBObject();
        query.put("caseName", in);

        WriteResult result = reportBo.delete(Constant.CASE_RUNTIME, query);
        reportBo.insertList(caseRunTimes);
    }

}