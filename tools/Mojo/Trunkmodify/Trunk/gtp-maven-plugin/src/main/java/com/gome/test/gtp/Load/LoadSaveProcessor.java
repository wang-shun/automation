package com.gome.test.gtp.Load;


import com.gome.test.gtp.ReportBo;
import com.gome.test.gtp.ReportJMT;
import com.gome.test.gtp.utils.Util;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zhangjiadi on 15/11/5.
 */
public class LoadSaveProcessor implements Runnable {
    private String fileName;
    private int fileIndex;
    private ReportBo reportBo ;
    private String environment;
    private String dbTablename;
    private int taskId;
    private int taskListId;
    private ConcurrentLinkedQueue queue;

    public LoadSaveProcessor(String fileName,int fileIndex,String environment,String dbTablename,int taskId,int taskListId,ConcurrentLinkedQueue queue) {
        super();
        this.fileName = fileName;
        this.fileIndex = fileIndex;
        this.environment=environment;
        this.dbTablename=dbTablename;
        reportBo = new ReportBo();
        this.taskId=taskId;
        this.taskListId=taskListId;
        this.queue=queue;
    }

    public void run() {

        File file = new File(fileName + ".tmp" + fileIndex) ;
        try{
            if(file.isFile()) {
                execSave(file);

                file.delete();
            }
        }catch (Exception ex)
        {
            System.out.println(String .format("文件:%s,入库发生异常",file.getPath()));
        }

    }

    private void execSave(File file) throws Exception
    {
        BufferedReader br = null;
        String sLine=null;


        try {
            br = Util.getReader(file);
            while ((sLine = br.readLine()) != null) {
                saveBD(sLine);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();

        }finally {
            if(br!=null)
                br.close();
        }
    }

    private boolean saveBD(String line) {
        boolean result = false;
        try {

            ReportJMT model = new ReportJMT(line, environment);
            model.setTaskID(taskId);
            model.setTaskListID(taskListId);
            //写入日志明细
            LoadLogUtil.createLogDetail(model);
            String createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String json = createJson(model,createTimeStr);
            this.queue.add(model);

            if (json.length() > 0) {
                save(json);
            }
        } catch (Exception ex) {
          System.out.println(String.format(" 记录操作插入数据库失败,已保存数据%s！", line));
            saveErrorFile(line);

        }
        return result;
    }

    private String createJson(ReportJMT jmt,String createtime) {
        JSONObject obj = new JSONObject();
        obj.put(jmt.getSceneName_name(), jmt.getSceneName());
        obj.put(jmt.getLabelName_name(), jmt.getLabelName());
        obj.put(jmt.getResultVersion_name(), jmt.getResultVersion());
        obj.put(jmt.getSmallSceneName_name(), jmt.getSmallSceneName());
        obj.put(jmt.getElapsedCount_name(), jmt.getElapsedCount());
        obj.put(jmt.getErrorCount_name(),jmt.getErrorCount());
        obj.put(jmt.getAvg_name(), jmt.getAvg());
        obj.put(jmt.getElapsed50_name(), jmt.getElapsed50());
        obj.put(jmt.getElapsed90_name(), jmt.getElapsed90());
        obj.put(jmt.getElapsed95_name(), jmt.getElapsed95());
        obj.put(jmt.getElapsed99_name(), jmt.getElapsed99());
        obj.put(jmt.getMinElapsed_name(), jmt.getMinElapsed());
        obj.put(jmt.getMaxElapsed_name(), jmt.getMaxElapsed());
        obj.put(jmt.getFail_name(), jmt.getFail());
        obj.put(jmt.getTps_name(), jmt.getTps());
        obj.put(jmt.getEnvironment_name(), jmt.getEnvironment());
        obj.put(jmt.getTps_name(),jmt.getTps());
        obj.put(jmt.gettimeStamp_name(),jmt.gettimeStamp());
        obj.put(jmt.getGrpThreads_name(),jmt.getGrpThreads());
        obj.put(jmt.getTemplate_name(),jmt.getTemplate());
        obj.put(jmt.getSumElapsed_name(),jmt.getSumElapsed());
        obj.put(jmt.gettaskID_name(),jmt.gettaskID());
        obj.put(jmt.gettaskListID_name(),jmt.getTaskListID());
        obj.put(jmt.getCreateTime_name(), createtime);
        obj.put(jmt.getisEnable_name(), true);
        obj.put(jmt.getTimeVersionName(),jmt.getTimeVersion());
        obj.put(jmt.getUnLoad_name(),false);

        return obj.toString();
    }

    private void save(String json) {
        reportBo.insert(json, dbTablename);
    }

    private void saveErrorFile(String line)
    {
        try {
            String errorFilePath = String.format("%serror.csv",fileName);
            Util.writeLine(line, errorFilePath);
        }catch (Exception ex)
        {
            System.out.println(String.format(" 记录异常数据失败,%s！", line));
        }
    }
}
