package com.gome.test.gtp;

import com.gome.test.gtp.model.JMTReport;

/**
 * Created by Administrator on 2015/7/31.
 */
public class ReportJMT {

    public ReportJMT()
    {

    }

    public ReportJMT(JMTReport jmtReport)
    {
        this.environment=jmtReport.getEnvironment();

            this.sceneName=jmtReport.getSceneName();
            this.labelName=jmtReport.getLabelName();
            this.resultVersion=jmtReport.getResultVersion();
            this.smallSceneName=jmtReport.getSmallSceneName();
            this.elapsedCount= String.valueOf(jmtReport.getElapsedCount()) ;
            this.avg= String.valueOf(jmtReport.getAvg());
            this.elapsed50=String.valueOf(jmtReport.getElapsed50());
                    this.elapsed90 = String.valueOf(jmtReport.getElapsed90());
            this.elapsed95=String.valueOf(jmtReport.getElapsed95());
            this.elapsed99=String.valueOf(jmtReport.getElapsed99());
            this.minElapsed=String.valueOf(jmtReport.getMinElapsed());
            this.maxElapsed=String.valueOf(jmtReport.getMaxElapsed());
            this.fail=String.valueOf(jmtReport.getFail());
            this.tps=String.valueOf(jmtReport.getTps());
            this.kbs=String.valueOf(jmtReport.getTps());
            this.grpThreads=String.valueOf(jmtReport.getGrpThreads());
            this.timeStamp=String.valueOf(jmtReport.getTimeStamp());
            this.template=String.valueOf(jmtReport.getTemplate());
            this.errorCount=String.valueOf(jmtReport.getErrorCount());
            this.sumElapsed=String.valueOf(jmtReport.getSumElapsed());
            this.taskID= jmtReport.getTaskID();
            this.taskListID = jmtReport.getTaskListID();


    }

// "sceneName,labelName,resultVersion,smallsSceneName,elapsedCount,avg,elapsed50,elapsed90,elapsed95,elapsed99,
// minElapsed,maxElapsed,fail,tps,kbs,grpThreads,timeStamp,template";
    @Override
    public String toString()
    {
        return String.format("this.sceneName:%s,this.labelName:%s,this.resultVersion:%s,this.smallSceneName:%s,this.elapsedCount:%s,this.avg:%s,this.elapsed50:%s," +
                        "this.elapsed90:%s,this.elapsed95:%s,this.elapsed99:%s,this.minElapsed:%s,this.maxElapsed:%s,this.fail:%s,this.tps:%s,this.kbs:%s,this.grpThreads:%s," +
                        "this.timeStamp:%s,this.template:%s,this.errorCount:%s,this.sumElapsed:%s,this.taskID:%s,this.taskListID:%s",this.sceneName,this.labelName,this.resultVersion,this.smallSceneName,this.elapsedCount,this.avg,this.elapsed50,
                            this.elapsed90,this.elapsed95,this.elapsed99,this.minElapsed,this.maxElapsed,this.fail,this.tps,this.kbs,this.grpThreads,
                            this.timeStamp,this.template,this.errorCount,this.sumElapsed,this.taskID,this.taskListID);
    }

    public ReportJMT(String value,String environment)
    {
        this.environment=environment;
        String [] valueList=value.split("[,]");
        if(valueList.length>0)
        {
            this.sceneName=valueList[0].trim();
            this.labelName=valueList[1].trim();
            this.resultVersion=valueList[2].trim();
            this.smallSceneName=valueList[3].trim();
            this.elapsedCount=valueList[4].trim();
            this.avg=valueList[5].trim();
            this.elapsed50=valueList[6].trim();
            this.elapsed90=valueList[7].trim();
            this.elapsed95=valueList[8].trim();
            this.elapsed99=valueList[9].trim();
            this.minElapsed=valueList[10].trim();
            this.maxElapsed=valueList[11].trim();
            this.fail=valueList[12].trim();
            this.tps=valueList[13].trim();
            this.kbs=valueList[14].trim();
            this.grpThreads=valueList[15].trim();
            this.timeStamp=valueList[16].trim();
            this.template=valueList[17].trim();
            this.errorCount=valueList[18].trim();
            this.sumElapsed=valueList[19].trim();

        }
    }
    private String sceneName;
    private String labelName;
    private String resultVersion;
    private String smallSceneName;
    private String elapsedCount;
    private String avg;
    private String elapsed50;
    private String elapsed90;
    private String elapsed95;
    private String elapsed99;
    private String minElapsed;
    private String maxElapsed;
    private String fail;
    private String tps;

    private String kbs;
    private String timeStamp;
    private String environment;
    private String template;
    private String grpThreads;
    private String errorCount;
    private String sumElapsed;
    private int taskID;
    private int taskListID;

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }



    public void setTaskListID(int taskListID) {
        this.taskListID = taskListID;
    }

    public int getTaskListID() {
        return taskListID;
    }




    public int gettaskID() {return taskID;}
    public String gettaskID_name() {return "taskid";}
    public String gettaskListID_name() {return "tasklistid";}
    public String getSumElapsed() {return sumElapsed;}
    public String getSumElapsed_name() {return "sumElapsed";}

    public String getGrpThreads() {return grpThreads;}
    public String getGrpThreads_name() {return "grpThreads";}
    public String getTemplate() { return template; }
    public String getTemplate_name() { return "template"; }
    public String getkbs() {return kbs;}
    public String gettimeStamp() { return timeStamp; }
    public String getkbs_name() {return "kbs";}
    public String gettimeStamp_name() { return "timeStamp"; }


    public String getSceneName() {return sceneName;}
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
        int indeix_=this.resultVersion.indexOf("_");
        if(indeix_>0)
            return this.resultVersion.substring(0,indeix_);
        else
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
    public String getElapsedCount() {
        return elapsedCount;
    }
    public String getAvg() {
        return avg;
    }
    public String getElapsed50() {
        return elapsed50;
    }
    public String getElapsed90() {
        return elapsed90;
    }
    public String getElapsed95() {
        return elapsed95;
    }
    public String getElapsed99() {
        return elapsed99;
    }
    public String getMinElapsed() {
        return minElapsed;
    }
    public String getMaxElapsed() {
        return maxElapsed;
    }
    public String getFail() {
        return fail;
    }
    public String getTps() {
        return tps;
    }
    public String getEnvironment() {
        return environment;
    }
    public String getAvg_name() {
        return "avg";
    }
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
    public String getErrorCount_name() {
        return "errorCount";
    }
    public String getErrorCount(){ return  errorCount ;}
    public String getTimeVersion(){
        int indeix_=this.resultVersion.indexOf("_");
        if(indeix_>0)
            return this.resultVersion.substring(indeix_+1);
        else
            return "";
    }
    public String getTimeVersionName(){return "timeVersion";}

    public String getCreateTime_name(){return "createTime" ;}

    public String getisEnable_name(){return "isEnable" ; }

    public String getUnLoad_name(){return "UnLoad" ; }

    public String getTimeStamp_name(){return  "timeStamp";}

    public String getCheckDataKey()
    {
        return String.format("%s-%s-%s-%s-%s",this.sceneName,this.smallSceneName,this.resultVersion,this.environment,this.template);
    }

}
