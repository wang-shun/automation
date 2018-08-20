package com.gome.test.gtp.Load;

import com.gome.test.gtp.ReportJMT;
import com.gome.test.gtp.model.JMTReport;

/**
 * Created by zhangjiadi on 15/11/10.
 */
public class JMTReportTimeStamp implements Comparable<JMTReportTimeStamp> {

    public JMTReportTimeStamp(Long timeStamp, ReportJMT jmtReport)
    {
        this.timeStamp=timeStamp;
        this.jmtReport=jmtReport;
    }

    public JMTReportTimeStamp()
    {

    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ReportJMT getJmtReport() {
        return jmtReport;
    }

    public void setJmtReport(ReportJMT jmtReport) {
        this.jmtReport = jmtReport;
    }

    private Long timeStamp;
    private ReportJMT jmtReport;




    @Override
    public int compareTo(JMTReportTimeStamp o) {
        return  (int) (timeStamp - o.timeStamp) ;
    }
}
