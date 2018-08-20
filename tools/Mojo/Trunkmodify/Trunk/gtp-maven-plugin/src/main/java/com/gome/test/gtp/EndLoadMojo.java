package com.gome.test.gtp;


import com.gome.test.gtp.Load.*;
import com.gome.test.gtp.model.*;
import com.gome.test.gtp.utils.Constant;

import com.gome.test.utils.Logger;
import com.gome.test.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @Mojo(name = "gtp")
 * @goal endLoad
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 * jenkins slave 执行的第最后步骤。更新task状态，并保存结果至mongodb
 * 示例  mvn gtp:end -DtaskListId=1 -DtaskType=22
 */
@SpringApplicationConfiguration(classes = Application.class)
public class EndLoadMojo extends AbstractMojo {

    /**
     * @parameter expression="${taskListId}"
     */
    private int taskListId;

    /**
     * @parameter expression="${taskType}"
     */
    private int taskType;

    /**
     * @parameter
     */
    private String resultCSVPath;

    /**
     * @parameter
     */
    private String environment;

    /**
     * @parameter default-value = "true"
     */
    private Boolean isTestDB;

    /**
     * @parameter expression="${project.build.directory}"
     */
    private File targetdir;


    public static void main(String[] args) throws Exception {


//        System.setProperty("logroot","/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/jmt_file/jmt_test/ExecuteTest/target/");
        EndLoadMojo mojo = new EndLoadMojo();
//        mojo.resultCSVPath = "/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/LoadStandardProject/LoadStandardProject/ExecuteTest/target/results/result.csv";
//        mojo.targetdir=new File("/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/LoadStandardProject/LoadStandardProject/ExecuteTest/target/results");
        mojo.resultCSVPath = "/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/jmt_file/jmt_test/ExecuteTest/target/results/OMS/results/result.csv";
        mojo.targetdir = new File("/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/jmt_file/jmt_test/ExecuteTest/target/results/OMS/");
        mojo.environment = "UAT";
        mojo.taskListId = 5037;
        mojo.taskType = 3;
        mojo.isTestDB = true;
        mojo.execute();

    }

    private Log endLoadLogger;
    private Log errLoadLogger;


