package com.gome.test.gtp.Load;

import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by zhangjiadi on 15/11/9.
 */
public class JmtReportData implements Comparable<JmtReportData> {

    private String sceneName;
    private String labelName;
    private String resultVersion;
    private String smallSceneName;
    private int elapsedCount;
    private double avg;
    private double elapsed50;
    private double elapsed90;
    private double elapsed95;
    private double elapsed99;
    private double minElapsed;
    private double maxElapsed;
    private double fail;
    private double tps;
    private String environment;
    private int errorCount;
    private double grpThreads;
    private String template;
    private Long timeStamp;
    private String begin;
    private String end;
    private double tpsAvg;
    private double tpsMax;
    private double tpsMin;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    private String createTime;

    public double getTpsAvg() {
        return tpsAvg;
    }

    public void setTpsAvg(double tpsAvg) {
        this.tpsAvg = tpsAvg;
    }

    public double getTpsMax() {
        return tpsMax;
    }

    public void setTpsMax(double tpsMax) {
        this.tpsMax = tpsMax;
    }

    public double getTpsMin() {
        return tpsMin;
    }

    public void setTpsMin(double tpsMin) {
        this.tpsMin = tpsMin;
    }



    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }



    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String tpsAvg_name(){return  "TPS(AVG)";}
    public String tpsMax_name(){return "TPS(Max)";}
    public String tpsMin_name(){return "TPS(Min)";}


    public String getSceneName() {
        return sceneName;
    }

    public String getTemplate_name(){ return  "template";}
    public String getTimeStamp_name(){return  "timeStamp";}
    public String getSceneName_name() {
        return "sceneName";
    }

    public String getLabelName_name() {
        return "labelName";
    }

    public String getLabelName() {
        return labelName;
    }

    public String getResultVersion() {
        return resultVersion;
    }

    public String getOnlyVersion(){
        int index= resultVersion.indexOf("_");
        if(index>0)
            return resultVersion.substring(0,index);
        return resultVersion;
    }

    public String getResultVersion_name() {
        return "resultVersion";
    }

    public String getSmallSceneName() {
        return smallSceneName;
    }

    public String getSmallSceneName_name() {
        return "smallSceneName";
    }

    public String getElapsedCount_name() {
        return "elapsedCount";
    }

    public int getElapsedCount() {
        return elapsedCount;
    }

    public double getAvg() {
        return avg;
    }

    public double getElapsed50() {
        return elapsed50;
    }

    public double getElapsed90() {
        return elapsed90;
    }

    public double getElapsed95() {
        return elapsed95;
    }

    public double getElapsed99() {
        return elapsed99;
    }

    public double getMinElapsed() {
        return minElapsed;
    }

    public double getMaxElapsed() {
        return maxElapsed;
    }

    public double getGrpThreads() {
        return grpThreads;
    }
    public int getErrorCount() {
        return errorCount;
    }

    public double getFail() {
        return fail;
    }

    public double getTps() {
        return tps;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getAvg_name() {
        return "avg";
    }

    public String getGrpThreads_name() { return  "grpThreads" ; }

    public String getErrorCount_name() { return "errorCount" ;}

    public String getElapsed90_name() {
        return "elapsed90";
    }

    public String getElapsed50_name() {
        return "elapsed50";
    }

    public String getElapsed95_name() {
        return "elapsed95";
    }

    public String getElapsed99_name() {
        return "elapsed99";
    }

    public String getMinElapsed_name() {
        return "minElapsed";
    }

    public String getMaxElapsed_name() {
        return "maxElapsed";
    }

    public String getFail_name() {
        return "fail";
    }

    public String getTps_name() {
        return "tps";
    }

    public String getEnvironment_name() {
        return "environment";
    }

    public String getError() {
        Double error = Double.valueOf(this.fail);
        DecimalFormat df = new DecimalFormat("0.0000");
        return df.format(error);
    }

    public String getThroughput() {
        Double dou = Double.valueOf(this.getTps());
        DecimalFormat df = new DecimalFormat("0.0000");
        if (dou < 1) {

            if (dou * 60  < 1 ) {
                return df.format (dou * 60 * 60) + "/hour";
            } else {
                return df.format(dou * 60) + "/min";
            }
        } else {
            return df.format(dou) + "/sec";
        }

    }


    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public void setResultVersion(String resultVersion) {
        this.resultVersion = resultVersion;
    }

    public void setSmallSceneName(String smallSceneName) {
        this.smallSceneName = smallSceneName;
    }

    public void setElapsedCount(int elapsedCount) {
        this.elapsedCount = elapsedCount;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public void setElapsed50(double elapsed50) {
        this.elapsed50 = elapsed50;
    }

    public void setElapsed90(double elapsed90) {
        this.elapsed90 = elapsed90;
    }

    public void setElapsed95(double elapsed95) {
        this.elapsed95 = elapsed95;
    }

    public void setElapsed99(double elapsed99) {
        this.elapsed99 = elapsed99;
    }

    public void setMinElapsed(double minElapsed) {
        this.minElapsed = minElapsed;
    }

    public void setMaxElapsed(double maxElapsed) {
        this.maxElapsed = maxElapsed;
    }

    public void setFail(double fail) {
        this.fail = fail;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setGrpThreads(double grpThreads) {
        this.grpThreads = grpThreads;
    }



    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }


    @Override
    public int compareTo(JmtReportData  o) {
        return (int) ( this.getTimeStamp() - o.getTimeStamp());
    }
    

}
