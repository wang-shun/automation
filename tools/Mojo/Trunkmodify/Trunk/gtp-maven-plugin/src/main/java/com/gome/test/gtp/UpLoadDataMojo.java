package com.gome.test.gtp;


import com.gome.test.gtp.Load.LoadSave;
import com.gome.test.gtp.model.JMTReport;
import com.gome.test.gtp.model.JmtAGGReport;
import com.gome.test.gtp.model.JmtReportData;
import com.gome.test.gtp.utils.Constant;
import com.gome.test.utils.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Mojo(name = "gtp")
 * @goal upLoadData
 * @requiresDependencyResolution compile+runtime
 * @requiresProject false
 * jenkins slave 执行的第最后步骤。更新task状态，并保存结果至mongodb
 * 示例  mvn gtp:upLoadData
 */
@SpringApplicationConfiguration(classes = Application.class)
public class UpLoadDataMojo extends AbstractMojo {

    /**
     * @parameter expression="${begindate}"
     */
    private String begindate;

    /**
     * @parameter expression="${enddate}"
     */
    private String enddate;


    public static void main(String[] args) throws Exception {


//        System.setProperty("logroot","/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/jmt_file/jmt_test/ExecuteTest/target/");
        UpLoadDataMojo mojo = new UpLoadDataMojo();
//        mojo.resultCSVPath = "/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/LoadStandardProject/LoadStandardProject/ExecuteTest/target/results/result.csv";
//        mojo.targetdir=new File("/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/LoadStandardProject/LoadStandardProject/ExecuteTest/target/results");

        mojo.execute();

    }


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        System.out.println("开始执行 更新插件");

        ReportBo bo = new ReportBo();

        while (true) {

            List<JMTReport> list = bo.getJMTReportByResultVersion(begindate);

            if (!StringUtils.isEmpty(enddate)) {
                if (list.size() > 0 && list.get(0).getResultVersion().compareTo(enddate) >= 0)
                    break;
            }

            //赋值
            if (list.size() > 0) {
                List<ReportJMT> reportJMTs = new ArrayList<ReportJMT>();
                HashMap<String, List<ReportJMT>> listHashMap = new HashMap<String, List<ReportJMT>>();

                for (JMTReport jmtReport : list) {
                    ReportJMT reportJMT = new ReportJMT(jmtReport);
                    String key = reportJMT.getResultVersion() + "_" + reportJMT.getTimeVersion() + "_" + reportJMT.getEnvironment() + "_" + reportJMT.getSceneName() + "_" + reportJMT.getSmallSceneName() + "_" + reportJMT.getTemplate();

                    if (!listHashMap.containsKey(key)) {
                        listHashMap.put(key, new ArrayList<ReportJMT>());
                    }

                    listHashMap.get(key).add(reportJMT);
                }

                for (String key : listHashMap.keySet()) {
                    List<JmtAGGReport> aggReportList = bo.getJMTReportList(listHashMap.get(key));
                    for (JmtAGGReport aggReport : aggReportList) {
                        LoadSave loadSave = new LoadSave(aggReport.getEnvironment(), Constant.JMT_REPORT, Constant.JMTAGG_REPORT);
                        //删除原来已经入库的数据
                        loadSave.deleteAGReport(aggReport.getTaskID(), aggReport.getTaskListID(), aggReport.getGrpThreads(), aggReport.getSceneName(), aggReport.getResultVersion(), aggReport.getTimeVersion(), aggReport.getSmallSceneName(), aggReport.getLabelName());
                        //将数据入库
                        loadSave.save(aggReport.getJmtAGGReport());
                    }
                }
                begindate = list.get(0).getOnlyVersion();
            } else
                break;

        }

        System.out.println("执行完毕 更新插件 ！ ");

    }


}