    public void execute() throws MojoExecutionException, MojoFailureException {
        initLog();
        writeLog("国美自动化性能测试平台，自动化测试框架EndLoad开始执行！taskListId:" + taskListId);
        TaskList list = null;
        String dbTablename = "";
        String dbJmtAggReportname = "";

        try {

            if (taskListId == 0) {
                MOJOErrorLog("taskListId=0 ！不再继续执行！");
                throw new MojoExecutionException("taskListId=0 ！不再继续执行！");
            }

            if (resultCSVPath == null) {
                MOJOErrorLog("resultCSVPath参数为NULL ！不再继续执行！");
                throw new MojoExecutionException("resultCSVPath is null！");
            }

            File file = new File(resultCSVPath);
            if (!file.isFile() || !file.exists()) {
                MOJOErrorLog("resultCSVPath不存在！不再继续执行！");
                throw new MojoExecutionException("No result file！");
            }

            StringBuffer log = new StringBuffer("[EndLoadMojo] ");
            int status = Constant.JOB_COMPLETED;
            TaskInfoBo taskInfoBo = new TaskInfoBo();
            writeLog("获取taskList");
            list = taskInfoBo.getTaskListById(taskListId);

            if (list == null) {
                MOJOErrorLog(String.format("id 为 %d 的 taskList 不存在！！",
                        taskListId));
                throw new Exception(String.format("id 为 %d 的 taskList 不存在！！",
                        taskListId));
            }

            if (list != null && list.getTaskState() != Constant.JOB_RUNNING) {
                MOJOErrorLog(String.format("id 为 %d 的 taskList 状态不是 %d 而是 %d ！！",
                        taskListId, Constant.JOB_RUNNING, list.getTaskState()));
                throw new Exception(String.format("id 为 %d 的 taskList 状态不是 %d 而是 %d ！！",
                        taskListId, Constant.JOB_RUNNING, list.getTaskState()));
            }

            ConcurrentLinkedQueue<ReportJMT> queue = new ConcurrentLinkedQueue<ReportJMT>();
            LoadSave loadSave = null;
            try {

                isTestDB = list.getTaskFrom().toUpperCase().equals(Constant.ENQUEUE_BY_Test.toUpperCase());
                dbTablename = isTestDB ? Constant.TESTJMT_REPORT : Constant.JMT_REPORT;
                dbJmtAggReportname = isTestDB ? Constant.TESTJMTAGG_REPORT : Constant.JMTAGG_REPORT;
                writeLog("dbTablename：" + dbTablename);
                //拆分文件
                FileSplit fileSplit = new FileSplit(environment);
                loadSave = new LoadSave(environment, dbTablename, dbJmtAggReportname);
                writeLog("开始拆分文件，" + file.getPath());
                int tempFileLength = fileSplit.split(file);
                if (tempFileLength > 0) {
                    writeLog("开始更新数据库已经存在的数据状态");
                    //更新数据库
                    executeCheckData(fileSplit.getData());
                    LoadLogUtil.setlogDetailHashMap(fileSplit.getData());
                    //插入数据
                    writeLog("开始插入数据！");
                    loadSave.save(file, tempFileLength, list.getTaskID(), taskListId, queue);
                }

                log.append(" LoadTest Complete");


            } catch (Exception ex) {
                if (list == null) {
                    MOJOErrorLog("获取任务列表失败！原因：" + ex.getMessage());
                }
                System.out.print(ex.getStackTrace());
                log.append(ex.getMessage());
                status = Constant.JOB_ERROR;
            }


            try {
                if (!queue.isEmpty()) {

                    //开始计算聚合报告存入db
                    List<JmtAGGReport> aggReportList = getJMTReportList(queue);

                    for (JmtAGGReport aggReport : aggReportList) {
                        //删除原来已经入库的数据
                        loadSave.deleteAGReport(aggReport.getTaskID(),aggReport.getTaskListID(),aggReport.getGrpThreads(), aggReport.getSceneName(),aggReport.getResultVersion(),aggReport.getTimeVersion(),aggReport.getSmallSceneName(),aggReport.getLabelName());
                        //将数据入库
                        loadSave.save(aggReport.getJmtAGGReport());
                    }
                }


            } catch (Exception ex) {
                System.out.print(ex.getStackTrace());
                log.append(ex.getMessage());
                status = Constant.JOB_ERROR;
            }


            writeLog("更新数据库状态！");
            taskInfoBo.updateTaskStatus(this.taskListId, Constant.JOB_RUNNING, status, log.toString());

        } catch (Exception ex) {
            MOJOErrorLog("插件停止执行，" + ex.getMessage());
        }
        saveLog(list, isTestDB);
        Application.Close();
        System.out.println("---------------------EndLoadMojo end---------------------");
        writeLog("国美自动化性能测试平台，自动化测试框架EndLoad执行完毕！taskListId:" + taskListId);
    }

    //更新已经存在的数据
    private void executeCheckData(List<String> dataList) throws Exception {

        for (String data : dataList) {
            LoadDataForCheck loadData = new LoadDataForCheck(data);
            update(loadData);
        }

    }

    private void update(LoadDataForCheck model) {
        Query query = new Query();
        query.addCriteria(Criteria.where(model.getSceneName_name()).is(model.getSceneName()));
        query.addCriteria(Criteria.where(model.getResultVersion_name()).is(model.getOnlyResultVersion()));

        if (!StringUtils.isEmpty(model.getOnlyTimeVersion()))
            query.addCriteria(Criteria.where(model.getTimeVersion_name()).is(model.getOnlyTimeVersion()));

        query.addCriteria(Criteria.where(model.getSmallSceneName_name()).is(model.getSmallSceneName()));
        query.addCriteria(Criteria.where(model.getEnvironment_name()).is(model.getEnvironment()));
        query.addCriteria(Criteria.where(model.getTemplate_name()).is(model.getTemplate()));
        ReportBo reportBo = new ReportBo();
        reportBo.updateEnable(query);
    }



    //计算聚合报告
    public List<JmtAGGReport> getJMTReportList(ConcurrentLinkedQueue queue) {
        List<ReportJMT> jmtReports = new ArrayList<ReportJMT>();

        while (true) {
            if (queue.isEmpty())
                break;
          ReportJMT report =(ReportJMT) queue.poll();

            jmtReports.add(report);
        }


        return new ReportBo().getJMTReportList(jmtReports);


    }




