package com.gome.test.gtp;

import com.gome.test.gtp.Load.JMTReportTimeStamp;
import com.gome.test.gtp.dao.mongodb.ReportDao;
import com.gome.test.gtp.model.JMTReport;
import com.gome.test.gtp.model.JmtAGGReport;
import com.gome.test.gtp.utils.Util;
import com.gome.test.utils.StringUtils;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReportBo {
    private ReportDao reportDao;

    public ReportBo() {
        reportDao = (ReportDao) Application.getBean(ReportDao.class);
    }

    public WriteResult insert(String json, String collectionName) {
        System.out.println(json+" tableName:"+ collectionName);
//        try {
//            Util.writeLine(json, "/Users/zhangjiadi/Documents/GOME/Doraemon/LoadTest/jmt_file/DBresult.csv");
//        }catch (Exception ex)
//        {
//
//        }

        return reportDao.insert(json, collectionName);
    }


    public WriteResult insert(Object object) {
        return reportDao.insert(object);
    }

    public WriteResult insertList(List<? extends Object> object) {
        return reportDao.insertList(object);
    }

    public WriteResult delete(String collectionName, DBObject query) {
        return reportDao.delete(collectionName, query);
    }

    public void updateEnable(Query query)
    {
        Update update=new Update();
        update.set("isEnable",false);
        reportDao.update(query,update);
    }


    public void updateUnLoad(ReportJMT model) {
        Query query = new Query();
        query.addCriteria(Criteria.where(model.getSceneName_name()).is(model.getSceneName()));
        query.addCriteria(Criteria.where(model.getResultVersion_name()).is(model.getResultVersion()));
        if(!StringUtils.isEmpty(model.getTimeVersion()))
            query.addCriteria(Criteria.where(model.getTimeVersion()).is(model.getTimeVersion()));
        query.addCriteria(Criteria.where(model.getSmallSceneName_name()).is(model.getSmallSceneName()));
        query.addCriteria(Criteria.where(model.getEnvironment_name()).is(model.getEnvironment()));
        query.addCriteria(Criteria.where(model.getTemplate_name()).is(model.getTemplate()));
        query.addCriteria(Criteria.where(model.getTimeStamp_name()).is(String.valueOf(model.gettimeStamp())));
        query.addCriteria(Criteria.where(model.getGrpThreads_name()).is(String.valueOf(model.getGrpThreads())));
        query.addCriteria(Criteria.where(model.getLabelName_name()).is(model.getLabelName()));

        Update update=new Update();
        update.set("UnLoad", true);
        reportDao.update(query,update);
    }



    //计算聚合报告
    public List<JmtAGGReport> getJMTReportList(List<ReportJMT> jmtReports) {


        HashMap<String, List<ReportJMT>> mapJmtReport = getGroupList(jmtReports);
        List<JmtAGGReport> result = new ArrayList<JmtAGGReport>();
        for (String key : mapJmtReport.keySet()) {
            List<ReportJMT> jmtList = (List<ReportJMT>) mapJmtReport.get(key);

            List<JMTReportTimeStamp> reports = new ArrayList<JMTReportTimeStamp>();
            for (ReportJMT jmtReport : jmtList) {
                reports.add(new JMTReportTimeStamp(Long.valueOf(jmtReport.gettimeStamp()) , jmtReport));
            }

            //根据GrpThreads进行分组
            HashMap<Long, List<ReportJMT>> mapGrpThreadReport = getGrpThreadReport(reports);

            //遍历分组后的数据，进行计算
            for (Long grpThread : mapGrpThreadReport.keySet()) {
                List<ReportJMT> jmtGrpList = mapGrpThreadReport.get(grpThread);

                result.add(getDateForJMTReportList(jmtGrpList));
            }

        }

        Collections.sort(result);
        return result;


    }


    private HashMap<String,List<ReportJMT>> getGroupList(List<ReportJMT> jmtReports)
    {
        HashMap<String,List<ReportJMT>> mapJmtReport=new HashMap<String, List<ReportJMT>>();
        for(ReportJMT jmt : jmtReports)
        {
            String key=String.format("%s_%s_%s_%s",jmt.getSceneName(),jmt.getSmallSceneName(),jmt.getResultVersion(),jmt.getLabelName());
            if(!mapJmtReport.containsKey(key))
                mapJmtReport.put(key,new ArrayList<ReportJMT>());
            mapJmtReport.get(key).add(jmt);
        }
        return  mapJmtReport;
    }


    //将数据针对GrpThread进行分组
    private HashMap<Long,List<ReportJMT>> getGrpThreadReport(List<JMTReportTimeStamp> jmtList)
    {
        //根据时间排序
        Collections.sort(jmtList);
        Long lastGrpThreads=0L;
        HashMap<Long,List<ReportJMT>> mapGrpThreadReport=new HashMap<Long, List<ReportJMT>>();

        //根据GrpThreads进行分组
        for(JMTReportTimeStamp reportTimeStamp: jmtList)
        {
            ReportJMT report =reportTimeStamp.getJmtReport();

            if(lastGrpThreads > 0 && Long.valueOf(report.getGrpThreads())  < lastGrpThreads )
            {
                updateUnLoad(report);
                continue;
            }

            lastGrpThreads = Long.valueOf(report.getGrpThreads()) ;

            if(!mapGrpThreadReport.containsKey(lastGrpThreads))
                mapGrpThreadReport.put(lastGrpThreads,new ArrayList<ReportJMT>());

            mapGrpThreadReport.get(lastGrpThreads).add(report);
        }
        return mapGrpThreadReport;

    }

    private JmtAGGReport getDateForJMTReportList(List<ReportJMT> list)
    {

        ReportJMT patReport =null;

        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        SimpleDateFormat sdfDate =   new SimpleDateFormat( "yyyy-MM-dd" );
        double maxEapsed=0;
        double minEapsed=Double.MAX_VALUE;
        int errorCount=0;
        int elapsedCount=0;
        int count=0;
        double elapsed50=0;
        double elapsed90=0;
        double elapsed95=0;
        double elapsed99=0;
        double minTps = Double.MAX_VALUE;
        double maxTps = 0;
        double tps=0;
        double avg=0;

        Long begin = Long.MAX_VALUE;
        Long end = Long.MIN_VALUE;
        double sumEapsed=0;
        Long timeStamp=Long.MIN_VALUE;
        Long endtimeStamp= Long.MIN_VALUE;
        for(int i=0;i< list.size();i++)
        {
            ReportJMT jmtReport =list.get(i) ;

            if(patReport ==null )
                patReport=jmtReport;


            if(maxEapsed <Long.valueOf(jmtReport.getMaxElapsed()) )
                maxEapsed=Long.valueOf(jmtReport.getMaxElapsed()) ;

            if(minEapsed > Long.valueOf(jmtReport.getMinElapsed()))
                minEapsed=Long.valueOf(jmtReport.getMinElapsed());

            errorCount = errorCount + Integer.valueOf(jmtReport.getErrorCount());
            elapsedCount = elapsedCount +Integer.valueOf(jmtReport.getElapsedCount());
            count =count + 1;
            sumEapsed= sumEapsed +Double.valueOf(jmtReport.getSumElapsed()) ;
            elapsed50 = elapsed50 +Double.valueOf(jmtReport.getElapsed50());
            elapsed90 = elapsed90 + Double.valueOf(jmtReport.getElapsed90());
            elapsed95 = elapsed95 + Double.valueOf(jmtReport.getElapsed95());
            elapsed99 = elapsed99 + Double.valueOf(jmtReport.getElapsed99());

            Long resultVersion=Long.valueOf(jmtReport.gettimeStamp()) ;
            if(begin > resultVersion) {
                begin = resultVersion;
                timeStamp=Long.valueOf(jmtReport.gettimeStamp()) ;
            }
            if(end < resultVersion) {
                end = resultVersion;
                endtimeStamp= Long.valueOf(jmtReport.gettimeStamp()) ;
            }

            if(minTps > Double.valueOf(jmtReport.getTps()) )
                minTps = Double.valueOf(jmtReport.getTps());
            if(maxTps <  Double.valueOf(jmtReport.getTps()))
                maxTps= Double.valueOf(jmtReport.getTps());

            tps =tps +  Double.valueOf(jmtReport.getTps());

        }



        JmtAGGReport report=new JmtAGGReport();
        report.setSceneName(patReport.getSceneName());
        report.setSmallSceneName(patReport.getSmallSceneName());
        report.setLabelName(patReport.getLabelName());
        report.setBegin(sdf.format(begin) );
        if(sdfDate.format(begin).equals(sdfDate.format(end)))
            report.setEnd(new SimpleDateFormat("HH:mm:ss" ).format(end));
        else
            report.setEnd(sdf.format(end));
        report.setTimeStamp(timeStamp);
        report.setResultVersion(patReport.getResultVersion());
        report.setAvg(Util.setScaleFour(sumEapsed / Double.valueOf(elapsedCount), 4));
        report.setMinElapsed(minEapsed);
        report.setMaxElapsed(maxEapsed);
        report.setElapsed50(Util.setScaleFour(elapsed50 / count, 0));
        report.setElapsed90(Util.setScaleFour(elapsed90 / count, 0));
        report.setElapsed95(Util.setScaleFour(elapsed95 / count, 0));
        report.setElapsed99(Util.setScaleFour(elapsed99 / count, 0));
        report.setFail(Util.setScaleFour(errorCount / Double.valueOf(elapsedCount), 4));
        report.setTpsAvg(Util.setScaleFour(tps / count, 4));

        report.setTps((endtimeStamp - timeStamp) == 0 ? 0 : Util.setScaleFour(Double.valueOf(elapsedCount) / ((endtimeStamp - timeStamp) / 1000), 4));
        report.setErrorCount(errorCount);
        report.setElapsedCount(elapsedCount);
        report.setTpsMax(maxTps);
        report.setTpsMin(minTps);
        report.setCreateTime(new Date().toString());
        report.setTimeVersion(patReport.getTimeVersion());
        report.setGrpThreads(Double.valueOf(patReport.getGrpThreads()));
        report.setTaskID(patReport.gettaskID());
        report.setTaskListID(patReport.getTaskListID());
        report.setTemplate(patReport.getTemplate());
        report.setEnvironment(patReport.getEnvironment());
        return report;

    }


    public static void main(String[] args) {
        System.out.println(Util.nowTimestamp());
        System.out.println(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SS");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));
    }

    public List<JMTReport> getJMTReportByResultVersion(String resultVersion)
    {
       return reportDao.getJMTReportByResultVersion(resultVersion) ;
    }

}
