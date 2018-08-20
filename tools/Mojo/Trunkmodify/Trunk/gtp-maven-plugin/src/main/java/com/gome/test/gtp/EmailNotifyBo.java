package com.gome.test.gtp;

import com.gome.test.gtp.Load.LoadConfBo;
import com.gome.test.gtp.dao.TaskInfoDao;
import com.gome.test.gtp.dao.TaskListDao;

import com.gome.test.gtp.jenkins.JenkinsBo;
import com.gome.test.gtp.model.*;
import com.gome.test.gtp.report.EmailService;
import com.gome.test.gtp.report.TestResultService;
import com.gome.test.gtp.utils.Constant;
import com.gome.test.gtp.utils.Util;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by lizonglin on 2015/9/2/0002.
 */
public class EmailNotifyBo {

    private TaskListDao taskListDao;
    private TaskInfoDao taskInfoDao;
    private TestResultService testResultService;
    private JenkinsBo jenkinsBo;
    private EmailService emailService;
    private VelocityEngine engine = new VelocityEngine();
    private static boolean hasError;
    private LoadConfBo loadConfBo = new LoadConfBo();
    private Properties properties;

    public EmailNotifyBo() {
        taskListDao = (TaskListDao) Application.getBean(TaskListDao.class);
        taskInfoDao = (TaskInfoDao) Application.getBean(TaskInfoDao.class);
        testResultService = (TestResultService) Application.getBean(TestResultService.class);
        jenkinsBo = (JenkinsBo) Application.getBean(JenkinsBo.class);
        emailService = (EmailService) Application.getBean(EmailService.class);
        properties = loadConfBo.getPropsByResource("email.properties");
        initVelocityEngine(engine);
    }

    public void endMojoSendEmail(int taskListId, int taskType,Long splitTime, String fileName ) throws Exception {
        hasError = false;

        /**
         * Merge TaskReport;  TaskInfo;   Properties
         */

        TaskList taskList = taskListDao.get(taskListId);
        System.out.println(taskList.getTaskID()+"1");
        TaskInfo taskInfo = taskInfoDao.get(taskList.getTaskID());
        System.out.println(taskInfo.getTaskID()+"2");
        /**
         * email ToList
         */
        String emailList = taskInfo.getEmailList().trim();
        List<String> toList = Arrays.asList(emailList.split(","));
/*        String groupEmail = taskInfoDao.getGroupNameByTaskid(taskInfo.getTaskID());
        if (groupEmail.endsWith("yolo24.com") && !toList.contains(groupEmail)) {
            toList.add(0, groupEmail);
        }*/
//2016.11.02liangwei  修改


/*        String emailList = "liangwei-ds@yolo24.com";
        List<String> toList = Arrays.asList(emailList.split(","));*/
        List<String> ccList;
        /**
         * 公共邮件参数
         */
        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setMail_from(properties.getProperty("gtp.email.from").trim());
        emailInfo.setPersonalName(properties.getProperty("gtp.email.personalname").trim());
        emailInfo.setServerHost(properties.getProperty("gtp.email.serverhost").trim());
        emailInfo.setUsername(properties.getProperty("gtp.email.username").trim());
        emailInfo.setPassword(properties.getProperty("gtp.email.password").trim());

        if (taskType == Constant.TASK_TYPE_API) {
            /**
             * API EmailInfo
             */
            String emailContent = getAPIGUIEmailContent(taskList, taskInfo, taskType,splitTime,fileName);
            emailInfo.setMail_head_info(properties.getProperty("gtp.email.api.headinfo").trim());
            emailInfo.setMail_head_value(properties.getProperty("gtp.email.api.headvalue").trim());
            String subject = String.format("%s (%d-%s)",
                    properties.getProperty("gtp.email.api.subject").trim(), taskInfo.getTaskID(), taskInfo.getTaskName());
            if (hasError) {
                emailInfo.setSubject(subject + "——部分失败");
            } else {
                emailInfo.setSubject(subject + "——全部通过");
            }
            emailInfo.setSubject(subject);
            ccList = Arrays.asList(properties.getProperty("gtp.email.api.cclist").trim().split(","));
            emailService.sendEmail(emailContent, toList, ccList, emailInfo);

        } else if (taskType == Constant.TASK_TYPE_GUI) {
            /**
             * GUI EmailInfo
             */
            String emailContent = getAPIGUIEmailContent(taskList, taskInfo, taskType,splitTime,fileName);
            emailInfo.setMail_head_info(properties.getProperty("gtp.email.gui.headinfo").trim());
            emailInfo.setMail_head_value(properties.getProperty("gtp.email.gui.headvalue").trim());
            String subject = String.format("%s (%d-%s)",
                    properties.getProperty("gtp.email.gui.subject").trim(), taskInfo.getTaskID(), taskInfo.getTaskName());
            if (hasError) {
                emailInfo.setSubject(subject + "——部分失败");
            } else {
                emailInfo.setSubject(subject + "——全部通过");
            }
            ccList = Arrays.asList(properties.getProperty("gtp.email.gui.cclist").trim().split(","));
            emailService.sendEmail(emailContent, toList, ccList, emailInfo);
        } else {
            /**
             * LOAD EmailInfo
             */
            Map<String, Object> model = getLoadEmailModelMap(taskListId);
            String loadEmailContent = VelocityEngineUtils.mergeTemplateIntoString(engine, Constant.LOAD_EMAIL_VM_TEMPLATE, "UTF-8", model);
            hasError = Boolean.valueOf(model.get(Constant.LOAD_EMAIL_HASERROR).toString());
            if (!taskList.getTaskFrom().toUpperCase().equals(Constant.ENQUEUE_BY_Test.toUpperCase())) {
                emailInfo.setMail_head_info(properties.getProperty("gtp.email.load.headinfo").trim());
                emailInfo.setMail_head_value(properties.getProperty("gtp.email.load.headvalue").trim());
                String subject = String.format("%s (%d-%s)",
                        properties.getProperty("gtp.email.load.subject").trim(), taskInfo.getTaskID(), taskInfo.getTaskName());
                if (hasError)
                    emailInfo.setSubject(subject + "——部分失败");
                else
                    emailInfo.setSubject(subject + "——全部通过");
                ccList = Arrays.asList(properties.getProperty("gtp.email.load.cclist").trim().split(","));
                emailService.sendEmail(loadEmailContent, toList, ccList, emailInfo);
            }
        }
    }