    private void initLog() {
        if (targetdir == null) {
            Logger.info("targetdir is null!");
            File file = new File(resultCSVPath);
            if (file.getParentFile().exists())
                targetdir = file.getParentFile();
        }

        System.setProperty("logroot", targetdir.getPath() + File.separator + "log" + File.separator);

        File logsDir = new File(targetdir.getPath(), "log");
        if (!logsDir.exists())
            logsDir.mkdirs();
        endLoadLogger = LogFactory.getLog("endMojomessage");
        errLoadLogger = LogFactory.getLog("errmessage");

    }

    private void writeLog(String mess) {
        //窗体打印日志
        getLog().info(mess);
        //文档记录日志
        endLoadLogger.info(mess);
    }


    private void MOJOErrorLog(String mess) {
        mess = String.format("Mojo插件（endLoad）发生异常，异常原因：%s", mess);
        getLog().error(mess);
        endLoadLogger.error(mess);
        errLoadLogger.error(mess);
        LoadLogUtil.saveErrorMess(mess);
    }

    private void saveLog(TaskList list, boolean isTestDB) {

        String logUrl = list == null ? "" : list.getJobName();
        if (StringUtils.isEmpty(logUrl))
            LoadLogUtil.saveErrorMess(String.format("taskid:%s 的jobName为空！ ", list.getTaskID()));
        int taskId = list == null ? 0 : list.getTaskID();
        //得到异常文件记录的异常记录,并且修改文件名称为jobName
        StringBuffer errorMess = getErrorMess(logUrl);
        //创建loadLogInfo对象以方便json写入
        TaskLoadReport taskLoadReport = createTaskLoadReport(taskId, logUrl, isTestDB);
        //读取全部场景记录文件
        File scenariosmessFile = new File(String.format("%s%s%s%sscenariosmess.log", targetdir.getPath(), File.separator, "log", File.separator));
        if (scenariosmessFile.isFile() && scenariosmessFile.exists()) {
            try {
                //取全部场景组成allDetails，将已经生成报告的场景total修改为真实数量，数量为0则生成报告存在异常，也需要加入到日志文件中
                List<LoadLogDetail> allDetails = LoadLogUtil.createAllLoadLogDetail(scenariosmessFile, taskLoadReport.getLoadLogDetails());
                taskLoadReport.setTotal(allDetails.size());
                taskLoadReport.setPass(allDetails.size());
                for (LoadLogDetail detail : allDetails) {
                    if (detail.getTotal() == 0)//全部场景中total为0的，说明是有问题的，也需要加入到报告结果中
                    {
                        taskLoadReport.getLoadLogDetails().add(detail);
                        taskLoadReport.setPass(taskLoadReport.getPass() - 1);
                        taskLoadReport.setFail(taskLoadReport.getFail() + 1);
                    }
                }
            } catch (Exception ex) {
                LoadLogUtil.saveErrorMess(String.format("生成异常日志记录失败！获取全部场景内容失败！读取scenariosmess.log发生异常，", ex.getMessage()));
            }

        } else {
            LoadLogUtil.saveErrorMess(String.format("生成异常日志记录失败！获取全部场景内容失败！scenariosmess.log不存在!"));
        }
        //防止日志明细为空，默认加入一条记录
        if (taskLoadReport.getLoadLogDetails().size() == 0)
            taskLoadReport.getLoadLogDetails().add(new LoadLogDetail());
        //加入gtp所使用的插件异常信息
        errorMess.append(LoadLogUtil.getErrorMess());
        taskLoadReport.setErrorMessage(errorMess.toString());
        //转换json
        String jsonString = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(taskLoadReport);
        } catch (Exception ex) {
            jsonString = String.format("{\"taskId\":%s," +
                    "\"startTime\":9223372036854775807," +
                    "\"endTime\":-9223372036854775808," +
                    "\"error\":0," +
                    "\"success\":0,\"createTime\":%s," +
                    "\"errorMessage\":\" %s \",\"mojoLogUrl\":\"%s\"," +
                    "\"total\":0,\"loadMessList\":[{\"sceneName\":null,\"labelName\":null," +
                    "\"resultVersion\":null,\"smallSceneName\":null,\"startTime\":9223372036854775807," +
                    "\"endTime\":-9223372036854775808,\"error\":0,\"success\":0,\"total\":0,\"duration\":0}]" +
                    ",\"duration\":0}", taskLoadReport.getTaskId(), new Date(), taskLoadReport.getErrorMessage(), taskLoadReport.getMojoLogUrl());
        }


