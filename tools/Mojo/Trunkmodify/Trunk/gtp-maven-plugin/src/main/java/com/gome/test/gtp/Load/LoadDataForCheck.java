package com.gome.test.gtp.Load;

/**
 * Created by zhangjiadi on 15/11/5.
 */
public class LoadDataForCheck {
    private String sceneName;
    private String resultVersion;
    private String smallSceneName;
    private String environment;
    private String template;

    public LoadDataForCheck(String data){

            String[] list=data.split("[-]");
            if(list.length >= 4)
            {
                this.sceneName=list[0];
                this.smallSceneName=list[1];
                this.resultVersion=list[2];
                this.environment=list[3];
                this.template="";
            }
        if(list.length==5)
            this.template=list[4];

    }

    public String getSceneName_name() {
        return "sceneName";
    }
    public String getSceneName() {return sceneName;}
    public String getSmallSceneName() {
        return smallSceneName;
    }
    public String getSmallSceneName_name() {
        return "smallSceneName";
    }
    public String getResultVersion() {
        return resultVersion;
    }
    public String getResultVersion_name() {
        return "resultVersion";
    }
    public String getEnvironment_name() {
        return "environment";
    }
    public String getEnvironment() {
        return environment;
    }
    public String getTemplate() { return template; }
    public String getTemplate_name() { return "template"; }

    public String getOnlyResultVersion(){
        int index= this.resultVersion.indexOf("_");
        if(index>0)
            return  resultVersion.substring(0,index);
        return  this.resultVersion;
    }

    public String getOnlyTimeVersion()
    {
        int index= this.resultVersion.indexOf("_");
        if(index>0)
            return  resultVersion.substring(index+1);
        return  "";
    }
    public String getTimeVersion_name(){
        return "timeVersion";
    }

}