    public void updateTaskListLogAfterEmail(int taskListId) {
        String afterEmailLog = "Email has been sent";
        taskListDao.updateTaskListLog(taskListId, afterEmailLog);
    }

    private String getAPIGUIEmailContent(TaskList taskList, TaskInfo taskInfo, int taskType,Long splitTime, String fileName) throws IOException {
//        TaskReport taskReport = testResultService.getMergedReportByTaskIdSplitTime(taskList.getTaskID(), taskList.getSplitTime().getTime());
        TaskReport taskReport = testResultService.getMergedReportByTaskIdSplitTime(taskList.getTaskID(), splitTime);
        taskReport.setDetails(testResultService.mergeCaseResult(taskReport));
//        return genEmailContentByTaskReport(taskReport, taskInfo, properties, taskType, taskList.getJobName());
        return genEmailContentByTaskReport(taskReport, taskInfo, properties, taskType, fileName);
    }

    private void initVelocityEngine(VelocityEngine engine) {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        try {
            engine.init(p);
        } catch (Exception e) {
            System.out.println("初始化 VelocityEngine 失败！");
            e.printStackTrace();
        }
    }

    private String genEmailContentByTaskReport(TaskReport taskReport, TaskInfo taskInfo, Properties properties, int taskType, String jobName) throws IOException {
        Map<String, String> repMap = new HashMap<String, String>();
        repMap.put(Constant.API_EMAIL_TASKID, String.valueOf(taskInfo.getTaskID()));
        repMap.put(Constant.API_EMAIL_TASKNAME, taskInfo.getTaskName());

/*
        repMap.put(Constant.API_EMAIL_TASKID, "10712");
        repMap.put(Constant.API_EMAIL_TASKNAME, "HTTP—API线上监控");
*/

        repMap.put(Constant.API_EMAIL_TOTAL, String.valueOf(taskReport.getTotalCases()));
        repMap.put(Constant.API_EMAIL_PASSED, String.valueOf(taskReport.getPass()));
        repMap.put(Constant.API_EMAIL_FAILED, String.valueOf(taskReport.getFail()));
        repMap.put(Constant.API_EMAIL_ABORTED, String.valueOf(taskReport.getAborted()));
        repMap.put(Constant.API_EMAIL_STARTTIME, Util.timestamp2String(new Timestamp(taskReport.getStartTime())));
        repMap.put(Constant.API_EMAIL_ENDTIME, Util.timestamp2String(new Timestamp(taskReport.getEndTime())));
        repMap.put(Constant.API_EMAIL_DURATION, String.valueOf(taskReport.getDuration() / 1000.0));
        repMap.put(Constant.API_EMAIL_TASKLISTGUID, taskReport.get_id());
        repMap.put(Constant.API_EMAIL_ERRORCONTENT, genErrorInfo(taskReport));
        repMap.put(Constant.API_EMAIL_LINKHOST, properties.getProperty("gtp.email.linkhost").trim());

        repMap.put(Constant.GUI_EMAIL_FTP_HOST, properties.getProperty("gtp.email.gui.ftp.host"));
        repMap.put(Constant.GUI_EMAIL_FTP_SUBDIR, properties.getProperty("gtp.email.gui.ftp.subdir"));
        repMap.put(Constant.GUI_EMAIL_FTP_JOBNAME, jobName);

        repMap.put(Constant.JENKINS_BASEURL, properties.getProperty("gtp.jenkins.baseurl"));

        if (taskType == Constant.TASK_TYPE_API)
            return jenkinsBo.replaceFileFromResourceName(repMap, Constant.API_EMAIL_TEMPLATE);
        else if (taskType == Constant.TASK_TYPE_GUI)
            return jenkinsBo.replaceFileFromResourceName(repMap, Constant.GUI_EMAIL_TEMPLATE);
        else if (taskType == Constant.TASK_TYPE_LOAD)
            return jenkinsBo.replaceFileFromResourceName(repMap, Constant.LOAD_EMAIL_TEMPLATE);
        else
            return "任务类型非法：" + taskType;
    }