        //记录数据库
        ReportBo reportBo = new ReportBo();
        reportBo.insert(jsonString, "TaskLoadReport");
    }


    //读取异常文件日志，得到异常信息
    private StringBuffer getErrorMess(String logUrl) {

        //得到文件并修改文件名称为jobName
        File errorlog = new File(String.format("%s%slog%serrmess.log", targetdir.getPath(), File.separator, File.separator));//geterrorLogFile(logUrl);

        StringBuffer errorMess = new StringBuffer();
        List<String> errorMessageList = new ArrayList<String>();
        if (errorlog.isFile() && errorlog.exists()) {
            try {
                errorMessageList = readFileLog(errorlog);
            } catch (Exception ex) {
                LoadLogUtil.saveErrorMess(String.format("生成异常日志记录失败！errmess.log解析内容失败！原因:" + ex.getMessage()));
            }

        } else {
            LoadLogUtil.saveErrorMess(String.format("生成异常日志记录失败！errmess.log不存在！"));
        }

        for (String err : errorMessageList) {
            errorMess.append(err + "\n");
        }
        return errorMess;
    }


    private TaskLoadReport createTaskLoadReport(int taskId, String logUrl, boolean isTestDB) {
        TaskLoadReport taskLoadReport = new TaskLoadReport();
        taskLoadReport.setTaskId(taskId);
        taskLoadReport.setMojoLogUrl(logUrl);
        taskLoadReport.setIsTest(isTestDB);
        taskLoadReport.setTaskListId(taskListId);
        taskLoadReport.setCreateTime(new Date().getTime());
        try {
            taskLoadReport.setLoadLogDetails(new ArrayList<LoadLogDetail>());
            Long duration = 0L;

            Long startTime = Long.MAX_VALUE;
            Long endTime = Long.MIN_VALUE;

            HashMap<String, LoadLogDetail> logDetailHashMap = LoadLogUtil.getLogDetailHashMap();

            for (String key : logDetailHashMap.keySet()) {
                LoadLogDetail detail = logDetailHashMap.get(key);
                taskLoadReport.getLoadLogDetails().add(detail);
                duration += detail.getDuration();
                startTime = startTime < detail.getStartTime() ? startTime : detail.getStartTime();
                endTime = endTime > detail.getEndTime() ? endTime : detail.getEndTime();
            }

            taskLoadReport.setDuration(duration);
            taskLoadReport.setPass(0);
            taskLoadReport.setTotal(0);
            taskLoadReport.setFail(0);
            taskLoadReport.setCreateTime(new Date().getTime());
            if (startTime != Long.MAX_VALUE && endTime != Long.MIN_VALUE) {
                taskLoadReport.setStartTime(startTime);
                taskLoadReport.setEndTime(endTime);
            } else {
                taskLoadReport.setStartTime(null);
                taskLoadReport.setEndTime(null);
            }

        } catch (Exception ex) {
            LoadLogUtil.saveErrorMess("生成日志json对象失败！原因：" + ex.getMessage());
        }
        return taskLoadReport;
    }

    private List<String> readFileLog(File file) throws Exception {
        BufferedReader br = null;
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        InputStreamReader inputStreamReader = null;
        String sLine = null;
        List<String> errorList = new ArrayList<String>();

        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            inputStreamReader = new InputStreamReader(bufferedInputStream, "UTF-8");
            br = new BufferedReader(inputStreamReader, 20 * 1024 * 1024);
            while ((sLine = br.readLine()) != null) {
                errorList.add(sLine);
            }
        } catch (Exception ex) {
            LoadLogUtil.saveErrorMess(String.format("读取异常日志文件失败，文件路径%s,原因 %s ", file.getPath(), ex.getMessage()));

        } finally {
            if (fileInputStream != null)
                fileInputStream.close();
            if (bufferedInputStream != null)
                bufferedInputStream.close();
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (br != null)
                br.close();
        }
        return errorList;
    }


}