    private Map<String, Object> getLoadEmailModelMap(int taskListId) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        TaskLoadReport taskLoadReport = testResultService.getLoadReportByTaskListId(taskListId);
        boolean hasError = false;
        for (LoadLogDetail loadLogDetail : taskLoadReport.getLoadLogDetails()) {
            if (loadLogDetail.getError() > 0) {
                hasError = true;
                break;
            }
        }
        modelMap.put(Constant.LOAD_EMAIL_TASKLOADREPORT, taskLoadReport);
        modelMap.put(Constant.LOAD_EMAIL_HASERROR, hasError);
        modelMap.put(Constant.LOAD_EMAIL_UTIL, new Util());
        modelMap.put(Constant.LOAD_EMAIL_FTP_URL, String.format("%s%s", properties.getProperty("gtp.email.load.ftp.host"), taskLoadReport.getMojoLogUrl()));
        modelMap.put(Constant.LOAD_EMAIL_JENKINS_URL, String.format("http://%s/job/%s/", properties.getProperty("gtp.jenkins.baseurl"), taskLoadReport.getMojoLogUrl()));
        modelMap.put(Constant.LOAD_EMAIL_LOGDETAIL_URL, String.format("http://%s%s%s", properties.getProperty("gtp.email.linkhost"), properties.getProperty("gtp.email.load.logdetail.subdir"), taskLoadReport.get_id()));
        return modelMap;
    }

    private String genErrorInfo(TaskReport taskReport) {
        CaseResult[] caseResults = taskReport.getDetails();
        List<CaseResult> caseResultList = new ArrayList<CaseResult>();
        getErrorCaseResult(caseResults, caseResultList);

        /**
         * caseResultList中的所有CaseResult没有children且testResult为fail
         */
        String errorContent = "";
        if (caseResultList.size() > 0) {
            hasError = true;
            for (CaseResult caseResult : caseResultList) {
                errorContent += String.format("<tr><th>用例名称</th><th>测试结果</th><th>开始时间</th><th>结束时间</th><th>耗时(s)</th><th>所有人</th><th>用例描述</th></tr>" +
                                "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr><tr><th>错误信息</th><td colspan='6'>%s</td></tr>" +
                                "<tr><th>发送报文信息</th><td colspan='6'>%s</td></tr>" +
                                "<tr><td colspan='7' style='border-top-color: #000000'></td></tr>",
                        caseResult.getTestCaseName(), caseResult.getTestResult(), Util.timestamp2String(new Timestamp(caseResult.getStartTime())),
                        Util.timestamp2String(new Timestamp(caseResult.getEndTime())), String.valueOf(caseResult.getDuration() / 1000.0),
                        caseResult.getOwner(), caseResult.getCaseDesc(), caseResult.getErrorMessage().replaceAll("\\$", ".").contains("html")?"校验失败且返回内容包含页面,根据startTime在mongo库查询数据查看具体信息"+taskReport.getStartTime():caseResult.getErrorMessage().replaceAll("\\$", "."),caseResult.getPost());
            }
        }

        return errorContent;
    }

    private void getErrorCaseResult(CaseResult[] caseResults, List<CaseResult> caseResultList) {
        if (caseResults.length > 0) {
            for (CaseResult caseResult : caseResults) {
                if (caseResult.getTestResult().equals("fail") &&
                        (!caseResult.getErrorMessage().equals("") || !caseResult.getStackTrace().equals(""))) {
                    caseResultList.add(caseResult);
                }
                getErrorCaseResult(caseResult.getChildren(), caseResultList);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        EmailNotifyBo emailNotifyBo = new EmailNotifyBo();
        List toList = new ArrayList();
        List ccList = new ArrayList();
//        toList.add("469130744@qq.com");
//        ccList.add("zonglinli86@163.com");
        ccList.add("tech-test-arch@yolo24.com");
//        toList.add("lizonglin@yolo24.com");
//        toList.add("tech-test-arch@yolo24.com");
        toList.add("tech-test-arch@yolo24.com");

        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setMail_from("gome-test-gtp@yolo24.com");
        emailInfo.setMail_head_info("gtp email test");
        emailInfo.setMail_head_value("gtp email test1");
        emailInfo.setPersonalName("gome-test-gtp");
        emailInfo.setServerHost("mail.yolo24.com");
        emailInfo.setUsername("gome-test-gtp");
        emailInfo.setPassword("gtp123456");
        emailInfo.setSubject("测试GTP邮件——请忽略");

        InputStream fis = EmailNotifyBo.class.getResourceAsStream(String.format("/%s", "API-Email-Template.html"));
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String resultCfgStr = "";
        String line;
        while ((line = br.readLine()) != null) {
            resultCfgStr = resultCfgStr + line + "\r\n";
        }

        emailNotifyBo.emailService.sendEmail(resultCfgStr, toList, ccList, emailInfo);

//        emailNotifyBo.send("smtp.idc.pub","gome-test-gtp","qxz486b\\4#c7","测试","测试邮件","gome-test-gtp@yolo24.com","tech-test-arch@yolo24.com");
//        emailNotifyBo.send("smtp.idc.pub","lizonglin","gome.657687","测试","测试邮件","lizonglin@yolo24.com","tech-test-arch@yolo24.com");



        //创建邮件发送类 JavaMailSender
        //用于发送基本的文本邮件信息（不能包括附件，及图片）
//        JavaMailSender sender = new JavaMailSenderImpl();
//
//
//        //设置邮件服务主机
//        ((JavaMailSenderImpl)sender).setHost("smtp.idc.pub");
//        //发送者邮箱的用户名
//        ((JavaMailSenderImpl)sender).setUsername("gome-test-gtp");
//        //发送者邮箱的密码
//        ((JavaMailSenderImpl)sender).setPassword("qxz486b\\4#c7");
//
//        //配置文件，用于实例化java.mail.session
//        Properties pro = System.getProperties();
//
//        //登录SMTP服务器,需要获得授权
//        //测试 sohu 的邮箱可以获得授权
//        pro.put("mail.smtp.auth", "false");
//        pro.put("mail.smtp.socketFactory.port", "25");
//        pro.put("mail.smtp.socketFactory.fallback", "false");
//
//        //通过文件获取信息
//        ((JavaMailSenderImpl)sender).setJavaMailProperties(pro);
//
//
//        //创建基本邮件信息
//        MailMessage mailMessage = new SimpleMailMessage();
//
//        //发送者地址，必须填写正确的邮件格式，否者会发送失败
//        mailMessage.setFrom("gome-test-gtp@yolo24.com");
//        //邮件主题
//        mailMessage.setSubject("测试邮件");
//        //邮件内容，简单的邮件信息只能添加文本信息
//        mailMessage.setText("测试");
//        //邮件接收者的邮箱地址
//        mailMessage.setTo("tech-test-arch@yolo24.com");
//
//
//        //发送邮件，参数可以是数组
//        //sender.send(SimpleMailMessage[])
//        sender.send((SimpleMailMessage)mailMessage);
    }

}
